/**
 * OpenAPI 3 文档解析器
 *
 * 适配 springdoc-openapi 2.x（springdoc-openapi-starter-webmvc-ui 2.5.0）输出的 OpenAPI 3.0.x 文档，
 * 同时向后兼容 Swagger 2.0（springfox 时代）文档，便于外部文档源与历史样例混用。
 *
 * 与旧版（纯 Swagger 2.0 语义）的关键差异，也是本次重写的全部动因：
 *  1. 数据模型位置：definitions -> components.schemas
 *  2. $ref 前缀：#/definitions/（14 字符）-> #/components/schemas/（21 字符）。
 *     旧版硬编码 substr(14) 会把 OpenAPI 3 的引用截成 "schemas/XxxReq"，导致依赖模型与请求样例全部查不到
 *  3. 请求体：parameters[in=body] -> requestBody.content[媒体类型].schema
 *  4. 响应体：responses[code].schema -> responses[code].content[媒体类型].schema，
 *     且 springdoc 对响应实际输出的是通配媒体类型（字面量见 CONTENT_PRIORITY），不能只认 application/json
 *  5. 必填语义：属性上的 required:true -> schema 级 required 数组
 *  6. 服务地址：schemes/host/basePath -> servers[].url（支持变量替换与相对地址）
 *  7. 顶层 tags 可能缺失：springdoc 只在 operation 上写 tags，需从 paths 反向合成，否则左侧导航为空
 *  8. 新增 allOf/oneOf/anyOf 组合、内联匿名模型、nullable、enum、二进制响应的处理
 */
;layui.define(function (exports) {
    "use strict";

    var $ = layui.jquery;

    /** OpenAPI 3 与 Swagger 2 的模型引用前缀 */
    var REF_SCHEMAS_V3 = "#/components/schemas/";
    var REF_SCHEMAS_V2 = "#/definitions/";

    /** paths 下的合法 HTTP 方法，用于跳过 summary/description/servers/parameters 等 path 级字段 */
    var HTTP_METHODS = ["get", "post", "put", "delete", "patch", "head", "options", "trace"];

    /** 鉴权头与 Bearer 前缀，与后端 AuthInterceptor 保持一致 */
    var AUTH_HEADER = "Authorization";
    var BEARER_PREFIX = "Bearer ";

    /**
     * content 媒体类型选择优先级。
     * springdoc 对响应统一输出通配媒体类型（数组第 2 项），旧版只取 application/json 会导致响应模型全部为空。
     * 注意：该字面量含星号加斜杠，写进块注释会提前闭合注释造成语法错误，故注释中一律用文字描述
     */
    var CONTENT_PRIORITY = [
        "application/json",
        "*/*",
        "application/x-www-form-urlencoded",
        "multipart/form-data",
        "text/plain",
        "application/octet-stream"
    ];

    /** 请求样例生成递归深度上限，防御自引用模型导致爆栈 */
    var MAX_MOCK_DEPTH = 8;

    var g_ = {};

    /* ==================== 模块内部状态（每次 resolve 重置，支持切换分组不刷新页面） ==================== */

    var g_apidoc = null;          // 归一化后的文档
    var g_schemas = {};           // components.schemas（或 Swagger 2 的 definitions）
    var g_anon = {};              // 内联匿名模型注册表
    var g_anonSeq = 0;
    var g_servers = [];           // 归一化后的 servers
    var g_activeServer = "";      // 当前选中的 server 绝对地址
    var g_securitySchemes = {};   // components.securitySchemes
    var g_docOrigin = "";         // 文档来源站点，用于解析相对 server url

    /* ==================== 基础工具 ==================== */

    /** HTML 转义：文档中的 description/summary 属外部输入，直接插入模板会破坏 laytpl 渲染 */
    function esc(v) {
        if (v === null || v === undefined) {
            return "";
        }
        return String(v)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function isHttpMethod(k) {
        return HTTP_METHODS.indexOf(String(k).toLowerCase()) >= 0;
    }

    function lookupSchema(name) {
        if (!name) {
            return null;
        }
        return g_schemas[name] || g_anon[name] || null;
    }

    /** 按 JSON Pointer 解析任意 $ref（含 #/components/parameters/X 等非 schema 引用） */
    function resolveRef(ref) {
        if (!ref || typeof ref !== "string" || ref.charAt(0) !== "#") {
            return null;
        }
        var cur = g_apidoc;
        var parts = ref.substring(1).split("/");
        for (var i = 0; i < parts.length; i++) {
            var seg = parts[i];
            if (seg === "") {
                continue;
            }
            seg = decodeURIComponent(seg).replace(/~1/g, "/").replace(/~0/g, "~");
            if (cur === null || cur === undefined) {
                return null;
            }
            cur = cur[seg];
        }
        return cur || null;
    }

    /**
     * 从 $ref 截取模型名。
     * 旧版固定 substr(14) 只认 #/definitions/，此处按前缀精确匹配并保留「取最后一段」兜底
     */
    function cutRef(ref) {
        if (!ref || typeof ref !== "string") {
            return "";
        }
        if (ref.indexOf(REF_SCHEMAS_V3) === 0) {
            return decodeURIComponent(ref.substring(REF_SCHEMAS_V3.length));
        }
        if (ref.indexOf(REF_SCHEMAS_V2) === 0) {
            return decodeURIComponent(ref.substring(REF_SCHEMAS_V2.length));
        }
        var i = ref.lastIndexOf("/");
        return i >= 0 ? decodeURIComponent(ref.substring(i + 1)) : ref;
    }

    /** 合并 allOf（含 $ref 分支）；oneOf/anyOf 同样按合并展示，避免 UI 出现空白模型 */
    function mergeAllOf(schema) {
        if (!schema || (!schema.allOf && !schema.oneOf && !schema.anyOf)) {
            return schema;
        }
        var branches = schema.allOf || schema.oneOf || schema.anyOf;
        var merged = {type: "object", properties: {}, required: []};
        $.each(branches, function (i, sub) {
            var real = (sub && sub.$ref) ? resolveRef(sub.$ref) : sub;
            if (!real) {
                return;
            }
            real = mergeAllOf(real);
            if (real.properties) {
                $.extend(merged.properties, real.properties);
            }
            if (real.required) {
                merged.required = merged.required.concat(real.required);
            }
            if (real.description && !merged.description) {
                merged.description = real.description;
            }
        });
        // 自身直属属性优先级最高
        if (schema.properties) {
            $.extend(merged.properties, schema.properties);
        }
        if (schema.required) {
            merged.required = merged.required.concat(schema.required);
        }
        if (schema.description) {
            merged.description = schema.description;
        }
        return merged;
    }

    /** 基本类型 + format 的展示名。旧版对未知 format 一律降级为 string，丢失了信息 */
    function dataType(type, format) {
        var t = type || "string";
        if (!format) {
            return t;
        }
        switch (format) {
            case "int32":
                return "int";
            case "int64":
                return "long";
            case "float":
            case "double":
            case "decimal":
                return format;
            case "byte":
            case "binary":
            case "date":
            case "date-time":
            case "password":
            case "email":
            case "uuid":
                return "string(" + format + ")";
            default:
                return t + "(" + format + ")";
        }
    }

    /** 模型名渲染为可点击链接，点击后由 tplApiBody 的 gotoModel 滚动定位到「依赖数据」 */
    function wrapShowType(name) {
        return "<a href=\"javascript:;\" onclick=\"gotoModel('" + esc(name) + "');\">" + esc(name) + "</a>";
    }

    /**
     * 标量转文本，供模板的 value 属性直接输出。
     * 对象/数组转 JSON，避开 [object Object]；同时做 HTML 转义（双引号会变成 &quot;，
     * 在属性值里会被浏览器正确解回）；不再用 || "" 兼容，否则默认值 0 与 false 会被吃掉
     */
    function scalarText(v) {
        if (v === null || v === undefined) {
            return "";
        }
        if (typeof v === "object") {
            return esc(JSON.stringify(v));
        }
        return esc(v);
    }

    /**
     * 汇总 schema 上的取值约束，产出一段已转义的纯文本，供表格「约束」列直接输出。
     * springdoc 会把 @NotNull / @Size / @Pattern / @Max 等 Bean Validation 注解翻译到这些字段上，
     * 旧版完全没有呈现，调试时只能靠猜
     */
    function constraintText(schema) {
        if (!schema) {
            return "";
        }
        var parts = [];
        if (schema["enum"] && schema["enum"].length) {
            parts.push("可选值: " + schema["enum"].join(" | "));
        }
        if (schema.pattern) {
            parts.push("正则: " + schema.pattern);
        }
        var min = schema.minimum !== undefined ? schema.minimum : schema.exclusiveMinimum;
        var max = schema.maximum !== undefined ? schema.maximum : schema.exclusiveMaximum;
        if (min !== undefined || max !== undefined) {
            parts.push("取值范围: " + (min !== undefined ? min : "不限") + " ~ " + (max !== undefined ? max : "不限"));
        }
        if (schema.minLength !== undefined || schema.maxLength !== undefined) {
            parts.push("长度: " + (schema.minLength !== undefined ? schema.minLength : 0)
                + " ~ " + (schema.maxLength !== undefined ? schema.maxLength : "不限"));
        }
        if (schema["default"] !== undefined) {
            parts.push("默认值: " + schema["default"]);
        }
        if (schema.example !== undefined) {
            parts.push("示例: " + (typeof schema.example === "object"
                ? JSON.stringify(schema.example) : schema.example));
        }
        return esc(parts.join("；"));
    }

    /**
     * 从 content 中挑选媒体类型。
     * springdoc 响应实测输出通配类型，请求体输出 application/json，上传接口输出 multipart/form-data
     */
    function pickContent(content) {
        if (!content || typeof content !== "object") {
            return {contentType: "", media: null};
        }
        var keys = [];
        $.each(content, function (k) {
            keys.push(k);
        });
        if (!keys.length) {
            return {contentType: "", media: null};
        }
        for (var i = 0; i < CONTENT_PRIORITY.length; i++) {
            for (var j = 0; j < keys.length; j++) {
                if (keys[j].toLowerCase() === CONTENT_PRIORITY[i].toLowerCase()) {
                    return {contentType: keys[j], media: content[keys[j]] || null};
                }
            }
        }
        return {contentType: keys[0], media: content[keys[0]] || null};
    }

    /* ==================== 类型解析 ==================== */

    /**
     * 解析数据类型，包装成统一的类型描述。
     * 返回值：{type, showtype, modelflag, refs, isFile, isEnum, enumValues, nullable}
     */
    function resolveRealType(pmeta) {
        var r = {
            type: "string",
            showtype: "string",
            modelflag: false,
            refs: [],
            isFile: false,
            isEnum: false,
            enumValues: null,
            nullable: false
        };
        if (!pmeta) {
            return r;
        }
        r.nullable = !!pmeta.nullable;

        // 1) $ref
        if (pmeta.$ref) {
            var rn = cutRef(pmeta.$ref);
            r.type = rn;
            r.modelflag = true;
            r.refs.push(rn);
            r.showtype = wrapShowType(rn);
            return done(r);
        }

        // 2) allOf / oneOf / anyOf 组合
        if (pmeta.allOf || pmeta.oneOf || pmeta.anyOf) {
            var merged = mergeAllOf(pmeta);
            var aname = "InlineModel" + (++g_anonSeq);
            g_anon[aname] = merged;
            r.type = aname;
            r.modelflag = true;
            r.refs.push(aname);
            r.showtype = wrapShowType(aname);
            // 组合分支中的具名模型也要收集，便于「依赖数据」展示
            var branches = pmeta.allOf || pmeta.oneOf || pmeta.anyOf;
            $.each(branches, function (i, sub) {
                if (sub && sub.$ref) {
                    r.refs.push(cutRef(sub.$ref));
                }
            });
            return done(r);
        }

        // 3) 枚举
        if (pmeta["enum"] && pmeta["enum"].length) {
            r.isEnum = true;
            r.enumValues = pmeta["enum"];
            r.type = pmeta.type || "string";
            r.showtype = r.type + "(enum:" + esc(pmeta["enum"].join(" | ")) + ")";
            return done(r);
        }

        // 4) 数组
        if (pmeta.type === "array") {
            var inner = resolveRealType(pmeta.items);
            r.type = inner.type;
            r.showtype = "array(" + inner.showtype + ")";
            r.modelflag = inner.modelflag;
            r.refs = inner.refs.slice();
            r.isFile = inner.isFile;
            return done(r);
        }

        // 5) 对象
        if (pmeta.type === "object") {
            if (pmeta.additionalProperties) {
                var ap = resolveRealType(pmeta.additionalProperties);
                r.type = ap.type;
                r.showtype = "object(" + ap.showtype + ")";
                r.modelflag = ap.modelflag;
                r.refs = ap.refs.slice();
                return done(r);
            }
            if (pmeta.properties) {
                // 内联匿名对象模型：注册后按具名模型处理
                var iname = "InlineModel" + (++g_anonSeq);
                g_anon[iname] = pmeta;
                r.type = iname;
                r.modelflag = true;
                r.refs.push(iname);
                r.showtype = wrapShowType(iname);
                return done(r);
            }
            r.type = "object";
            r.showtype = "object";
            return done(r);
        }

        // 6) 二进制 / 文件
        if (pmeta.type === "string" && (pmeta.format === "binary" || pmeta.format === "byte")) {
            r.isFile = true;
        }

        r.type = pmeta.type || "string";
        r.showtype = dataType(r.type, pmeta.format);
        return done(r);
    }

    function done(r) {
        if (r.nullable) {
            r.showtype = r.showtype + "?";
        }
        return r;
    }

    /* ==================== servers 归一化 ==================== */

    /**
     * OpenAPI 3 用 servers[].url 取代 Swagger 2 的 schemes/host/basePath 三件套。
     * 支持 {var} 变量替换（取 default）与相对地址（相对文档来源站点补全）
     */
    function buildServers() {
        var list = [];
        var raw = g_apidoc.servers;
        if (raw && raw.length) {
            $.each(raw, function (i, s) {
                if (!s) {
                    return;
                }
                var url = s.url || "";
                if (s.variables) {
                    $.each(s.variables, function (k, v) {
                        var def = (v && (v["default"] !== undefined ? v["default"] : "")) || "";
                        url = url.replace(new RegExp("\\{" + k + "\\}", "g"), def);
                    });
                }
                var abs;
                if (/^https?:\/\//i.test(url)) {
                    abs = url;
                } else {
                    var base = g_docOrigin || location.origin;
                    abs = base.replace(/\/+$/, "") + (url.charAt(0) === "/" ? url : "/" + url);
                }
                list.push({
                    url: url,
                    abs: abs.replace(/\/+$/, ""),
                    description: esc(s.description || "")
                });
            });
        }
        // Swagger 2.0 兼容：由 schemes/host/basePath 三件套合成 servers
        if (!list.length && g_apidoc.host) {
            var schemes = (g_apidoc.schemes && g_apidoc.schemes.length)
                ? g_apidoc.schemes
                : [location.protocol.replace(":", "")];
            var basePath = g_apidoc.basePath || "";
            $.each(schemes, function (i, sc) {
                var url = sc + "://" + g_apidoc.host + basePath;
                list.push({
                    url: url,
                    abs: url.replace(/\/+$/, ""),
                    description: "由 Swagger 2 的 schemes/host/basePath 合成"
                });
            });
        }
        // 文档未声明 servers 时回退到文档来源站点，保证调试功能可用
        if (!list.length) {
            var fallback = g_docOrigin || location.origin;
            list.push({url: fallback, abs: fallback.replace(/\/+$/, ""), description: "文档来源站点"});
        }
        return list;
    }

    /* ==================== 安全方案 ==================== */

    function buildSecuritySchemes() {
        var c = g_apidoc.components || {};
        return c.securitySchemes || g_apidoc.securityDefinitions || {};
    }

    /**
     * 面向首页展示的安全方案列表。
     * g_securitySchemes 保持原始值不动，因为 requirementNeedsAuthHeader 要用 type/scheme/in/name
     * 做小写比较，一旦转义就会破坏判定；展示数据单独归一化并转义，与「解析层转义、模板直出」的约定一致
     */
    function buildSecuritySchemeList() {
        var list = [];
        $.each(g_securitySchemes || {}, function (name, scheme) {
            if (!scheme) {
                return;
            }
            var type = String(scheme.type || "");
            var detail = [];
            if (scheme.scheme) {
                detail.push("scheme=" + scheme.scheme);
            }
            if (scheme.bearerFormat) {
                detail.push("bearerFormat=" + scheme.bearerFormat);
            }
            if (scheme["in"]) {
                detail.push("in=" + scheme["in"]);
            }
            // apiKey 方案的 name 是实际的头/查询参数名，与 securityScheme 的 key 不是一回事
            if (scheme.name && type.toLowerCase() === "apikey") {
                detail.push("name=" + scheme.name);
            }
            list.push({
                name: esc(name),
                type: esc(type),
                detail: esc(detail.join(", ")),
                description: esc(scheme.description || ""),
                // 是否由顶栏 Token 输入框统一提供，决定首页要不要提示用户去填 Token
                isBearerToken: type.toLowerCase() === "http"
                    && String(scheme.scheme || "").toLowerCase() === "bearer"
            });
        });
        return list;
    }

    /** 是否为鉴权头参数（springfox 时代 controller 上手写的 @Parameter(name="Authorization", in=HEADER)） */
    function isAuthHeaderParam(p) {
        return !!p
            && String(p.name || "").toLowerCase() === AUTH_HEADER.toLowerCase()
            && String(p["in"] || "").toLowerCase() === "header";
    }

    /** 判断某个 security 需求是否最终要求 Authorization 头 */
    function requirementNeedsAuthHeader(requirement) {
        var need = false;
        $.each(requirement || {}, function (name) {
            var scheme = g_securitySchemes[name];
            if (!scheme) {
                // 未声明的方案按名字兜底判断，兼容后端未配 securitySchemes 的文档
                if (name.toLowerCase() === AUTH_HEADER.toLowerCase() || /bearer/i.test(name)) {
                    need = true;
                }
                return;
            }
            var type = String(scheme.type || "").toLowerCase();
            if (type === "http" && String(scheme.scheme || "").toLowerCase() === "bearer") {
                need = true;
            } else if (type === "apikey"
                && String(scheme["in"] || "").toLowerCase() === "header"
                && String(scheme.name || "").toLowerCase() === AUTH_HEADER.toLowerCase()) {
                need = true;
            }
        });
        return need;
    }

    /** 该接口是否需要携带 Token：operation 级 security 优先，其次文档级，最后兜底看显式声明的 header 参数 */
    function needToken(op) {
        var sec = op.security || g_apidoc.security;
        if (sec && sec.length) {
            for (var i = 0; i < sec.length; i++) {
                if (requirementNeedsAuthHeader(sec[i])) {
                    return true;
                }
            }
            return false;
        }
        var params = op.parameters || [];
        for (var j = 0; j < params.length; j++) {
            if (isAuthHeaderParam(params[j])) {
                return true;
            }
        }
        return false;
    }

    /* ==================== 参数 / 请求体 / 响应归一化 ==================== */

    /** path 级 parameters 与 operation 级合并，operation 级同名（name+in）优先 */
    function mergePathParameters(pathItem, op) {
        var result = [];
        var seen = {};
        $.each(op.parameters || [], function (i, p) {
            if (!p) {
                return;
            }
            var real = p.$ref ? resolveRef(p.$ref) : p;
            if (!real) {
                return;
            }
            seen[(real.name || "") + "::" + (real["in"] || "")] = true;
            result.push(real);
        });
        $.each(pathItem.parameters || [], function (i, p) {
            if (!p) {
                return;
            }
            var real = p.$ref ? resolveRef(p.$ref) : p;
            if (!real) {
                return;
            }
            var key = (real.name || "") + "::" + (real["in"] || "");
            if (!seen[key]) {
                result.push(real);
            }
        });
        return result;
    }

    /** 归一化单个参数：补 showtype/itemtype，并按 OpenAPI 3 的 schema 位置取类型 */
    function normalizeParameter(p) {
        var schema = p.schema || {};
        // Swagger 2 兼容：类型直接写在参数上
        if (!p.schema && p.type) {
            schema = {type: p.type, format: p.format, items: p.items, "enum": p["enum"]};
        }
        var rt = resolveRealType(schema);
        p.showtype = rt.showtype;
        p.itemtype = rt.type;
        p.modelflag = rt.modelflag;
        p.refs = rt.refs;
        p.isFile = rt.isFile || p.type === "file";
        p.isEnum = rt.isEnum;
        p.enumValues = rt.enumValues;
        p.isObjectParam = !!schema.properties || (rt.modelflag && isObjectModel(rt.type));
        p.inLower = String(p["in"] || "").toLowerCase();
        p.defaultValue = scalarText(schema["default"] !== undefined ? schema["default"] : p["default"]);
        p.exampleValue = scalarText(schema.example);
        p.constraint = constraintText(schema);
        p.isAuthHeader = isAuthHeaderParam(p);
        p.description = esc(p.description || "");
        p.required = !!p.required;
        return p;
    }

    function isObjectModel(name) {
        var s = lookupSchema(name);
        if (!s) {
            return false;
        }
        s = mergeAllOf(s);
        return !!s.properties || s.type === "object";
    }

    /**
     * 展平 query 中的对象参数。
     * springdoc 未标注 @ParameterObject 时，会把 @ModelAttribute 查询对象输出成
     * 单个 name=req、schema=$ref 的 query 参数；而 Spring MVC 实际按 page=1&size=10 扁平绑定，
     * 直接渲染会导致调试表单无法填写，故此处按对象属性展开为独立输入项
     */
    function buildDebugParameters(op) {
        var result = [];
        $.each(op.parameters || [], function (i, p) {
            // 鉴权头由顶栏 Token 统一提供，不再作为普通参数出现，避免两处输入互相冲突
            if (op.needToken && p.isAuthHeader) {
                return;
            }
            var expandable = p.inLower === "query" && p.isObjectParam;
            if (!expandable) {
                result.push($.extend({}, p, {debugName: p.name, expandedFrom: ""}));
                return;
            }
            var model = lookupSchema(p.itemtype);
            model = model ? mergeAllOf(model) : null;
            var props = (model && model.properties) || null;
            if (!props) {
                result.push($.extend({}, p, {debugName: p.name, expandedFrom: ""}));
                return;
            }
            var requiredList = (model && model.required) || [];
            $.each(props, function (pname, pmeta) {
                var rt = resolveRealType(pmeta);
                result.push({
                    name: pname,
                    debugName: pname,
                    expandedFrom: p.name,
                    "in": "query",
                    inLower: "query",
                    required: requiredList.indexOf(pname) >= 0,
                    description: esc(pmeta.description || ""),
                    showtype: rt.showtype,
                    itemtype: rt.type,
                    modelflag: rt.modelflag,
                    refs: rt.refs,
                    isFile: rt.isFile,
                    isEnum: rt.isEnum,
                    enumValues: rt.enumValues,
                    constraint: constraintText(pmeta),
                    isAuthHeader: false,
                    defaultValue: scalarText(pmeta["default"]),
                    exampleValue: scalarText(pmeta.example)
                });
            });
        });
        return result;
    }

    /**
     * Swagger 2.0 兼容：把 parameters 中的 in=body / in=formData 提升为 OpenAPI 3 的 requestBody。
     * 提升后 op.parameters 只保留 query/path/header/cookie，
     * 使后续归一化与模板渲染只需面对一种请求体结构
     */
    function liftV2RequestBody(op) {
        // 仅对 Swagger 2 文档生效，OpenAPI 3 已有 requestBody，不得干预
        if (op.requestBody || !g_apidoc.swagger) {
            return;
        }
        var bodyParam = null;
        var formParams = [];
        var kept = [];
        $.each(op.parameters || [], function (i, p) {
            if (!p) {
                return;
            }
            var inLower = String(p["in"] || "").toLowerCase();
            if (inLower === "body") {
                bodyParam = p;
            } else if (inLower === "formdata") {
                formParams.push(p);
            } else {
                kept.push(p);
            }
        });
        if (!bodyParam && !formParams.length) {
            return;
        }
        op.parameters = kept;
        // Swagger 2 的媒体类型在 consumes，operation 级优先于文档级
        var consumes = (op.consumes && op.consumes.length) ? op.consumes
            : ((g_apidoc.consumes && g_apidoc.consumes.length) ? g_apidoc.consumes : null);

        if (bodyParam) {
            var bodyCt = consumes ? consumes[0] : "application/json";
            var bodyContent = {};
            bodyContent[bodyCt] = {schema: bodyParam.schema || null};
            op.requestBody = {
                required: !!bodyParam.required,
                description: bodyParam.description || "",
                content: bodyContent
            };
            return;
        }

        var formCt = consumes ? consumes[0] : "application/x-www-form-urlencoded";
        var properties = {};
        var required = [];
        $.each(formParams, function (i, p) {
            // Swagger 2 把类型直接写在参数上，需搬进 property
            var pmeta = {
                type: p.type || "string",
                format: p.format,
                items: p.items,
                "enum": p["enum"],
                description: p.description
            };
            // Swagger 2 的 type=file 在 OpenAPI 3 中表达为 string + format=binary
            if (pmeta.type === "file") {
                pmeta.type = "string";
                pmeta.format = "binary";
            }
            properties[p.name] = pmeta;
            if (p.required) {
                required.push(p.name);
            }
        });
        var formContent = {};
        formContent[formCt] = {schema: {type: "object", properties: properties, required: required}};
        op.requestBody = {required: required.length > 0, content: formContent};
    }

    /** 归一化 requestBody（OpenAPI 3 新增结构，取代 Swagger 2 的 in=body 参数） */
    function normalizeRequestBody(op) {
        op.reqBody = null;
        var rb = op.requestBody;
        if (!rb) {
            return;
        }
        if (rb.$ref) {
            rb = resolveRef(rb.$ref) || rb;
        }
        var picked = pickContent(rb.content);
        var schema = (picked.media && picked.media.schema) || null;
        var rt = resolveRealType(schema);
        var lower = (picked.contentType || "").toLowerCase();
        var isForm = lower.indexOf("form-data") >= 0 || lower.indexOf("x-www-form-urlencoded") >= 0;
        op.reqBody = {
            required: !!rb.required,
            contentType: picked.contentType || "application/json",
            description: esc(rb.description || ""),
            showtype: rt.showtype,
            itemtype: rt.type,
            modelflag: rt.modelflag,
            refs: rt.refs,
            isForm: isForm,
            isFile: rt.isFile,
            // 表单类请求体展开为字段列表，multipart 下的 binary 字段渲染为文件上传控件
            formFields: isForm ? buildFormFields(schema) : [],
            mockJson: isForm ? "" : g_.jsonmock.mock(schema)
        };
    }

    /** 表单 / multipart 请求体的字段展开 */
    function buildFormFields(schema) {
        var fields = [];
        if (!schema) {
            return fields;
        }
        if (schema.$ref) {
            schema = resolveRef(schema.$ref) || schema;
        }
        schema = mergeAllOf(schema);
        if (!schema.properties) {
            return fields;
        }
        var requiredList = schema.required || [];
        $.each(schema.properties, function (name, pmeta) {
            var rt = resolveRealType(pmeta);
            fields.push({
                name: name,
                required: requiredList.indexOf(name) >= 0,
                description: esc(pmeta.description || ""),
                showtype: rt.showtype,
                itemtype: rt.type,
                isFile: rt.isFile || pmeta.format === "binary",
                constraint: constraintText(pmeta),
                defaultValue: scalarText(pmeta["default"])
            });
        });
        return fields;
    }

    /**
     * 归一化 responses。
     * OpenAPI 3 的 schema 位于 content[媒体类型].schema；
     * 实测 springdoc 输出的是通配媒体类型，且二进制接口（如验证码图片）的 content 为空数组
     */
    function normalizeResponses(op) {
        $.each(op.responses || {}, function (status, resp) {
            if (!resp) {
                return;
            }
            if (resp.$ref) {
                var resolved = resolveRef(resp.$ref);
                if (resolved) {
                    resp = resolved;
                    op.responses[status] = resp;
                }
            }
            var picked = pickContent(resp.content);
            var schema = (picked.media && picked.media.schema) || null;
            // Swagger 2.0 兼容：schema 直接挂在 response 上，没有 content 层
            if (!schema && resp.schema) {
                schema = resp.schema;
            }
            var rt = resolveRealType(schema);
            var lower = (picked.contentType || "").toLowerCase();
            resp.contentType = picked.contentType;
            if (!resp.contentType) {
                // Swagger 2 的媒体类型在 produces，response 级优先于文档级
                var produces = (resp.produces && resp.produces.length) ? resp.produces : g_apidoc.produces;
                resp.contentType = (produces && produces.length) ? produces[0] : "";
            }
            resp.itemtype = rt.type;
            resp.modelflag = rt.modelflag;
            resp.refs = rt.refs;
            resp.hasBody = !!picked.media || !!schema;
            resp.isBinary = !resp.hasBody
                || lower.indexOf("octet-stream") >= 0
                || lower.indexOf("image/") === 0
                || lower.indexOf("application/pdf") >= 0;
            resp.showtype = resp.hasBody ? rt.showtype : "<span class=\"data-none\">无响应体</span>";
            resp.description = esc(resp.description || "");
            if (resp.headers) {
                $.each(resp.headers, function (hname, hmeta) {
                    if (!hmeta) {
                        return;
                    }
                    var hrt = resolveRealType(hmeta.schema || hmeta);
                    hmeta.showtype = hrt.showtype;
                    hmeta.itemtype = hrt.type;
                    hmeta.refs = hrt.refs;
                    hmeta.description = esc(hmeta.description || "");
                });
            }
        });
    }

    /* ==================== 依赖模型收集 ==================== */

    /** 递归收集并在 models 中登记具名模型，models 先占位再展开，天然阻断循环引用 */
    function normalizeModel(name, models) {
        if (!name || models.hasOwnProperty(name)) {
            return;
        }
        var raw = lookupSchema(name);
        if (!raw) {
            return;
        }
        var merged = mergeAllOf(raw);
        var norm = {
            name: name,
            description: esc(merged.description || ""),
            requiredList: merged.required || [],
            properties: {}
        };
        models[name] = norm;

        var props = merged.properties || {};
        var requiredList = merged.required || [];
        $.each(props, function (pname, pmeta) {
            var rt = resolveRealType(pmeta);
            norm.properties[pname] = {
                name: pname,
                showtype: rt.showtype,
                itemtype: rt.type,
                description: esc(pmeta.description || ""),
                // OpenAPI 3 的必填语义在 schema 级 required 数组，不再是属性上的 required:true
                required: requiredList.indexOf(pname) >= 0,
                nullable: !!pmeta.nullable,
                isEnum: rt.isEnum,
                enumValues: rt.enumValues,
                constraint: constraintText(pmeta),
                example: pmeta.example !== undefined ? pmeta.example : "",
                format: pmeta.format || "",
                pattern: pmeta.pattern || ""
            };
            $.each(rt.refs, function (i, mn) {
                normalizeModel(mn, models);
            });
        });

        // additionalProperties 引用的模型同样需要登记
        if (merged.additionalProperties) {
            var aprt = resolveRealType(merged.additionalProperties);
            $.each(aprt.refs, function (i, mn) {
                normalizeModel(mn, models);
            });
        }
    }

    /** 汇总该接口涉及的全部依赖模型，供模板「依赖数据」区渲染 */
    function collectModels(op) {
        op.models = {};
        var names = [];
        $.each(op.parameters || [], function (i, p) {
            names = names.concat(p.refs || []);
        });
        $.each(op.debugParameters || [], function (i, p) {
            names = names.concat(p.refs || []);
        });
        if (op.reqBody) {
            names = names.concat(op.reqBody.refs || []);
            $.each(op.reqBody.formFields || [], function (i, f) {
                if (f.itemtype && lookupSchema(f.itemtype)) {
                    names.push(f.itemtype);
                }
            });
        }
        $.each(op.responses || {}, function (status, resp) {
            names = names.concat(resp.refs || []);
            $.each(resp.headers || {}, function (hname, hmeta) {
                names = names.concat((hmeta && hmeta.refs) || []);
            });
        });
        $.each(names, function (i, n) {
            normalizeModel(n, op.models);
        });
    }

    /* ==================== info 归一化 ==================== */

    /**
     * info 字段归一化。
     * laytpl 的 {{ }} 不做 HTML 转义，而标题/描述/联系方式均来自 Java 注解（可能含 < > & 等字符），
     * 统一在解析层转义，模板直接输出即可；
     * 同时把 contact/license 补齐为空对象，避开模板直取 d.info.contact.name 在无 contact 时抛异常
     */
    function normalizeInfo() {
        var info = g_apidoc.info || {};
        var contact = info.contact || {};
        var license = info.license || {};
        g_apidoc.info = {
            title: esc(info.title || ""),
            description: esc(info.description || ""),
            version: esc(info.version || ""),
            termsOfService: esc(info.termsOfService || ""),
            contact: {
                name: esc(contact.name || ""),
                email: esc(contact.email || ""),
                url: esc(contact.url || "")
            },
            license: {
                name: esc(license.name || ""),
                url: esc(license.url || "")
            }
        };
    }

    /* ==================== tags 合成 ==================== */

    /**
     * 合成左侧导航所需的 tags。
     * 实测 springdoc 输出的文档没有顶层 tags（只在 operation 上写），
     * 旧版直接遍历 apidoc.tags 会得到 null，导致左侧导航与顶部搜索全空
     */
    function buildTags() {
        var order = [];
        var index = {};

        function ensure(name, description) {
            if (!index[name]) {
                index[name] = {name: name, description: esc(description || ""), paths: []};
                order.push(name);
            } else if (description && !index[name].description) {
                index[name].description = esc(description);
            }
            return index[name];
        }

        // 先采纳文档声明的顶层 tags，保留其顺序与描述
        $.each(g_apidoc.tags || [], function (i, t) {
            if (t && t.name) {
                ensure(esc(t.name), t.description);
            }
        });

        // 再从各 operation 补齐未声明的 tag
        $.each(g_apidoc.paths || {}, function (path, pathItem) {
            if (!pathItem) {
                return;
            }
            $.each(pathItem, function (method, op) {
                if (!isHttpMethod(method) || !op) {
                    return;
                }
                var names = (op.tags && op.tags.length) ? op.tags : ["default"];
                $.each(names, function (i, tn) {
                    // summary/description 已在 _parse 中转义，此处不得重复转义
                    ensure(esc(tn), "").paths.push({
                        name: op.summary,
                        description: op.description,
                        path: path,
                        httpmethod: method,
                        deprecated: !!op.deprecated,
                        needToken: needToken(op)
                    });
                });
            });
        });

        var tags = [];
        $.each(order, function (i, n) {
            tags.push(index[n]);
        });
        g_apidoc.tags = tags;
    }

    /* ==================== 主解析流程 ==================== */

    function _parse() {
        g_schemas = (g_apidoc.components && g_apidoc.components.schemas) || g_apidoc.definitions || {};
        g_securitySchemes = buildSecuritySchemes();
        g_servers = buildServers();
        g_activeServer = g_servers.length ? g_servers[0].abs : "";
        normalizeInfo();

        $.each(g_apidoc.paths || {}, function (path, pathItem) {
            if (!pathItem) {
                return;
            }
            $.each(pathItem, function (method, op) {
                // OpenAPI 3 的 path 级字段（summary/description/servers/parameters/$ref）不是 HTTP 方法，必须跳过
                if (!isHttpMethod(method) || !op) {
                    return;
                }
                op.path = path;
                op.httpmethod = method;
                // summary/description 用于展示，在此统一转义；path 与 httpmethod 保持原文，
                // 因为它们要参与请求 URL 拼接与 DOM 属性选择器匹配，转义会破坏功能
                op.summary = esc(op.summary || op.operationId || path);
                op.description = esc(op.description || "");
                op.parameters = mergePathParameters(pathItem, op);
                // 提升必须最先执行，且先于 normalizeParameter：
                //  1) 否则 Swagger 2 的 in=body / in=formData 会残留在调试参数里，多出一个错误的输入项
                //  2) normalizeParameter 会对 description 做 HTML 转义，提升时再搬进 requestBody 会被二次转义
                liftV2RequestBody(op);
                $.each(op.parameters, function (i, p) {
                    normalizeParameter(p);
                });
                // needToken 必须先于展平算出：展平要据此剔除鉴权头参数
                op.needToken = needToken(op);
                op.debugParameters = buildDebugParameters(op);
                normalizeRequestBody(op);
                normalizeResponses(op);
                collectModels(op);
            });
        });

        buildTags();
    }

    /**
     * 解析入口。
     * @param _apidoc   OpenAPI 3（或 Swagger 2）文档对象
     * @param _opts     可选项：{docOrigin: 文档来源站点绝对地址}，用于解析相对 servers 与 logo
     */
    g_.resolve = function (_apidoc, _opts) {
        if (!g_.looksLikeApiDoc(_apidoc)) {
            throw new Error("not a valid OpenAPI/Swagger document");
        }
        // 重置状态，支持在同一页面内切换分组 / 切换文档源
        g_anon = {};
        g_anonSeq = 0;
        g_docOrigin = (_opts && _opts.docOrigin) || "";
        g_apidoc = _apidoc;
        _parse();
        return g_apidoc;
    };

    /**
     * 判断响应体是否真的是 API 文档。
     * 后端 GlobalExceptionHandler 会把资源解析异常转成 HTTP 200 + 错误信封 JSON，
     * 仅凭 HTTP 状态码无法识别失败，必须校验文档特征字段
     */
    g_.looksLikeApiDoc = function (obj) {
        return !!obj && typeof obj === "object"
            && (typeof obj.openapi === "string" || typeof obj.swagger === "string")
            && !!obj.paths;
    };

    /* ==================== 对外查询接口（供 swagger-ui.js 与模板使用） ==================== */

    g_.doc = function () {
        return g_apidoc;
    };

    g_.esc = esc;

    g_.servers = function () {
        return g_servers;
    };

    g_.activeServer = function () {
        return g_activeServer || (g_servers.length ? g_servers[0].abs : "");
    };

    g_.setServer = function (abs) {
        if (abs) {
            g_activeServer = String(abs).replace(/\/+$/, "");
        }
        return g_activeServer;
    };

    g_.docOrigin = function () {
        return g_docOrigin || location.origin;
    };

    /** 拼接完整请求地址：OpenAPI 3 的 servers[].url 已含 context-path，直接前缀拼接即可 */
    g_.requestUrl = function (path) {
        var base = g_.activeServer();
        var p = path || "";
        if (p.charAt(0) !== "/") {
            p = "/" + p;
        }
        return base.replace(/\/+$/, "") + p;
    };

    g_.securitySchemes = function () {
        return g_securitySchemes;
    };

    /** 已转义的安全方案展示列表，供首页「鉴权方式」区直接渲染 */
    g_.securitySchemeList = function () {
        return buildSecuritySchemeList();
    };

    g_.needToken = needToken;

    /** 按后端 AuthInterceptor 的约定组装鉴权头：严格 "Bearer " 前缀（含空格） */
    g_.authHeaderValue = function (token) {
        var t = String(token || "").trim();
        if (!t) {
            return "";
        }
        return t.toLowerCase().indexOf(BEARER_PREFIX.toLowerCase()) === 0 ? t : BEARER_PREFIX + t;
    };

    g_.authHeaderName = function () {
        return AUTH_HEADER;
    };

    /**
     * 直接产出可并入 $.ajax headers 的鉴权头；无 Token 时返回空对象。
     * 不传 token 则取已保存的 Token
     */
    g_.authHeader = function (token) {
        var headers = {};
        var value = g_.authHeaderValue(token === undefined ? g_.token() : token);
        if (value) {
            headers[AUTH_HEADER] = value;
        }
        return headers;
    };

    /* ==================== Token 会话 ==================== */

    /**
     * 调试用 Token 的读取与保存。
     * 存 localStorage 以便刷新页面、切换分组后不必重复粘贴；
     * 无痕模式 / 禁用存储下 localStorage 会抛异常，故全部包 try 并退回内存
     */
    var TOKEN_STORAGE_KEY = "apidoc.bearerToken";
    var g_memToken = "";

    g_.token = function () {
        try {
            var v = localStorage.getItem(TOKEN_STORAGE_KEY);
            if (v !== null) {
                return v;
            }
        } catch (e) {
            // 存储不可用，退回内存值
        }
        return g_memToken;
    };

    g_.saveToken = function (v) {
        var t = String(v === null || v === undefined ? "" : v).trim();
        g_memToken = t;
        try {
            if (t) {
                localStorage.setItem(TOKEN_STORAGE_KEY, t);
            } else {
                localStorage.removeItem(TOKEN_STORAGE_KEY);
            }
        } catch (e) {
            // 存储不可用时仅在本次会话内生效
        }
        return t;
    };

    /** logo 等资源按文档来源站点拼接，取代旧版的 schemes[0]://host */
    g_.originUrl = function (path) {
        var base = g_.docOrigin();
        var p = path || "";
        if (p.charAt(0) !== "/") {
            p = "/" + p;
        }
        return base.replace(/\/+$/, "") + p;
    };

    /* ==================== 请求样例生成 ==================== */

    /**
     * 生成 JSON 请求样例。
     * 相比旧版增强：支持 components.schemas、allOf、enum、example/default 优先、
     * 循环引用与深度保护（旧版仅靠 _parent 单层比较，遇到 A->B->A 会无限递归）
     */
    g_.jsonmock = (function () {

        var _ = {
            /**
             * @param target 模型名（string）或 schema 对象
             */
            mock: function (target) {
                if (!target) {
                    return "";
                }
                var schema = (typeof target === "string") ? lookupSchema(target) : target;
                if (!schema) {
                    return "";
                }
                try {
                    var value = genValue(schema, {}, 0);
                    if (value === undefined) {
                        return "";
                    }
                    return JSON.stringify(value, null, "\t");
                } catch (e) {
                    return "";
                }
            }
        };

        function genValue(schema, visiting, depth) {
            if (!schema || depth > MAX_MOCK_DEPTH) {
                return "";
            }
            if (schema.$ref) {
                var name = cutRef(schema.$ref);
                var ref = lookupSchema(name);
                if (!ref) {
                    return {};
                }
                if (visiting[name]) {
                    // 循环引用：置空对象终止递归
                    return {};
                }
                var next = $.extend({}, visiting);
                next[name] = true;
                return genValue(ref, next, depth + 1);
            }
            if (schema.allOf || schema.oneOf || schema.anyOf) {
                return genValue(mergeAllOf(schema), visiting, depth + 1);
            }
            // 显式样例值优先，文档里已有 example 时直接采用
            if (schema.example !== undefined) {
                return schema.example;
            }
            if (schema["default"] !== undefined) {
                return schema["default"];
            }
            if (schema["enum"] && schema["enum"].length) {
                return schema["enum"][0];
            }

            var type = schema.type;
            if (!type && schema.properties) {
                type = "object";
            }
            switch (type) {
                case "array":
                    if (!schema.items) {
                        return [];
                    }
                    return [genValue(schema.items, visiting, depth + 1)];
                case "object":
                    var obj = {};
                    if (schema.properties) {
                        $.each(schema.properties, function (k, v) {
                            obj[k] = genValue(v, visiting, depth + 1);
                        });
                    } else if (schema.additionalProperties) {
                        obj["key"] = genValue(schema.additionalProperties, visiting, depth + 1);
                    }
                    return obj;
                case "integer":
                case "number":
                    return 0;
                case "boolean":
                    return false;
                case "string":
                    return genString(schema.format);
                default:
                    return "";
            }
        }

        function genString(format) {
            switch (format) {
                case "date":
                    return "2020-01-01";
                case "date-time":
                    return "2020-01-01 00:00:00.000";
                case "uuid":
                    return "00000000-0000-0000-0000-000000000000";
                case "email":
                    return "user@example.com";
                case "byte":
                case "binary":
                case "password":
                    return "";
                default:
                    return "";
            }
        }

        return _;
    })();

    exports('nswagger', g_);
});

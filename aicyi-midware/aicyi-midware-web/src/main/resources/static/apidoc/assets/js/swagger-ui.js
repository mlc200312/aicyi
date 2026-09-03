/**
 * 接口文档前端入口
 *
 * 适配 springdoc-openapi 2.x（springdoc-openapi-starter-webmvc-ui 2.5.0）输出的 OpenAPI 3.0.x 文档，
 * 同时兼容外部 Swagger 2.0 文档源与本地 example.json 样例。
 *
 * 与旧版（springfox / Swagger 2.0 语义）的关键差异，也是本次重写的全部动因：
 *  1. 文档端点：/v2/api-docs -> /v3/api-docs
 *  2. 分组：由 /v3/api-docs/swagger-config 的 urls[] 发现，再按 /v3/api-docs/{group} 加载，顶栏可切换
 *  3. 左侧导航不再直读 apidoc.tags（springdoc 不输出顶层 tags，旧版会得到 null 导致导航全空），
 *     改用 nswagger 从 paths 反向合成的结果
 *  4. 服务地址来自文档 servers[]，顶栏可切换；logo 不再用 schemes[0]://host 拼接
 *  5. 调试请求可携带 Bearer Token，存 localStorage，刷新与切换分组后不必重复粘贴
 *  6. 后端 GlobalExceptionHandler 会把资源解析异常、未知分组等失败包装成 HTTP 200 + 错误信封，
 *     仅凭状态码无法识别失败，故加载后必须用 nswagger.looksLikeApiDoc 校验文档特征字段
 *
 * 运行环境为 layui v2.2.2：其 element nav 事件回调参数在点击顶级项时是 li、点击子项时是 dd，
 * 本文件对两者做了归一化；layui.onevent 采用覆盖式存储，重复注册同名事件不会累积回调。
 */
layui.config({
    base: 'assets/layext/'
}).extend({
    nlaytpl: 'nlaytpl',
    ncmntool: 'ncmntool',
    nswagger: 'nswagger'
});

layui.use(['layer', 'element', 'form', 'nlaytpl', 'nswagger', 'ncmntool', 'upload'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        element = layui.element,
        form = layui.form,
        ncmntool = layui.ncmntool,
        nlaytpl = layui.nlaytpl,
        nswagger = layui.nswagger;

    /** springdoc 的文档端点；分组文档为 API_DOCS_PATH + '/' + group，分组清单在其下的 swagger-config */
    var API_DOCS_PATH = '/v3/api-docs';
    var SWAGGER_CONFIG_PATH = '/swagger-config';

    /** 运行期状态 */
    var g_ctx = {
        docUrl: '',     // 当前已加载文档的完整地址
        siteBase: '',   // 当前文档所在站点根地址（含 contextPath）
        groups: [],     // swagger-config 发现的分组，元素为 {name, url}
        group: '',      // 当前分组名，空表示未分组
        lastNav: null   // 当前选中的导航元素，切换 server 后据此重渲染当前视图
    };

    /* ==================== URL 工具 ==================== */

    function originOf(url) {
        var m = /^(https?:\/\/[^\/?#]+)/i.exec(url);
        return m ? m[1] : location.origin;
    }

    /**
     * 本 UI 部署在 {contextPath}/apidoc/ 下，据此反推应用根地址，
     * 使默认文档地址在配置了 server.servlet.context-path 时依然正确
     */
    function siteBase() {
        var pathname = location.pathname;
        var i = pathname.indexOf('/apidoc/');
        var ctx = i > 0 ? pathname.substring(0, i) : '';
        return location.origin + ctx;
    }

    /** 以 refUrl 为基准解析相对地址；swagger-config 返回的 urls[].url 通常是相对路径 */
    function resolveUrl(u, refUrl) {
        if (!u) {
            return u;
        }
        if (/^https?:\/\//i.test(u)) {
            return u;
        }
        var origin = originOf(refUrl || location.href);
        if (u.charAt(0) === '/') {
            return origin + u;
        }
        var m = /^https?:\/\/[^\/?#]+(\/.*)?$/i.exec(refUrl || '');
        var dir = ((m && m[1]) || '/').replace(/[^\/]*$/, '');
        return origin + dir + u;
    }

    /**
     * 拆分文档地址为 {isV3, siteBase, base, group}。
     * base 为分组根（.../v3/api-docs），group 为分组名（可空）。
     * contextPath 用惰性分组匹配，使 /ctx/v3/api-docs/all 也能正确拆出 ctx 与 all
     */
    function splitDocUrl(url) {
        var m = /^(https?:\/\/[^\/?#]*)((?:\/[^\/?#]*)*?)\/v3\/api-docs(?:\/([^\/?#]+))?/i.exec(url);
        if (!m) {
            return {isV3: false, siteBase: originOf(url), base: url, group: ''};
        }
        var ctx = m[2] || '';
        return {
            isV3: true,
            siteBase: m[1] + ctx,
            base: m[1] + ctx + API_DOCS_PATH,
            group: m[3] || ''
        };
    }

    /**
     * 归一化用户输入。
     * 保留旧版的便捷用法：只填 host 时自动补 http:// 与文档端点
     */
    function normalizeInput(raw) {
        var u = String(raw || '').trim();
        if (u.charAt(0) === '#') {
            u = u.substring(1);
        }
        if (u === '') {
            // IDEA 内置预览（_ijt=）下页面由 file 协议或预览端口提供，取不到后端，直接看本地样例
            if (location.search && location.search.indexOf('_ijt=') !== -1) {
                return 'example.json';
            }
            return siteBase() + API_DOCS_PATH;
        }
        if (u === 'example.json') {
            return u;
        }
        if (!/^https?:\/\//i.test(u)) {
            u = (u.charAt(0) === '/') ? location.origin + u : 'http://' + u;
        }
        // 只给了站点地址时补全 springdoc 默认文档端点；已指向具体文档（.json）则原样使用
        if (!/\/v3\/api-docs(\/|$|\?|#)/i.test(u) && !/\.json($|\?|#)/i.test(u)) {
            u = u.replace(/\/+$/, '') + API_DOCS_PATH;
        }
        return u;
    }

    /** 仅用于 document.title 这类纯文本场景：解析层输出的 info 字段是 HTML 转义后的字符串 */
    function textOf(html) {
        var holder = document.createElement('div');
        holder.innerHTML = String(html === null || html === undefined ? '' : html);
        return holder.textContent || holder.innerText || '';
    }

    /* ==================== 加载流程 ==================== */

    /**
     * 读取 springdoc 的分组清单。
     * 无论成功失败都必须回调，否则加载流程会卡死；
     * 同样要防 HTTP 200 + 错误信封，故必须校验 urls 字段本身
     */
    function fetchGroups(baseUrl, callback) {
        $.ajax({
            url: baseUrl + SWAGGER_CONFIG_PATH,
            dataType: 'json',
            type: 'get',
            success: function (cfg) {
                var groups = [];
                if (cfg && Array.isArray(cfg.urls)) {
                    $.each(cfg.urls, function (i, u) {
                        if (u && u.url) {
                            groups.push({
                                name: String(u.name || u.url),
                                url: resolveUrl(u.url, baseUrl)
                            });
                        }
                    });
                }
                callback(groups);
            },
            error: function () {
                // 分组端点不可用（未开启 groups.enabled 或被拦截）时退回不分组加载
                callback([]);
            }
        });
    }

    /** 按 URL 中的分组名选中分组；找不到或未指定时用第一个 */
    function pickGroup(groups, wanted) {
        if (!groups.length) {
            return null;
        }
        if (wanted) {
            for (var i = 0; i < groups.length; i++) {
                if (groups[i].name === wanted) {
                    return groups[i];
                }
            }
        }
        return groups[0];
    }

    function loadDoc() {
        var url = normalizeInput($("#iptApiUrl").val() || location.hash);
        $("#iptApiUrl").val(url);

        // 切换文档时先清空旧内容，避免加载失败后仍展示上一份文档
        $(".api-main").empty();
        var loader = layer.load();
        var parts = splitDocUrl(url);

        if (!parts.isV3) {
            // example.json 或外部完整文档地址：没有分组概念，直接加载
            g_ctx.groups = [];
            g_ctx.group = '';
            renderGroupSelect([], '');
            var sb = /^https?:\/\//i.test(url) ? originOf(url) : siteBase();
            fetchDoc(url, sb, loader);
            return;
        }
        fetchGroups(parts.base, function (groups) {
            g_ctx.groups = groups;
            var group = pickGroup(groups, parts.group);
            g_ctx.group = group ? group.name : '';
            renderGroupSelect(groups, g_ctx.group);
            fetchDoc(group ? group.url : parts.base, parts.siteBase, loader);
        });
    }

    function fetchDoc(url, docSiteBase, loader) {
        $.ajax({
            url: url,
            dataType: 'json',
            type: 'get',
            success: function (apidoc) {
                if (!nswagger.looksLikeApiDoc(apidoc)) {
                    layer.msg('返回内容不是有效的 OpenAPI 文档：' + describeBadDoc(apidoc), {icon: 5, time: 8000});
                    return;
                }
                try {
                    nswagger.resolve(apidoc, {docOrigin: docSiteBase});
                } catch (e) {
                    layer.msg('解析失败，请确认文档配置是否正确', {icon: 5});
                    console.error(e);
                    return;
                }
                g_ctx.docUrl = url;
                g_ctx.siteBase = docSiteBase;
                g_ctx.lastNav = null;
                afterResolve(apidoc);
            },
            error: function (xhr) {
                layer.msg('加载失败（HTTP ' + ((xhr && xhr.status) || '未知') + '），请确认API文档的地址是否正确',
                    {icon: 5, time: 6000});
            },
            complete: function () {
                layer.close(loader);
            }
        });
    }

    /**
     * 把「HTTP 200 但内容不是文档」的原因回显出来。
     * 最常见的是后端错误信封（文档端点被鉴权拦截、分组名不存在），
     * 此时只提示「加载失败」会让人误以为地址写错
     */
    function describeBadDoc(res) {
        if (res === null || res === undefined || res === '') {
            return '空响应';
        }
        if (typeof res !== 'object') {
            return String(res).slice(0, 120);
        }
        if (res.code !== undefined || res.message !== undefined) {
            return '后端返回错误信封 code=' + res.code + ' message=' + res.message
                + '（文档端点可能被鉴权拦截，或分组名不存在）';
        }
        if (!res.paths) {
            return '缺少 paths 字段';
        }
        return '缺少 openapi / swagger 版本字段';
    }

    function afterResolve(apidoc) {
        var doc = nswagger.doc();
        document.title = textOf(apidoc.info && apidoc.info.title) || '接口文档';

        // 目标应用可能自带 logo，取不到时 checkimg 不回调，保留本地默认图
        ncmntool.checkimg(nswagger.originUrl('/apidoc/assets/img/logo.png'), function (imgurl) {
            $(".logo img").attr("src", imgurl);
        });

        rememberUrl(g_ctx.docUrl);
        renderServerSelect();
        renderNav(doc);
        renderQuick(doc);
    }

    /** 用 replaceState 而非直接赋值 location.hash，避免每次加载都往浏览器历史塞一条记录 */
    function rememberUrl(url) {
        try {
            if (window.history && history.replaceState) {
                history.replaceState(null, '', '#' + url);
                return;
            }
        } catch (e) {
            // 某些环境下 replaceState 不可用，退回 hash 赋值
        }
        location.hash = url;
    }

    /* ==================== 顶栏渲染 ==================== */

    /** 渲染分组下拉；没有分组（外部文档 / example.json）时隐藏 */
    function renderGroupSelect(groups, activeName) {
        var $bar = $("#barGroup"), $sel = $("#selGroup");
        if (!groups || !groups.length) {
            $sel.empty();
            $bar.hide();
            form.render('select');
            return;
        }
        var html = '';
        $.each(groups, function (i, g) {
            html += '<option value="' + nswagger.esc(g.url) + '"'
                + (g.name === activeName ? ' selected' : '') + '>'
                + nswagger.esc(g.name) + '</option>';
        });
        $sel.html(html);
        $bar.show();
        form.render('select');
    }

    /** 渲染 server 下拉；即使只有一个也显示，便于调试时确认请求打到哪个地址 */
    function renderServerSelect() {
        var servers = nswagger.servers();
        var $bar = $("#barServer"), $sel = $("#selServer");
        if (!servers || !servers.length) {
            $sel.empty();
            $bar.hide();
            form.render('select');
            return;
        }
        var active = nswagger.activeServer();
        var html = '';
        $.each(servers, function (i, s) {
            var label = s.abs + (s.description ? '（' + s.description + '）' : '');
            html += '<option value="' + nswagger.esc(s.abs) + '"'
                + (s.abs === active ? ' selected' : '') + '>'
                + nswagger.esc(label) + '</option>';
        });
        $sel.html(html);
        $bar.show();
        form.render('select');
    }

    /* ==================== 导航与内容渲染 ==================== */

    /**
     * layui 2.2.2 的 nav 事件：点击顶级项回调参数是 li，点击子项回调参数是 dd，
     * 两者都要取出内部的 a 才能读到 dpath / dhttpmethod，故在此归一
     */
    function navAnchor(ele) {
        return $(ele).children('a').first();
    }

    function navIsHome(ele) {
        var $ele = $(ele);
        return $ele.hasClass('nav-home') || $ele.closest('.nav-home').length > 0;
    }

    function renderNav(doc) {
        nlaytpl.render(".api-main")("comp/tplApiMain.html", {tags: doc.tags}, function () {
            element.init();
            bindNavTips();
            // 渲染主页
            $(".nav-home a").click();
        });
    }

    function renderHome(doc) {
        nlaytpl.render(".main-body")("comp/tplHomeBody.html", {
            info: doc.info,
            // OpenAPI 3 用 openapi 字段，Swagger 2 用 swagger 字段
            specVersion: nswagger.esc(doc.openapi || doc.swagger || ''),
            servers: nswagger.servers(),
            activeServer: nswagger.activeServer(),
            // 用已转义的展示列表，不用原始 securitySchemes：后者要留给鉴权判定做小写比较
            securitySchemes: nswagger.securitySchemeList(),
            // docUrl / group 来自 swagger-config 与用户输入，属外部数据，展示前统一转义
            docUrl: nswagger.esc(g_ctx.docUrl),
            group: nswagger.esc(g_ctx.group),
            tags: doc.tags
        }, function () {
            element.init();
        });
    }

    function renderApiBody(ele) {
        var doc = nswagger.doc();
        if (!doc) {
            return;
        }
        var $a = navAnchor(ele);
        var dpath = $a.attr("dpath"), dhttpmethod = $a.attr("dhttpmethod");
        var pathItem = doc.paths[dpath];
        var mmeta = pathItem ? pathItem[dhttpmethod] : null;
        if (!mmeta) {
            layer.msg('未找到接口定义：' + (dhttpmethod || '') + ' ' + (dpath || ''), {icon: 5});
            return;
        }
        nlaytpl.render(".main-body")("comp/tplApiBody.html", {
            apidoc: doc,
            tagname: $a.attr("dtag"),
            dpath: dpath,
            dhttpmethod: dhttpmethod,
            mmeta: mmeta
        }, function () {
            element.init();
            form.render();
        });
    }

    function renderQuick(doc) {
        nlaytpl.render(".api-quick")("comp/tplApiQuick.html", {tags: doc.tags}, function () {
            form.render("select");
        });
    }

    /** 切换 server 后请求地址随之变化，而地址是在模板渲染时算出的，故需重渲染当前视图 */
    function rerenderCurrent() {
        if (g_ctx.lastNav && document.body.contains(g_ctx.lastNav)) {
            $(g_ctx.lastNav).click();
            return;
        }
        $(".nav-home a").click();
    }

    /* ==================== 事件绑定（只注册一次，layui.onevent 为覆盖式存储） ==================== */

    function bindNavTips() {
        // 导航内容每次渲染都会重建，故用事件委托而非直接绑定，避免重复绑定与失效
        $(".api-main")
            .off('mouseover.apidoc mouseout.apidoc')
            .on('mouseover.apidoc', '.layui-nav-item a[dtitle]', function () {
                layer.tips($(this).attr("dtitle"), this, {time: 0});
            })
            .on('mouseout.apidoc', '.layui-nav-item a[dtitle]', function () {
                layer.closeAll('tips');
            });
    }

    function bindEvents() {
        $(".logo").click(function () {
            $(".nav-home a").click();
            $(".layui-side-scroll").scrollTop(0);
        });

        element.on('nav(left-nav)', function (ele) {
            g_ctx.lastNav = ele;
            if (navIsHome(ele)) {
                renderHome(nswagger.doc());
                return;
            }
            $(".layui-nav-itemed").removeClass("layui-nav-itemed");
            $(ele).closest(".layui-nav-item").addClass("layui-nav-itemed");
            var $a = navAnchor(ele);
            $(".layui-side-scroll").scrollTop(
                $a.offset().top - $(".layui-side").offset().top + $(".layui-side").scrollTop());
            renderApiBody(ele);
        });

        form.on('select(api-quick)', function (data) {
            if (!data.value) {
                return;
            }
            var pm = data.value.split("::");
            $(".left-nav a[dpath='" + pm[1] + "'][dhttpmethod='" + pm[0] + "']").click();
        });

        form.on('select(doc-group)', function (data) {
            if (!data.value || data.value === g_ctx.docUrl) {
                return;
            }
            var parts = splitDocUrl(data.value);
            var group = null;
            $.each(g_ctx.groups, function (i, g) {
                if (g.url === data.value) {
                    group = g;
                    return false;
                }
            });
            g_ctx.group = group ? group.name : '';
            fetchDoc(data.value, parts.isV3 ? parts.siteBase : g_ctx.siteBase, layer.load());
        });

        form.on('select(doc-server)', function (data) {
            nswagger.setServer(data.value);
            rerenderCurrent();
        });

        $("#iptApiUrl, .btn-clearurl").on('mouseover', function () {
            $(".btn-clearurl").show();
        }).on('mouseout', function () {
            $(".btn-clearurl").hide();
        });
        $(".btn-clearurl").click(function () {
            $("#iptApiUrl").val('');
        });

        $(".btn-gourl").click(loadDoc)
            .on('mouseover', function () {
                layer.tips("点击加载目标地址的API文档", this, {time: 0, tips: 3});
            })
            .on('mouseout', function () {
                layer.closeAll('tips');
            });

        // Token：保存后所有需要鉴权的接口调试都会自动携带
        var $token = $("#iptToken");
        $token.val(nswagger.token());
        $token.on('keydown', function (e) {
            if (e.keyCode === 13) {
                saveToken();
            }
        });
        $(".btn-savetoken").click(saveToken)
            .on('mouseover', function () {
                layer.tips("保存后，需要鉴权的接口调试会自动携带 Bearer Token；清空并保存即移除",
                    this, {time: 0, tips: 3});
            })
            .on('mouseout', function () {
                layer.closeAll('tips');
            });
    }

    function saveToken() {
        var token = nswagger.saveToken($("#iptToken").val());
        $("#iptToken").val(token);
        layer.msg(token ? 'Token 已保存，调试请求将自动携带' : 'Token 已清除',
            {icon: token ? 6 : 5, time: 1800});
    }

    bindEvents();
    loadDoc();
});

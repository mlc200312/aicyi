# aicyi

> 爱创意、爱科技、爱生活

aicyi 是一套基于 Spring Boot 2.7 的 Java 基础设施框架/SDK，提供开箱即用的通用中间件与公共组件，帮助开发者快速构建微服务应用。

## 特性

- **统一响应模型**：`Result<D>` 统一封装 API 返回结果（`code`/`message`/`data`/`traceId`），`@EnableMidwareWeb` 一键启用全局异常处理、鉴权拦截、请求日志与链路追踪
- **链路追踪**：`TraceIdFilter` 为每个请求生成/透传 `traceId`（`X-Trace-Id` 请求/响应头 + MDC），异常响应自动回填 `Result.traceId`
- **分布式 ID 生成**：基于 Redis 协调的 Snowflake 算法，通过 Redis 自动注册 WorkerId（租约 + 心跳续约 + 失效自动恢复），固定 5 位 workerId
- **分布式锁**：基于 Redisson 的分布式锁，支持阻塞/非阻塞/带租约等多种模式，内置模板方法（`execute`/`tryExecute`）
- **统一消息系统**：邮件（JavaMail + FreeMarker/Thymeleaf 模板）、短信（Twilio/云片/邮件网关默认实现）、MQ（Spring Cloud Stream RabbitMQ）统一抽象，`MailMessage`/`SmsMessage`/`MqMessage` + `UnifiedMessageManager`
- **统一缓存抽象**：`Cache<K,V>` 接口 + `RedisCache` 实现（TTL 抖动、防击穿锁、批量读写），配合 Redisson / Caffeine 本地缓存
- **JWT 认证**：Bearer Token 认证 + 刷新令牌，支持多设备登录，`@IgnoreAuth` 注解跳过认证，`AuthenticationTokens` 静态工具免注入调用
- **数据访问**：MyBatis / MyBatis-Plus 基础能力（分页上限 500、乐观锁、字段自动填充）、`PageUtils` 分页工具、枚举 TypeHandler
- **Spring Boot Starter 自动装配**：按需引入（`AutoConfiguration.imports` + `@ConditionalOnClass`/`@ConditionalOnProperty`），可插拔
- **通用工具集**：JSON（Jackson）、对象映射（MapStruct）、Excel（EasyExcel/Apache POI）、二维码（zxing）、加解密（AES/RSA/MD5）等

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 8 |
| 框架 | Spring Boot | 2.7.18 |
| 微服务 | Spring Cloud | 2021.0.8 |
| 配置中心 | Spring Cloud Alibaba (Nacos) | 2021.0.5.0 |
| ORM | MyBatis / MyBatis-Plus | 2.3.1 / 3.5.3.1 |
| 数据库 | MySQL | 8.0.x |
| 缓存 | Redis (Redisson 3.9.1 / Caffeine 2.9.3) | |
| 消息队列 | RabbitMQ (Spring Cloud Stream) | |
| 认证 | JWT (jjwt) | 0.13.0 |
| 定时任务 | XXL-Job | 2.5.0 |

## 模块结构

```
aicyi
├── aicyi-base-dependencies          # BOM：统一版本管理
├── aicyi-base-starter-parent        # Parent POM：插件 + 公共依赖管理
├── aicyi-commons                    # 公共基础库
│   ├── aicyi-commons-core           # 核心接口：ID、缓存、锁、消息、Token、模板引擎
│   ├── aicyi-commons-lang           # 语言基础：异常、枚举、返回码、Result、分页模型
│   ├── aicyi-commons-logging        # 日志门面
│   ├── aicyi-commons-security       # 安全：加解密、JWT
│   └── aicyi-commons-util           # 工具：JSON、Snowflake、Excel、二维码、对象映射
├── aicyi-midware                    # 中间件实现
│   ├── aicyi-midware-db             # 数据库
│   │   ├── aicyi-midware-db-commons       # MyBatis 日志、TypeHandler、PageUtils 分页
│   │   └── aicyi-midware-db-mybatisplus   # MyBatis-Plus：分页/乐观锁/字段自动填充
│   ├── aicyi-midware-kit            # 通用工具：IdUtils、SpringEnvironmentHelper（静态入口）
│   ├── aicyi-midware-message        # 消息
│   │   ├── aicyi-midware-message-core         # 统一消息抽象（UnifiedMessageManager/模板）
│   │   ├── aicyi-midware-message-mail         # 邮件实现（MailMessage）
│   │   ├── aicyi-midware-message-sms          # 短信实现（SmsMessage：twilio/yunPian/default）
│   │   ├── aicyi-midware-message-mq           # MQ 适配层（MqMessage）
│   │   ├── aicyi-midware-message-db           # 消息模板 DB 持久化（可选引入）
│   │   └── aicyi-midware-message-spring-boot-starter  # 消息自动装配入口
│   ├── aicyi-midware-rabbitmq       # RabbitMQ 集成（StreamMqSender）
│   ├── aicyi-midware-redis          # Redis：增强模板工厂、Cache、分布式锁、Snowflake、Token
│   ├── aicyi-midware-web            # Web：统一响应、认证拦截器、请求日志、异常处理、链路追踪
│   └── aicyi-midware-spring-boot-starter  # 自动装配入口（Redis/Snowflake/MyBatis-Plus）
└── docs                             # 使用文档
```

## 架构设计

```
┌─────────────────────────────────────────────┐
│                  业务应用                     │
│  controller → service → dao → database      │
└──────────────────────┬──────────────────────┘
                       │ 依赖
┌──────────────────────▼──────────────────────┐
│               aicyi-midware                  │
│  redis / rabbitmq / message / web / db / kit │
│  → 具体实现（Redis、Redisson、Spring Cloud） │
└──────────────────────┬──────────────────────┘
                       │ 依赖
┌──────────────────────▼──────────────────────┐
│               aicyi-commons                  │
│  core / lang / logging / security / util     │
│  → 纯接口定义（不依赖 Spring）               │
└─────────────────────────────────────────────┘
```

- **commons 层**：定义纯 Java 接口与语言基础（`Result`、异常、`IdGenerator`、`DistributedLock`、`MessageSender`、`Cache`、`Token` 等），不依赖 Spring，保证可移植性
- **midware 层**：基于 Spring Boot 生态提供具体实现，通过 `AutoConfiguration.imports` 自动装配，`aicyi-midware-spring-boot-starter` 提供 Redis/Snowflake/MyBatis-Plus 自动配置，Web 能力由 `@EnableMidwareWeb` 显式启用
- **业务应用**：通过引入需要的模块 + 对应开关（`aicyi.*.enabled`）按需启用能力

## 配置开关（缺省语义）

自动装配统一走 `@ConditionalOnProperty`，各能力缺省语义如下（与代码保持一致，新增开关需同步更新本表）：

| 配置项 | 缺省 | 说明 |
|--------|------|------|
| `aicyi.web.trace-id.enabled` | **开** | TraceIdFilter：traceId 写入 MDC 并回写 `X-Trace-Id` 响应头 |
| `aicyi.web.body-cache.enabled` | **开** | 请求体缓存过滤器（`aicyi.web.body-cache.max-size` 可调上限，字节，默认 256KB） |
| `aicyi.web.request-log.enabled` | **开** | 请求日志拦截器，另需 `@EnableMidwareWeb(enableRequestLog = true)`（缺省开启） |
| `aicyi.redis.enabled` | **开** | Redis 增强模板工厂/锁能力，需显式开启；锁管理器还需容器存在 `RedissonClient` Bean |
| `aicyi.snowflake.enabled` | **关** | 分布式 Snowflake ID，需显式开启 |
| `aicyi.mybatis-plus.enabled` | **开** | MyBatis-Plus 增强（分页/乐观锁/自动填充），引入模块即生效 |
| `aicyi.message.email.enabled` | **开** | 邮件渠道 |
| `aicyi.message.sms.enabled` | **开** | 短信渠道（`aicyi.message.sms.provider`：default/twilio/yunPian） |
| `aicyi.message.mq.enabled` | **开** | MQ 渠道（`aicyi.message.mq.provider`：rabbitMq） |
| `aicyi.message.template.enabled` | **开** | 消息模板 DB 持久化（另需容器中已有 `EnhancedRedisTemplateFactory` 与 `RedissonClient` Bean，缺失时静默跳过） |

> 口径约定：Web 请求链路上的能力（日志/链路/请求体缓存）缺省开，降低接入成本；
> 依赖外部基础设施的能力（Redis/雪花 ID/消息渠道/模板库）缺省关，避免未配置基础设施时启动失败。

日志配置：基础包提供 opt-in 的 logback 片段 `aicyi/logback-aicyi-defaults.xml`，
应用在自己的 `logback-spring.xml` 中 `<include resource="aicyi/logback-aicyi-defaults.xml"/>` 即可复用
（`aicyi.logging.path`、`aicyi.logging.max-history`、`aicyi.logging.max-file-size` 等配置项可覆盖）；
基础包不再随 jar 分发根级 `logback-spring.xml`，不会静默接管应用日志。

## 快速开始

```bash
# 克隆项目
git clone <repo-url>
cd aicyi

# 构建并安装到本地仓库（供 aicyi-example 等下游项目使用）
mvn clean install -DskipTests
```

> 注：当前仓库仅包含基础包（BOM/commons/midware）与文档，`aicyi-example` 示例应用在独立仓库中维护；
> 集成方式见下文及 [快速开始指南](https://github.com/mlc200312/aicyi-example/blob/main/docs/quickstart.md)。

详细说明请参考 [快速开始指南](https://github.com/mlc200312/aicyi-example/blob/main/docs/quickstart.md)，高级功能请参考 [高级功能文档](./advanced.md)。

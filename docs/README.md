# aicyi

> 爱创意、爱科技、爱生活

aicyi 是一套基于 Spring Boot 2.7 的 Java 基础设施框架/SDK，提供开箱即用的通用中间件与公共组件，帮助开发者快速构建微服务应用。

## 特性

- **统一响应模型**：`Response<T>` 统一封装 API 返回结果，`@EnableRestApi` 一键启用全局异常处理
- **分布式 ID 生成**：基于 Redis 协调的 Snowflake 算法，支持自动注册 WorkerId 与故障恢复
- **分布式锁**：基于 Redisson 的分布式锁，支持阻塞/非阻塞/带租约等多种模式，内置模板方法
- **统一消息系统**：邮件（JavaMail + FreeMarker/Thymeleaf 模板）、短信（Twilio/云片）、MQ（RabbitMQ）统一抽象
- **JWT 认证**：Bearer Token 认证 + 刷新令牌，支持多设备登录，`@IgnoreAuth` 注解跳过认证
- **多级缓存**：支持 Redis（Jedis/Lettuce）、JetCache、Guava、Ehcache、Caffeine
- **Spring Boot Starter 自动装配**：零配置接入，自动注册所需 Bean
- **通用工具集**：JSON（Jackson/FastJSON/Gson）、Excel（EasyExcel/Apache POI）、对象映射（Orika/MapStruct）、二维码生成等

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 8 |
| 框架 | Spring Boot | 2.7.18 |
| 微服务 | Spring Cloud | 2021.0.9 |
| ORM | MyBatis / MyBatis-Plus | |
| 数据库 | MySQL | 8.0.x |
| 缓存 | Redis (Jedis / Redisson / JetCache) | |
| 消息队列 | RabbitMQ (Spring Cloud Stream) | |
| 认证 | JWT (jjwt) | 0.11.5 |
| 工具 | Hutool | 5.8.36 |

## 模块结构

```
aicyi
├── aicyi-base-dependencies          # BOM：统一版本管理
├── aicyi-base-starter-parent        # Parent POM：插件 + 公共依赖管理
├── aicyi-commons                    # 公共基础库
│   ├── aicyi-commons-core           # 核心接口：ID、缓存、锁、消息、Token
│   ├── aicyi-commons-lang           # 语言基础：异常、枚举、返回码
│   ├── aicyi-commons-logging        # 日志门面
│   ├── aicyi-commons-security       # 安全：加解密、JWT
│   └── aicyi-commons-util           # 工具：JSON、Snowflake、Excel、二维码
├── aicyi-midware                    # 中间件实现
│   ├── aicyi-midware-db             # 数据库：MyBatis 日志、TypeHandler、分页
│   ├── aicyi-midware-message        # 消息：邮件/SMS/MQ 统一发送
│   ├── aicyi-midware-rabbitmq       # RabbitMQ 集成
│   ├── aicyi-midware-redis          # Redis：增强模板、分布式锁、Snowflake
│   ├── aicyi-midware-web            # Web：统一响应、认证拦截器、异常处理
│   └── aicyi-midware-spring-boot-starter  # 自动装配入口
└── aicyi-example                    # 示例应用
    ├── aicyi-example-boot           # 启动模块
    ├── aicyi-example-web            # REST 控制器
    ├── aicyi-example-service        # 业务服务
    ├── aicyi-example-dao            # 数据访问层
    ├── aicyi-example-domain         # 领域模型
    ├── aicyi-example-consumer       # MQ 消费者
    └── aicyi-example-test           # 功能测试
```

## 架构设计

```
┌─────────────────────────────────────────────┐
│                  aicyi-example               │
│  web → service → domain → dao → database    │
└──────────────────────┬──────────────────────┘
                       │ 依赖
┌──────────────────────▼──────────────────────┐
│               aicyi-midware                  │
│  redis / rabbitmq / message / web / db       │
│  → 具体实现（Redis、Redisson、Spring Cloud） │
└──────────────────────┬──────────────────────┘
                       │ 依赖
┌──────────────────────▼──────────────────────┐
│               aicyi-commons                  │
│  core / lang / logging / security / util     │
│  → 纯接口定义（不依赖 Spring）               │
└─────────────────────────────────────────────┘
```

- **commons 层**：定义纯 Java 接口，不依赖 Spring，保证可移植性
- **midware 层**：基于 Spring Boot 生态提供具体实现，通过 `spring.factories` 自动装配
- **example 层**：演示如何集成使用，采用 DDD 分层结构

目录结构的详细评估和后续迁移建议见 [项目目录结构评估](./architecture.md)。

## 快速开始

```bash
# 克隆项目
git clone <repo-url>
cd aicyi

# 构建
mvn clean install -DskipTests

# 运行示例应用
cd aicyi-example/aicyi-example-boot
mvn spring-boot:run
```

详细说明请参考 [快速开始指南](./quickstart.md)，高级功能请参考 [高级功能文档](./advanced.md)。

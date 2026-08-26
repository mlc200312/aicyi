# 快速开始指南

本文档将引导你从零开始运行 aicyi 示例应用（`aicyi-example`），并了解如何将 aicyi 框架集成到自己的项目中。

> **注意**：aicyi 框架仓库（BOM / commons / midware + 本 docs）与 `aicyi-example` 示例应用分属两个仓库。
> 本文第 1～5 节以 `aicyi-example` 仓库为示例说明运行方式，实际集成请直接参考第 6 节。

## 环境要求

| 软件 | 版本要求 | 用途 |
|------|----------|------|
| JDK | 1.8+ | 运行环境 |
| Maven | 3.6+ | 构建 |
| MySQL | 8.0+ | 业务库（t_user / t_student / message_template） |
| Redis | 5.0+ | 缓存、分布式锁、Snowflake WorkerId、Token 存储 |
| RabbitMQ | 3.8+ | 消息队列（Spring Cloud Stream） |
| Nacos Server | 2.x | 配置中心（test/prod 环境配置均存放于此） |

> 说明：示例应用通过 `spring.config.import` 从 Nacos 读取配置，未连上 Nacos 时应用无法启动；
> 若仅体验框架核心能力，可自行将 Nacos 中的配置落回 `application.yml` 并移除 Nacos 依赖。

## 1. 初始化数据库

执行 `aicyi-example` 仓库中的 SQL 脚本创建示例表：

```bash
mysql -u root -p < aicyi-example/aicyi-example-dao/db/init.sql
```

脚本会创建以下表：
- `t_user` — 用户表
- `t_student` — 学生表
- `message_template` — 消息模板表（含示例列，可插入测试模板数据）

## 2. 启动 Nacos 并导入配置

示例应用的所有环境配置已迁移到 Nacos 配置中心（`spring-cloud-alibaba 2021.0.5.0` 的
`spring.config.import` 机制，无需 `bootstrap.yml`）。

1. 启动 Nacos Server（默认 `127.0.0.1:8848`，可用环境变量 `NACOS_SERVER_ADDR` 覆盖）。
2. Nacos 控制台 → 命名空间 → 为 test 环境创建命名空间，记录命名空间 ID
   （本地已内置默认值 `29b4684f-0751-4290-a0a4-f65a51893ef6`，可用 `NACOS_NAMESPACE` 覆盖）。
3. 在目标命名空间 → 配置管理 → 配置列表中，按 Data ID 逐个创建 YAML 配置（内容见
   `aicyi-example/aicyi-example-boot/nacos/` 目录）：

| Data ID | Group | 说明 |
| --- | --- | --- |
| `aicyi-example.yml` | DEFAULT_GROUP | 共享配置（含 Spring Cloud Stream 交换机/绑定声明） |
| `aicyi-example-test.yml` | DEFAULT_GROUP | test 环境配置（aicyi.message.*、aicyi.snowflake.*） |
| `aicyi-redis.yml` | DEFAULT_GROUP | Redis 连接配置 |
| `aicyi-rabbitmq.yml` | DEFAULT_GROUP | RabbitMQ 连接与 Stream binder 配置 |
| `aicyi-datasource.yml` | DEFAULT_GROUP | 数据源配置（test 环境） |

> 敏感凭证（邮箱/短信账号等）在配置中保留 `${...}` 环境变量占位符，实际值由部署环境注入。
> Nacos 开启鉴权时，通过 `NACOS_USERNAME` / `NACOS_PASSWORD` 环境变量注入账号密码。

## 3. 构建项目

```bash
# 1) 先构建并安装 aicyi 基础包到本地仓库（BOM/commons/midware）
cd aicyi
mvn clean install -DskipTests

# 2) 再构建 aicyi-example 示例应用
cd ../aicyi-example
mvn clean install -DskipTests
```

## 4. 启动应用

```bash
cd aicyi-example/aicyi-example-boot
mvn spring-boot:run
```

应用启动后默认监听 **80 端口**（见 `application.yml` 中 `server.port: 80`）。

## 5. 验证接口

### 5.1 获取图形验证码（登录需要）

```bash
curl http://localhost/captcha/get-captcha
```

返回示例：

```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "uuid": "0196e2d6-...",
    "captcha": "http://localhost/captcha/0196e2d6-..."
  }
}
```

浏览器打开 `data.captcha` 对应的图片地址，查看图片中的验证码；登录时提交 `uuid` 与图片中的 `verCode`。
（测试环境默认跳过验证码一致性校验，`verCode` 传任意值即可。）

### 5.2 用户注册

```bash
curl -X POST http://localhost/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456",
    "mobile": "13800138000"
  }'
```

返回示例：

```json
{
  "code": 0,
  "message": "Success",
  "data": null
}
```

### 5.3 用户登录

```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456",
    "uuid": "<5.1 获取的 uuid>",
    "verCode": "<图片中的验证码>"
  }'
```

返回示例：

```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "userId": "1",
    "token": {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "refreshToken": "0196e2d6-...",
      "accessTokenExpiresIn": 86400,
      "refreshTokenExpiresIn": 604800
    }
  }
}
```

### 5.4 刷新 Token

```bash
curl -X POST http://localhost/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{ "refreshToken": "<5.3 返回的 refreshToken>" }'
```

### 5.5 访问需要认证的接口

```bash
curl http://localhost/user/get-user-info \
  -H "Authorization: Bearer <5.3 返回的 accessToken>"
```

### 5.6 查看接口文档

启动应用后访问：`http://localhost/api-doc.html`（示例应用基于 springfox 3.0 OAS3 配置，静态资源由
`WebConfiguration` 映射到 `classpath:/api-doc.html`）。

## 将 aicyi 集成到你的项目

### 步骤 1：添加 Maven 依赖

在你的 `pom.xml` 中引入 aicyi 的 Parent POM（统一版本与插件管理）：

```xml
<parent>
    <groupId>io.github.aicyi.base</groupId>
    <artifactId>aicyi-base-starter-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
```

### 步骤 2：引入所需模块

`aicyi-midware-spring-boot-starter` 仅提供 Redis/Snowflake/MyBatis-Plus 自动配置（其中 redis、db-mybatisplus
为 provided+optional，需自行引入）；`@EnableMidwareWeb` 位于 `aicyi-midware-web`，同样需显式引入：

```xml
<dependencies>
    <!-- 核心公共库 -->
    <dependency>
        <groupId>io.github.aicyi.commons</groupId>
        <artifactId>aicyi-commons-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.aicyi.commons</groupId>
        <artifactId>aicyi-commons-lang</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.aicyi.commons</groupId>
        <artifactId>aicyi-commons-util</artifactId>
    </dependency>

    <!-- Web 能力：@EnableMidwareWeb（统一响应/异常/鉴权/请求日志/链路追踪） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-web</artifactId>
    </dependency>

    <!-- 中间件自动装配（Redis/Snowflake/MyBatis-Plus） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-spring-boot-starter</artifactId>
    </dependency>

    <!-- Redis 增强模板/锁（aicyi.redis.enabled=true 时生效） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-redis</artifactId>
    </dependency>

    <!-- MyBatis-Plus 增强（aicyi.mybatis-plus.enabled 缺省开启） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-db-mybatisplus</artifactId>
    </dependency>

    <!-- 消息系统（可选：邮件/短信/MQ） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-message-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

### 步骤 3：启用框架功能

在启动类添加注解：

```java
@SpringBootApplication
@EnableMidwareWeb            // 启用统一响应、全局异常处理、鉴权、请求日志与链路追踪
@MapperScan("com.your.dao")  // 扫描 MyBatis Mapper
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

> 鉴权默认开启（`enableAuth = true`），此时容器中必须存在 `AuthenticationTokenService` Bean，否则启动即失败。
> 可使用 `aicyi-midware-redis` 提供的 `JwtRefreshAuthenticationTokenService`（示例见 [高级功能文档](./advanced.md#jwt-认证与授权)）。

### 步骤 4：配置 application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aicyi?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379

aicyi:
  redis:
    enabled: true        # 开启 Redis 增强模板
  snowflake:
    enabled: true        # 开启分布式 ID
    service-name: your-service   # 多服务共用同一 Redis 时必须各不相同
  mybatis-plus:
    enabled: true        # 缺省即开启，可省略
```

## 常见问题

### Q: 启动报 Nacos 配置导入失败？

确认 Nacos 已启动且目标命名空间下已导入全部 Data ID（见第 2 节），并检查 `NACOS_SERVER_ADDR` / `NACOS_NAMESPACE`
环境变量；导入为非 optional，Nacos 不可达或配置缺失时应用启动失败。

### Q: 启动报 Redis 连接失败？

确保 Redis 已启动，检查 Nacos 中 `aicyi-redis.yml` 的 host/port 配置；若未启用 Redis 能力，可将
`aicyi.redis.enabled` 置为 `false`。

### Q: 如何关闭 Snowflake ID 生成？

```yaml
aicyi:
  snowflake:
    enabled: false
```

### Q: 如何跳过特定接口的认证？

在 Controller 类或方法上添加 `@IgnoreAuth` 注解即可。

### Q: 如何关闭整个应用的鉴权？

```java
@EnableMidwareWeb(enableAuth = false)
```

### Q: 如何引入日志模板？

在自己的 `logback-spring.xml` 中显式引入基础包日志片段：

```xml
<include resource="aicyi/logback-aicyi-defaults.xml"/>
```

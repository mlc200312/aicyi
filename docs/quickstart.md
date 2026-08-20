# 快速开始指南

本文档将引导你从零开始运行 aicyi 示例应用，并了解如何将 aicyi 框架集成到自己的项目中。

> **注意**：当前仓库仅包含基础包（BOM / commons / midware）与文档，`aicyi-example` 示例应用
> 未包含在本仓库中。第 1～5 节中的 `aicyi-example/*` 路径为演示说明，实际集成请直接参考第 6 节。

## 环境要求

| 软件 | 版本要求 |
|------|----------|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 5.0+ |
| RabbitMQ | 3.8+ |

## 1. 初始化数据库

执行 SQL 脚本创建示例表：

```bash
mysql -u root -p < aicyi-example/aicyi-example-dao/db/init.sql
```

脚本会创建以下表：
- `t_user` — 用户表
- `t_student` — 学生表
- `message_template` — 消息模板表

## 2. 配置环境

编辑 `aicyi-example/aicyi-example-boot/src/main/resources/application-test.yml`，修改数据库、Redis、RabbitMQ 等连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aicyi?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379
    password:

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 3. 构建项目

```bash
# 在项目根目录执行
mvn clean install -DskipTests
```

## 4. 启动应用

```bash
cd aicyi-example/aicyi-example-boot
mvn spring-boot:run
```

应用启动后默认监听 **80 端口**。

## 5. 验证接口

### 5.1 用户注册

```bash
curl -X POST http://localhost/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456",
    "mobile": "13800138000",
    "email": "test@example.com"
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

### 5.2 用户登录

```bash
curl -X POST http://localhost/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456"
  }'
```

返回示例：

```json
{
  "code": 0,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 7200
  }
}
```

### 5.3 访问需要认证的接口

```bash
curl http://localhost/user/info \
  -H "Authorization: Bearer <your_access_token>"
```

### 5.4 查看 Swagger 文档

启动应用后访问：`http://localhost/swagger-ui/index.html`

## 将 aicyi 集成到你的项目

### 步骤 1：添加 Maven 依赖

在你的 `pom.xml` 中引入 aicyi 的 Parent POM：

```xml
<parent>
    <groupId>io.github.aicyi.base</groupId>
    <artifactId>aicyi-base-starter-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
```

### 步骤 2：引入所需模块

```xml
<dependencies>
    <!-- 核心公共库 -->
    <dependency>
        <groupId>io.github.aicyi.commons</groupId>
        <artifactId>aicyi-commons-core</artifactId>
    </dependency>

    <!-- 中间件自动装配（包含 Redis、Web、ID 生成等） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

    <!-- 消息系统（可选） -->
    <dependency>
        <groupId>io.github.aicyi.midware</groupId>
        <artifactId>aicyi-midware-message-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 步骤 3：启用框架功能

在启动类添加注解：

```java
@SpringBootApplication
@EnableMidwareWeb            // 启用统一响应、全局异常处理、鉴权与请求日志
@MapperScan("com.your.dao")  // 扫描 MyBatis Mapper
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 步骤 4：配置 application.yml

```yaml
server:
  port: 8080

aicyi:
  snowflake:
    enabled: true
    service-name: your-service
```

## 常见问题

### Q: 启动报 Redis 连接失败？

确保 Redis 已启动，并检查 `application-test.yml` 中的 Redis 配置是否正确。

### Q: 如何关闭 Snowflake ID 生成？

```yaml
aicyi:
  snowflake:
    enabled: false
```

### Q: 如何跳过特定接口的认证？

在 Controller 类或方法上添加 `@IgnoreAuth` 注解即可。
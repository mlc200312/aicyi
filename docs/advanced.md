# 高级功能文档

本文档详细介绍 aicyi 框架各核心功能的使用方式。

## 目录

- [统一响应与异常处理](#统一响应与异常处理)
- [JWT 认证与授权](#jwt-认证与授权)
- [分布式 ID 生成](#分布式-id-生成)
- [分布式锁](#分布式锁)
- [统一消息系统](#统一消息系统)
- [缓存管理](#缓存管理)
- [数据访问](#数据访问)
- [MQ 消息队列](#mq-消息队列)

---

## 统一响应与异常处理

### 启用方式

在启动类添加 `@EnableMidwareWeb` 注解，框架会注册全局异常处理器，所有 API 返回值统一为 `Result<D>` 格式。

```java
@SpringBootApplication
@EnableMidwareWeb
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### Result 结构

`code` 为 Integer 类型，成功固定为 `0`；错误码为 5 位整数，前 3 位对齐 HTTP 状态码段（如 40001 → 400 段）。

```json
{
  "code": 0,
  "message": "Success",
  "data": { ... }
}
```

### 返回成功

```java
@GetMapping("/user/{id}")
public Result<UserVO> getUser(@PathVariable Long id) {
    UserVO user = userService.findById(id);
    return Result.success(user);
}
```

### 返回失败

```java
// 抛出业务异常
throw new BusinessException(CommonResultCode.PARAM_ERROR, "用户名不能为空");

// 或直接构造
return Result.failure(CommonResultCode.PARAM_ERROR);
```

### 预定义返回码

| 返回码 | 说明 |
|--------|------|
| `SUCCESS` | 0 — 操作成功 |
| `PARAM_ERROR` | 40001 — 参数错误 |
| `BUSINESS_ERROR` | 40002 — 业务错误（默认业务错误码） |
| `UNAUTHORIZED` | 40101 — 未授权 |
| `TOKEN_EXPIRED` | 40102 — Token 过期 |
| `FORBIDDEN` | 40300 — 禁止访问 |
| `NOT_FOUND` | 40401 — 资源不存在 |
| `SYSTEM_ERROR` | 50001 — 系统错误 |

### 支持的异常类型

`GlobalExceptionHandler` 自动处理以下异常：

| 异常类型 | 处理方式 |
|----------|----------|
| `IllegalArgumentException` | HTTP 400，返回 40001 与异常消息 |
| `HttpMessageNotReadableException` | HTTP 400，请求体解析失败 |
| `HttpRequestMethodNotSupportedException` | HTTP 405，请求方法不支持 |
| `HttpMediaTypeNotSupportedException` | HTTP 415，媒体类型不支持 |
| `BindException` | HTTP 400，`@Valid` 校验失败，返回详细字段错误 |
| `ConstraintViolationException` | HTTP 400，Bean Validation 校验失败 |
| `MissingServletRequestParameterException` | HTTP 400，缺少必填参数 |
| `BaseException`（含 `BusinessException`、`UnauthorizedException` 等） | HTTP 200 + 业务错误码 |
| `Exception` | 兜底处理，HTTP 200 + 50001，堆栈不外泄 |

---

## JWT 认证与授权

### 配置

`@EnableMidwareWeb` 默认开启鉴权（`enableAuth = true`），框架自动注册鉴权拦截器，无需手工注册；
容器中不存在 `AuthenticationTokenService` Bean 时启动即失败（fail-fast），因此业务方必须先定义 Token 服务 Bean。

可直接使用 aicyi-midware-redis 提供的基于 Redis 刷新 Token 的实现：

```java
@Configuration
public class WebConfiguration {

    @Bean
    public AuthenticationTokenService<IJWTInfo> authenticationTokenService(StringRedisTemplate redisTemplate) {
        AuthenticationConfig config = new AuthenticationConfig();
        // 配置密钥、签发者、access/refresh token 有效期、多设备登录数等
        return new JwtRefreshAuthenticationTokenService<>(config, redisTemplate, JWTInfo.class);
    }
}
```

排除路径（如静态资源、接口文档）通过注解属性声明，同时排除鉴权与请求日志：

```java
@EnableMidwareWeb(excludePathPatterns = {"/webjars/**", "/v2/api-docs"})
```

### 跳过认证

在 Controller 类或方法上使用 `@IgnoreAuth` 注解：

```java
@RestController
@IgnoreAuth  // 整个 Controller 跳过认证
public class PublicController {

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
```

```java
@RestController
public class UserController {

    @IgnoreAuth  // 仅该方法跳过认证
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        // ...
    }
}
```

### 获取当前用户信息

```java
// 在认证通过后，可以通过 CurrentContextHolder 获取当前用户
String userId = CurrentContextHolder.getUserId();
String username = CurrentContextHolder.getUsername();
```

### 客户端请求格式

```
Authorization: Bearer <access_token>
```

---

## 分布式 ID 生成

### 原理

基于 Redis 协调的 Snowflake 算法，各服务实例通过 Redis 自动注册 WorkerId，支持：
- 自动 WorkerId 分配与回收
- 心跳保活
- 宕机自动恢复
- 时钟回拨容忍

### 配置

```yaml
aicyi:
  snowflake:
    enabled: true
    service-name: your-service-name    # 服务名称（必填）
    worker-id-bits: 5                  # WorkerId 位数，默认 5
    datacenter-id: 0                   # 数据中心 ID
    ttl-seconds: 60                    # WorkerId 租约时间（秒）
    heartbeat-seconds: 20              # 心跳间隔（秒）
    auto-recover: true                 # 是否自动恢复
    clock-backward-tolerance-ms: 5     # 时钟回拨容忍（毫秒）
    epoch: 1672531200000               # 起始时间戳（毫秒）
```

### 使用方式

```java
@Autowired
private IdGenerator idGenerator;

public void createOrder() {
    long orderId = idGenerator.nextId();
    // ...
}
```

---

## 分布式锁

### 接口定义

`DistributedLock` 接口提供了完整的分布式锁语义：

```java
public interface DistributedLock {
    void lock() throws InterruptedException;                    // 阻塞获取
    void lock(Duration leaseTime) throws InterruptedException;  // 带租约
    boolean tryLock();                                          // 立即尝试
    boolean tryLock(Duration waitTime) throws InterruptedException;
    boolean tryLock(Duration waitTime, Duration leaseTime) throws InterruptedException;
    void unlock();
    boolean isHeldByCurrentThread();
    boolean isLocked();
    boolean forceUnlock();                                      // 强制释放
}
```

### 使用方式

#### 手动加锁/解锁

```java
@Autowired
private DistributedLockManager lockManager;

public void processOrder(Long orderId) throws InterruptedException {
    DistributedLock lock = lockManager.getLock("order:" + orderId);
    lock.lock();
    try {
        // 业务逻辑
    } finally {
        lock.unlock();
    }
}
```

#### 使用模板方法（推荐）

```java
// 执行无返回值任务
lock.execute(() -> {
    // 自动加锁/解锁
    doSomething();
});

// 执行有返回值任务
String result = lock.execute(() -> {
    return computeResult();
});

// 尝试执行（带超时）
boolean success = lock.tryExecute(Duration.ofSeconds(5), () -> {
    doSomething();
});
```

#### 使用 tryLock

```java
DistributedLock lock = lockManager.getLock("resource:123");
if (lock.tryLock(Duration.ofSeconds(3), Duration.ofSeconds(30))) {
    try {
        // 持有锁最多 30 秒
        doWork();
    } finally {
        lock.unlock();
    }
} else {
    log.warn("获取锁失败");
}
```

---

## 统一消息系统

### 架构

```
UnifiedMessageManager（统一入口）
    ├── EmailMessageSender（邮件）
    ├── SmsMessageSender（短信）
    └── MqMessageSender（MQ）
```

### 发送邮件

```java
@Autowired
private UnifiedMessageManager messageManager;

public void sendWelcomeEmail(String to) {
    MessageContent content = MessageContent.builder()
            .messageType(MessageType.EMAIL)
            .to(to)
            .subject("欢迎注册")
            .templateCode("welcome-email")  // 对应 message_template 表中的模板编码
            .param("username", "张三")
            .param("loginUrl", "https://example.com/login")
            .build();

    messageManager.send(content);
}
```

### 发送短信

```java
MessageContent content = MessageContent.builder()
        .messageType(MessageType.SMS)
        .to("13800138000")
        .templateCode("verify-code")
        .param("code", "123456")
        .build();

messageManager.send(content);
```

### 发送 MQ 消息

```java
MessageContent content = MessageContent.builder()
        .messageType(MessageType.MQ)
        .destination("order.exchange")
        .param("orderId", orderId)
        .param("userId", userId)
        .build();

// 同步发送
messageManager.send(content);

// 异步发送
messageManager.sendAsync(content, result -> {
    if (result.isSuccess()) {
        log.info("消息发送成功");
    }
});

// 批量发送
List<MessageContent> messages = Arrays.asList(content1, content2);
messageManager.sendBatch(messages);
```

### 消息模板

在 `message_template` 表中配置模板：

```sql
INSERT INTO message_template (template_code, template_name, message_type, engine_type, subject, content, signature)
VALUES ('welcome-email', '欢迎邮件', 'EMAIL', 'FREEMARKER', '欢迎注册', '您好 ${username}，欢迎注册！', 'Aicyi 团队');
```

支持的模板引擎：
- `FREEMARKER` — FreeMarker 模板
- `SIMPLE` — 简单字符串替换

### 配置

```yaml
aicyi:
  message:
    email:
      host: smtp.example.com
      port: 465
      username: noreply@example.com
      password: your_password
      protocol: smtps
      default-encoding: UTF-8
      from: noreply@example.com

    sms:
      provider: twilio  # twilio 或 yunpian
      twilio:
        account-sid: your_account_sid
        auth-token: your_auth_token
        from: +1234567890

    mq:
      provider: rabbitmq
      default-exchange: default.exchange
```

---

## 缓存管理

### 多级缓存支持

aicyi 集成了多种缓存方案：

| 缓存 | 适用场景 |
|------|----------|
| Redis（Jedis） | 分布式缓存 |
| Redisson | 分布式对象、锁 |
| JetCache | 注解式缓存，支持本地+远程两级 |
| Guava Cache | 本地缓存 |
| Caffeine | 高性能本地缓存 |
| Ehcache | 本地缓存，支持磁盘持久化 |

### EnhancedRedisTemplate

框架提供了增强版 `RedisTemplate`：

```java
@Autowired
private EnhancedRedisTemplate redisTemplate;

// 设置带过期时间的值
redisTemplate.setWithExpire("key", "value", Duration.ofMinutes(10));

// 批量操作
redisTemplate.multiSet(map);
```

---

## 数据访问

### MyBatis 日志

配置 MyBatis SQL 日志输出：

```yaml
mybatis:
  configuration:
    log-impl: io.github.aicyi.midware.db.commons.ibatis.IbatisLogger
```

`IbatisLogger` 会将 SQL 语句和参数以结构化格式输出，方便调试。

### 分页工具

`PageUtils` 提供便捷的分页支持：

```java
@Autowired
private UserMapper userMapper;

public Page<User> listUsers(int pageNum, int pageSize) {
    Page<User> page = PageUtils.startPage(pageNum, pageSize);
    userMapper.selectAll();
    return page;
}
```

### 基础实体

所有实体建议继承 `BaseEntity`，自动包含通用字段：

```java
public class BaseEntity {
    private Long id;
    private Integer deleted;    // 软删除标记
    private Integer version;    // 乐观锁版本
    private Date createTime;
    private Date updateTime;
}
```

---

## MQ 消息队列

### 配置

aicyi 通过 Spring Cloud Stream 集成 RabbitMQ，支持多种交换机模式：

```yaml
spring:
  cloud:
    stream:
      bindings:
        direct-output:          # 直连模式
          destination: direct.exchange
          binder: rabbit
        topic-output:           # 主题模式
          destination: topic.exchange
          binder: rabbit
        delayed-output:         # 延迟消息
          destination: delayed.exchange
          binder: rabbit
```

### 生产者

```java
@Autowired
private StreamBridge streamBridge;

public void sendMessage(String routingKey, Object payload) {
    Message<String> message = MessageBuilder
            .withPayload(JSON.toJSONString(payload))
            .setHeader("routingKey", routingKey)
            .build();
    streamBridge.send("topic-output", message);
}
```

### 消费者

```java
@Component
public class OrderMessageHandler {

    @Bean
    public Consumer<String> orderInput() {
        return message -> {
            log.info("收到订单消息: {}", message);
            // 处理业务逻辑
        };
    }
}
```

### 延迟消息

配置延迟交换机后，可以发送延迟消息：

```java
Message<String> delayedMessage = MessageBuilder
        .withPayload(JSON.toJSONString(payload))
        .setHeader("x-delay", 5000)  // 延迟 5 秒
        .build();
streamBridge.send("delayed-output", delayedMessage);
```
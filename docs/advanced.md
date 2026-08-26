# 高级功能文档

本文档详细介绍 aicyi 框架各核心功能的使用方式。文中示例均对照源码校验，可放心引用。

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
失败响应还会自动回填 `traceId`（与 `X-Trace-Id` 响应头一致），便于客户端关联服务端日志：

```json
{
  "code": 40001,
  "message": "Bad Request",
  "data": null,
  "traceId": "0196e2d6c4c56d6db2b1ad4d9c2f8f8f"
}
```

成功响应示例：

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
// 自定义错误码/消息
return Result.failure(40001, "用户名不能为空");
```

### 预定义返回码（`CommonResultCode`）

| 返回码 | code / message | 说明 |
|--------|----------------|------|
| `SUCCESS` | 0 / Success | 操作成功 |
| `PARAM_ERROR` | 40001 / Bad Request | 参数错误 |
| `BUSINESS_ERROR` | 40002 / Business Error | 业务错误（默认业务错误码） |
| `UNAUTHORIZED` | 40101 / Unauthorized | 未授权 |
| `TOKEN_EXPIRED` | 40102 / Token Expired | Token 过期 |
| `FORBIDDEN` | 40300 / No Permission | 禁止访问 |
| `NOT_FOUND` | 40401 / Not Found | 资源不存在 |
| `SYSTEM_ERROR` | 50001 / Internal Server Error | 系统错误 |

> 码段规范：错误码为 5 位整数，前 3 位对齐 HTTP 状态码段，后 2 位为段内序号（从 01 起）；
> 业务自定义错误码需遵守同一规则，业务错误归入 4xx 段，500xx 仅保留给系统级错误。

### 支持的异常类型

`GlobalExceptionHandler`（`io.github.aicyi.midware.web.exception`）自动处理以下异常：

| 异常类型 | HTTP 状态 | 业务码 | 说明 |
|----------|-----------|--------|------|
| `IllegalArgumentException` | 200 | 40001 + 异常消息 | 业务参数/前置校验，按业务级口径处理 |
| `SystemException` | 200 | 50001 | 系统级异常，回显通用文案，不泄露内部 message |
| `BaseException`（含 `BusinessException`、`UnauthorizedException`、`TokenExpiredException` 等） | 200 | 异常携带的错误码 | 业务错误 |
| `Exception`（兜底） | 200 | 50001 | 未知异常，堆栈不外泄 |
| `HttpMessageNotReadableException` | 400 | 40001 | 请求体解析失败（非法 JSON 等） |
| `BindException` | 400 | 40001 | `@Valid` 校验失败，返回字段级错误 |
| `ConstraintViolationException` | 400 | 40001 | Bean Validation 校验失败 |
| `MissingServletRequestParameterException` | 400 | 40001 | 缺少必填参数 |
| `MethodArgumentTypeMismatchException` | 400 | 40001 | 参数类型转换失败 |
| `MissingPathVariableException` | 400 | 40001 | 路径变量缺失 |
| `ServletRequestBindingException` | 400 | 40001 | `@RequestHeader`/Cookie 绑定失败 |
| `HttpRequestMethodNotSupportedException` | 405 | 40001 | 请求方法不支持 |
| `HttpMediaTypeNotSupportedException` | 415 | 40001 | 媒体类型不支持 |
| `NoHandlerFoundException` | 404 | 40401 | 无匹配处理器（需开启 `spring.mvc.throw-exception-if-no-handler-found=true`） |

> HTTP 口径约定：传输级错误（非法请求体、方法/媒体类型不支持、参数缺失/绑定失败/类型转换失败、无匹配处理器）→ 4xx 状态码 + 业务码；
> 业务级错误（`BaseException` 及其子类、`IllegalArgumentException`、`SystemException`）与未知异常 → HTTP 200 + 业务错误码。
> 所有失败响应均回填 `traceId`。

---

## JWT 认证与授权

### 配置

`@EnableMidwareWeb` 默认开启鉴权（`enableAuth = true`），框架自动注册鉴权拦截器，无需手工注册；
容器中不存在 `AuthenticationTokenService` Bean 时启动即失败（fail-fast），防止鉴权配置遗漏流入生产。
可直接使用 `aicyi-midware-redis` 提供的基于 Redis 刷新 Token 的实现：

```java
@Configuration
public class WebConfiguration {

    @Bean
    public AuthenticationTokenService<IJWTInfo> authenticationTokenService(
            EnhancedRedisTemplateFactory templateFactory) {
        AuthenticationConfig config = AuthenticationConfig.builder()
                .secretKey("your-secret-key")
                .issuer("your-app")
                .subject("your-app.com")
                .refreshTokenTtl(7)
                .refreshTokenTimeUnit(TimeUnit.DAYS)
                .accessTokenTtl(1)
                .accessTokenTimeUnit(TimeUnit.DAYS)
                .multiTokenAllowed(true)      // 是否允许多设备登录
                .multiTokenCount(2)           // 多设备登录数上限
                .build();
        return new JwtRefreshAuthenticationTokenService<>(
                config,
                templateFactory.getStringRedisTemplate(),
                UserInfo.class);   // UserInfo 需实现 IJWTInfo（getId/getUniqueName/getDeviceId）
    }
}
```

> `AuthenticationConfig` 使用 Builder 构建，必填项为 `secretKey`、`refreshTokenTtl`、`accessTokenTtl`（均须为正）。
> 业务自定义 `UserInfo` 需实现 `IJWTInfo` 接口；JWT 主体默认由 `JwtPrincipalHandler` 写入上下文。

排除路径（如静态资源、接口文档）通过注解属性声明，同时排除鉴权与请求日志拦截器：

```java
@EnableMidwareWeb(excludePathPatterns = {"/webjars/**", "/v2/api-docs", "/favicon.ico", "/error"})
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

### 创建 Token / 获取当前用户

业务代码无需注入服务实例，可直接使用静态工具 `AuthenticationTokens`
（启动时由 `AuthenticationTokenServiceRegistrar` 自动注册，仅注册第一个 Bean）：

```java
// 登录成功：创建 Token 对
TokenPair pair = AuthenticationTokens.createToken(userInfo, null);

// 刷新 AccessToken
TokenPair newPair = AuthenticationTokens.refreshToken(refreshToken);

// 退出登录（吊销 refreshToken）
AuthenticationTokens.revokeToken(refreshToken);

// 校验 / 解析
boolean valid = AuthenticationTokens.validateAccessToken(accessToken);
UserInfo principal = AuthenticationTokens.parsePrincipal(accessToken);
```

在认证通过后，可通过 `CurrentContextHolder` 获取当前用户：

```java
String userId = CurrentContextHolder.getUserId();
String username = CurrentContextHolder.getUsername();
```

### 客户端请求格式

```
Authorization: Bearer <access_token>
```

> 拦截器严格校验 `Bearer ` 前缀（含空格）；Token 过期返回 40102，无效/解析失败返回 40101，
> 前端可依据 40102 触发 Token 刷新。

---

## 分布式 ID 生成

### 原理

基于 Redis 协调的 Snowflake 算法，各服务实例通过 Redis 自动注册 WorkerId，支持：
- 自动 WorkerId 分配与回收（Redis `SETNX` + Lua 续约/释放）
- 心跳保活（租约未续期则过期，其他实例可接管）
- 租约失效自动恢复（`auto-recover`）
- 时钟回拨容忍

> 底层 `SnowflakeIdGenerator` 固定使用 5 位 workerId（最大 31），`worker-id-bits` 配置其他值将在启动期校验失败。

### 配置

```yaml
aicyi:
  snowflake:
    enabled: true
    service-name: your-service-name    # 服务名，多服务共用同一 Redis 时必须各不相同（缺省 default-service 会共享命名空间并告警）
    worker-id-bits: 5                  # 固定 5 位，不可修改（其他值启动失败）
    datacenter-id: 0                   # 数据中心 ID，默认 0
    ttl-seconds: 60                    # WorkerId 租约时间（秒），默认 60
    heartbeat-seconds: 20              # 心跳间隔（秒），缺省按 ttl/3 计算（最小 1）
    auto-recover: true                 # 租约失效后是否自动恢复，默认 false
    clock-backward-tolerance-ms: 5     # 时钟回拨容忍（毫秒），默认 5
    epoch: 1672531200000               # 起始时间戳（毫秒），默认 1672531200000
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

也支持静态工具调用（未启用 `aicyi.snowflake` 时降级为本地 `SnowflakeIdGenerator(workerId=0)`，
仅适用于单机，多实例会产生重复 ID 并输出告警）：

```java
long orderId = IdUtils.generateId();   // io.github.aicyi.midware.kit.util.IdUtils
```

---

## 分布式锁

### 接口定义

`DistributedLock` 接口（`io.github.aicyi.commons.core.lock`）提供了完整的分布式锁语义：

```java
public interface DistributedLock {
    String name();                                          // 锁名称（资源标识）
    void lock() throws InterruptedException;                // 阻塞获取（默认 watchdog 自动续租）
    void lock(Duration leaseTime) throws InterruptedException; // 阻塞获取，固定租约（不续租）
    boolean tryLock();                                      // 立即尝试
    boolean tryLock(Duration waitTime) throws InterruptedException;
    boolean tryLock(Duration waitTime, Duration leaseTime) throws InterruptedException;
    void unlock();                                          // 仅持有者可释放
    boolean isHeldByCurrentThread();
    boolean isLocked();
    boolean forceUnlock();                                  // 强制释放
    // 模板方法
    void execute(Runnable task) throws InterruptedException;
    <T> T execute(Callable<T> task) throws Exception;
    boolean tryExecute(Duration waitTime, Runnable task) throws InterruptedException;
    <T> T tryExecute(Duration waitTime, Callable<T> task, T fallback) throws Exception;
}
```

> 启用前置：`aicyi.redis.enabled=true` 且容器中存在 `RedissonClient` Bean 时，
> 自动装配 `DistributedLockManager`（`RedissonDistributedLockManager`）。Redisson 版本为 3.9.1。

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

// 尝试执行（带等待超时）
boolean success = lock.tryExecute(Duration.ofSeconds(5), () -> {
    doSomething();
});

// 尝试执行并返回结果（获取失败返回 fallback）
String result = lock.tryExecute(Duration.ofSeconds(5), () -> computeResult(), "default");
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
    ├── EmailMessageSender（邮件，MessageType.MAIL）
    ├── SmsMessageSender（短信，MessageType.SMS）
    └── MqMessageSender（MQ，MessageType.MQ）
```

- `MessageType` 枚举：`MAIL`、`SMS`、`PUSH`、`MQ`、`WECHAT_MP`
- 各渠道自动配置通过开关（`aicyi.message.*.enabled`）装配为 `MessageSender` Bean，`UnifiedMessageManager` 按消息类型路由发送器
- 引入 `aicyi-midware-message-spring-boot-starter` 后，`UnifiedMessageManager` Bean 自动装配

### 发送邮件

使用 `MailMessage`（Builder 构建，收件人必填；支持直接内容或模板两种方式）：

```java
@Autowired
private UnifiedMessageManager messageManager;

// 方式一：直接内容
MailMessage mail = MailMessage.builder()
        .to("user@example.com")
        .cc("cc@example.com")
        .subject("欢迎注册")
        .content("<h1>Hello</h1>")
        .html()                         // 或 .html(true) / .text()
        .attachment(attachment)         // 可多个
        .build();

// 方式二：模板消息（templateId 对应 message_template 表 template_code）
MailMessage mail = MailMessage.builder()
        .to("user@example.com")
        .subject("欢迎注册")
        .templateId("welcome-email")
        .templateParam("username", "张三")
        .build();

// 也提供快速工厂方法
MailMessage.of("user@example.com", "welcome-email", params);
MailMessage.withContent("内容", "主题", "user@example.com");

messageManager.send(mail);
```

### 发送短信

使用 `SmsMessage`：

```java
SmsMessage sms = SmsMessage.builder()
        .phoneNumber("13800138000")
        .content("您的验证码是：123456")
        .sign("Aicyi")
        .build();

// 模板方式
SmsMessage sms = SmsMessage.of("13800138000", "verify-code",
        Maps.ofStr("code", "123456").build());

messageManager.send(sms);
```

### 发送 MQ 消息

使用 `MqMessage`（`destination` 为 Spring Cloud Stream 绑定通道名，非交换机名）：

```java
MqMessage mq = MqMessage.builder()
        .content(orderId)               // 消息体
        .destination("message-output")  // 绑定通道名
        .build();

// 延迟消息（delayLevel 单位毫秒，写入 x-delay 头）
MqMessage delayed = MqMessage.builder()
        .content(orderId)
        .destination("delayed-output")
        .delayLevel(5000L)              // 延迟 5 秒
        .build();

messageManager.send(mq);
```

### 统一入口 API

```java
// 同步发送
MessageSendResult result = messageManager.send(content);
if (result.isSuccess()) { ... }

// 异步发送（内部使用有界线程池，回调中携带发送方 MDC 上下文）
messageManager.sendAsync(content, new MessageSendCallback() {
    @Override
    public void onComplete(MessageSendResult result) { ... }
    @Override
    public void onError(Exception e) { ... }
});

// 按优先级发送（当前版本仅校验非空，行为等价于 send）
messageManager.send(content, MessagePriority.NORMAL);

// 批量发送
List<MessageSendResult> results = messageManager.sendBatch(Arrays.asList(mail, sms));
```

### 消息模板

启用 `aicyi.message.template.enabled=true` 后，模板由 DB 持久化并做两级缓存（本地 Caffeine + Redis），
从 `message_template` 表加载（需容器存在 `EnhancedRedisTemplateFactory` 与 `RedissonClient` Bean，缺失时静默跳过）。

表结构定义见 `aicyi-example/aicyi-example-dao/db/init.sql`，插入一条模板示例：

```sql
INSERT INTO message_template
    (template_code, template_name, message_type, format, engine_type, subject, content, signature, variables)
VALUES
    ('welcome-email', '欢迎邮件', 'mail', 'HTML', 'THYMELEAF',
     '欢迎注册', '您好 ${username}，欢迎注册！', 'Aicyi 团队', '["username"]');
```

> 字段说明：`message_type` 使用小写枚举值（mail/sms/mq/...）；`engine_type` 支持
> `SIMPLE`（${key} 占位符替换）/ `FREEMARKER` / `THYMELEAF` / `MUSTACHE`；
> `variables` 为 JSON 数组，声明模板必需的参数名（缺失时发送校验失败）。
> 邮件渠道装配时自动注册 `THYMELEAF` 与 `FREEMARKER` 引擎，`SIMPLE` 引擎默认始终注册。

### 配置

```yaml
aicyi:
  message:
    email:
      enabled: true
      host: smtp.example.com
      port: 465
      username: noreply@example.com
      password: your_password
      from-name: Aicyi 团队        # 发件人显示名（发件地址固定用 username）

    sms:
      enabled: true
      provider: twilio            # default（邮件网关转短信）/ twilio / yunPian
      username: your_account_sid
      password: your_auth_token
      from: +1234567890

    mq:
      enabled: true
      provider: rabbitMq          # 固定 rabbitMq（基于 Spring Cloud Stream）

    template:
      enabled: true               # 模板 DB 持久化（可选）
```

> 业务可自定义 `MessageSender` Bean 覆盖默认渠道实现：基础包内置渠道先注册、业务自定义后注册，
> 同一消息类型后注册覆盖先注册。

---

## 缓存管理

### 统一缓存抽象

aicyi 通过 `Cache<K,V>` 接口（`io.github.aicyi.commons.core.cache`）提供统一缓存契约，
`RedisCache`（`io.github.aicyi.midware.redis.cache`）为 Redis 实现，支持：

- `get(key)` / `get(key, loader)`（loader 回填带防击穿锁保护）
- `getAll(keys)` / `put(key, value, ttl)` / `putAll` / `evict` / `evictBatch` / `exists` / `clear` / `stats`
- TTL 抖动、缓存空值、防击穿锁、剩余过期时间查询

```java
@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<String, UserInfo> userInfoCache(EnhancedRedisTemplateFactory templateFactory) {
        StringRedisTemplate redisTemplate = templateFactory.getStringRedisTemplate();
        CacheConfig cacheConfig = RedisCacheConfig.builder()
                .globalPrefix("aicyi.cache")
                .cacheName("userInfo")
                .ttl(Duration.ofMinutes(10))
                .cacheNull(true)
                .build();
        return new RedisCache<>(redisTemplate, cacheConfig, new CacheWrapperCodec<>(UserInfo.class));
    }
}
```

使用：

```java
@Autowired
private Cache<String, UserInfo> userInfoCache;

UserInfo user = userInfoCache.get("user:1", userId -> userService.findById(userId));
```

### EnhancedRedisTemplateFactory

框架不直接暴露 `RedisTemplate` Bean，而是提供增强工厂 `EnhancedRedisTemplateFactory`
（`io.github.aicyi.midware.redis.template`，`aicyi.redis.enabled=true` 时自动装配），
按需创建并缓存各类 `RedisTemplate`：

```java
@Autowired
private EnhancedRedisTemplateFactory templateFactory;

StringRedisTemplate stringRedisTemplate = templateFactory.getStringRedisTemplate();        // String 模板
RedisTemplate<String, T> jsonTemplate = templateFactory.getJsonRedisTemplate(UserInfo.class); // 类型化 JSON 模板
RedisTemplate<String, Object> generic = templateFactory.getGenericJsonRedisTemplate();      // 通用 JSON 模板
```

### 其他缓存组件

| 组件 | 说明 |
|------|------|
| `RedisCache` | 统一 `Cache` 接口的 Redis 实现 |
| Redisson | 分布式对象、分布式锁（`RedissonClient` 由业务定义 Bean） |
| Caffeine | 本地缓存（消息模板本地缓存 `TemplateLocalCache` 等场景） |

---

## 数据访问

### MyBatis 日志

配置 MyBatis SQL 日志输出（`IbatisLogger` 将 SQL 按 DAO logger 输出，由 logback 的 DAO 级别统一控制）：

```yaml
mybatis:
  configuration:
    log-impl: io.github.aicyi.midware.db.commons.ibatis.IbatisLogger
```

### 分页工具

`PageUtils`（`io.github.aicyi.midware.db.commons`）基于 PageHelper 提供便捷分页，返回 Spring Data 的 `Pageable`/`Page`：

```java
@Autowired
private UserMapper userMapper;

public Page<User> listUsers(int pageNum, int pageSize) {
    // PageParam 提供 getPageOrDefault/getSizeOrDefault（缺省 1/10，size 上限 500）
    return PageUtils.getPage(new PageParam(pageNum, pageSize), () -> userMapper.selectAll());
}

// 仅取列表（不执行 count 查询）
List<User> users = PageUtils.getList(pageParam, () -> userMapper.selectByExample(example));

// 直接构造 Pageable
Pageable pageable = PageUtils.createPageable(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
```

> PageHelper 会将 orderBy 直接拼接进 SQL，`PageUtils` 对排序字段做了白名单校验（仅 `[A-Za-z0-9_]+`），
> 非法字段抛 `PARAM_ERROR`，防止 ORDER BY 注入。

### MyBatis-Plus 自动增强

`aicyi.mybatis-plus.enabled`（缺省开启）时自动装配：

- **分页拦截器**：MySQL 方言，`maxLimit = 500`（与 `PageParam.MAX_SIZE` 对齐，防止深分页）
- **乐观锁拦截器**：`OptimisticLockerInnerInterceptor`
- **公共字段自动填充**：`MybatisPlusMetaObjectHandler` —— 插入时填充 `createTime`/`updateTime`，并给 `deleted`/`version` 初始值；更新时仅刷新 `updateTime`
  （仅对带 `@TableField(fill = ...)` 标注的字段生效）

### 基础实体与字段约定

`BaseEntity`（`io.github.aicyi.commons.lang.model`）是 DO 分层归属的**标记型基类**，本身不内置公共字段；
主键、乐观锁与审计字段由代码生成器在子类中生成，按约定字段名（`id`/`deleted`/`version`/`createTime`/`updateTime`）统一处理：

- `BaseEntityUtils.setDefaultValue(entity, idGenerator)`：按约定字段名反射填充默认值
- MyBatis-Plus 场景：由 `MybatisPlusMetaObjectHandler` 自动填充
- 示例实体字段约定：

```java
public class User extends BaseEntity {
    private Long id;
    private String username;
    private BooleanType deleted;   // 软删除标记，0:未删除 1:已删除
    private Integer version;       // 乐观锁版本
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

---

## MQ 消息队列

### 配置

aicyi 通过 Spring Cloud Stream 集成 RabbitMQ（`aicyi-midware-rabbitmq` 提供 `StreamMqSender`），
支持多种交换机模式。示例应用中通过 `MessageChannels` 声明通道，并在 Nacos 的 `aicyi-example.yml` 中配置绑定：

```yaml
spring:
  cloud:
    stream:
      default-binder: rabbit
      binders:
        rabbit:
          type: rabbit
          environment:
            spring:
              rabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
                virtual-host: /
      bindings:
        message-output:          # 通道名（MqMessage.destination 对应此名）
          destination: default.exchange
          group: default.queue
          content-type: application/json
          binder: rabbit
        direct-output:           # 直连模式
          destination: direct.exchange
          binder: rabbit
        topic-output:            # 主题模式（动态 routingKey）
          destination: topic.exchange
          binder: rabbit
        delayed-output:          # 延迟消息
          destination: delayed.exchange
          binder: rabbit
```

### 生产者

统一消息入口直接使用 `MqMessage`（见上文「统一消息系统 - 发送 MQ 消息」）；也可以直接注入 `MqSender`
（自动装配的 `StreamMqSender` Bean）：

```java
@Autowired
private MqSender mqSender;

public void sendMessage(String channel, Object payload) {
    mqSender.send(channel, payload);
    // 带消息头 / 延迟消息
    mqSender.send(channel, payload, Maps.of("routingKey", "order.created").build());
    mqSender.sendDelayed("delayed-output", payload, 5000L);
}
```

### 消费者

示例应用使用 `@EnableBinding` + `@StreamListener` 消费（也可使用函数式 `Consumer` Bean）：

```java
@Configuration
@EnableBinding(MessageChannels.MessageInput.class)
public class MessageConfiguration {
}

@Component
public class OrderMessageHandler {

    @StreamListener(MessageChannels.INPUT)
    public void onOrder(String message) {
        log.info("收到订单消息: {}", message);
        // 处理业务逻辑
    }
}
```

### 延迟消息

发送带 `delayLevel` 的 `MqMessage`（写入 `x-delay` 头）到延迟通道，配合 RabbitMQ delayed-exchange 插件实现延迟投递。
（示例应用中延迟交换机由外部声明，`declare-exchange: false`。）

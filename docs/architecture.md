# 项目目录结构评估

## 当前结论

当前仓库采用 Maven 多模块结构，主线是合理的：

```text
aicyi
├── aicyi-base-dependencies       # 版本 BOM
├── aicyi-base-starter-parent     # Maven parent 和插件管理
├── aicyi-commons                 # 基础抽象与通用工具
├── aicyi-midware                 # 中间件实现与 Spring Boot starter
└── aicyi-example                 # 示例应用
```

整体依赖方向应保持为：

```text
aicyi-example -> aicyi-midware -> aicyi-commons -> aicyi-base-*
```

这个方向适合基础设施 SDK：上层示例只演示用法，中间件模块负责具体技术实现，commons 提供更稳定的公共模型、接口和工具。

## 合理之处

1. BOM、parent、业务模块分开，版本治理和插件治理有独立发布单元。
2. `aicyi-commons` 和 `aicyi-midware` 分层清晰，能避免 Redis、RabbitMQ、Spring Web 等实现依赖污染最底层抽象。
3. `aicyi-midware-message`、`aicyi-midware-db` 内部继续拆子模块，适合按能力选择依赖。
4. `aicyi-example` 单独隔离，避免示例代码进入正式 SDK 发布面。

## 主要问题

1. **示例模块职责不够干净**

   `aicyi-example-boot` 当前依赖 `aicyi-example-test`，而且启动配置里引用了 `io.github.aicyi.test.*` 下的 fixture 类。这会让运行时应用依赖测试辅助代码。短期可保留以避免大范围搬迁，但推荐后续拆成 `aicyi-example-fixture` 或把这些类移入 `src/test/java`。

2. **模块命名不完全统一**

   部分 artifactId 使用完整前缀，如 `aicyi-midware-redis`；部分子模块使用短名，如 `aicyi-midware-message-core`、`aicyi-midware-db-commons`。短名在 Maven 坐标中可用，但跨仓库消费时识别度较低。建议新模块统一使用完整前缀，例如 `aicyi-midware-aicyi-midware-message-core`、`aicyi-midware-aicyi-midware-db-commons`。

3. **starter 和实现模块边界需要继续收紧**

   `aicyi-midware-spring-boot-starter` 是总 starter，`aicyi-midware-message-spring-boot-starter` 是消息 starter。这个方向合理，但 starter 内依赖应尽量使用 `optional`/`provided` 表达能力开关，避免引入一个 starter 时带入不需要的 Redis、MQ、DB 实现。

4. **示例应用运行入口有重复**

   `aicyi-example-boot` 和 `aicyi-example-consumer` 都是可运行 Spring Boot 应用，应该各自声明自己的 `mainClass`。当前已修正 consumer 的打包入口。

5. **本地运行产物污染目录**

   多模块下 `target/`、`logs/`、`.DS_Store`、示例测试生成图片和 Excel 文件容易散落在子模块。当前已通过 `.gitignore` 统一忽略。

## 推荐目标结构

```text
aicyi
├── aicyi-base-dependencies
├── aicyi-base-starter-parent
├── aicyi-commons
│   ├── aicyi-commons-lang
│   ├── aicyi-commons-core
│   ├── aicyi-commons-logging
│   ├── aicyi-commons-security
│   └── aicyi-commons-util
├── aicyi-midware
│   ├── aicyi-midware-db
│   │   ├── aicyi-midware-aicyi-midware-db-commons
│   │   └── aicyi-midware-aicyi-midware-db-mybatisplus
│   ├── aicyi-midware-message
│   │   ├── aicyi-midware-aicyi-midware-message-core
│   │   ├── aicyi-midware-aicyi-midware-message-mail
│   │   ├── aicyi-midware-aicyi-midware-message-mq
│   │   ├── aicyi-midware-aicyi-midware-message-sms
│   │   └── aicyi-midware-aicyi-midware-message-spring-boot-starter
│   ├── aicyi-midware-rabbitmq
│   ├── aicyi-midware-redis
│   ├── aicyi-midware-web
│   └── aicyi-midware-spring-boot-starter
└── aicyi-example
    ├── aicyi-example-domain
    ├── aicyi-example-dao
    ├── aicyi-example-service
    ├── aicyi-example-web
    ├── aicyi-example-boot
    ├── aicyi-example-consumer
    └── aicyi-example-fixture
```

## 迁移建议

1. 先保持源码目录不大搬迁，只收敛 POM 和文档，确保当前构建稳定。
2. 第二步把 `aicyi-example-test/src/main/java/io/github/aicyi/test` 中被主应用引用的 fixture 类迁移到 `aicyi-example-fixture`，启动模块只在测试或演示配置中依赖它。
3. 第三步如需发布到外部仓库，再统一短 artifactId。这个动作会影响所有下游依赖坐标，应单独作为 breaking change 处理。
4. 长期建议给 `commons`、`midware`、`example` 增加依赖方向检查，防止上层模块反向进入底层。

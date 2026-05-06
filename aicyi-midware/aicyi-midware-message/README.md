aicyi-midware-message
├── message-core                  # 纯抽象层（无 Spring 依赖）
├── message-mail                  # 邮件实现（SMTP / 云邮件）
├── message-mq                    # MQ 实现（RabbitMQ / Kafka）
├── message-sms                   # 短信实现（阿里云 / Twilio 等）
└── message-spring-boot-starter   # 自动装配（对外唯一入口）
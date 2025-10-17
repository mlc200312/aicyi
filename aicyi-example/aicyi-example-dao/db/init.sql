create table t_user
(
    id          bigint               not null comment '主键'
        primary key,
    age         int        default 0 not null comment '年龄',
    id_card     varchar(32)          null comment '身份证',
    mobile      varchar(11)          not null comment '手机号',
    gender_type tinyint(2)           null comment '性别，1:男；2：女；',
    birthday    datetime(3)          null comment '生日',
    deleted     tinyint(2) default 0 not null comment '删除标记，0：未删除，1：已删除',
    version     int        default 0 not null comment '版本',
    create_time datetime(3)          not null comment '创建时间',
    update_time datetime(3)          not null comment '更新时间'
)
    comment '用户表';

create index idx_id_card
    on t_user (id_card)
    comment '身份证索引';

create unique index uk_mobile
    on t_user (mobile)
    comment '手机号唯一键';

create table t_student
(
    id            bigint                   not null comment '主键'
        primary key,
    user_id       bigint                   not null comment '用户ID',
    score         decimal(15, 2) default 0 not null comment '成绩',
    grade_type    varchar(2)               not null comment '年级',
    register_time datetime(3)              not null comment '注册时间',
    deleted       tinyint(2)     default 0 not null comment '删除标记，0：未删除，1：已删除',
    version       int            default 0 not null comment '版本',
    create_time   datetime(3)              not null comment '创建时间',
    update_time   datetime(3)              not null comment '更新时间'
)
    comment '学生表';

create unique index uk_user_id
    on t_student (user_id)
    comment '用户ID唯一键';


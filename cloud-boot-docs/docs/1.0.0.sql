-- ----------------------------
-- Database structure for cloud
-- ----------------------------
CREATE DATABASE `docker` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `docker`.`user`
(
    `id`           bigint(20) unsigned                     NOT NULL COMMENT '用户ID',
    `role`         varchar(10) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '角色：管理员：ADMIN，用户：USER',
    `username`     varchar(20) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '用户名',
    `password`     varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
    `name`         varchar(30) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '名称',
    `phone`        varchar(30) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '手机号',
    `is_disable`   tinyint(2) unsigned                     NOT NULL COMMENT '是否禁用',
    `data_version` bigint(20) unsigned                     NOT NULL COMMENT '数据版本号（乐观锁）',
    `created_at`   datetime(6)                             NOT NULL COMMENT '创建时间',
    `updated_at`   datetime(6) DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '用户表';


-- ----------------------------
-- Init table data for user
-- Password: 123456
-- ----------------------------
insert `docker`.`user`(id, role, username, password, name, phone, is_disable, data_version, created_at)
    value (1, 'ADMIN', 'admin', '$2a$10$.YIXxeNKjaWMS1d8HzSaa.z9wrMastAAg8eMp1vknnj8qW0ZX8ETi', '系统管理员',
           '15159845510', 0, 0, '2021-02-10 00:00:00');


-- ----------------------------
-- Table structure for script
-- ----------------------------
CREATE TABLE `docker`.`script`
(
    `id`                     bigint(20) unsigned                                          NOT NULL COMMENT '脚本 ID',
    `service_name`           varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
    `docker_compose_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci    NOT NULL COMMENT 'docker-compose 编排文件内容',
    `is_auto_update`         tinyint(2) unsigned                                          NOT NULL COMMENT '是否自动更新',
    `data_version`           bigint(20) unsigned                                          NOT NULL COMMENT '数据版本号（乐观锁）',
    `created_at`             datetime(6)                                                  NOT NULL COMMENT '创建时间',
    `updated_at`             datetime(6) DEFAULT NULL COMMENT '修改时间',
    `user_id`                bigint(20) unsigned                                          NOT NULL COMMENT '用户 ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_serviceName` (`service_name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='脚本信息表';
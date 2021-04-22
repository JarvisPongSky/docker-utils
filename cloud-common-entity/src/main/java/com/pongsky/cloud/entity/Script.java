package com.pongsky.cloud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 脚本信息表
 * <p>
 * unique - userId + serviceName
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Script {

    /**
     * 脚本 ID
     */
    private Long id;

    /**
     * 服务名称
     * <p>
     * example: halo
     */
    private String serviceName;

    /**
     * docker-compose 编排文件内容
     */
    private String dockerComposeContent;

    /**
     * 是否自动更新
     */
    private Integer isAutoUpdate;

    /**
     * 数据版本号（乐观锁）
     */
    private Long dataVersion;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

    /**
     * 用户ID
     */
    private Long userId;

}

package com.pongsky.cloud.entity;

import com.pongsky.cloud.utils.jwt.enums.AuthRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户表
 * <p>
 * unique - role + phone
 *
 * @author pengsenhao
 * @create 2021-02-11
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 角色：管理员：ADMIN，用户：USER
     */
    private AuthRole role;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 名称
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 是否禁用
     */
    private Integer isDisable;

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

}

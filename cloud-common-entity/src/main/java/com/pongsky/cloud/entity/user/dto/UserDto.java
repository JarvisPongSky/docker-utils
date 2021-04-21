package com.pongsky.cloud.entity.user.dto;

import com.pongsky.cloud.utils.constant.ConstantUtils;
import com.pongsky.cloud.validator.CreateGroup;
import com.pongsky.cloud.validator.SearchGroup;
import com.pongsky.cloud.validator.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author pengsenhao
 * @create 2021-02-11
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserDto {

    /**
     * 用户名
     */
    @NotBlank(groups = {CreateGroup.class, SearchGroup.class})
    @Length(min = 5, max = 20, groups = {CreateGroup.class, UpdateGroup.class, SearchGroup.class})
    private String username;

    /**
     * 密码
     */
    @NotBlank(groups = {CreateGroup.class, SearchGroup.class})
    @Length(min = 6, max = 20, groups = {CreateGroup.class, UpdateGroup.class, SearchGroup.class})
    private String password;

    /**
     * 名称
     */
    @NotBlank(groups = {CreateGroup.class})
    @Length(max = 30, groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    /**
     * 手机号
     */
    @NotBlank(groups = {CreateGroup.class})
    @Length(max = 30, groups = {CreateGroup.class, UpdateGroup.class})
    @Pattern(regexp = ConstantUtils.PATTERN_BY_CHINA_PHONE, message = "格式错误", groups = {CreateGroup.class, UpdateGroup.class})
    private String phone;

}

package com.pongsky.cloud.entity.script.dto;

import com.pongsky.cloud.validator.CreateGroup;
import com.pongsky.cloud.validator.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 脚本信息
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ScriptDto {

    /**
     * 服务名称
     * <p>
     * example: halo
     */
    @NotBlank(groups = {CreateGroup.class})
    @Length(max = 30, groups = {CreateGroup.class})
    private String serviceName;

    /**
     * docker-compose 编排文件内容
     */
    @NotBlank(groups = {CreateGroup.class})
    private String dockerComposeContent;

    /**
     * 是否自动更新
     */
    @NotNull(groups = {CreateGroup.class})
    @Range(min = 0, max = 1, groups = {CreateGroup.class, UpdateGroup.class})
    private Integer isAutoUpdate;

}

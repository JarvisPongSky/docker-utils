package com.pongsky.cloud.entity.script.dto;

import com.pongsky.cloud.validator.CreateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

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
     * base 文件目录，以 / 结尾
     * <p>
     * example: ~/Downloads/halo/
     * <p>
     * TIPS: 建议每个服务都有单独文件夹，防止窜在一起不好识别
     */
    @NotBlank(groups = {CreateGroup.class})
    @Length(max = 100, groups = {CreateGroup.class})
    private String baseDir;

    /**
     * docker-compose 编排文件内容
     */
    @NotBlank(groups = {CreateGroup.class})
    private String dockerComposeContent;

}
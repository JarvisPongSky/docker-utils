package com.pongsky.cloud.entity.script.dto;

import com.pongsky.cloud.validator.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UpdateServiceDto {

    /**
     * 镜像
     */
    @NotBlank(groups = {UpdateGroup.class})
    @Length(max = 30, groups = {UpdateGroup.class})
    private String repository;

    /**
     * 标签
     */
    @NotBlank(groups = {UpdateGroup.class})
    @Length(max = 30, groups = {UpdateGroup.class})
    private String tag;

}
package com.pongsky.cloud.entity.script.dto;

import com.pongsky.cloud.validator.SearchGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * 脚本信息
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SearchScriptDto {

    /**
     * 服务名称
     * <p>
     * example: halo
     */
    @Length(max = 30, groups = {SearchGroup.class})
    private String serviceName;

}

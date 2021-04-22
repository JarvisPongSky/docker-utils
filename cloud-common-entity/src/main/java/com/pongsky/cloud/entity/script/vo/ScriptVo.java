package com.pongsky.cloud.entity.script.vo;

import com.pongsky.cloud.entity.script.BaseScript;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.text.MessageFormat;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ScriptVo extends BaseScript {

    /**
     * 脚本 ID
     */
    private Long id;

    /**
     * 是否自动更新
     */
    private Integer isAutoUpdate;

    /**
     * 自动更新 URL
     */
    private String autoUpdateUrl;

    /**
     * 自动更新URL
     */
    private static final String AUTO_UPDATE_URL = "{0}/api/autoUpdate/{1}/{2}";

    public ScriptVo setAutoUpdateUrl(String baseUrl, Long userId, String serviceName) {
        this.autoUpdateUrl = MessageFormat.format(AUTO_UPDATE_URL, baseUrl, userId.toString(), serviceName);
        return this;
    }

}

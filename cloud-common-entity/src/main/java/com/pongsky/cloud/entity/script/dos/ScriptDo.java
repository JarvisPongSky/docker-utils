package com.pongsky.cloud.entity.script.dos;

import com.pongsky.cloud.entity.script.BaseScript;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ScriptDo extends BaseScript {

    /**
     * 脚本 ID
     */
    private Long id;

    /**
     * 是否自动更新
     */
    private Integer isAutoUpdate;

    /**
     * 数据版本号（乐观锁）
     */
    private Long dataVersion;

}

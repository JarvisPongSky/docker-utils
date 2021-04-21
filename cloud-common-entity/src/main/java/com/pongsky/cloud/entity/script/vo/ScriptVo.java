package com.pongsky.cloud.entity.script.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ScriptVo {

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
     * base 文件目录，以 / 结尾
     * <p>
     * example: ~/Downloads/halo/
     * <p>
     * TIPS: 建议每个服务都有单独文件夹，防止窜在一起不好识别
     */
    private String baseDir;

    /**
     * docker-compose 编排文件内容
     */
    private String dockerComposeContent;

    /**
     * base 启动脚本，用于创建 docker-compose 编排文件
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private List<String> baseStartScript;

    /**
     * 启动脚本
     * <p>
     * format: docker stack deploy -c {@link ScriptVo#getBaseDir()}docker-compose.yml {@link ScriptVo#getServiceName()}
     * <p>
     * example: docker stack deploy -c ~/Downloads/halo/docker-compose.yml halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String startScript;

    /**
     * 关闭脚本
     * <p>
     * format: docker stack down {@link ScriptVo#getServiceName()}
     * <p>
     * example: docker stack down halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String downScript;

    /**
     * 更新脚本
     * <p>
     * format: docker service update --image ${0}:${1} {@link ScriptVo#getServiceName()}_{@link ScriptVo#getServiceName()}
     * <p>
     * example: docker service update --image registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.8 halo_halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String updateScript;

}
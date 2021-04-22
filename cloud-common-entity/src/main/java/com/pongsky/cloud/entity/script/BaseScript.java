package com.pongsky.cloud.entity.script;

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
public class BaseScript {

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
     * base 启动脚本，用于创建 docker-compose 编排文件
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private List<String> baseStartScript;

    public List<String> getBaseStartScript() {
        if (baseStartScript == null) {
            baseStartScript = List.of(
                    "rm -rf ~/docker-deploy-script/" + serviceName + "/docker-compose.yml",
                    "mkdir -p ~/docker-deploy-script/" + serviceName,
                    "echo \"" + dockerComposeContent + "\" > ~/docker-deploy-script/" + serviceName + "/docker-compose.yml"
            );
        }
        return baseStartScript;
    }

    /**
     * 启动脚本
     * <p>
     * example: docker stack deploy -c ~/Downloads/halo/docker-compose.yml halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String startScript;

    public String getStartScript() {
        if (startScript == null) {
            startScript = "docker stack deploy -c ~/docker-deploy-script/" + serviceName + "/docker-compose.yml" + " " + serviceName;
        }
        return startScript;
    }

    /**
     * 关闭脚本
     * <p>
     * example: docker stack down halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String downScript;

    public String getDownScript() {
        if (downScript == null) {
            downScript = "docker stack down " + serviceName;
        }
        return downScript;
    }

    /**
     * 更新脚本
     * <p>
     * example: docker service update --image registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.8 halo_halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String updateScript;

    public String getUpdateScript() {
        if (updateScript == null) {
            updateScript = "docker service update --image {0}:{1} " + serviceName + "_" + serviceName;
        }
        return updateScript;
    }

}

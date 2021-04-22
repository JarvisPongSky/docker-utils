package com.pongsky.cloud.utils.docker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 脚本信息
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Script {

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

    /**
     * 启动脚本
     * <p>
     * example: docker stack deploy -c ~/Downloads/halo/docker-compose.yml halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String startScript;

    /**
     * 关闭脚本
     * <p>
     * example: docker stack down halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String downScript;

    /**
     * 更新脚本
     * <p>
     * example: docker service update --image registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.8 halo_halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String updateScript;

}

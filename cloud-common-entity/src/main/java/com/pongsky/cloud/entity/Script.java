package com.pongsky.cloud.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
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
     * docker-compose 编排文件名称
     * <p>
     * example & default: docker-compose.yml
     * <p>
     * WARN: 保存后会自动创建文件。如存在同名文，则会进行删除处理。
     */
    private String dockerComposeFileName;

    /**
     * docker-compose 编排文件内容
     */
    private String dockerComposeContent;

    /**
     * base 启动脚本，用于创建 docker-compose 编排文件
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String baseStartScript;

    /**
     * 启动脚本
     * <p>
     * format: docker stack deploy -c {@link Script#getBaseDir()}{@link Script#getDockerComposeFileName()} {@link Script#getServiceName()}
     * <p>
     * example: docker stack deploy -c ~/Downloads/halo/docker-compose.yml halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String startScript;

    /**
     * 关闭脚本
     * <p>
     * format: docker stack down {@link Script#getServiceName()}
     * <p>
     * example: docker stack down halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String downScript;

    /**
     * 更新脚本
     * <p>
     * format: docker service update --image ${0}:${1} {@link Script#getServiceName()}_{@link Script#getServiceName()}
     * <p>
     * example: docker service update --image registry.cn-shanghai.aliyuncs.com/pongsky/halo:prod-1.4.8 halo_halo
     * <p>
     * INFO: 脚本由系统自动生成。
     */
    private String updateScript;

    /**
     * 数据版本号（乐观锁）
     */
    private Long dataVersion;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 构建脚本信息
     *
     * @return this
     */
    public Script buildScriptInfo() {
        this.baseStartScript = JSON.toJSONString(List.of(
                "rm -rf " + baseDir + dockerComposeFileName,
                "echo \"" + dockerComposeContent + "\" > " + baseDir + dockerComposeFileName
        ));
        this.startScript = "docker stack deploy -c " + baseDir + dockerComposeFileName + " " + serviceName;
        this.downScript = "docker stack down " + serviceName;
        this.updateScript = "docker service update --image {0}:{1} " + serviceName + "_" + serviceName;
        return this;
    }

}

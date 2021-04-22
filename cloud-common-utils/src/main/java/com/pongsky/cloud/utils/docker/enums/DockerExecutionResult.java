package com.pongsky.cloud.utils.docker.enums;

import com.pongsky.cloud.utils.docker.dto.Script;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;

/**
 * docker 执行结果
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@Getter
@AllArgsConstructor
public enum DockerExecutionResult {

    /**
     * 创建服务
     */
    CREATE_SERVICE_RESULT("Creating service {0}_{0}", null, null, null),

    /**
     * 删除服务
     */
    REMOVE_SERVICE_RESULT("Removing service {0}_{0}", null, null, null),

    /**
     * 更新服务
     */
    UPDATE_SERVICE_RESULT(null, "{0}_{0}", null, "verify: Service converged");

    /**
     * 结果
     */
    private final String result;

    /**
     * 开始匹配
     */
    private final String start;

    /**
     * 结束匹配
     */
    private final String end;

    /**
     * 包含匹配
     */
    private final String contains;

    /**
     * 校验创建服务结果
     *
     * @param cmdResult 指令执行结果
     * @param script    脚本信息
     */
    public static void validationCreateService(String cmdResult, Script script) {
        String result = MessageFormat.format(CREATE_SERVICE_RESULT.getResult(), script.getServiceName());
        if (!result.equals(cmdResult)) {
            throw new RuntimeException("创建服务执行失败: " + cmdResult);
        }
    }

    /**
     * 校验删除服务结果
     *
     * @param cmdResult 指令执行结果
     * @param script    脚本信息
     */
    public static void validationRemoveService(String cmdResult, Script script) {
        String result = MessageFormat.format(REMOVE_SERVICE_RESULT.getResult(), script.getServiceName());
        if (!result.equals(cmdResult)) {
            throw new RuntimeException("删除服务执行失败: " + cmdResult);
        }
    }

    /**
     * 校验更新服务结果
     *
     * @param cmdResult 指令执行结果
     * @param script    脚本信息
     */
    public static void validationUpdateService(String cmdResult, Script script) {
        String start = MessageFormat.format(UPDATE_SERVICE_RESULT.getStart(), script.getServiceName());
        if (!cmdResult.startsWith(start) || !cmdResult.contains(UPDATE_SERVICE_RESULT.getContains())) {
            throw new RuntimeException("更新服务执行失败: " + cmdResult);
        }
    }

}

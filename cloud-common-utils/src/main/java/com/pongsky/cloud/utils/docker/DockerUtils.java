package com.pongsky.cloud.utils.docker;

import com.pongsky.cloud.utils.docker.dto.Script;
import com.pongsky.cloud.utils.docker.enums.DockerExecutionResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

/**
 * docker 工具类
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
public class DockerUtils {

    /**
     * 运行脚本
     *
     * @param cmd 指令
     * @return 执行结果
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static String runScript(String cmd) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
        process.waitFor();
        SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(sis, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.length() > 0 ? result.substring(0, result.lastIndexOf("\n")) : result.toString();
    }

    /**
     * 创建服务
     *
     * @param script 脚本信息
     * @return 执行结果
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static String createService(Script script) throws IOException, InterruptedException {
        for (String baseStartScript : script.getBaseStartScript()) {
            runScript(baseStartScript);
        }
        String cmdResult = runScript(script.getStartScript());
        DockerExecutionResult.validationCreateService(cmdResult, script);
        return cmdResult;
    }

    /**
     * 删除服务
     *
     * @param script 脚本信息
     * @return 执行结果
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static String removeService(Script script) throws IOException, InterruptedException {
        String cmdResult = runScript(script.getDownScript());
        DockerExecutionResult.validationRemoveService(cmdResult, script);
        return cmdResult;
    }

    /**
     * 更新服务
     *
     * @param script 脚本信息
     * @param repository 镜像
     * @param tag        标签
     * @return 执行结果
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static String updateService(Script script, String repository, String tag)
            throws IOException, InterruptedException {
        String cmdResult = runScript(MessageFormat.format(script.getUpdateScript(), repository, tag));
        DockerExecutionResult.validationUpdateService(cmdResult, script);
        return cmdResult;
    }

}

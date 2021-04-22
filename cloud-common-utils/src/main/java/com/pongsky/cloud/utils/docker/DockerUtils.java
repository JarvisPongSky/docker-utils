package com.pongsky.cloud.utils.docker;

import com.pongsky.cloud.utils.docker.dto.Script;
import com.pongsky.cloud.utils.docker.enums.DockerExecutionResult;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Set;

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
    public static Set<String> runScript(String cmd) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
        process.waitFor();
        SequenceInputStream sis = new SequenceInputStream(process.getInputStream(), process.getErrorStream());
        Set<String> result = new LinkedHashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(sis, StandardCharsets.UTF_8))) {
            String line;
            while ((StringUtils.isNotBlank(line = br.readLine()))) {
                result.add(line);
            }
        }
        return result;
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
        String cmdResult = String.join("\n", runScript(script.getStartScript()));
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
        String cmdResult = String.join("\n", runScript(script.getDownScript()));
        DockerExecutionResult.validationRemoveService(cmdResult, script);
        return cmdResult;
    }

    /**
     * Docker Pull CMD
     */
    private static final String PULL_CMD = "docker pull {0}:{1}";

    /**
     * 更新服务
     *
     * @param script     脚本信息
     * @param repository 镜像
     * @param tag        标签
     * @return 执行结果
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static Set<String> updateService(Script script, String repository, String tag)
            throws IOException, InterruptedException {
        DockerExecutionResult.validationPullImage(String.join("\n",
                runScript(MessageFormat.format(PULL_CMD, repository, tag))), repository, tag);
        Set<String> cmdResults = runScript(MessageFormat.format(script.getUpdateScript(), repository, tag));
        String cmdResult = String.join("\n", cmdResults);
        DockerExecutionResult.validationUpdateService(cmdResult, script);
        return cmdResults;
    }

}

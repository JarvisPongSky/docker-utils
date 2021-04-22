package com.pongsky.cloud.controller.api;

import com.pongsky.cloud.response.annotation.ResponseResult;
import com.pongsky.cloud.service.ScriptService;
import com.pongsky.cloud.utils.docker.dto.AliContainerPushInfo;
import com.pongsky.cloud.validator.CreateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;

/**
 * 自行更新
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@ResponseResult
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/autoUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiAutoUpdateController {

    /**
     * 阿里云仓库地址
     */
    private static final String REPOSITORY_ADDRESS = "registry.{0}.aliyuncs.com/{1}/{2}";

    private final ScriptService scriptService;

    /**
     * 镜像自动更新
     *
     * @param userId      用户ID
     * @param serviceName 服务名称
     * @param info        镜像推送信息
     * @return 镜像自动更新
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    @PostMapping("/{userId}/{serviceName}")
    public Set<String> autoUpdate(@PathVariable Long userId,
                                  @PathVariable String serviceName,
                                  @Validated({CreateGroup.class}) @RequestBody AliContainerPushInfo info)
            throws IOException, InterruptedException {
        String repository = MessageFormat.format(REPOSITORY_ADDRESS, info.getRepository().getRegion(),
                info.getRepository().getNamespace(), info.getRepository().getName());
        Long scriptId = scriptService.findIdByUserIdAndServiceName(userId, serviceName);
        return scriptService.autoUpdateService(scriptId, repository, info.getPushData().getTag());
    }

}

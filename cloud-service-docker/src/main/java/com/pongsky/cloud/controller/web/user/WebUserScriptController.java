package com.pongsky.cloud.controller.web.user;

import com.pongsky.cloud.entity.Script;
import com.pongsky.cloud.entity.script.dto.ScriptDto;
import com.pongsky.cloud.entity.script.dto.SearchScriptDto;
import com.pongsky.cloud.entity.script.vo.ScriptVo;
import com.pongsky.cloud.exception.ValidationException;
import com.pongsky.cloud.model.dto.PageQuery;
import com.pongsky.cloud.model.vo.PageResponse;
import com.pongsky.cloud.response.annotation.ResponseResult;
import com.pongsky.cloud.service.ScriptService;
import com.pongsky.cloud.utils.jwt.enums.AuthRole;
import com.pongsky.cloud.validator.CreateGroup;
import com.pongsky.cloud.validator.SearchGroup;
import com.pongsky.cloud.web.request.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 脚本模块
 *
 * @author pengsenhao
 * @create 2021-04-21
 */
@ResponseResult
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + AuthRole.USER_ROLE + "')")
@RequestMapping(value = "/web/user/script", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebUserScriptController {

    private final ScriptService scriptService;

    /**
     * 保存脚本信息
     *
     * @param request   request
     * @param scriptDto 脚本信息
     */
    @PostMapping
    public void save(HttpServletRequest request,
                     @Validated({CreateGroup.class}) @RequestBody ScriptDto scriptDto) {
        if (!scriptDto.getBaseDir().endsWith(Script.BASE_DIR_SUFFIX)) {
            throw new ValidationException("base 文件目录，以 " + Script.BASE_DIR_SUFFIX + " 结尾");
        }
        Long userId = AuthUtils.getAuthUserId(request);
        scriptService.existsByUserIdAndServiceName(userId, scriptDto.getServiceName());
        scriptService.save(userId, scriptDto);
    }

    /**
     * 删除脚本信息
     *
     * @param request  request
     * @param scriptId 脚本ID
     */
    @DeleteMapping("/{scriptId:[0-9]+}")
    public void remove(HttpServletRequest request, @PathVariable Long scriptId) {
        Long userId = AuthUtils.getAuthUserId(request);
        scriptService.existsByUserIdAndScriptId(userId, scriptId);
        scriptService.remove(scriptId);
    }

    /**
     * 查询脚本信息
     *
     * @param request         request
     * @param pageQuery       分页信息
     * @param searchScriptDto 查询信息
     * @return 查询脚本信息
     */
    @GetMapping
    public PageResponse<ScriptVo> query(HttpServletRequest request, PageQuery pageQuery,
                                        @Validated({SearchGroup.class}) SearchScriptDto searchScriptDto) {
        Long userId = AuthUtils.getAuthUserId(request);
        return scriptService.query(userId, pageQuery, searchScriptDto);
    }

    /**
     * 查询脚本信息
     *
     * @param request  request
     * @param scriptId 脚本ID
     * @return 查询脚本信息
     */
    @GetMapping("/{scriptId:[0-9]+}")
    public ScriptVo query(HttpServletRequest request, @PathVariable Long scriptId) {
        Long userId = AuthUtils.getAuthUserId(request);
        scriptService.existsByUserIdAndScriptId(userId, scriptId);
        return scriptService.query(scriptId);
    }

}

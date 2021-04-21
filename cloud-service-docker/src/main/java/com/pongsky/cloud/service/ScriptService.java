package com.pongsky.cloud.service;

import com.pongsky.cloud.entity.Script;
import com.pongsky.cloud.entity.script.dos.ScriptDo;
import com.pongsky.cloud.entity.script.dto.ScriptDto;
import com.pongsky.cloud.entity.script.dto.SearchScriptDto;
import com.pongsky.cloud.entity.script.vo.ScriptVo;
import com.pongsky.cloud.exception.DeleteException;
import com.pongsky.cloud.exception.DoesNotExistException;
import com.pongsky.cloud.exception.InsertException;
import com.pongsky.cloud.exception.ValidationException;
import com.pongsky.cloud.mapper.ScriptMapper;
import com.pongsky.cloud.model.dto.PageQuery;
import com.pongsky.cloud.model.vo.PageResponse;
import com.pongsky.cloud.utils.snowflake.SnowFlakeUtils;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Service
@RequiredArgsConstructor
public class ScriptService {

    private final MapperFacade mapperFacade;
    private final ScriptMapper scriptMapper;
    private final SnowFlakeUtils snowFlakeUtils;

    /**
     * 根据用户ID和服务名称校验是否存在
     *
     * @param userId      用户ID
     * @param serviceName 服务名称
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public void existsByUserIdAndServiceName(Long userId, String serviceName) {
        Integer count = scriptMapper.countByUserIdAndServiceName(userId, serviceName);
        if (count > 0) {
            throw new ValidationException("服务名称 " + serviceName + " 已存在，请更换其他名称重试");
        }
    }

    /**
     * 保存脚本信息
     *
     * @param userId    用户ID
     * @param scriptDto 脚本信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Long userId, ScriptDto scriptDto) {
        Script script = mapperFacade.map(scriptDto, Script.class)
                .setId(snowFlakeUtils.getId())
                .buildScript()
                .setDataVersion(0L)
                .setCreatedAt(LocalDateTime.now())
                .setUserId(userId);
        InsertException.validation("脚本信息保存失败", scriptMapper.save(script));
    }

    /**
     * 根据用户ID和脚本ID校验是否存在
     *
     * @param userId   用户ID
     * @param scriptId 脚本ID
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public void existsByUserIdAndScriptId(Long userId, Long scriptId) {
        Integer count = scriptMapper.countByUserIdAndId(userId, scriptId);
        if (count == 0) {
            throw new ValidationException("脚本信息权限不足");
        }
    }

    /**
     * 删除脚本信息
     *
     * @param scriptId 脚本ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long scriptId) {
        DeleteException.validation("脚本信息删除失败", scriptMapper.removeById(scriptId));
    }

    /**
     * 查询脚本信息
     *
     * @param userId          用户ID
     * @param pageQuery       分页信息
     * @param searchScriptDto 查询信息
     * @return 查询脚本信息
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public PageResponse<ScriptVo> query(Long userId, PageQuery pageQuery, SearchScriptDto searchScriptDto) {
        long count = scriptMapper.pageCountByUserId(userId, searchScriptDto);
        List<ScriptDo> scriptDos = scriptMapper.pageFindByUserId(userId, pageQuery, searchScriptDto);
        return new PageResponse<>(mapperFacade.mapAsList(scriptDos, ScriptVo.class), pageQuery, count);
    }

    /**
     * 查询脚本信息
     *
     * @param scriptId 脚本ID
     * @return 查询脚本信息
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ScriptVo query(Long scriptId) {
        ScriptDo scriptDo = scriptMapper.findById(scriptId)
                .orElseThrow(() -> new DoesNotExistException("脚本信息不存在"));
        return mapperFacade.map(scriptDo, ScriptVo.class);
    }

}

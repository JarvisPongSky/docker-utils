package com.pongsky.cloud.mapper;

import com.pongsky.cloud.entity.Script;
import com.pongsky.cloud.entity.script.dos.ScriptDo;
import com.pongsky.cloud.entity.script.dto.ScriptDto;
import com.pongsky.cloud.entity.script.dto.SearchScriptDto;
import com.pongsky.cloud.model.dto.PageQuery;
import com.pongsky.cloud.model.emums.Active;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Mapper
public interface ScriptMapper {

    /**
     * 保存脚本信息
     *
     * @param script 脚本信息
     * @return 保存脚本信息
     */
    @Insert("insert `script`(id,active,service_name,base_dir,docker_compose_content,is_auto_update,data_version, " +
            "created_at,user_id) " +
            "value(#{data.id},#{data.active},#{data.serviceName},#{data.baseDir},#{data.dockerComposeContent}, " +
            "#{data.isAutoUpdate},#{data.dataVersion},#{data.createdAt},#{data.userId})")
    Integer save(@Param("data") Script script);

    /**
     * 修改脚本信息
     *
     * @param id          脚本ID
     * @param dataVersion 数据版本号
     * @param scriptDto   脚本信息
     * @return 修改脚本信息
     */
    @Update("<script>" +
            "update `script` " +
            "set update_at = now() " +
            "<if test = 'data.active != null' >" +
            ",active = #{data.active,jdbcType=VARCHAR} " +
            "</if>" +
            "<if test = 'data.serviceName != null' >" +
            ",service_name = #{data.serviceName} " +
            "</if>" +
            "<if test = 'data.baseDir != null' >" +
            ",base_dir = #{data.baseDir} " +
            "</if>" +
            "<if test = 'data.dockerComposeContent != null' >" +
            ",docker_compose_content = #{data.dockerComposeContent} " +
            "</if>" +
            "<if test = 'data.isAutoUpdate != null' >" +
            ",is_auto_update = #{data.isAutoUpdate} " +
            "</if>" +
            "where id = #{id} " +
            "and data_version = #{dataVersion} " +
            "</script>")
    Integer modify(@Param("id") Long id,
                   @Param("dataVersion") Long dataVersion,
                   @Param("data") ScriptDto scriptDto);

    /**
     * 根据脚本ID删除数据
     *
     * @param id 脚本ID
     * @return 根据脚本ID删除数据
     */
    @Delete("delete from `script` " +
            "where id = #{id} ")
    Integer removeById(@Param("id") Long id);

    /**
     * 根据脚本ID查询数据
     *
     * @param id 脚本ID
     * @return 根据脚本ID查询数据
     */
    @Select("select s.id,s.active,s.service_name,s.base_dir,s.docker_compose_content,s.is_auto_update,s.data_version " +
            "from `script` s " +
            "where s.id = #{id} ")
    Optional<ScriptDo> findById(@Param("id") Long id);

    /**
     * 根据用户ID和脚本ID查询总数
     *
     * @param userId 用户ID
     * @param id     脚本ID
     * @return 根据用户ID和脚本ID查询总数
     */
    @Select("select count(s.id) " +
            "from `script` s " +
            "where s.user_id = #{userId} " +
            "and s.id = #{id} ")
    Integer countByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);

    /**
     * 根据用户ID和环境和服务名称查询总数
     *
     * @param userId      用户ID
     * @param id          脚本ID
     * @param active      环境
     * @param serviceName 服务名称
     * @return 根据用户ID和环境和服务名称查询总数
     */
    @Select("<script>" +
            "select count(s.id) " +
            "from `script` s " +
            "where s.user_id = #{userId} " +
            "<if test = 'id != null' >" +
            "and s.id != #{id} " +
            "</if>" +
            "and s.active = #{active,jdbcType=VARCHAR} " +
            "and s.service_name = #{serviceName} " +
            "</script>")
    Integer countByNotIdAndUserIdAndActiveAndServiceName(@Param("id") Long id,
                                                         @Param("userId") Long userId,
                                                         @Param("active") Active active,
                                                         @Param("serviceName") String serviceName);

    /**
     * 根据用户ID查询脚本信息
     *
     * @param userid          用户ID
     * @param pageQuery       分页信息
     * @param searchScriptDto 查询信息
     * @return 根据用户ID查询脚本信息
     */
    @Select("<script>" +
            "select s.id,s.active,s.service_name,s.base_dir,s.docker_compose_content,s.is_auto_update,s.data_version " +
            "from `script` s " +
            "where s.user_id = #{userId} " +
            "<if test = 'search.serviceName != null' >" +
            "and s.service_name = #{search.serviceName} " +
            "</if>" +
            "order by s.created_at desc " +
            "limit #{page.offset},#{page.pageSize} " +
            "</script>")
    List<ScriptDo> pageFindByUserId(@Param("userId") Long userid,
                                    @Param("page") PageQuery pageQuery,
                                    @Param("search") SearchScriptDto searchScriptDto);

    /**
     * 根据用户ID查询脚本信息总数
     *
     * @param userid          用户ID
     * @param searchScriptDto 查询信息
     * @return 根据用户ID查询脚本信息总数
     */
    @Select("<script>" +
            "select count(s.id) " +
            "from `script` s " +
            "where s.user_id = #{userId} " +
            "<if test = 'search.serviceName != null' >" +
            "and s.service_name = #{search.serviceName} " +
            "</if>" +
            "</script>")
    Integer pageCountByUserId(@Param("userId") Long userid,
                              @Param("search") SearchScriptDto searchScriptDto);

}

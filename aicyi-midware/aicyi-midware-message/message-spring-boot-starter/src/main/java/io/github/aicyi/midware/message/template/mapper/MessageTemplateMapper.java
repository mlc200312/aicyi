package io.github.aicyi.midware.message.template.mapper;

import java.util.List;

import io.github.aicyi.midware.message.core.template.MessageTemplate;
import io.github.aicyi.midware.message.template.model.MessageTemplateExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface MessageTemplateMapper {
    @SelectProvider(type=MessageTemplateSqlProvider.class, method="countByExample")
    long countByExample(MessageTemplateExample example);

    @DeleteProvider(type=MessageTemplateSqlProvider.class, method="deleteByExample")
    int deleteByExample(MessageTemplateExample example);

    @Delete({
        "delete from message_template",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into message_template (id, template_code, ",
        "template_name, message_type, ",
        "format, subject, ",
        "signature, variables, ",
        "enabled, remark, ",
        "deleted, version, ",
        "create_time, update_time, ",
        "content)",
        "values (#{id,jdbcType=BIGINT}, #{templateCode,jdbcType=VARCHAR}, ",
        "#{templateName,jdbcType=VARCHAR}, #{messageType,jdbcType=VARCHAR}, ",
        "#{format,jdbcType=VARCHAR}, #{subject,jdbcType=VARCHAR}, ",
        "#{signature,jdbcType=VARCHAR}, #{variables,jdbcType=VARCHAR}, ",
        "#{enabled,jdbcType=TINYINT}, #{remark,jdbcType=VARCHAR}, ",
        "#{deleted,jdbcType=TINYINT}, #{version,jdbcType=INTEGER}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
        "#{content,jdbcType=LONGVARCHAR})"
    })
    int insert(MessageTemplate record);

    @InsertProvider(type=MessageTemplateSqlProvider.class, method="insertSelective")
    int insertSelective(MessageTemplate record);

    @SelectProvider(type=MessageTemplateSqlProvider.class, method="selectByExampleWithBLOBs")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="template_code", property="templateCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="template_name", property="templateName", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_type", property="messageType", jdbcType=JdbcType.VARCHAR),
        @Result(column="format", property="format", jdbcType=JdbcType.VARCHAR),
        @Result(column="subject", property="subject", jdbcType=JdbcType.VARCHAR),
        @Result(column="signature", property="signature", jdbcType=JdbcType.VARCHAR),
        @Result(column="variables", property="variables", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="content", property="content", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<MessageTemplate> selectByExampleWithBLOBs(MessageTemplateExample example);

    @SelectProvider(type=MessageTemplateSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="template_code", property="templateCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="template_name", property="templateName", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_type", property="messageType", jdbcType=JdbcType.VARCHAR),
        @Result(column="format", property="format", jdbcType=JdbcType.VARCHAR),
        @Result(column="subject", property="subject", jdbcType=JdbcType.VARCHAR),
        @Result(column="signature", property="signature", jdbcType=JdbcType.VARCHAR),
        @Result(column="variables", property="variables", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<MessageTemplate> selectByExample(MessageTemplateExample example);

    @Select({
        "select",
        "id, template_code, template_name, message_type, format, subject, signature, ",
        "variables, enabled, remark, deleted, version, create_time, update_time, content",
        "from message_template",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="template_code", property="templateCode", jdbcType=JdbcType.VARCHAR),
        @Result(column="template_name", property="templateName", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_type", property="messageType", jdbcType=JdbcType.VARCHAR),
        @Result(column="format", property="format", jdbcType=JdbcType.VARCHAR),
        @Result(column="subject", property="subject", jdbcType=JdbcType.VARCHAR),
        @Result(column="signature", property="signature", jdbcType=JdbcType.VARCHAR),
        @Result(column="variables", property="variables", jdbcType=JdbcType.VARCHAR),
        @Result(column="enabled", property="enabled", jdbcType=JdbcType.TINYINT),
        @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR),
        @Result(column="deleted", property="deleted", jdbcType=JdbcType.TINYINT),
        @Result(column="version", property="version", jdbcType=JdbcType.INTEGER),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="content", property="content", jdbcType=JdbcType.LONGVARCHAR)
    })
    MessageTemplate selectByPrimaryKey(Long id);

    @UpdateProvider(type=MessageTemplateSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") MessageTemplate record, @Param("example") MessageTemplateExample example);

    @UpdateProvider(type=MessageTemplateSqlProvider.class, method="updateByExampleWithBLOBs")
    int updateByExampleWithBLOBs(@Param("record") MessageTemplate record, @Param("example") MessageTemplateExample example);

    @UpdateProvider(type=MessageTemplateSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") MessageTemplate record, @Param("example") MessageTemplateExample example);

    @UpdateProvider(type=MessageTemplateSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(MessageTemplate record);

    @Update({
        "update message_template",
        "set template_code = #{templateCode,jdbcType=VARCHAR},",
          "template_name = #{templateName,jdbcType=VARCHAR},",
          "message_type = #{messageType,jdbcType=VARCHAR},",
          "format = #{format,jdbcType=VARCHAR},",
          "subject = #{subject,jdbcType=VARCHAR},",
          "signature = #{signature,jdbcType=VARCHAR},",
          "variables = #{variables,jdbcType=VARCHAR},",
          "enabled = #{enabled,jdbcType=TINYINT},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "deleted = #{deleted,jdbcType=TINYINT},",
          "version = #{version,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "content = #{content,jdbcType=LONGVARCHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKeyWithBLOBs(MessageTemplate record);

    @Update({
        "update message_template",
        "set template_code = #{templateCode,jdbcType=VARCHAR},",
          "template_name = #{templateName,jdbcType=VARCHAR},",
          "message_type = #{messageType,jdbcType=VARCHAR},",
          "format = #{format,jdbcType=VARCHAR},",
          "subject = #{subject,jdbcType=VARCHAR},",
          "signature = #{signature,jdbcType=VARCHAR},",
          "variables = #{variables,jdbcType=VARCHAR},",
          "enabled = #{enabled,jdbcType=TINYINT},",
          "remark = #{remark,jdbcType=VARCHAR},",
          "deleted = #{deleted,jdbcType=TINYINT},",
          "version = #{version,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(MessageTemplate record);
}
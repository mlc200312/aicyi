package io.github.aicyi.midware.message.template.mapper;

import io.github.aicyi.midware.message.core.model.MessageTemplate;
import io.github.aicyi.midware.message.template.model.MessageTemplateExample;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

public class MessageTemplateSqlProvider {

    public String countByExample(MessageTemplateExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("message_template");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String deleteByExample(MessageTemplateExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("message_template");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String insertSelective(MessageTemplate record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("message_template");
        
        if (record.getId() != null) {
            sql.VALUES("id", "#{id,jdbcType=BIGINT}");
        }
        
        if (record.getTemplateCode() != null) {
            sql.VALUES("template_code", "#{templateCode,jdbcType=VARCHAR}");
        }
        
        if (record.getTemplateName() != null) {
            sql.VALUES("template_name", "#{templateName,jdbcType=VARCHAR}");
        }
        
        if (record.getMessageType() != null) {
            sql.VALUES("message_type", "#{messageType,jdbcType=VARCHAR}");
        }
        
        if (record.getFormat() != null) {
            sql.VALUES("format", "#{format,jdbcType=VARCHAR}");
        }
        
        if (record.getEngineType() != null) {
            sql.VALUES("engine_type", "#{engineType,jdbcType=VARCHAR}");
        }
        
        if (record.getSubject() != null) {
            sql.VALUES("subject", "#{subject,jdbcType=VARCHAR}");
        }
        
        if (record.getSignature() != null) {
            sql.VALUES("signature", "#{signature,jdbcType=VARCHAR}");
        }
        
        if (record.getVariables() != null) {
            sql.VALUES("variables", "#{variables,jdbcType=VARCHAR}");
        }
        
        if (record.getEnabled() != null) {
            sql.VALUES("enabled", "#{enabled,jdbcType=TINYINT}");
        }
        
        if (record.getRemark() != null) {
            sql.VALUES("remark", "#{remark,jdbcType=VARCHAR}");
        }
        
        if (record.getDeleted() != null) {
            sql.VALUES("deleted", "#{deleted,jdbcType=TINYINT}");
        }
        
        if (record.getVersion() != null) {
            sql.VALUES("version", "#{version,jdbcType=INTEGER}");
        }
        
        if (record.getCreateTime() != null) {
            sql.VALUES("create_time", "#{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.VALUES("update_time", "#{updateTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getContent() != null) {
            sql.VALUES("content", "#{content,jdbcType=LONGVARCHAR}");
        }
        
        return sql.toString();
    }

    public String selectByExampleWithBLOBs(MessageTemplateExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("id");
        } else {
            sql.SELECT("id");
        }
        sql.SELECT("template_code");
        sql.SELECT("template_name");
        sql.SELECT("message_type");
        sql.SELECT("format");
        sql.SELECT("engine_type");
        sql.SELECT("subject");
        sql.SELECT("signature");
        sql.SELECT("variables");
        sql.SELECT("enabled");
        sql.SELECT("remark");
        sql.SELECT("deleted");
        sql.SELECT("version");
        sql.SELECT("create_time");
        sql.SELECT("update_time");
        sql.SELECT("content");
        sql.FROM("message_template");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String selectByExample(MessageTemplateExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("id");
        } else {
            sql.SELECT("id");
        }
        sql.SELECT("template_code");
        sql.SELECT("template_name");
        sql.SELECT("message_type");
        sql.SELECT("format");
        sql.SELECT("engine_type");
        sql.SELECT("subject");
        sql.SELECT("signature");
        sql.SELECT("variables");
        sql.SELECT("enabled");
        sql.SELECT("remark");
        sql.SELECT("deleted");
        sql.SELECT("version");
        sql.SELECT("create_time");
        sql.SELECT("update_time");
        sql.FROM("message_template");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String updateByExampleSelective(Map<String, Object> parameter) {
        MessageTemplate record = (MessageTemplate) parameter.get("record");
        MessageTemplateExample example = (MessageTemplateExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("message_template");
        
        if (record.getId() != null) {
            sql.SET("id = #{record.id,jdbcType=BIGINT}");
        }
        
        if (record.getTemplateCode() != null) {
            sql.SET("template_code = #{record.templateCode,jdbcType=VARCHAR}");
        }
        
        if (record.getTemplateName() != null) {
            sql.SET("template_name = #{record.templateName,jdbcType=VARCHAR}");
        }
        
        if (record.getMessageType() != null) {
            sql.SET("message_type = #{record.messageType,jdbcType=VARCHAR}");
        }
        
        if (record.getFormat() != null) {
            sql.SET("format = #{record.format,jdbcType=VARCHAR}");
        }
        
        if (record.getEngineType() != null) {
            sql.SET("engine_type = #{record.engineType,jdbcType=VARCHAR}");
        }
        
        if (record.getSubject() != null) {
            sql.SET("subject = #{record.subject,jdbcType=VARCHAR}");
        }
        
        if (record.getSignature() != null) {
            sql.SET("signature = #{record.signature,jdbcType=VARCHAR}");
        }
        
        if (record.getVariables() != null) {
            sql.SET("variables = #{record.variables,jdbcType=VARCHAR}");
        }
        
        if (record.getEnabled() != null) {
            sql.SET("enabled = #{record.enabled,jdbcType=TINYINT}");
        }
        
        if (record.getRemark() != null) {
            sql.SET("remark = #{record.remark,jdbcType=VARCHAR}");
        }
        
        if (record.getDeleted() != null) {
            sql.SET("deleted = #{record.deleted,jdbcType=TINYINT}");
        }
        
        if (record.getVersion() != null) {
            sql.SET("version = #{record.version,jdbcType=INTEGER}");
        }
        
        if (record.getCreateTime() != null) {
            sql.SET("create_time = #{record.createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.SET("update_time = #{record.updateTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getContent() != null) {
            sql.SET("content = #{record.content,jdbcType=LONGVARCHAR}");
        }
        
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByExampleWithBLOBs(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("message_template");
        
        sql.SET("id = #{record.id,jdbcType=BIGINT}");
        sql.SET("template_code = #{record.templateCode,jdbcType=VARCHAR}");
        sql.SET("template_name = #{record.templateName,jdbcType=VARCHAR}");
        sql.SET("message_type = #{record.messageType,jdbcType=VARCHAR}");
        sql.SET("format = #{record.format,jdbcType=VARCHAR}");
        sql.SET("engine_type = #{record.engineType,jdbcType=VARCHAR}");
        sql.SET("subject = #{record.subject,jdbcType=VARCHAR}");
        sql.SET("signature = #{record.signature,jdbcType=VARCHAR}");
        sql.SET("variables = #{record.variables,jdbcType=VARCHAR}");
        sql.SET("enabled = #{record.enabled,jdbcType=TINYINT}");
        sql.SET("remark = #{record.remark,jdbcType=VARCHAR}");
        sql.SET("deleted = #{record.deleted,jdbcType=TINYINT}");
        sql.SET("version = #{record.version,jdbcType=INTEGER}");
        sql.SET("create_time = #{record.createTime,jdbcType=TIMESTAMP}");
        sql.SET("update_time = #{record.updateTime,jdbcType=TIMESTAMP}");
        sql.SET("content = #{record.content,jdbcType=LONGVARCHAR}");
        
        MessageTemplateExample example = (MessageTemplateExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByExample(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("message_template");
        
        sql.SET("id = #{record.id,jdbcType=BIGINT}");
        sql.SET("template_code = #{record.templateCode,jdbcType=VARCHAR}");
        sql.SET("template_name = #{record.templateName,jdbcType=VARCHAR}");
        sql.SET("message_type = #{record.messageType,jdbcType=VARCHAR}");
        sql.SET("format = #{record.format,jdbcType=VARCHAR}");
        sql.SET("engine_type = #{record.engineType,jdbcType=VARCHAR}");
        sql.SET("subject = #{record.subject,jdbcType=VARCHAR}");
        sql.SET("signature = #{record.signature,jdbcType=VARCHAR}");
        sql.SET("variables = #{record.variables,jdbcType=VARCHAR}");
        sql.SET("enabled = #{record.enabled,jdbcType=TINYINT}");
        sql.SET("remark = #{record.remark,jdbcType=VARCHAR}");
        sql.SET("deleted = #{record.deleted,jdbcType=TINYINT}");
        sql.SET("version = #{record.version,jdbcType=INTEGER}");
        sql.SET("create_time = #{record.createTime,jdbcType=TIMESTAMP}");
        sql.SET("update_time = #{record.updateTime,jdbcType=TIMESTAMP}");
        
        MessageTemplateExample example = (MessageTemplateExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(MessageTemplate record) {
        SQL sql = new SQL();
        sql.UPDATE("message_template");
        
        if (record.getTemplateCode() != null) {
            sql.SET("template_code = #{templateCode,jdbcType=VARCHAR}");
        }
        
        if (record.getTemplateName() != null) {
            sql.SET("template_name = #{templateName,jdbcType=VARCHAR}");
        }
        
        if (record.getMessageType() != null) {
            sql.SET("message_type = #{messageType,jdbcType=VARCHAR}");
        }
        
        if (record.getFormat() != null) {
            sql.SET("format = #{format,jdbcType=VARCHAR}");
        }
        
        if (record.getEngineType() != null) {
            sql.SET("engine_type = #{engineType,jdbcType=VARCHAR}");
        }
        
        if (record.getSubject() != null) {
            sql.SET("subject = #{subject,jdbcType=VARCHAR}");
        }
        
        if (record.getSignature() != null) {
            sql.SET("signature = #{signature,jdbcType=VARCHAR}");
        }
        
        if (record.getVariables() != null) {
            sql.SET("variables = #{variables,jdbcType=VARCHAR}");
        }
        
        if (record.getEnabled() != null) {
            sql.SET("enabled = #{enabled,jdbcType=TINYINT}");
        }
        
        if (record.getRemark() != null) {
            sql.SET("remark = #{remark,jdbcType=VARCHAR}");
        }
        
        if (record.getDeleted() != null) {
            sql.SET("deleted = #{deleted,jdbcType=TINYINT}");
        }
        
        if (record.getVersion() != null) {
            sql.SET("version = #{version,jdbcType=INTEGER}");
        }
        
        if (record.getCreateTime() != null) {
            sql.SET("create_time = #{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.SET("update_time = #{updateTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getContent() != null) {
            sql.SET("content = #{content,jdbcType=LONGVARCHAR}");
        }
        
        sql.WHERE("id = #{id,jdbcType=BIGINT}");
        
        return sql.toString();
    }

    protected void applyWhere(SQL sql, MessageTemplateExample example, boolean includeExamplePhrase) {
        if (example == null) {
            return;
        }
        
        String parmPhrase1;
        String parmPhrase1_th;
        String parmPhrase2;
        String parmPhrase2_th;
        String parmPhrase3;
        String parmPhrase3_th;
        if (includeExamplePhrase) {
            parmPhrase1 = "%s #{example.oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{example.oredCriteria[%d].allCriteria[%d].value} and #{example.oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{example.oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        } else {
            parmPhrase1 = "%s #{oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{oredCriteria[%d].allCriteria[%d].value} and #{oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        }
        
        StringBuilder sb = new StringBuilder();
        List<MessageTemplateExample.Criteria> oredCriteria = example.getOredCriteria();
        boolean firstCriteria = true;
        for (int i = 0; i < oredCriteria.size(); i++) {
            MessageTemplateExample.Criteria criteria = oredCriteria.get(i);
            if (criteria.isValid()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sb.append(" or ");
                }
                
                sb.append('(');
                List<MessageTemplateExample.Criterion> criterions = criteria.getAllCriteria();
                boolean firstCriterion = true;
                for (int j = 0; j < criterions.size(); j++) {
                    MessageTemplateExample.Criterion criterion = criterions.get(j);
                    if (firstCriterion) {
                        firstCriterion = false;
                    } else {
                        sb.append(" and ");
                    }
                    
                    if (criterion.isNoValue()) {
                        sb.append(criterion.getCondition());
                    } else if (criterion.isSingleValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase1, criterion.getCondition(), i, j));
                        } else {
                            sb.append(String.format(parmPhrase1_th, criterion.getCondition(), i, j,criterion.getTypeHandler()));
                        }
                    } else if (criterion.isBetweenValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase2, criterion.getCondition(), i, j, i, j));
                        } else {
                            sb.append(String.format(parmPhrase2_th, criterion.getCondition(), i, j, criterion.getTypeHandler(), i, j, criterion.getTypeHandler()));
                        }
                    } else if (criterion.isListValue()) {
                        sb.append(criterion.getCondition());
                        sb.append(" (");
                        List<?> listItems = (List<?>) criterion.getValue();
                        boolean comma = false;
                        for (int k = 0; k < listItems.size(); k++) {
                            if (comma) {
                                sb.append(", ");
                            } else {
                                comma = true;
                            }
                            if (criterion.getTypeHandler() == null) {
                                sb.append(String.format(parmPhrase3, i, j, k));
                            } else {
                                sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getTypeHandler()));
                            }
                        }
                        sb.append(')');
                    }
                }
                sb.append(')');
            }
        }
        
        if (sb.length() > 0) {
            sql.WHERE(sb.toString());
        }
    }
}
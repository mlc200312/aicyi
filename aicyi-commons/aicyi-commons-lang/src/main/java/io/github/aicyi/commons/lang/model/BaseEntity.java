package io.github.aicyi.commons.lang.model;

import io.github.aicyi.commons.lang.DoBean;

/**
 * @author Mr.Min
 * @description DO 数据实体基类（标记型）：仅约定 DO 分层归属，不内置公共字段；
 * 主键、乐观锁与审计字段由代码生成器在子类中生成，由 BaseEntityUtils 等工具按约定字段名反射填充
 * @date 2025/9/29
 **/
public abstract class BaseEntity extends BaseBean implements DoBean {
}

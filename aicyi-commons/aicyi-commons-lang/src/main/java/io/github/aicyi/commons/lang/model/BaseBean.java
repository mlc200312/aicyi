package io.github.aicyi.commons.lang.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Bean 基类
 *
 * <p>基于反射提供 equals/hashCode/toString 的默认实现，子类无需重复编写。
 *
 * <p><b>注意：</b>反射 toString 会输出全部字段，若子类包含敏感字段
 * （如密码、token、手机号等），必须覆写 toString 做脱敏处理。
 * 反射实现性能有限，高频热点路径上的大对象建议自行覆写。
 *
 * <p><b>继承约束：</b>反射实现会比较全部非静态、非瞬态字段（含父类字段）。
 * 需要身份相等语义的类（如作为 Set/Map 键、按引用去重的实体）、
 * 字段频繁变更或含深层嵌套集合的类，不应继承本类，自行实现 equals/hashCode。
 *
 * @author Mr.Min
 * @date 2025/8/5
 **/
public class BaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

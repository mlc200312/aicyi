package io.github.aicyi.midware.mybatisflex.typehandler;

import io.github.aicyi.commons.lang.EnumType;
import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用枚举 TypeHandler
 *
 * @param <E>
 */
public class GenericEnumTypeHandler<E extends Enum<E> & EnumType> extends BaseTypeHandler<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericEnumTypeHandler.class);

    private Class<E> type;
    private Map<Integer, E> enumMap = new HashMap<>();

    public GenericEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        E[] enums = type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }

        for (E e : enums) {
            enumMap.put(e.getCode(), e);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setInt(i, parameter.getCode());
        } else {
            ps.setObject(i, parameter.getCode(), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        if (value == null) {
            return null;
        }
        return convert(value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString()));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        return rs.wasNull() ? null : enumMap.get(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        return cs.wasNull() ? null : enumMap.get(value);
    }

    private E convert(int value) {
        E result = enumMap.get(value);
        if (result == null) {
            LOGGER.warn("Unknown value {} for enum {}", value, type.getSimpleName());
            return null;
        }
        return result;
    }
}
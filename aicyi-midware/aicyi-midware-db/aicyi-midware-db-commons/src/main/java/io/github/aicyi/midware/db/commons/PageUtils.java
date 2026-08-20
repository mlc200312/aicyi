package io.github.aicyi.midware.db.commons;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import io.github.aicyi.commons.lang.exception.BusinessException;
import io.github.aicyi.commons.lang.model.PageParam;
import io.github.aicyi.commons.lang.CommonResultCode;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Mr.Min
 * @description 分页工具类
 * @date 09:52
 **/
public class PageUtils {

    /**
     * 排序字段白名单：PageHelper 会将 orderBy 直接拼接进 SQL，当前版本无内置防注入校验，
     * 用户可控的排序字段必须先过白名单，防止 ORDER BY 注入
     */
    private static final Pattern SAFE_ORDER_BY_COLUMN = Pattern.compile("[A-Za-z0-9_]+");

    public static Pageable createPageable(int page, int size, Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }

    public static Pageable createPageable(int page, int size) {
        return createPageable(page, size, Sort.unsorted());
    }

    public static Pageable first(int size) {
        return createPageable(1, size);
    }

    public static <T> Page<T> getPage(Pageable pageable, ISelect select, boolean count) {
        int pageNum = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        com.github.pagehelper.Page<T> page = PageHelper.startPage(pageNum, pageSize, count);
        String orderBy = String.join(",",
                pageable.getSort()
                        .stream()
                        .map(order -> checkOrderByColumn(order.getProperty()) + " " + order.getDirection().name())
                        .toArray(String[]::new));
        page.setOrderBy(orderBy);
        com.github.pagehelper.Page<T> selectPage = page.doSelectPage(select);
        return new PageImpl<>(selectPage.getResult(), pageable, selectPage.getTotal());
    }

    /**
     * 校验排序字段合法性，非法时抛 PARAM_ERROR 业务异常，避免拼入 SQL 构成注入
     */
    private static String checkOrderByColumn(String property) {
        if (property == null || !SAFE_ORDER_BY_COLUMN.matcher(property).matches()) {
            throw new BusinessException(CommonResultCode.PARAM_ERROR, "illegal order by column: " + property);
        }
        return property;
    }

    public static <T> Page<T> getPage(PageParam pageParam, ISelect select) {
        Pageable pageable = createPageable(pageParam.getPage(), pageParam.getSize());
        return getPage(pageable, select, true);
    }

    public static <T> List<T> getList(PageParam pageParam, ISelect select) {
        Pageable pageable = createPageable(pageParam.getPage(), pageParam.getSize());
        Page<T> page = getPage(pageable, select, false);
        return page.getContent();
    }
}

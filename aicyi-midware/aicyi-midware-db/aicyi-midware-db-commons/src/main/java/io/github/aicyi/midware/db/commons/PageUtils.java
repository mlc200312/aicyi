package io.github.aicyi.midware.db.commons;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import org.springframework.data.domain.*;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 09:52
 **/
public class PageUtils {

    public static Pageable createPageable(int page, int size, Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }

    public static Pageable createPageable(int page, int size) {
        return createPageable(page - 1, size, Sort.unsorted());
    }

    public static Pageable first(int size) {
        return createPageable(0, size);
    }

    public static <T> Page<T> createPage(Pageable pageable, ISelect select, boolean count) {
        com.github.pagehelper.Page<T> page = PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize(), count);
        String[] array = pageable.getSort().stream().map(order -> order.getProperty() + " " + order.getDirection().name()).toArray(String[]::new);
        page.setOrderBy(String.join(",", array));
        com.github.pagehelper.Page<T> selectPage = page.doSelectPage(select);
        return new PageImpl<>(selectPage.getResult(), pageable, selectPage.getTotal());
    }

    public static <T> Page<T> createPage(Pageable pageable, ISelect select) {
        return createPage(pageable, select, true);
    }
}

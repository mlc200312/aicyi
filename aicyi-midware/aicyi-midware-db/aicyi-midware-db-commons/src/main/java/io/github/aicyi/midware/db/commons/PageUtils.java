package io.github.aicyi.midware.db.commons;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import io.github.aicyi.commons.lang.PageParam;
import org.springframework.data.domain.*;

import java.util.List;

/**
 * @author Mr.Min
 * @description 分页工具类
 * @date 09:52
 **/
public class PageUtils {

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
                        .map(order -> order.getProperty() + " " + order.getDirection().name())
                        .toArray(String[]::new));
        page.setOrderBy(orderBy);
        com.github.pagehelper.Page<T> selectPage = page.doSelectPage(select);
        return new PageImpl<>(selectPage.getResult(), pageable, selectPage.getTotal());
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

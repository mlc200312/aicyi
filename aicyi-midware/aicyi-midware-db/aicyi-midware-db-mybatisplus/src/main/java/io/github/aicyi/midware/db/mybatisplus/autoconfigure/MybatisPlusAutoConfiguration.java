package io.github.aicyi.midware.db.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.aicyi.midware.db.mybatisplus.MybatisPlusMetaObjectHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * @author Mr.Min
 * @description MyBatis-Plus 自动配置
 * <p>
 * 提供开箱即用的 MP 基础能力：分页拦截器（MySQL）、乐观锁拦截器、公共字段自动填充。
 * 所有 Bean 均以 {@code @ConditionalOnMissingBean} 声明，业务可整体替换或按需自定义。
 * 开关：{@code aicyi.mybatis-plus.enabled}（缺省开启）。
 * 分页上限默认 500，与 {@code PageParam.MAX_SIZE} 对齐，防止深分页拖垮存储层。
 * @date 2026/8/20
 **/
@AutoConfiguration
@ConditionalOnClass(MybatisPlusInterceptor.class)
@ConditionalOnProperty(
        prefix = "aicyi.mybatis-plus",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class MybatisPlusAutoConfiguration {

    /**
     * 分页上限，与 aicyi-commons-lang PageParam.MAX_SIZE 保持一致
     */
    private static final long MAX_PAGE_SIZE = 500L;

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(MAX_PAGE_SIZE);
        interceptor.addInnerInterceptor(pagination);

        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler mybatisPlusMetaObjectHandler() {
        return new MybatisPlusMetaObjectHandler();
    }
}

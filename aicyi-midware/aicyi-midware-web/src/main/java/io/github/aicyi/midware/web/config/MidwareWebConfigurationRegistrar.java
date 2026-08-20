package io.github.aicyi.midware.web.config;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.midware.web.annotation.EnableMidwareWeb;
import io.github.aicyi.midware.web.auth.AuthInterceptor;
import io.github.aicyi.midware.web.auth.AuthenticationTokenServiceRegistrar;
import io.github.aicyi.midware.web.exception.GlobalExceptionHandler;
import io.github.aicyi.midware.web.filter.CachingRequestBodyFilter;
import io.github.aicyi.midware.web.filter.TraceIdFilter;
import io.github.aicyi.midware.web.log.RequestLogInterceptor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.Map;

/**
 * @author Mr.Min
 * @description {@link EnableMidwareWeb} 装配注册器
 * <p>
 * 读取注解属性并按需注册 Bean 定义，取代在各配置类中以 {@code @Bean} 返回 null 的条件装配方式：
 * <ul>
 *     <li>{@link MidwareWebMvcConfiguration}：拦截器聚合注册（始终注册，按拦截器 Bean 是否存在决定注册哪些拦截器）</li>
 *     <li>{@link TraceIdFilter}：链路追踪过滤器（最先执行，保证后续全部日志携带 traceId）
 *     <li>{@link CachingRequestBodyFilter}：请求体缓存过滤器</li>
 *     <li>{@link RequestLogInterceptor}：请求信息日志拦截器，由 {@code enableRequestLog} 与
 *     <li>{@link AuthInterceptor}、{@link AuthenticationTokenServiceRegistrar}：鉴权能力，由 {@code enableAuth} 控制</li>
 * </ul>
 * <p>
 * 重复标注 {@link EnableMidwareWeb} 时仅首个声明生效，后续声明输出告警日志并跳过
 * @date 2026/8/14
 **/
public class MidwareWebConfigurationRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MidwareWebConfigurationRegistrar.class);

    /**
     * 配置项：请求信息日志拦截器是否启用
     */
    private static final String AICYI_WEB_REQUEST_LOG_ENABLED = "aicyi.web.request-log.enabled";

    /**
     * 配置项：链路追踪过滤器是否启用
     */
    private static final String AICYI_WEB_TRACE_ID_ENABLED = "aicyi.web.trace-id.enabled";

    /**
     * 装配标记 Bean 定义名，用于检测重复标注
     */
    private static final String ENABLED_MARKER_BEAN_NAME = MidwareWebConfigurationRegistrar.class.getName() + ".enabledMarker";

    /**
     * 请求体缓存过滤器执行顺序
     */
    private static final int CACHING_FILTER_ORDER = 1;

    /**
     * 链路追踪过滤器执行顺序：最先执行，保证后续过滤器/拦截器/业务日志均携带 traceId
     */
    private static final int TRACE_FILTER_ORDER = 0;

    private Environment environment;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {

        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableMidwareWeb.class.getName());
        if (attributes == null) {
            return;
        }

        // 重复标注检测：仅首个声明生效，避免重复注册与属性覆盖
        if (registry.containsBeanDefinition(ENABLED_MARKER_BEAN_NAME)) {
            LOGGER.warn("Duplicate @EnableRestApi detected on '{}', duplicated declaration is ignored. Keep the annotation in only one place",
                    importingClassMetadata.getClassName());
            return;
        }
        registry.registerBeanDefinition(ENABLED_MARKER_BEAN_NAME,
                BeanDefinitionBuilder.genericBeanDefinition(Object.class)
                        .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                        .getBeanDefinition());

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributes);
        boolean enableAuth = annotationAttributes.getBoolean("enableAuth");
        boolean enableRequestLog = annotationAttributes.getBoolean("enableRequestLog");
        String[] excludePathPatterns = annotationAttributes.getStringArray("excludePathPatterns");

        // Web MVC 配置：按拦截器 Bean 是否存在决定注册内容（始终注册）
        BeanDefinitionBuilder mvcConfigurationBuilder = BeanDefinitionBuilder.genericBeanDefinition(MidwareWebMvcConfiguration.class)
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        // 第三个构造参数为鉴权放行路径，其余参数由容器按构造器自动装配
        mvcConfigurationBuilder.getBeanDefinition().getConstructorArgumentValues()
                .addIndexedArgumentValue(2, excludePathPatterns);
        registerBeanDefinition(registry, "restApiWebMvcConfiguration", mvcConfigurationBuilder);

        // 链路追踪过滤器（最先执行）
        if (isPropertyEnabled(AICYI_WEB_TRACE_ID_ENABLED)) {
            registerTraceIdFilter(registry);
        }

        // 请求体缓存过滤器（始终开启）
        registerCachingRequestBodyFilter(registry);

        // 请求信息日志拦截器
        if (enableRequestLog && isPropertyEnabled(AICYI_WEB_REQUEST_LOG_ENABLED)) {
            registerBeanDefinition(registry, "requestLogInterceptor",
                    BeanDefinitionBuilder.genericBeanDefinition(RequestLogInterceptor.class));
        }

        // 鉴权拦截器与 Token 服务注册器
        if (enableAuth) {
            registerBeanDefinition(registry, "authInterceptor",
                    BeanDefinitionBuilder.genericBeanDefinition(AuthInterceptor.class)
                            .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR));
            registerBeanDefinition(registry, "authenticationTokenServiceRegistrar",
                    BeanDefinitionBuilder.genericBeanDefinition(AuthenticationTokenServiceRegistrar.class)
                            .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR));
        }

        // 全局异常处理器
        registerBeanDefinition(registry, "globalExceptionHandler",
                BeanDefinitionBuilder.genericBeanDefinition(GlobalExceptionHandler.class));
    }

    /**
     * 注册链路追踪过滤器
     */
    private void registerTraceIdFilter(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FilterRegistrationBean.class)
                .addPropertyValue("filter", BeanDefinitionBuilder.genericBeanDefinition(TraceIdFilter.class).getBeanDefinition())
                .addPropertyValue("urlPatterns", Collections.singletonList("/*"))
                .addPropertyValue("order", TRACE_FILTER_ORDER);
        registerBeanDefinition(registry, "traceIdFilter", builder);
    }

    /**
     * 注册请求体缓存过滤器
     */
    private void registerCachingRequestBodyFilter(BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FilterRegistrationBean.class)
                .addPropertyValue("filter", BeanDefinitionBuilder.genericBeanDefinition(CachingRequestBodyFilter.class).getBeanDefinition())
                .addPropertyValue("urlPatterns", Collections.singletonList("/*"))
                .addPropertyValue("order", CACHING_FILTER_ORDER);
        registerBeanDefinition(registry, "cachingRequestBodyFilter", builder);
    }

    private static void registerBeanDefinition(BeanDefinitionRegistry registry, String beanName, BeanDefinitionBuilder builder) {
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    /**
     * 判断开关配置项是否启用，未配置时缺省开启
     */
    private boolean isPropertyEnabled(String propertyName) {
        Boolean enabled = environment.getProperty(propertyName, Boolean.class);
        return enabled == null || enabled;
    }
}

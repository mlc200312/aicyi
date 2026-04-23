package io.github.aicyi.example.boot.config;

import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:59
 **/
@EnableOpenApi//@EnableOpenApi用于开启Swagger在 Spring Boot，可放在启动类上，也可放在这里
@Configuration
public class SwaggerConfiguration {

    @Value("${server.port:80}")
    private String serverPort;

    @Bean
    public Docket allApi() {
        ApiInfo apiInfo = (new ApiInfoBuilder())
                .title("接口文档")
                .description("接口文档示例")
                .contact(new Contact("Leon Min", "", "15910436675@163.com"))
                .version("1.0")
                .build();
        String ipAddress = SystemUtils.getIpAddress();
        LoggerFactory.getLogger(WebConfiguration.class).info("Swagger url 'http://{}:{}/api-doc.html'!", ipAddress, serverPort);
        return (new Docket(DocumentationType.OAS_30))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .apis(RequestHandlerSelectors.basePackage("io.github.aicyi.example.web"))
                .paths(PathSelectors.any())
                .build();
    }

//    @Bean
//    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
//        return new BeanPostProcessor() {
//
//            @Override
//            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
//                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
//                }
//                return bean;
//            }
//
//            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
//                List<T> copy = mappings.stream()
//                        .filter(mapping -> mapping.getPatternParser() == null)
//                        .collect(Collectors.toList());
//                mappings.clear();
//                mappings.addAll(copy);
//            }
//
//            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
//                try {
//                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
//                    Assert.notNull(field, "field");
//                    field.setAccessible(true);
//                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
//                } catch (IllegalArgumentException | IllegalAccessException e) {
//                    throw new IllegalStateException(e);
//                }
//            }
//        };
//    }
}

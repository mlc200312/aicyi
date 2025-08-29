package com.aichuangyi.example.core;

import com.aichuangyi.commons.util.DateTimeUtils;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.example.AicyiExampleApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aicyiframework.core.mail.Attachment;
import com.aicyiframework.core.mail.EmailManager;
import com.aicyiframework.core.mail.TemplateEngine;
import io.jsonwebtoken.lang.Maps;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:41
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class EmailManagerTest extends BaseLoggerTest {

    @Autowired
    private EmailManager emailManager;

    private List<String> toList;

    private TemplateEngine templateEngine;

    @Before
    public void before() {
        toList = Arrays.asList("15910436675@163.com", "minliangchao@flashexpress.com");

        // 创建模拟的TemplateEngine
        templateEngine = Mockito.mock(TemplateEngine.class);
    }

    @SneakyThrows
    @Test
    @Override
    public void test() {
        String htmlContent = "<html><body><h1>Hello World!</h1><p>这是一封HTML邮件</p></body></html>";
        boolean isSend = emailManager.sendHtmlEmail(toList, "这是一个Html", htmlContent);

        assert isSend;

        log("test", isSend);
    }

    @SneakyThrows
    @Test
    public void test2() {
        boolean connection = emailManager.testConnection();

        Attachment attachment = new Attachment();
        attachment.setName("test.xlsx");
        attachment.setFile(new File("/Users/liangchaomin/Downloads/1.xlsx"));
        attachment.setContentType("xlsx");
        List<Attachment> attachmentList = Arrays.asList(attachment);
        boolean isSend = emailManager.sendEmailWithAttachment(toList, "附件", "测试带附件的邮件", attachmentList);

        assert connection && isSend;

        log("test2", connection, isSend);
    }

    @SneakyThrows
    @Test
    public void test3() {
        String subject = "复杂模板测试";
        String templateName = "order-confirmation.ftl";

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", IdGenerator.generateId() + "");
        variables.put("userName", "王五");
        variables.put("orderDate", DateTimeUtils.formatLDateTime(LocalDateTime.now(), DateTimeUtils.DATE_PATTERN));
        Map<String, Object> build1 = Maps.of("", new Object())
                .and("name", "笔记本电脑")
                .and("price", 5999.00)
                .and("quantity", 1)
                .build();
        Map<String, Object> build2 = Maps.of("", new Object())
                .and("name", "鼠标")
                .and("price", 199.00)
                .and("quantity", 2)
                .build();
        List<Map<String, Object>> products = Arrays.asList(build1, build2);
        variables.put("products", products);
        variables.put("totalAmount", 6397.00);

        // 模拟模板引擎返回内容
        String expectedHtml = "<html><body>...</body></html>";
        when(templateEngine.process(eq(templateName), eq(variables))).thenReturn(expectedHtml);

        // 执行测试
        boolean isSend = emailManager.sendTemplateEmail(toList, subject, templateName, variables);

        assert isSend;

        log("test3", isSend);
    }

    @SneakyThrows
    @Test
    public void test4() {
        CompletableFuture<Boolean> async = emailManager.sendEmailAsync(toList, "附件", "测试带附件的邮件");

        assert async.get();

        log("test4", async.get());
    }
}

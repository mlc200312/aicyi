package io.github.aicyi.example.boot.message;

import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.commons.util.DateTimeUtils;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.midware.message.mail.sender.EmailSender;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.midware.message.mail.model.MailAttachment;
import io.github.aicyi.midware.message.mail.template.TemplateEngine;
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
public class MailSenderTest extends BaseLoggerTest {

    @Autowired
    private EmailSender emailSender;

    private List<String> toList;
    private TemplateEngine templateEngine;

    @Before
    @Override
    public void beforeTest() {
        toList = Arrays.asList("15910436675@163.com", "mlc200312@163.com");

        // 创建模拟的TemplateEngine
        templateEngine = Mockito.mock(TemplateEngine.class);
    }

    @SneakyThrows
    @Test
    @Override
    public void test() {
        String htmlContent = "<html><body><h1>Hello World!</h1><p>这是一封HTML邮件</p></body></html>";
        boolean isSend = emailSender.sendHtml(toList, "这是一个Html", htmlContent);
        assert isSend;
    }

    @SneakyThrows
    @Test
    public void test2() {
        boolean connection = emailSender.testConnection();
        MailAttachment attachment = new MailAttachment();
        attachment.setName("test.xlsx");
        String absolutePath = new File("").getAbsoluteFile().getParentFile().getPath();
        File file = new File(absolutePath + "/aicyi-example-test/src/test/resources/test/bank_insert.xlsx");
        attachment.setFile(file);
        attachment.setContentType("xlsx");
        List<MailAttachment> attachmentList = Arrays.asList(attachment);
        boolean isSend = emailSender.sendWithAttachment(toList, "附件", "测试带附件的邮件", attachmentList);
        assert connection && isSend;
    }

    @SneakyThrows
    @Test
    public void test3() {
        String subject = "复杂模板测试";
        String templateName = "order-confirmation.ftl";
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", IdUtils.generateId() + "");
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
        boolean isSend = emailSender.sendTemplate(toList, subject, templateName, variables);
        assert isSend;
    }

    @SneakyThrows
    @Test
    public void test4() {
        CompletableFuture<Boolean> async = emailSender.sendAsync(toList, "异步短信", "测试异步发送短信");
        assert async.get();

        log(async.get());
    }
}

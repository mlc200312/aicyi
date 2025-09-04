package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.JaxbUtils;
import io.github.aicyi.test.domain.BaseLoggerTest;
import io.github.aicyi.test.domain.Message;
import io.github.aicyi.test.util.DataSource;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2020-04-20
 **/
public class JaxbUtilsTest extends BaseLoggerTest {
    private Message message;
    private String xml;

    @Before
    public void before() {
        message = DataSource.getMessage();
        xml = DataSource.getMessageXml();
    }

    @SneakyThrows
    @Test
    public void test() {
        String xml = JaxbUtils.bean2Xml(message);

        log("test", xml);
    }

    @Test
    public void xml2BeanTest() throws JAXBException {
        Message message = JaxbUtils.xml2Bean(xml, Message.class);

        log("xml2Bean", message);
    }
}

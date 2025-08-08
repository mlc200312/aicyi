package com.aichuangyi.test.base.util;

import com.aichuangyi.base.util.JaxbUtils;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.Message;
import com.aichuangyi.test.util.DataSource;
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

    @Test
    public void bean2XmlTest() throws JAXBException {
        String xml = JaxbUtils.bean2Xml(message);

        log("bean2Xml", xml);
    }

    @Test
    public void xml2BeanTest() throws JAXBException {
        Message message = JaxbUtils.xml2Bean(xml, Message.class);

        log("xml2Bean", message);
    }
}

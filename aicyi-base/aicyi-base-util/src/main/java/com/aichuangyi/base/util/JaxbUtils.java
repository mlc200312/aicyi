package com.aichuangyi.base.util;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author Mr.Min
 * @description xml 工具类
 * @date 2019-06-19
 **/
public class JaxbUtils {

    /**
     * 实体Bean转化成Xml
     *
     * @param bean
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> String bean2Xml(T bean) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(bean.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "GBK");
        StringWriter writer = new StringWriter();
        marshaller.marshal(bean, writer);
        return writer.toString();
    }

    /**
     * Xml文件流转化成实体Bean
     *
     * @param stream
     * @param clazz
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> T xml2Bean(InputStream stream, Class<T> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object bean = unmarshaller.unmarshal(stream);
        return (T) bean;
    }

    /**
     * 根据Xml文件路径转化成实体Bean
     *
     * @param xmlPath
     * @param clazz
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> T xmlPath2Bean(String xmlPath, Class<T> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object bean = unmarshaller.unmarshal(new File(xmlPath));
        return (T) bean;
    }

    /**
     * Xml转化成实体Bean
     *
     * @param xml
     * @param clazz
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> T xml2Bean(String xml, Class<T> clazz) throws JAXBException {
        InputStream xmlInputStream = getXMLInputStream(xml);
        return xml2Bean(xmlInputStream, clazz);
    }

    /**
     * 获取Xml文件流
     *
     * @param xml
     * @return
     */
    private static InputStream getXMLInputStream(String xml) {
        if (StringUtils.isNotEmpty(xml)) {
            try {
                return new ByteArrayInputStream(xml.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}

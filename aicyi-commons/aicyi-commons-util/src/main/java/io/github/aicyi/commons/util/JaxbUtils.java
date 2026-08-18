package io.github.aicyi.commons.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Mr.Min
 * @description xml 工具类
 * @date 2019-06-19
 **/
public final class JaxbUtils {

    /**
     * JAXBContext 创建开销大且线程安全，按类型缓存复用
     */
    private static final ConcurrentMap<Class<?>, JAXBContext> CONTEXT_CACHE = new ConcurrentHashMap<>();

    private JaxbUtils() {
    }

    /**
     * 实体Bean转化成Xml
     *
     * @param bean
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> String bean2Xml(T bean) throws JAXBException {
        if (bean == null) {
            throw new IllegalArgumentException("bean can not be null");
        }
        JAXBContext context = getContext(bean.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
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
        if (stream == null) {
            throw new IllegalArgumentException("xml stream can not be null");
        }
        JAXBContext context = getContext(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(stream);
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
        if (xmlPath == null || xmlPath.trim().isEmpty()) {
            throw new IllegalArgumentException("xml path can not be blank");
        }
        JAXBContext context = getContext(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(new File(xmlPath));
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
        if (xml == null || xml.trim().isEmpty()) {
            throw new IllegalArgumentException("xml can not be blank");
        }
        return xml2Bean(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), clazz);
    }

    private static JAXBContext getContext(Class<?> clazz) throws JAXBException {
        JAXBContext context = CONTEXT_CACHE.get(clazz);
        if (context == null) {
            context = JAXBContext.newInstance(clazz);
            JAXBContext existing = CONTEXT_CACHE.putIfAbsent(clazz, context);
            if (existing != null) {
                context = existing;
            }
        }
        return context;
    }
}

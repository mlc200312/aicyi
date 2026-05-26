package io.github.aicyi.midware.utils;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.util.id.SnowflakeIdGenerator;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Mr.Min
 * @description ID生成工具类
 * @date 19:15
 **/
public class IdUtils implements InitializingBean {

    private static final SnowflakeIdGenerator DEFAULT_ID_GENERATOR = new SnowflakeIdGenerator(0, 0);

    private static IdUtils INSTANCE;

    private IdGenerator idGenerator;

    public IdUtils(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public static long generateId() {
        if (INSTANCE == null || INSTANCE.idGenerator == null) {
            return DEFAULT_ID_GENERATOR.nextId();
        }
        return INSTANCE.idGenerator.nextId();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.idGenerator == null) {
            throw new IllegalStateException("IdUtils 未完成初始化，请确认 Spring 容器已加载且 IdGenerator 已注入");
        }

        IdUtils.INSTANCE = this;
    }
}

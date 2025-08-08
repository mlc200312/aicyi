package com.aichuangyi.test.base.util;

import com.aichuangyi.base.util.id.IdGenerator;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:37
 **/
public class IdGeneratorTest extends BaseLoggerTest {

    @Test
    public void newIdTest() {
        for (int i = 0; i < 10; i++) {
            long nextId = IdGenerator.newId();
            log("newIdTest", nextId);
        }
    }

    @Test
    public void newV7IdTest() {
        for (int i = 0; i < 10; i++) {
            String uuid = IdGenerator.newV7Id();
            log("newV7IdTest", uuid);
        }
    }

    @Override
    public String getTestName() {
        return "SnowflakeTest";
    }
}

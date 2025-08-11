package com.aichuangyi.test.commons.util;

import com.aichuangyi.commons.util.id.IdGenerator;
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
            long nextId = IdGenerator.generateId();
            log("newIdTest", nextId);
        }
    }

    @Test
    public void newV7IdTest() {
        for (int i = 0; i < 10; i++) {
            String uuid = IdGenerator.generateV7Id();
            log("newV7IdTest", uuid);
        }
    }
}

package com.aichuangyi.test.commons.util;


import com.aichuangyi.commons.core.BizNoGenerator;
import com.aichuangyi.commons.core.IdGenerator;
import com.aichuangyi.commons.util.id.Snowflake;
import com.aichuangyi.commons.util.id.UUIDV7Generator;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:37
 **/
public class IdGeneratorTest extends BaseLoggerTest {

    @Test
    public void test() {
        IdGenerator idGenerator = new Snowflake(0, 0);

        for (int i = 0; i < 50; i++) {

            long id = idGenerator.nextId();

            System.out.println(id);
        }
    }

    @Test
    public void newV7IdTest() {

        BizNoGenerator bizNoGenerator = new UUIDV7Generator();

        for (int i = 0; i < 50; i++) {

            String uuid = bizNoGenerator.generateBizNo();

            System.out.println(uuid);
        }
    }
}

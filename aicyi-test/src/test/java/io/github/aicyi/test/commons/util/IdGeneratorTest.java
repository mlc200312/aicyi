package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.test.domain.BaseLoggerTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:37
 **/
public class IdGeneratorTest extends BaseLoggerTest {

    @Test
    public void test() {
        Set<Long> idSet = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            long id = IdGenerator.generateId();
            idSet.add(id);
            System.out.println(id);
        }

        assert idSet.size() == 50;
    }

    @Test
    public void newV7IdTest() {
        Set<String> uuidSet = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            String uuid = IdGenerator.generateV7Id();
            uuidSet.add(uuid);
            System.out.println(uuid);
        }

        assert uuidSet.size() == 50;
    }
}

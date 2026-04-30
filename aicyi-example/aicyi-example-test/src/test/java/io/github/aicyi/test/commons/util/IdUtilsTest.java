package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:37
 **/
public class IdUtilsTest extends BaseLoggerTest {

    @Override
    public void beforeTest() {

    }

    @Test
    public void test() {
        Set<Long> idSet = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            long id = IdUtils.generateId();
            idSet.add(id);
            System.out.println(id);
        }
        assert idSet.size() == 50;
    }

    @Test
    public void test2() {
        Set<String> uuidSet = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            String uuid = IdUtils.generateV7Id();
            uuidSet.add(uuid);
            System.out.println(uuid);
        }
        assert uuidSet.size() == 50;
    }
}

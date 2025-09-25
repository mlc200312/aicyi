package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.BeanCglibCopier;
import io.github.aicyi.example.domain.Example;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 21:17
 **/
public class BeanCglibCopierTest extends BaseLoggerTest {

    @SneakyThrows
    @Test
    public void test() {
        Example example = BeanCglibCopier.copy(DataSource.getExample(), Example.class);

        assert example != null && example.getId() != null;

        log("test", example);
    }
}

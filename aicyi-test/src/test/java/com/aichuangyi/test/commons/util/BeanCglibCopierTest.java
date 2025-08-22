package com.aichuangyi.test.commons.util;

import com.aichuangyi.commons.util.BeanCglibCopier;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.Example;
import com.aichuangyi.test.dto.ExampleResp;
import com.aichuangyi.test.util.DataSource;
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

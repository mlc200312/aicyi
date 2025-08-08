package com.aichuangyi.test.base.util;

import com.aichuangyi.base.util.BeanCglibCopier;
import com.aichuangyi.test.domain.BaseLoggerTest;
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
    public void copyTest() {
        ExampleResp resp = BeanCglibCopier.copy(DataSource.getExample(), ExampleResp.class);
        log("copyTest", resp);
    }
}

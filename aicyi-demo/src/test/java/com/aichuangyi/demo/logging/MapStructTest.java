package com.aichuangyi.demo.logging;

import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 18:52
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiDemoApplication.class)
public class MapStructTest extends BaseLoggerTest {

//    @Test
//    public void mapperTest() {
//        ExampleResp resp = ExampleMapper.INSTANCE.exampleToResp(DataSource.getExample());
//
//        log("mapperTest", resp);
//    }
}

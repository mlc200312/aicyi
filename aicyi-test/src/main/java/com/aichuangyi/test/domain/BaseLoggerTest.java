package com.aichuangyi.test.domain;


import com.aichuangyi.base.logging.Logger;
import com.aichuangyi.base.logging.LoggerFactory;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2020-04-20
 **/
public abstract class BaseLoggerTest {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseLoggerTest.class);

    public void log(String opt, Object... os) {
        int i = 0;
        StringBuilder sb = new StringBuilder(String.format("\n %s execute %s，输出的结果 : \n", this.getClass().getName(), opt));
        for (Object o : os) {
            sb.append(++i).append("、").append(o).append("\n");
        }
        LOGGER.info(sb);
    }

}

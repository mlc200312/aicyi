package com.aichuangyi.base.util.id;

import com.github.f4b6a3.uuid.UuidCreator;


/**
 * @author Mr.Min
 * @description ID生成
 * @date 19:15
 **/
public class IdGenerator {
    private static final Snowflake SNOWFLAKE = new Snowflake(0L, 0L);

    public static long newId() {
        return SNOWFLAKE.nextId();
    }

    public static String newV7Id() {
        return UuidCreator.getTimeOrdered().toString().replace("-", "");
    }
}

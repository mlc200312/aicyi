package com.aichuangyi.commons.util.id;

import com.github.f4b6a3.uuid.UuidCreator;


/**
 * @author Mr.Min
 * @description ID生成
 * @date 19:15
 **/
public class IdGenerator {
    private static final Snowflake SNOWFLAKE = new Snowflake(0L, 0L);

    public static long generateId() {
        return SNOWFLAKE.nextId();
    }

    public static String generateV7Id() {
        return UuidCreator.getTimeOrdered().toString().replace("-", "");
    }
}

package com.aichuangyi.commons.util.id;


import com.github.f4b6a3.uuid.UuidCreator;

/**
 * @author Mr.Min
 * @description ID生成
 * @date 19:15
 **/
public class IdGenerator {

    private static final Snowflake SNOWFLAKE = new Snowflake(0, 0);

    private IdGenerator() {
    }

    public static long generateId() {
        return SNOWFLAKE.nextId();
    }

    public static String generateV7Id() {
        return UuidCreator.getTimeOrderedWithRandom().toString().replace("-", "");
    }
}

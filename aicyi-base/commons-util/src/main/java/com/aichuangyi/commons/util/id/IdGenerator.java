package com.aichuangyi.commons.util.id;


/**
 * @author Mr.Min
 * @description ID生成
 * @date 19:15
 **/
public class IdGenerator {

    private IdGenerator() {
    }

    public static long generateId() {
        return new Snowflake(0L, 0L).nextId();
    }

    public static String generateV7Id() {
        return new UUIDV7Generator().generateBizNo();
    }
}

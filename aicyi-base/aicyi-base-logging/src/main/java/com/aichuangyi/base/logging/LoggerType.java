package com.aichuangyi.base.logging;

/**
 * @description  功能描述
 * @author  Mr.Min
 * @date  2019-06-23
 **/
public enum LoggerType {
    ACCESS("ACCESS"),
    CLIENT("CLIENT"),
    PERFORMANCE("PERFORMANCE"),
    SCHEDULE("SCHEDULE"),
    MESSAGE("MESSAGE"),
    BIZ("BIZ"),
    DAO("DAO");

    private String name;

    LoggerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

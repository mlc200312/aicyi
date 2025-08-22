package com.aichuangyi.commons.logging;

/**
 * @author Mr.Min
 * @description 日志类型
 * @date 2019-06-23
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

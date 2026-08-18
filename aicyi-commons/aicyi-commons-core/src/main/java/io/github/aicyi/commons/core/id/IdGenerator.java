package io.github.aicyi.commons.core.id;

/**
 * @author Mr.Min
 * @description ID生成器接口定义
 * @date 17:57
 **/
public interface IdGenerator {

    /**
     * 生成ID
     *
     * @return 全局唯一的 long 型 ID
     */
    long nextId();
}

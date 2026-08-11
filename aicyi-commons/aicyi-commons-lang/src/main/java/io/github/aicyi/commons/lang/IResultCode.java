package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description 统一结果接口
 * @date 10:39
 **/
public interface IResultCode extends IEnumType<Integer> {
    
    String getMessage();

    default String getDescription() {
        return getMessage();
    }
}

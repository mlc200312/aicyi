package io.github.aicyi.commons.lang;

/**
 * @author Mr.Min
 * @description 统一结果接口
 * @date 10:39
 **/
public interface IResult<K, V> {

    K getCode();

    String getMessage();

    V getData();
}

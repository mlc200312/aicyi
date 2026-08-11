package io.github.aicyi.commons.security.token;

import java.util.Map;

/**
 * @author Mr.Min
 * @description Token信息
 * @date 15:27
 **/
public interface TokenSession<P> {

    String getToken();

    P getPrincipal();

    Map<String, Object> getAttributes();
}

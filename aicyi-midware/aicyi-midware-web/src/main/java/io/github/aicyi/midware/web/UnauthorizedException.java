package io.github.aicyi.midware.web;

import io.github.aicyi.commons.lang.BaseException;
import io.github.aicyi.commons.lang.type.CommonResultCode;

/**
 * @author Mr.Min
 * @description 未授权异常
 * @date 2026/4/23
 **/
public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(CommonResultCode.UNAUTHORIZED);
    }
}

package com.aichuangyi.commons.util.id;

import com.aichuangyi.commons.core.BizNoGenerator;
import com.github.f4b6a3.uuid.UuidCreator;

/**
 * @author Mr.Min
 * @description uuid v7 生成策略
 * @date 18:03
 **/
public class UUIDV7Generator implements BizNoGenerator {

    @Override
    public String generateBizNo() {
        return UuidCreator.getTimeOrdered().toString().replace("-", "");
    }
}

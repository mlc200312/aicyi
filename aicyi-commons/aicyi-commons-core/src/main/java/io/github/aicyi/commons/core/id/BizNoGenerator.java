package io.github.aicyi.commons.core.id;

/**
 * @author Mr.Min
 * @description 业务号生成器接口定义（订单号、流水号等业务标识）
 * @date 17:58
 **/
public interface BizNoGenerator {

    /**
     * 生成业务号
     *
     * @return 全局唯一的业务编号
     */
    String generateBizNo();
}

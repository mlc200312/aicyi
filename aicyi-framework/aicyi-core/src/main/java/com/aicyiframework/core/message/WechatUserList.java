package com.aicyiframework.core.message;

import lombok.Data;

import java.util.List;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/23
 **/
@Data
public class WechatUserList {
    private int total;
    private int count;
    private String nextOpenId;
    private List<String> openIds;
}

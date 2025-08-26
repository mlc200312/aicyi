package com.aicyiframework.core.message;

import java.util.List;

/**
 * @Description: 微信用户列表
 * @Author: Mr.Min
 * @Date: 2025/8/23
 **/
public class WechatUserList {
    private int total;
    private int count;
    private String nextOpenId;
    private List<String> openIds;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNextOpenId() {
        return nextOpenId;
    }

    public void setNextOpenId(String nextOpenId) {
        this.nextOpenId = nextOpenId;
    }

    public List<String> getOpenIds() {
        return openIds;
    }

    public void setOpenIds(List<String> openIds) {
        this.openIds = openIds;
    }
}

package com.aicyiframework.core.message;

import java.util.List;
import java.util.Map;

/**
 * 微信公众号消息服务接口
 */
public interface WechatMpService {
    /**
     * 发送模板消息
     * @param openIds 接收者openId列表
     * @param templateId 模板ID
     * @param data 模板数据
     * @param url 跳转URL
     * @param miniprogramState 小程序状态(developer/trial/formal)
     * @return 消息ID
     */
    String sendTemplateMessage(
            List<String> openIds,
            String templateId,
            Map<String, WechatTemplateData> data,
            String url,
            String miniprogramState) throws MessageSendException;
    
    /**
     * 上传临时素材
     * @param file 文件
     * @param type 媒体类型(image/voice/video/thumb)
     * @return 媒体ID
     */
//    String uploadMedia(MultipartFile file, String type) throws MessageSendException;
    
    /**
     * 获取模板列表
     * @return 模板列表
     */
    List<WechatTemplate> getTemplates() throws MessageSendException;
    
    /**
     * 获取用户列表
     * @param nextOpenId 下一个openId
     * @return 用户列表
     */
    WechatUserList getUsers(String nextOpenId) throws MessageSendException;
    
    /**
     * 获取access token
     * @return access token
     */
    String getAccessToken() throws MessageSendException;
}
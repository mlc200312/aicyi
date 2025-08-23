package com.aicyiframework.core.message;

import io.jsonwebtoken.lang.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WechatMpServiceImpl implements WechatMpService, InitializingBean {
    private final WechatMpConfig config;
//    private final RestTemplate restTemplate;
//    private final RedisTemplate<String, String> redisTemplate;

    private static final String ACCESS_TOKEN_KEY = "wechat:mp:access_token:";
    private static final String TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send";
    private static final String UPLOAD_MEDIA_URL = "https://api.weixin.qq.com/cgi-bin/media/upload";
    private static final String GET_TEMPLATES_URL = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template";
    private static final String GET_USERS_URL = "https://api.weixin.qq.com/cgi-bin/user/get";

    public WechatMpServiceImpl(
            WechatMpConfig config
//            , RestTemplate restTemplate,
//            RedisTemplate<String, String> redisTemplate
    ) {
        this.config = config;
//        this.restTemplate = restTemplate;
//        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化时验证配置
        if (StringUtils.isEmpty(config.getAppId()) || StringUtils.isEmpty(config.getAppSecret())) {
            throw new IllegalStateException("Wechat MP config not properly set");
        }
    }

    @Override
    public String sendTemplateMessage(
            List<String> openIds,
            String templateId,
            Map<String, WechatTemplateData> data,
            String url,
            String miniprogramState) throws MessageSendException {

//        String accessToken = getAccessToken();
//        List<String> successMsgIds = new ArrayList<>();
//
//        for (String openId : openIds) {
//            try {
//                Map<String, Object> request = buildTemplateRequest(
//                        openId, templateId, data, url, miniprogramState);
//
//                String response = restTemplate.postForObject(
//                        TEMPLATE_MESSAGE_URL + "?access_token={accessToken}",
//                        request,
//                        String.class,
//                        accessToken);
//
//                JsonNode json = JsonUtils.parse(response);
//                if (json.has("errcode") && json.get("errcode").asInt() != 0) {
//                    throw new MessageSendException(
//                            "WECHAT_MP_ERROR",
//                            "Failed to send template message: " + json.get("errmsg").asText());
//                }
//
//                successMsgIds.add(json.get("msgid").asText());
//            } catch (Exception e) {
//                log.error("Send wechat template message failed, openId: {}", openId, e);
//                // 部分失败不影响其他消息发送
//            }
//        }
//
//        if (successMsgIds.isEmpty()) {
//            throw new MessageSendException("SEND_FAILED", "All template messages send failed");
//        }
//
//        return StringUtils.join(successMsgIds, ",");
        return null;
    }

    private Map<String, Object> buildTemplateRequest(
            String openId,
            String templateId,
            Map<String, WechatTemplateData> data,
            String url,
            String miniprogramState) {

        Map<String, Object> request = new HashMap<>();
        request.put("touser", openId);
        request.put("template_id", templateId);

        Map<String, Map<String, String>> templateData = new HashMap<>();
        data.forEach((key, value) -> {
            templateData.put(key, Maps
                    .of("value", value.getValue())
                    .and("color", value.getColor())
                    .build());
        });
        request.put("data", templateData);

        if (StringUtils.isNotBlank(url)) {
            request.put("url", url);
        }

        if (StringUtils.isNotBlank(miniprogramState)) {
            request.put("miniprogram", Maps
                    .of("appid", config.getAppId())
                    .and("pagepath", url)
                    .and("state", miniprogramState)
                    .build());
        }

        return request;
    }

//    @Override
//    public String uploadMedia(MultipartFile file, String type) throws MessageSendException {
//        try {
//            String accessToken = getAccessToken();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("media", new MultipartInputStreamFileResource(
//                    file.getInputStream(), file.getOriginalFilename()));
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    UPLOAD_MEDIA_URL + "?access_token={accessToken}&type={type}",
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class,
//                    accessToken,
//                    type);
//
//            JsonNode json = JsonUtils.parse(response.getBody());
//            if (json.has("errcode") && json.get("errcode").asInt() != 0) {
//                throw new MessageSendException(
//                        "UPLOAD_FAILED",
//                        "Failed to upload media: " + json.get("errmsg").asText());
//            }
//
//            return json.get("media_id").asText();
//        } catch (IOException e) {
//            throw new MessageSendException("IO_ERROR", "Failed to read file", e);
//        }
//    }

    @Override
    public List<WechatTemplate> getTemplates() throws MessageSendException {
        String accessToken = getAccessToken();
//        String response = restTemplate.getForObject(
//                GET_TEMPLATES_URL + "?access_token={accessToken}",
//                String.class,
//                accessToken);
//
//        JsonNode json = JsonUtils.parse(response);
//        if (json.has("errcode") && json.get("errcode").asInt() != 0) {
//            throw new MessageSendException(
//                    "API_ERROR",
//                    "Failed to get templates: " + json.get("errmsg").asText());
//        }
//
//        List<WechatTemplate> templates = new ArrayList<>();
//        json.get("template_list").forEach(template -> {
//            WechatTemplate t = new WechatTemplate();
//            t.setTemplateId(template.get("template_id").asText());
//            t.setTitle(template.get("title").asText());
//            t.setContent(template.get("content").asText());
//            t.setExample(template.get("example").asText());
//            templates.add(t);
//        });
//
//        return templates;
        return null;
    }

    @Override
    public WechatUserList getUsers(String nextOpenId) throws MessageSendException {
//        String accessToken = getAccessToken();
//        String url = GET_USERS_URL + "?access_token={accessToken}";
//        if (StringUtils.isNotBlank(nextOpenId)) {
//            url += "&next_openid={nextOpenId}";
//        }
//
//        String response = restTemplate.getForObject(
//                url,
//                String.class,
//                accessToken,
//                nextOpenId);
//
//        JsonNode json = JsonUtils.parse(response);
//        if (json.has("errcode") && json.get("errcode").asInt() != 0) {
//            throw new MessageSendException(
//                    "API_ERROR",
//                    "Failed to get users: " + json.get("errmsg").asText());
//        }
//
//        WechatUserList userList = new WechatUserList();
//        userList.setTotal(json.get("total").asInt());
//        userList.setCount(json.get("count").asInt());
//        userList.setNextOpenId(json.path("next_openid").asText());
//
//        if (json.has("data") && json.get("data").has("openid")) {
//            List<String> openIds = new ArrayList<>();
//            json.get("data").get("openid").forEach(id -> openIds.add(id.asText()));
//            userList.setOpenIds(openIds);
//        }
//
//        return userList;
        return null;
    }

    @Override
    public String getAccessToken() throws MessageSendException {
        String cacheKey = ACCESS_TOKEN_KEY + config.getAppId();
//        String cachedToken = redisTemplate.opsForValue().get(cacheKey);
//
//        if (StringUtils.isNotBlank(cachedToken)) {
//            return cachedToken;
//        }
//
//        String url = "https://api.weixin.qq.com/cgi-bin/token" +
//                "?grant_type=client_credential" +
//                "&appid=" + config.getAppId() +
//                "&secret=" + config.getAppSecret();
//
//        try {
//            String response = restTemplate.getForObject(url, String.class);
//            JsonNode json = JsonUtils.parse(response);
//
//            if (json.has("errcode")) {
//                throw new MessageSendException(
//                        "TOKEN_ERROR",
//                        "Failed to get access token: " + json.get("errmsg").asText());
//            }
//
//            String accessToken = json.get("access_token").asText();
//            int expiresIn = json.get("expires_in").asInt();
//
//            // 提前5分钟过期
//            redisTemplate.opsForValue().set(
//                    cacheKey,
//                    accessToken,
//                    expiresIn - 300,
//                    TimeUnit.SECONDS);
//
//            return accessToken;
//        } catch (Exception e) {
//            throw new MessageSendException("NETWORK_ERROR", "Failed to get access token", e);
//        }
        return null;
    }

    /**
     * 用于MultipartFile上传的内部类
     */
    private static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // 未知长度
        }
    }
}
package io.github.aicyi.midware.message.sms.sender;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.midware.message.core.exception.MessageSendException;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description YunPian短信服务
 * @date 18:14
 **/
public class YunPianSmsSender extends AbstractSmsSender {

    private YunpianClient client;

    public YunPianSmsSender(String apikey, Map<String, String> template) {
        super(template);
        this.client = new YunpianClient(apikey).init();
    }

    public YunPianSmsSender(String apikey) {
        this(apikey, new HashMap<>());
    }

    @Override
    public boolean send(String phoneNumber, String messageContent, String signName) {

        // 构建参数
        Map param = Maps
                .of(YunpianClient.MOBILE, phoneNumber)
                .and(YunpianClient.TEXT, messageContent)
                .build();

        // 发送短信
        Result result = client.sms().single_send(param);

        if (!result.isSucc()) {
            throw new MessageSendException("UNKNOWN_ERROR", "短信发送失败：" + result.getMsg());
        }

        return true;
    }

    @PreDestroy
    @Override
    public void shutdown() {
        super.shutdown();
        client.close();
    }
}

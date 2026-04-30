package io.github.aicyi.midware.kit;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.midware.message.sms.AbstractSmsManager;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description YunPian短信服务
 * @date 18:14
 **/
public class YunPianSmsManager extends AbstractSmsManager {

    private YunpianClient client;

    public YunPianSmsManager(String apikey, Map<String, String> template) {
        super(template);
        this.client = new YunpianClient(apikey).init();
    }

    public YunPianSmsManager(String apikey) {
        this(apikey, new HashMap<>());
    }

    @Override
    public boolean sendTextSms(String phoneNumber, String content, String signName) {

        // 构建参数
        Map param = Maps
                .of(YunpianClient.MOBILE, phoneNumber)
                .and(YunpianClient.TEXT, content)
                .build();

        // 发送短信
        Result result = client.sms().single_send(param);

        return result.isSucc();
    }

    @PreDestroy
    @Override
    public void shutdown() {
        super.shutdown();
        client.close();
    }
}

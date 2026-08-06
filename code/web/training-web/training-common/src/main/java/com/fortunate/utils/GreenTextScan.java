package com.fortunate.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "training.aliyun")
public class GreenTextScan {

    private String accessKeyId;
    private String secret;

   public Map<String, String> greenTextScan(String content) throws Exception {
    Config config = new Config();
    config.setAccessKeyId(accessKeyId);
    config.setAccessKeySecret(secret);
    config.setRegionId("cn-shanghai");
    config.setEndpoint("green.cn-shanghai.aliyuncs.com");
    config.setReadTimeout(6000);
    config.setConnectTimeout(3000);

    Client client = new Client(config);

    JSONObject serviceParameters = new JSONObject();
    serviceParameters.put("content", content);

    TextModerationPlusRequest request = new TextModerationPlusRequest();
    request.setService("comment_detection_pro");
    request.setServiceParameters(serviceParameters.toJSONString());

    Map<String, String> resultMap = new HashMap<>();

    try {
        TextModerationPlusResponse response = client.textModerationPlus(request);
        if (response.getStatusCode() == 200) {
            TextModerationPlusResponseBody body = response.getBody();
            if (body != null && 200 == body.getCode()) {
                TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = body.getData();
                if (data != null && data.getResult() != null) {
                    for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult result : data.getResult()) {
                        String label = result.getLabel();
                        String description = result.getDescription();
                        // 可以根据 label 做合并判断，或返回多个结果
                        resultMap.put("label", label);
                        resultMap.put("suggestion", description);
                        // 可选：break 或 continue 根据业务需求决定
                    }
                    return resultMap;
                }
            }
        }
        resultMap.put("suggestion", "pass");
        return resultMap;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}



    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"));
        config.setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
        //接入区域和地址请根据实际情况修改
        config.setRegionId("cn-shanghai");
        config.setEndpoint("green-cip.cn-shanghai.aliyuncs.com");
        //读取时超时时间，单位毫秒（ms）。
        config.setReadTimeout(6000);
        //连接时超时时间，单位毫秒（ms）。
        config.setConnectTimeout(3000);
        Client client = new Client(config);

        JSONObject serviceParameters = new JSONObject();
        serviceParameters.put("content", "测试文本内容");

        TextModerationPlusRequest textModerationPlusRequest = new TextModerationPlusRequest();
        // 检测类型
        textModerationPlusRequest.setService("comment_detection_pro");
        textModerationPlusRequest.setServiceParameters(serviceParameters.toJSONString());
        Map<String, String> resultMap = new HashMap<>();
        try {
            TextModerationPlusResponse response = client.textModerationPlus(textModerationPlusRequest);
            if (response.getStatusCode() == 200) {
                TextModerationPlusResponseBody result = response.getBody();
                System.out.println(JSON.toJSONString(result));
                System.out.println("requestId = " + result.getRequestId());
                System.out.println("code = " + result.getCode());
                System.out.println("msg = " + result.getMessage());
                Integer code = result.getCode();
                if (200 == code) {
                    TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = result.getData();

                    System.out.println(JSON.toJSONString(data, true));
                } else {
                    System.out.println("text moderation not success. code:" + code);
                }
            } else {
                System.out.println("response not success. status:" + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
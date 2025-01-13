package com.cc.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 短信发送工具类
 */
public class SMSUtils {

    //腾讯云账户
    private static final String secretId = "替换为自己的密钥";
    private static final String secretKey = "替换为自己的密钥";
    public static final String templateId_validate = "1357005"; //发送短信验证码
    public static final String templateId_password = "1740962"; //重置密码通知短信

    public static CompletableFuture<Void> sendMessage(String phoneNumber, String code, String templateId) {
        return CompletableFuture.runAsync(() -> {
            try {
                Credential cred = new Credential(secretId, secretKey);
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setReqMethod("POST");
                httpProfile.setConnTimeout(60);
                httpProfile.setEndpoint("sms.tencentcloudapi.com");

                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setSignMethod("HmacSHA256");
                clientProfile.setHttpProfile(httpProfile);
                SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);

                SendSmsRequest req = new SendSmsRequest();
                String sdkAppId = "1400656414";
                req.setSmsSdkAppId(sdkAppId);

                String signName = "火山大帮手";
                req.setSignName(signName);
                req.setTemplateId(templateId);

                String[] templateParamSet = {"1"};
                req.setTemplateParamSet(templateParamSet);

                String[] phoneNumberSet = {"+86" + phoneNumber};
                req.setPhoneNumberSet(phoneNumberSet);

                String[] templateParams = {code};
                req.setTemplateParamSet(templateParams);

                SendSmsResponse res = client.SendSms(req);


                System.out.println(SendSmsResponse.toJsonString(res));
                System.out.println(res.getRequestId());

            } catch (TencentCloudSDKException e) {
                e.printStackTrace();
            }
        });
    }
}

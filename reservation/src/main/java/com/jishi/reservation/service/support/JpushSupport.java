package com.jishi.reservation.service.support;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import com.jishi.reservation.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by sloan on 2017/9/23.
 */

@Slf4j
@Service
public class JpushSupport {


    private static PushPayload buildPushObj(String pushId,String message) {

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(pushId))
                .setMessage(Message.newBuilder()
                        .setMsgContent(message)
                        .addExtra("from", "JPush")
                        .build())
                .build();

    }



    public void sendPush(String pushId,String message){

        JPushClient jpushClient = new JPushClient(Constant.JPush_MASTER_SECRET, Constant.JPush_Appkey, null, ClientConfig.getInstance());

        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObj(pushId,message);

        try {
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);

        } catch (APIConnectionException e) {
            // Connection error, should retry later
            log.error("Connection error, should retry later", e);

        } catch (APIRequestException e) {
            // Should review the error, and fix the request
            log.error("Should review the error, and fix the request", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
        }

    }







    //
//    public static void main(String[] args) {
//
//        JPushClient jpushClient = new JPushClient(Constant.JPush_MASTER_SECRET,Constant.JPush_Appkey, null, ClientConfig.getInstance());
//
//        // For push, all you need do is to build PushPayload object.
//        PushPayload payload = buildPushObject();
//
//        try {
//            PushResult result = jpushClient.sendPush(payload);
//            log.info("Got result - " + result);
//
//        } catch (APIConnectionException e) {
//            // Connection error, should retry later
//            log.error("Connection error, should retry later", e);
//
//        } catch (APIRequestException e) {
//            // Should review the error, and fix the request
//            log.error("Should review the error, and fix the request", e);
//            log.info("HTTP Status: " + e.getStatus());
//            log.info("Error Code: " + e.getErrorCode());
//            log.info("Error Message: " + e.getErrorMessage());
//        }
//}
}

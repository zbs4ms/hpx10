package com.jishi.reservation.service.support;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.IHttpClient;
import cn.jiguang.common.connection.NettyHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import com.jishi.reservation.service.support.jpush.CustomPushClient;
import com.jishi.reservation.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * Created by sloan on 2017/9/23.
 */

@Slf4j
@Service
public class JpushSupport {

    private CustomPushClient pushClient = null;

    public static PushPayload buildPushObj(String pushId,String message) {

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

    public void sendPush(List<PushPayload> pushPayloadList) {
        if (pushPayloadList == null || pushPayloadList.isEmpty()) {
          return;
        }

        checkPushClient();
        for (PushPayload payload : pushPayloadList) {
            try {
                pushClient.sendRequest(payload);
            } catch (APIConnectionException e) {
                log.error("Connection error, should retry later. ");
            } catch (APIRequestException e) {
                log.error("Should review the error, and fix the request", e);
                log.info("HTTP Status: " + e.getStatus());
                log.info("Error Code: " + e.getErrorCode());
                log.info("Error Message: " + e.getErrorMessage());
                log.info("Send Message: " + payload.toString());
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Send Message: " + payload.toString());
            }
        }
    }

    private void checkPushClient() {
        if (pushClient == null) {
            synchronized (this) {
                if (pushClient == null) {
                    pushClient = CustomPushClient.newInstance(Constant.JPush_Appkey, Constant.JPush_MASTER_SECRET);
                }
            }
        }
    }

}

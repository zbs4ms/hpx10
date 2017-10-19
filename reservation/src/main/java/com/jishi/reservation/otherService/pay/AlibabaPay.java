package com.jishi.reservation.otherService.pay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.doraemon.base.util.RandomUtil;
import com.jishi.reservation.util.PayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zbs on 2017/9/19.
 */

@Service
@Slf4j
public class AlibabaPay {



    public String generateOrder(String subject, BigDecimal price) throws Exception {
        AlipayClient client = new DefaultAlipayClient(
                PayConstant.SERVER_URL,
                PayConstant.APP_ID,
                PayConstant.APP_PRIVATE_KEY,
                PayConstant.DATA_FORMAT,
                PayConstant.CHARSET,

                PayConstant.APP_PUBLIC_KEY,
                PayConstant.ENCRYPT
        );
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(subject);
        model.setSubject(subject);
        //生成订单号
        String orderNumber = generateUniqueOrderNumber();
        model.setOutTradeNo(orderNumber);
        model.setTimeoutExpress(PayConstant.TIME_OUT_EXPRESS);
        model.setTotalAmount(String.valueOf(price));
        model.setProductCode(PayConstant.QUICK_MSECURITY_PAY);
        log.info("生成的订单请求对象："+JSONObject.toJSONString(model));
        request.setBizModel(model);
        request.setNotifyUrl("http://doc.hpx10.com:50002/reservation/pay/aliPayCallBack");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = client.sdkExecute(request);
            log.info("支付宝返回的处理结果：\n"+JSONObject.toJSONString(response));
            log.info("支付宝订单号："+response.getTradeNo());
            return response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;

    }


    public String aliPay_notify(Map requestParams){
        System.out.println("支付宝支付结果通知"+requestParams.toString());
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();

        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, PayConstant.APP_PUBLIC_KEY, PayConstant.CHARSET, PayConstant.ENCRYPT);
            if(flag){
                if("TRADE_SUCCESS".equals(params.get("trade_status"))){
                    //付款金额
                    String amount = params.get("buyer_pay_amount");
                    //商户订单号
                    String out_trade_no = params.get("out_trade_no");
                    //支付宝交易号
                    String trade_no = params.get("trade_no");
                    //附加数据
                    String passback_params = URLDecoder.decode(params.get("passback_params"));

                    //todo
                    //判断支付金额和商户订单号和自己系统中的信息是否吻合，做判断

                }
            }
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "success";
    }

    public static String generateUniqueOrderNumber() throws Exception {

       String prefix = "jxt_";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = sdf.format(date);
        return prefix+format+RandomUtil.getRandomLetterAndNum(6);
    }


}

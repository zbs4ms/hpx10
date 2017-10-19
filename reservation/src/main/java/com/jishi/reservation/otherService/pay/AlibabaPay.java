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
import com.google.common.base.Preconditions;
import com.jishi.reservation.dao.mapper.OrderInfoMapper;
import com.jishi.reservation.dao.models.OrderInfo;
import com.jishi.reservation.otherService.pay.protocol.AliPayCallbackModel;
import com.jishi.reservation.service.enumPackage.OrderStatusEnum;
import com.jishi.reservation.util.PayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    OrderInfoMapper orderInfoMapper;

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


    public String aliPay_notify(AliPayCallbackModel model){
        System.out.println("支付宝支付结果通知:\n"+JSONObject.toJSONString(model));
        Map<String, String> params = model.toMap();
        log.info("转换后的map\n"+JSONObject.toJSONString(params));
        //获取支付宝POST过来反馈信息
       // Map<String,String> params = new HashMap<String,String>();


        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, PayConstant.APP_PUBLIC_KEY, PayConstant.CHARSET, PayConstant.ENCRYPT);
            log.info("验证结果："+flag);

                if("TRADE_SUCCESS".equals(params.get("trade_status"))){
                    //付款金额
                    String amount =  params.get("total_fee");
                    //商户订单号
                    String outTradeNo =  params.get("out_trade_no");
                    //支付宝交易号
                    String trade_no =  params.get("trade_no");
                    //支付时间
                    String payTime = params.get("notify_time");
                    //附加数据
                    //String passback_params = URLDecoder.decode(params.get("passback_params"));


                    //判断支付金额和商户订单号和自己系统中的信息是否吻合，做判断
                    OrderInfo orderInfo =  orderInfoMapper.queryByOutTradeNo(outTradeNo);
                    Preconditions.checkNotNull(orderInfo,"找不到该订单信息");
                    log.info("订单信息：\n"+JSONObject.toJSONString(orderInfo));
                    //改变订单状态和支付时间
                    orderInfo.setStatus(OrderStatusEnum.PAYED.getCode());
                    orderInfo.setPayTime(payTime);
                    orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
                    log.info("订单状态修改为已支付。订单id:"+orderInfo.getId());

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
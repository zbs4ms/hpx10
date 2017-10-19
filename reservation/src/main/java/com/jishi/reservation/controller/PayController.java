package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.pay.PayCallBack;
import com.jishi.reservation.service.pay.bean.TBPaymentModel;
import com.jishi.reservation.service.pay.bean.WXPaymentModel;
import com.jishi.reservation.service.pay.IHandle;
import com.jishi.reservation.service.pay.resultListener.TBPayResultListener;
import com.jishi.reservation.service.pay.resultListener.WXPayResultListener;
import com.jishi.reservation.util.Constant;
import com.us.base.common.controller.BaseController;
import com.us.base.util.xml.XMLParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/pay")
@Slf4j
@Api(description = "支付接口")
public class PayController extends BaseController {

    @Autowired
    PayCallBack payCallBack;

    /**
     * @param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "支付宝服务端支付接口", notes = "")
    @RequestMapping(value = "aliPay", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject aliPay() throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(Constant.ALIPAY_GATEWAY,
                   Constant.ALIPAY_APP_ID, Constant.ALIPAY_PRIVATE_KEY,
                   "json", Constant.ALIPAY_CHARSET, Constant.ALIPAY_PUBLIC_KEY, Constant.ALIPAY_SIGN_TYPE);

        return ResponseWrapper().addMessage("回调成功!").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    /**
     * 支付宝回调接口
     * @param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "支付宝支付回调接口", notes = "")
    @RequestMapping(value = "aliPayCallBack", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject aliPayCallBack(@ModelAttribute TBPaymentModel tbPaymentInfo) throws Exception {
        TBPayResultListener tbPayResultListener = new TBPayResultListener(tbPaymentInfo);
        try{
           // return payCallBack.startPayCall(wxPayResultListener);
        }catch (Exception e){
           // return wxPayResultListener.onFail(wxPaymentInfo);
        }
        return null;
    }

    /**
     * 微信支付回调接口
     * @param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "微信支付回调接口", notes = "")
    @RequestMapping(value = "weChatPayCallBack", method = RequestMethod.POST)
    @ResponseBody
    public String weChatPayCallBack(HttpServletRequest request) throws Exception {
        WXPayResultListener wxPayResultListener = null;
        WXPaymentModel wxPaymentModel = null;
        try {
            BufferedReader reader = request.getReader();
            String line = "";
            StringBuffer inputString = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }
            String xmlString = inputString.toString();
            request.getReader().close();
            log.info("----接收到的数据如下：---" + xmlString);
            Map map = XMLParser.getMapFromXML(xmlString);
            wxPaymentModel = JSON.parseObject(JSON.toJSONString(map), WXPaymentModel.class);
            wxPayResultListener = new WXPayResultListener(wxPaymentModel);
            List<IHandle> handleList = new ArrayList<>();
            //todo:加入业务list
            return payCallBack.startPayCall(wxPayResultListener,handleList);
        } catch (Exception e) {
            log.error("微信支付失败:", e);
            if (wxPayResultListener != null) {
                return wxPayResultListener.onFail(wxPaymentModel);
            }
            return "<xml>" +
                    "<return_code><![CDATA[FAIL]]></return_code>" +
                    "<return_msg><![CDATA[fail]]></return_msg>" +
                    "</xml>";
        }
    }


}
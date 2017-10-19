package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.protocol.AdminLogInfoData;
import com.jishi.reservation.dao.models.Manager;
import com.jishi.reservation.otherService.pay.AlibabaPay;
import com.jishi.reservation.otherService.pay.protocol.AliPayCallbackModel;
import com.jishi.reservation.service.ManagerService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.util.Common;
import com.jishi.reservation.util.CookieUtil;
import com.jishi.reservation.util.SessionUtil;
import com.us.base.common.controller.BaseController;
import com.us.base.util.MD5Encryption;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/pay")
@Slf4j
@Api(description = "支付接口")
public class PayController extends BaseController {



    @Autowired
    private AlibabaPay alibabaPay;

    /**
     * @param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "支付回调接口", notes = "")
    @RequestMapping(value = "aliPayCallBack", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject aliPayCallBack(
            AliPayCallbackModel model
    ) throws Exception {

        //todo  看到时候给我哪些参数...
        alibabaPay.aliPay_notify(model);

        return ResponseWrapper().addMessage("回调成功!").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }


    /**
     * @param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "阿里支付", notes = "")
    @RequestMapping(value = "aliPay", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject aliPay(
            @ApiParam(value = "支付的商品名称") @RequestParam(value = "subject") String subject,
            @ApiParam(value = "支付的商品价格 元为单位") @RequestParam(value = "price") BigDecimal price
            ) throws Exception {

        String response = alibabaPay.generateOrder(subject, price);

        return ResponseWrapper().addData(response).addMessage("请求成功!").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }
}

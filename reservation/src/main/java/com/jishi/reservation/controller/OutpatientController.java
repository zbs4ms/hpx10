package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.protocol.OutpatientPaymentInfoVO;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.OutpatientService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by liangxiong on 2017/10/25.
 */
@RestController
@RequestMapping("/outpatient")
@Slf4j
@Api(description = "门诊缴费相关接口")
public class OutpatientController extends MyBaseController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private OutpatientService outpatientService;

    @ApiOperation(value = "门诊缴费列表", response = OutpatientPaymentInfoVO.class)
    @RequestMapping(value="/paymentInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryOutpatientPamentInfo(HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId) throws Exception {
        if (accountId == null) {
          accountId = accountService.returnIdByToken(request);
          if(accountId.equals(-1L)){
            return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
          }
        }
        List<OutpatientPaymentInfoVO> outpatientPamentInfo = outpatientService.queryOutpatientPamentInfo(accountId);
        return ResponseWrapper().addData(outpatientPamentInfo).addMessage("添加成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    /*
    @ApiOperation(value = "某个医嘱的单据缴费详情")
    @RequestMapping(name="/adviceReceipt", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject singleAdviceReceipt(HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
              return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        return ResponseWrapper().addData().addMessage("添加成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }
    */

    @ApiOperation(value = "门诊缴费确认(可多个单据)")
    @RequestMapping(value="/batchpayConfirm", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject outpatientPamentConfirm(HttpServletRequest request, HttpServletResponse response,
                @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                @ApiParam(value = "单据ID，可以多个单据，以','分隔", required = true) @RequestParam(value = "docIds", required = true) String docIds,
                @ApiParam(value = "病人ID", required = true) @RequestParam(value = "brId", required = true) String brId,
                @ApiParam(value = "总金额", required = true) @RequestParam(value = "zje", required = true) Double zje,
                @ApiParam(value = "总结算金额", required = true) @RequestParam(value = "jsje", required = true) Double jsje,
                @ApiParam(value = "是否挂号单", required = true) @RequestParam(value = "sfghd", required = true) Integer sfghd,
                @ApiParam(value = "交易单ID", required = true) @RequestParam(value = "orderId", required = true) Long orderId) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
              return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        boolean rslt = outpatientService.batchpayConfirm(docIds, brId, zje, jsje, sfghd, orderId);
        return rslt ? ResponseWrapper().addMessage("成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode()) :
                      ResponseWrapper().addMessage("失败").ExeFaild(ReturnCodeEnum.FAILED.getCode());
    }
}
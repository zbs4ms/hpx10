package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.protocol.OutpatientPaymentInfoVO;
import com.jishi.reservation.controller.protocol.OutpatientVisitPrescriptionVO;
import com.jishi.reservation.controller.protocol.OutpatientVisitReceiptVO;
import com.jishi.reservation.controller.protocol.OutpatientVisitRecordVO;
import com.jishi.reservation.dao.models.OrderInfo;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.OrderInfoService;
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
import java.math.BigDecimal;
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

    @Autowired
    OrderInfoService orderInfoService;


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
        return ResponseWrapper().addData(outpatientPamentInfo).addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "生成订单", response = OrderInfo.class)
    @RequestMapping(value = "generateOrder", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject generateOrder(HttpServletRequest request,HttpServletResponse response,
            @ApiParam(value = "accountId 通过token找到", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "预交的名称 eg:住院预交款", required = true) @RequestParam(value = "subject", required = true) String subject,
            @ApiParam(value = "交易的金额", required = true) @RequestParam(value = "price", required = true) BigDecimal price,
            @ApiParam(value = "brId", required = true) @RequestParam(value = "brId", required = true) String brId) throws Exception {

        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }

        OrderInfo orderInfo = orderInfoService.generateOutpatient(accountId, brId, subject, price);

        return ResponseWrapper().addData(orderInfo).addMessage("订单生成成功!").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
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

    @ApiOperation(value = "门诊缴费确认(单个医嘱)")
    @RequestMapping(value="/confirmPayment", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject confirmPayment(HttpServletRequest request, HttpServletResponse response,
                  @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                  @ApiParam(value = "单据ID", required = true) @RequestParam(value = "docmentId", required = true) String docmentId,
                  @ApiParam(value = "病人ID", required = true) @RequestParam(value = "brId", required = true) String brId,
                  @ApiParam(value = "总金额", required = true) @RequestParam(value = "price", required = true) BigDecimal price,
                  @ApiParam(value = "总结算金额", required = false) @RequestParam(value = "payPrice", required = false) BigDecimal payPrice,
                  @ApiParam(value = "单据类型，1-收费单，4-挂号单", required = true) @RequestParam(value = "documentType", required = true) Integer documentType,
                  @ApiParam(value = "订单号", required = true) @RequestParam(value = "orderNumber", required = true) String orderNumber) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        //boolean rslt = outpatientService.batchpayConfirm(docIds, brId, zje, jsje, sfghd, orderId);
        return true ? ResponseWrapper().addMessage("成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode()) :
        ResponseWrapper().addMessage("失败").ExeFaild(ReturnCodeEnum.FAILED.getCode());
    }

    @ApiOperation(value = "门诊缴费确认(可多个单据)")
    @RequestMapping(value="/batchpayConfirm", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject outpatientPaymentConfirm(HttpServletRequest request, HttpServletResponse response,
                @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                @ApiParam(value = "单据ID，可以多个单据，以','分隔", required = true) @RequestParam(value = "docIds", required = true) String docIds,
                @ApiParam(value = "病人ID", required = true) @RequestParam(value = "brId", required = true) String brId,
                @ApiParam(value = "总金额", required = true) @RequestParam(value = "price", required = true) BigDecimal price,
                @ApiParam(value = "总结算金额", required = false) @RequestParam(value = "payPrice", required = false) BigDecimal payPrice,
                @ApiParam(value = "单据类型，1-收费单，4-挂号单", required = true) @RequestParam(value = "documentType", required = true) Integer documentType,
                @ApiParam(value = "订单号", required = true) @RequestParam(value = "orderNumber", required = true) Long orderNumber) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
              return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        //boolean rslt = outpatientService.batchpayConfirm(docIds, brId, zje, jsje, sfghd, orderId);
        return true ? ResponseWrapper().addMessage("成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode()) :
                      ResponseWrapper().addMessage("失败").ExeFaild(ReturnCodeEnum.FAILED.getCode());
    }

    @ApiOperation(value = "门诊记录", response = OutpatientVisitRecordVO.class)
    @RequestMapping(value="/visitRecord", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryVisitRecord(HttpServletRequest request, HttpServletResponse response,
                @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
                @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        List<OutpatientVisitRecordVO> recordVOList = outpatientService.queryVisitRecord(accountId, pageNum, pageSize);
        return ResponseWrapper().addData(recordVOList).addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "门诊记录的单据费用信息", response = OutpatientVisitReceiptVO.class)
    @RequestMapping(value="/visitReceipt", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject visitReceipt(HttpServletRequest request, HttpServletResponse response,
                                       @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                                       @ApiParam(value = "挂号单号", required = false) @RequestParam(value = "registerNum", required = false) String registerNum) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        List<OutpatientVisitReceiptVO> receiptVOList = outpatientService.queryVisitReceipt(registerNum);
        return ResponseWrapper().addData(receiptVOList).addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "门诊记录的单据处方信息", response = OutpatientVisitPrescriptionVO.class)
    @RequestMapping(value="/visitPrescription", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryVisitPrescription(HttpServletRequest request, HttpServletResponse response,
                                           @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                                           @ApiParam(value = "挂号单号", required = false) @RequestParam(value = "registerNum", required = false) String registerNum) throws Exception {
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        List<OutpatientVisitPrescriptionVO> prescriptionVOList = outpatientService.queryVisitPrescription(registerNum);
        return ResponseWrapper().addData(prescriptionVOList).addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }
}

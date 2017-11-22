package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.protocol.IMAccountVO;
import com.jishi.reservation.controller.protocol.IMChatInfo;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.dao.models.IMAccessRecord;
import com.jishi.reservation.dao.models.IMAccount;
import com.jishi.reservation.otherService.im.neteasy.model.IMUser;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.IMAccountService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by liangxiong on 2017/10/27.
 */
@RestController
@RequestMapping("/im")
@Slf4j
@Api(description = "IM用户账号相关接口")
public class IMAccountController extends MyBaseController {

    @Autowired
    AccountService accountService;

    @Autowired
    private IMAccountService imAccountService;

    @ApiOperation(value = "获取普通用户im账号，token可用于im客户端登录，没有则创建", response = IMAccountVO.class)
    @RequestMapping(value = "/getUserAccount", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getUserAccount(@RequestAttribute(value="accountId") Long accountId) throws Exception {

        IMAccount imAccount = imAccountService.getUserIMAccount(accountId);
        IMAccountVO imAccountVO =new IMAccountVO();
        imAccountVO.setImAccId(imAccount.getImAccId());
        imAccountVO.setImToken(imAccount.getImToken());
        return ResponseWrapper().addData(imAccountVO).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "获取医生im账号，token可用于im客户端登录，没有则创建", response = IMAccountVO.class)
    @RequestMapping(value = "/doctor/getDoctorAccount", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getDoctorAccount(
                  @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {
        IMAccount imAccount = imAccountService.getDoctorIMAccount(doctorId);
        IMAccountVO imAccountVO =new IMAccountVO();
        imAccountVO.setImAccId(imAccount.getImAccId());
        imAccountVO.setImToken(imAccount.getImToken());
        return ResponseWrapper().addData(imAccountVO).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "不建议使用的接口，获取普通用户im token", response = String.class)
    @RequestMapping(value = "/getUserToken", method = RequestMethod.GET)
    @ResponseBody
    @Deprecated
    public JSONObject getUserToken(@RequestAttribute(value="accountId") Long accountId) throws Exception {
        String imToken = imAccountService.getUserIMAccount(accountId).getImToken();
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "更新普通用户im token，token失效时调用", response = String.class)
    @RequestMapping(value = "/refreshUserToken", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject refreshUserToken(@RequestAttribute(value="accountId") Long accountId) throws Exception {
        String imToken = imAccountService.refreshUserToken(accountId);
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "更新医生im token，token失效时调用", response = String.class)
    @RequestMapping(value = "/doctor/refreshDoctorToken", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject refreshDoctorToken(
                                 @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {
        String imToken = imAccountService.refreshDoctorToken(doctorId);
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "获取聊天信息，医生im账户，用户im账号和token", response = IMChatInfo.class)
    @RequestMapping(value = "/chatToDocter", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject chatToDocter(@RequestAttribute(value="accountId") Long accountId,
                                         @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {
        IMChatInfo info = imAccountService.chatToDocter(accountId, doctorId);
        return ResponseWrapper().addData(info).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "获取咨询医生历史列表", response = Doctor.class)
    @RequestMapping(value = "/visitHistory", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject visitHistory(@RequestAttribute(value="accountId") Long accountId) throws Exception {
        List<Doctor> imAccessRecordList = imAccountService.queryUserIMAccessRecord(accountId);
        return ResponseWrapper().addData(imAccessRecordList).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "更新咨询医生时间", response = Doctor.class)
    @RequestMapping(value = "/updateVisitTime", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updateVisitTime(@RequestAttribute(value="accountId") Long accountId,
                      @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {
        boolean rslt = imAccountService.updateVisitRecord(accountId, doctorId);
        return rslt ? ResponseWrapper().addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode())
                      : ResponseWrapper().addMessage("failed").ExeSuccess(ReturnCodeEnum.FAILED.getCode());
    }

    @ApiOperation(value = "获取用户IM账号信息", response = IMUser.class)
    @RequestMapping(value = "/getUserIMDetail", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getUserDetail(@RequestAttribute(value="accountId") Long accountId) throws Exception {
        IMUser imUserVO = imAccountService.queryUser(accountId, false);
        return ResponseWrapper().addMessage("success").addData(imUserVO).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "获取医生IM账号信息", response = IMUser.class)
    @RequestMapping(value = "/doctor/getDoctorIMDetail", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getDoctorDetail(
                          @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {

        // TODO 医生账号怎么验证账号已登录
        IMUser imUserVO = imAccountService.queryUser(doctorId, true);
        return ResponseWrapper().addMessage("success").addData(imUserVO).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }
}

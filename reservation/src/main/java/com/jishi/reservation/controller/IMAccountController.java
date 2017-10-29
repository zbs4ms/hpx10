package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.protocol.IMChatInfo;
import com.jishi.reservation.dao.models.IMAccount;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.IMAccountService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @ApiOperation(value = "获取普通用户im token，该token可用于im客户端登录，没有则创建", response = String.class)
    @RequestMapping(value = "/getUserToken", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getUserToken(HttpServletRequest request, HttpServletResponse response,
                       @ApiParam(value = "accountId", required = false) @RequestParam(value = "accountId", required = false) Long accountId) throws Exception {

        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }

        String imToken = imAccountService.getUserIMAccount(accountId).getImToken();
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "获取医生im token，该token可用于im客户端登录，没有则创建", response = String.class)
    @RequestMapping(value = "/getDoctorToken", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getDoctorToken(HttpServletRequest request, HttpServletResponse response,
                               @ApiParam(value = "accountId", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                               @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {

        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        String imToken = imAccountService.getDoctorIMAccount(doctorId).getImToken();
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "更新普通用户im token，token失效时调用", response = String.class)
    @RequestMapping(value = "/refreshUserToken", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject refreshUserToken(HttpServletRequest request, HttpServletResponse response,
                                 @ApiParam(value = "accountId", required = false) @RequestParam(value = "accountId", required = false) Long accountId) throws Exception {

        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        String imToken = imAccountService.refreshUserToken(accountId);
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "更新医生im token，token失效时调用", response = String.class)
    @RequestMapping(value = "/refreshDoctorToken", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject refreshDoctorToken(HttpServletRequest request, HttpServletResponse response,
                                 @ApiParam(value = "accountId", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                                 @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {

        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        String imToken = imAccountService.refreshDoctorToken(doctorId);
        return ResponseWrapper().addData(imToken).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

  @ApiOperation(value = "获取聊天信息，医生im账户，用户im账号和token", response = String.class)
  @RequestMapping(value = "/chatToDocter", method = RequestMethod.PUT)
  @ResponseBody
  public JSONObject chatToDocter(HttpServletRequest request, HttpServletResponse response,
                                       @ApiParam(value = "accountId", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                                       @ApiParam(value = "doctorId", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId) throws Exception {

      if (accountId == null) {
          accountId = accountService.returnIdByToken(request);
          if(accountId.equals(-1L)){
              return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
          }
      }
      IMAccount imUserAccount = imAccountService.getUserIMAccount(accountId);
      IMAccount imDoctorAccount = imAccountService.getDoctorIMAccount(doctorId);
      IMChatInfo info = new IMChatInfo();
      info.setImSourceId(imUserAccount.getImAccId());
      info.setImDestId(imDoctorAccount.getImAccId());
      info.setImToken(imUserAccount.getImToken());
      return ResponseWrapper().addData(info).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
  }
}

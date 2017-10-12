package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.protocol.LoginData;
import com.jishi.reservation.dao.models.Account;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.enumPackage.SmsEnum;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/his_account")
@Slf4j
@Api(description = "对接了his系统的账号相关接口")
public class HisAccountController extends BaseController{

    @Autowired
    AccountService accountService;

    @ApiOperation(value = "新病人注册登记")
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject register(
            @ApiParam(value = "证件号") @RequestParam(value = "idNumber") String idNumber,
            @ApiParam(value = "证件类型") @RequestParam(value = "idNumberType") String idNumberType,
            @ApiParam(value = "姓名") @RequestParam(value = "name") String name,
            @ApiParam(value = "手机号") @RequestParam(value = "phone") String phone
            ) throws Exception {
        Preconditions.checkNotNull(idNumber,"请传入所需要的参数：idNumber");
        Preconditions.checkNotNull(idNumberType,"请传入所需要的参数：idNumberType");
        Preconditions.checkNotNull(name,"请传入所需要的参数：name");
        Preconditions.checkNotNull(phone,"请传入所需要的参数：phone");


        LoginData loginData = accountService.registerWithHis(idNumber,idNumberType ,name,phone);
        return ResponseWrapper().addData(loginData).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }



}

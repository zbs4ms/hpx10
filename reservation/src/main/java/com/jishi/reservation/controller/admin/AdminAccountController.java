package com.jishi.reservation.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.AccountDetailVO;
import com.jishi.reservation.controller.protocol.AdminLogInfoData;
import com.jishi.reservation.controller.protocol.DoctorVO;
import com.jishi.reservation.controller.protocol.SystemInfo;
import com.jishi.reservation.dao.models.Account;
import com.jishi.reservation.dao.models.Manager;
import com.jishi.reservation.dao.models.Permission;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.ManagerService;
import com.jishi.reservation.service.PermissionService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.util.Constant;
import com.jishi.reservation.util.CookieUtil;
import com.jishi.reservation.util.NetUtil;
import com.jishi.reservation.util.SessionUtil;
import com.us.base.util.MD5Encryption;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/ad/account")
@Slf4j
@Api(description = "后台管理平台的账号相关接口")
public class AdminAccountController extends MyBaseController {


    @Autowired
    AccountService accountService;


    @ApiOperation(value = "查询单个账号",response=Account.class)
    @RequestMapping(value = "queryAccount", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAccount(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "电话", required = false) @RequestParam(value = "phone", required = false) String phone) throws Exception {
        List<Account> accountList = accountService.queryAccount(accountId,phone, EnableEnum.EFFECTIVE.getCode());
        return ResponseWrapper().addData(accountList).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "查询全部账号",response=Account.class)
    @RequestMapping(value = "queryAllAccount", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllAccount(
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception {
        List<Account> accountList = accountService.queryAllAccount();
        return ResponseWrapper().addData(accountList).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "禁用单个账号")
    @RequestMapping(value = "failureAccount", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject failureAccount(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "电话", required = false) @RequestParam(value = "phone", required = false) String phone) throws Exception {
        accountService.failureAccount(accountId,phone);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }


    @ApiOperation(value = "admin 查询用户列表",response=DoctorVO.class)
    @RequestMapping(value = "queryUser", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAccountAdmin(
            @ApiParam(value = "查询的名字", required = false) @RequestParam(value = "key", required = false) String key,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "startPage", defaultValue = "1") Integer startPage,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) throws Exception {


        PageInfo<Account> page = accountService.queryAccountPage(key,startPage,pageSize);
        return ResponseWrapper().addData(page).addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }



    @ApiOperation(value = "admin 用户二级页面查询接口",response=AccountDetailVO.class)
    @RequestMapping(value = "queryUserDetail", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryUserDetail(
            @ApiParam(value = "用户的id", required = true) @RequestParam(value = "accountId", required = true) Long accountId
    ) throws Exception {

        Account account = accountService.queryUserDetail(accountId);
        return ResponseWrapper().addData(account).addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

}

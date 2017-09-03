package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.dao.models.Account;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/account")
@Slf4j
@Api(description = "账号相关接口")
public class AccountController extends BaseController{

    @Autowired
    AccountService accountService;

    @ApiOperation(value = "采用手机进行动态码登陆和注册(如果已经注册就走登陆,如果没有注册,先注册再登陆)")
    @RequestMapping(value = "loginOrRegisterThroughPhone", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject loginOrRegisterThroughPhone(
            @ApiParam(value = "电话", required = true) @RequestParam(value = "phone", required = true) String phone,
            @ApiParam(value = "动态码", required = true) @RequestParam(value = "dynamicCode", required = true) String dynamicCode) throws Exception {
        accountService.loginOrRegisterThroughPhone(phone,dynamicCode);

        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "账号密码登陆")
    @RequestMapping(value = "loginByPassword", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject loginByAccountAndPassword(
            @ApiParam(value = "账号", required = true) @RequestParam(value = "account", required = true) String account,
            @ApiParam(value = "密码", required = true) @RequestParam(value = "password", required = true) String password) throws Exception {
        Account result = accountService.loginByTelephoneAndPassword(account,password);
        return ResponseWrapper().addData("ok").addData(result).ExeSuccess();
    }



    @ApiOperation(value = "发送动态验证码")
    @RequestMapping(value = "sendDynamicCode", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject sendDynamicCode(
            @ApiParam(value = "电话", required = true) @RequestParam(value = "phone", required = true) String phone) throws Exception {
        String code = accountService.sendDynamicCode(phone);
        return ResponseWrapper().addData(code).ExeSuccess();
    }

    @ApiOperation(value = "传统的增加账号")
    @RequestMapping(value = "addAccount", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject addAccount(
            @ApiParam(value = "账号", required = true) @RequestParam(value = "account", required = true) String account,
            @ApiParam(value = "账号密码", required = true) @RequestParam(value = "passwd", required = true) String passwd,
            @ApiParam(value = "头像", required = false) @RequestParam(value = "headPortrait", required = false) String headPortrait,
            @ApiParam(value = "昵称", required = false) @RequestParam(value = "nick", required = false) String nick,
            @ApiParam(value = "电话", required = true) @RequestParam(value = "phone", required = true) String phone,
            @ApiParam(value = "邮箱", required = false) @RequestParam(value = "email", required = false) String email) throws Exception {
        accountService.addAccount(account,passwd,headPortrait,nick,phone,email);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "修改账号信息")
    @RequestMapping(value = "modifyAccountInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyAccountInfo(
            @ApiParam(value = "账号ID", required = true) @RequestParam(value = "accountId", required = true) Long accountId,
            @ApiParam(value = "昵称", required = false) @RequestParam(value = "nick", required = false) String nick,
            @ApiParam(value = "头像", required = false) @RequestParam(value = "headPortrait", required = false) String headPortrait,
            @ApiParam(value = "邮箱", required = false) @RequestParam(value = "email", required = false) String email) throws Exception {
        accountService.modifyAccountInfo(accountId,null,nick,headPortrait,email,null,null);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "修改账号的密码")
    @RequestMapping(value = "modifyAccountPasswd", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyAccountPasswd(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "电话", required = false) @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(value = "老密码", required = true) @RequestParam(value = "oldPasswd", required = true) String oldPasswd,
            @ApiParam(value = "新密码", required = true) @RequestParam(value = "newPasswd", required = true) String newPasswd) throws Exception {
        accountService.modifyAccountPasswd(accountId,phone,oldPasswd,newPasswd);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "修改账号绑定手机")
    @RequestMapping(value = "modifyAccountPhone", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyAccountPhone(
            @ApiParam(value = "账号ID", required = true) @RequestParam(value = "accountId", required = true) Long accountId,
            @ApiParam(value = "电话", required = true) @RequestParam(value = "phone", required = true) String phone) throws Exception {
        accountService.modifyAccountPhone(accountId,phone);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "查询单个账号",response=Account.class)
    @RequestMapping(value = "queryAccount", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAccount(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "电话", required = false) @RequestParam(value = "phone", required = false) String phone) throws Exception {
        List<Account> accountList = accountService.queryAccount(accountId,phone, EnableEnum.EFFECTIVE.getCode());
        return ResponseWrapper().addData(accountList).ExeSuccess();
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
        return ResponseWrapper().addData(accountList).ExeSuccess();
    }

    @ApiOperation(value = "禁用单个账号")
    @RequestMapping(value = "failureAccount", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject failureAccount(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "电话", required = false) @RequestParam(value = "phone", required = false) String phone) throws Exception {
        accountService.failureAccount(accountId,phone);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

//    public static void main(String[] args) throws ClientException {
//        //初始化ascClient需要的几个参数
//        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
//        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
//        //替换成你的AK
//        final String accessKeyId = "LTAIm15EE0fsgUe7";//你的accessKeyId,参考本文档步骤2
//        final String accessKeySecret = "rSGj1egwmEovEnLVSLQKVLlc6OfeBW";//你的accessKeySecret，参考本文档步骤2
//
//        IClientProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyId,
//                accessKeySecret);
//        DefaultProfile.addEndpoint("cn-shenzhen", "cn-shenzhen", product, domain);
//        IAcsClient acsClient = new DefaultAcsClient(profile);
//
//        //组装请求对象
//        SendSmsRequest request = new SendSmsRequest();
//        //使用post提交
//        request.setMethod(MethodType.POST);
//
//        //短信推荐使用单条调用的方式
//        request.setPhoneNumbers("17360036991");
//        //必填:短信签名-可在短信控制台中找到
//        request.setSignName("泸州锦欣妇产医院");
//        //必填:短信模板-可在短信控制台中找到
//        request.setTemplateCode("SMS_90995042");
//        request.setTemplateParam("{\"name\":\"csrr\", \"code\":\"666666\"}");
//
//        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
//        log.info("返回结果："+JSONObject.toJSONString(sendSmsResponse));
//        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
////请求成功
//        }
//    }
}

package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.protocol.AdminLogInfoData;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.ManagerService;
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


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@Api(description = "后台管理接口")
public class AdminController extends BaseController {

    @Autowired
    ManagerService managerService;


    /**
     * @param
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "登陆接口", notes = "")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject login(@ApiParam(value = "账号 示例数据:admin") @RequestParam(value = "account") String account,
                            @ApiParam(value = "密码 示例数据:admin") @RequestParam(value = "password") String password,
                            @RequestParam(value = "appKey", required = false) String appKey,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {


        Preconditions.checkNotNull(account, "请传入必要的参数：account");
        Preconditions.checkNotNull(password, "请传入必要的参数：password");

        Manager manager = managerService.findAccountByAccount(account);
        password = MD5Encryption.getMD5(password);
        //Preconditions.checkState(password.equals(account.getPassword()), "用户名或密码错误,请重新登录");
        if (!password.equals(manager.getPassword())) {
            return ResponseWrapper().addMessage("用户名或密码错误,请重新登录").ExeFaild();
        } else {

            //todo ...支持多登   有token就返回token,不覆盖；没有再生成
            String token = managerService.loginByAccountAndPwd(manager.getId());

            AdminLogInfoData logInfoData = new AdminLogInfoData();
            logInfoData.setToken(token);

            logInfoData.setAccount(manager.getAccount());

            //token存放到cookie,并设置httpOnly为true
            CookieUtil.addCookie(response, Common.ADMIN_TOKEN, token, 28880, true);
            //token存放到session
            SessionUtil.addSession(request, Common.ADMIN_TOKEN, token);

            return ResponseWrapper().addMessage("登陆成功！").addData(logInfoData).ExeSuccess(200);
        }
    }



    @ApiOperation(value = "退出登陆接口")
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject logout(HttpServletRequest request,HttpServletResponse response) throws Exception {


        String token = CookieUtil.getCookieByName(request,Common.ADMIN_TOKEN).getValue();
        SessionUtil.deleteSession(request,token);
        CookieUtil.deleteCookieByName(request,response,token);
        managerService.logout(token);

        return ResponseWrapper().addMessage("退出成功").ExeSuccess(200);

    }
}

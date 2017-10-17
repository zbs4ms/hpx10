package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.AdminLogInfoData;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.ManagerService;
import com.jishi.reservation.service.PermissionService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
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
import java.util.List;


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

    @Autowired
    PermissionService permissionService;


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
            //处理管理员的权限
            Gson gson = new Gson();
            List<String> list = gson.fromJson(manager.getPermission(),
                    new TypeToken<List<String>>() {
                    }.getType());

            logInfoData.setPermissionList(list);
            //token存放到cookie,并设置httpOnly为true
            CookieUtil.addCookie(response, Common.ADMIN_TOKEN, token, 28880, true);
            //token存放到session
            SessionUtil.addSession(request, Common.ADMIN_TOKEN, token);

            return ResponseWrapper().addMessage("登陆成功！").addData(logInfoData).ExeSuccess(200);
        }
    }






    @ApiOperation(value = "返回所有权限接口")
    @RequestMapping(value = "all_permission", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject all_permission(HttpServletRequest request,HttpServletResponse response) throws Exception {


        List<Permission> list =  permissionService.queryAll();

        return ResponseWrapper().addData(list).addMessage("查询成功").ExeSuccess(200);

    }


    @ApiOperation(value = "创建管理员账号")
    @RequestMapping(value = "crete", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject create(
            @ApiParam(value = "账号") @RequestParam(value = "account") String account,
            @ApiParam(value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(value = "权限列表  json数组格式 permission_id ['m_01','m_02']") @RequestParam(value = "permission") String permission
            ) throws Exception {

        managerService.create(account,password,permission);
        return ResponseWrapper().addMessage("创建成功").ExeSuccess(200);

    }



    @ApiOperation(value = "人员列表接口")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject create(
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc
    ) throws Exception {

        PageInfo<Manager> page =  managerService.queryByPage(Paging.create(pageNum,pageSize,orderBy,desc));
        return ResponseWrapper().addMessage("查询成功").ExeSuccess(200);

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

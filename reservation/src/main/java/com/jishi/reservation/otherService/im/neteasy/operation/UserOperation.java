package com.jishi.reservation.otherService.im.neteasy.operation;

import com.alibaba.fastjson.JSONArray;
import com.jishi.reservation.otherService.im.HttpParam;
import com.jishi.reservation.otherService.im.neteasy.IMHttpNeteasy;
import com.jishi.reservation.otherService.im.neteasy.model.IMUser;
import com.jishi.reservation.otherService.im.neteasy.response.ResComm;
import com.jishi.reservation.otherService.im.neteasy.response.ResUserCreate;

/**
 * Created by liangxiong on 2017/10/24.
 */
public class UserOperation {

    public static final String USER_CREATE = "/user/create.action";
    public static final String USER_UPDATE = "/user/update.action";
    public static final String USER_REFRESH_TOKEN = "/user/refreshToken.action";
    public static final String USER_BLOCK = "/user/block.action";
    public static final String USER_UNBLOCK = "/user/unblock.action";

    public static final String USER_UPDATE_UINFO = "/user/updateUinfo.action";
    public static final String USER_GET_UINFOS = "/user/getUinfos.action";
    public static final String USER_SET_DONNOP = "/user/setDonnop.action";

    private IMHttpNeteasy imHttpNeteasy;

    public UserOperation(IMHttpNeteasy imHttpNeteasy) {
        this.imHttpNeteasy = imHttpNeteasy;
    }

    //创建网易云通信ID
    public IMUser createUser(IMUser user) throws Exception {
        if (user == null) {
            return null;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", user.getAccid(), 32);
        httpParam.add("name", user.getName(), 64);
        httpParam.add("props", user.getProps(), 1024);
        httpParam.add("icon", user.getIcon(), 1024);
        httpParam.add("token", user.getToken(), 128);
        httpParam.add("sign", user.getSign(), 256);
        httpParam.add("email", user.getEmail(), 64);
        httpParam.add("birth", user.getBirth(), 16);
        httpParam.add("mobile", user.getMobile(), 32);
        httpParam.add("gender", user.getGender());
        httpParam.add("ex", user.getEx(), 1024);
        String httpRslt = imHttpNeteasy.doPost(USER_CREATE, httpParam);
        ResUserCreate userCreate = JSONArray.parseObject(httpRslt, ResUserCreate.class);
        return userCreate.getInfo();
    }

    //网易云通信ID更新, props和token
    public boolean userUpdate(String accid, String props, String token) throws Exception {
        if (accid == null || accid.length() == 0) {
            return false;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", accid, 32);

        if (props != null) {
        	httpParam.add("props", props, 1024);
        }
        if (token != null) {
        	httpParam.add("token", token, 128);
        }
        String httpRslt = imHttpNeteasy.doPost(USER_UPDATE, httpParam);
        ResComm userCreate = JSONArray.parseObject(httpRslt, ResComm.class);
        return userCreate.getCode() == 200;
    }

    //更新并获取新token
    public String refreshToken(String accid) throws Exception {
        if (accid == null || accid.length() == 0) {
            return null;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", accid, 32);
        String httpRslt = imHttpNeteasy.doPost(USER_REFRESH_TOKEN, httpParam);
        ResUserCreate userCreate = JSONArray.parseObject(httpRslt, ResUserCreate.class);
        return userCreate.getInfo().getToken();
    }

    //封禁/解禁网易云通信ID
    public boolean enableBlock(String accid, boolean block) throws Exception {
        if (accid == null || accid.length() == 0) {
            return false;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", accid, 32);
        String serverName = block ? USER_BLOCK : USER_UNBLOCK;
        String httpRslt = imHttpNeteasy.doPost(serverName, httpParam);
        ResComm userCreate = JSONArray.parseObject(httpRslt, ResComm.class);
        return userCreate.getCode() == 200;
    }

    //更新用户名片除props和token外
    public boolean updateUinfo(IMUser user) throws Exception {
        if (user == null) {
            return false;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", user.getAccid(), 32);
        httpParam.add("name", user.getName(), 64);
        //httpParam.add("props", user.getProps(), 1024);
        httpParam.add("icon", user.getIcon(), 1024);
        //httpParam.add("token", user.getToken(), 128);
        httpParam.add("sign", user.getSign(), 256);
        httpParam.add("email", user.getEmail(), 64);
        httpParam.add("birth", user.getBirth(), 16);
        httpParam.add("mobile", user.getMobile(), 32);
        httpParam.add("gender", user.getGender());
        httpParam.add("ex", user.getEx(), 1024);
        String httpRslt = imHttpNeteasy.doPost(USER_CREATE, httpParam);
        ResComm userCreate = JSONArray.parseObject(httpRslt, ResComm.class);
        return userCreate.getCode() == 200;
    }

    //获取用户名片
    public IMUser getUinfos(String accid) throws Exception {
        if (accid == null) {
            return null;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", accid, 32);
        String httpRslt = imHttpNeteasy.doPost(USER_GET_UINFOS, httpParam);
        return JSONArray.parseObject(httpRslt, IMUser.class);
    }

    //设置桌面端在线时，移动端是否需要推送
    public boolean setDonnop(String accid, boolean isDonnop) throws Exception {
        if (accid == null) {
            return false;
        }
        HttpParam httpParam = new HttpParam();
        httpParam.add("accid", accid, 32);
        httpParam.add("donnopOpen", Boolean.toString(isDonnop).toLowerCase());
        String httpRslt = imHttpNeteasy.doPost(USER_SET_DONNOP, httpParam);
        ResComm res = JSONArray.parseObject(httpRslt, ResComm.class);
        return res.getCode() == 200;
    }
}

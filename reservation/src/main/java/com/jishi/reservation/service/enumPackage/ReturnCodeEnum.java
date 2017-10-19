package com.jishi.reservation.service.enumPackage;

/**
 * Created by zbs on 2017/8/10.
 */

public enum ReturnCodeEnum {

    SUCCESS(200, "成功"),
    NOT_LOGIN(401, "登录信息过期"),
    FAILED(406, "失败"),
    ERR(500, "系统错误");

    private int code;
    private String desc;

    ReturnCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package com.jishi.reservation.service.enumPackage;

/**
 * Created by zbs on 2017/8/10.
 */

public enum ReturnCodeEnum {

    SUCCESS(200, "成功"),
    NOT_LOGIN(401, "登陆信息已过期，请重新登陆！"),
    FAILED(406, "失败"),
    ERR(500, "系统错误"),

    /** 门诊相关 10100-10199 **/
    OUTPATIENT_ERR_CONFIRM_ORDER_NULL(10100, "缴费记录不存在"),
    OUTPATIENT_ERR_CONFIRM_PAYMENT_NULL(10101, "订单不存在"),
    OUTPATIENT_ERR_CONFIRM_ORDER_NOT_MATCH(10102, "订单不匹配"),
    OUTPATIENT_ERR_CONFIRM_NO_PAY(10103, "订单未支付"),
    OUTPATIENT_ERR_CONFIRM_ORDER_TYPE_NOT_MATCH(10104, "订单类型不匹配"),
    OUTPATIENT_ERR_CONFIRM_FAILED(10105, "支付确认失败"),
    OUTPATIENT_ERR_GENERATE_ORDER_ATTR_NULL(10106, "订单相关信息缺失"),

    UNKNOWN_ERR(10000, "未知错误");

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

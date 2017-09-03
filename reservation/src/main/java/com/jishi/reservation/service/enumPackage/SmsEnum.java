package com.jishi.reservation.service.enumPackage;

/**
 * Created by sloan on 2017/9/1.
 */
public enum  SmsEnum {

    LOGIN_REGISTER("SMS_90995042","登录注册验证码"),
    CHANGE_BOUNDLE_TELEPHONE("SMS_90975048","换绑手机号");

    private String templateCode;
    private String name;
    SmsEnum(String templateCode,String name){
        this.templateCode = templateCode;
        this.name = name;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

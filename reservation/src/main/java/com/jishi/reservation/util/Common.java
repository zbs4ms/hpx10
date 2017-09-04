package com.jishi.reservation.util;

/**
 * Created by zbs on 2017/7/25.
 */
public class Common {

    public final static String TOKEN_HEADER = "token_";

    public final static String SMS_URL="http://121.40.152.170:9087/system-sms/api/send";
    public final static String SMS_TYPE="SystemWarning";
    public final static String SMS_shopId="jskj";
    public final static int SMS_NUMBER=1;
    public final static int SIX_HOURS = 6*60*60*1000;
    public final static String AFTERNOON = "下午";
    public final static String MORNING = "上午";

    public final static String HOSPITAL_NAME = "泸州锦欣妇产医院";




    /**  阿里相关      */
    public final static String ACCESS_KEY_ID = "LTAIm15EE0fsgUe7";
    public final static String ACCESS_KEY_SECRET = "rSGj1egwmEovEnLVSLQKVLlc6OfeBW";
    public final static String END_POINT = "oss-cn-shenzhen.aliyuncs.com";
    public final static String REGION = "oss-cn-shenzhen";
    public final static String BUCKET_NAME = "jishikeji-hospital";
    public final static String BATH_ALI_URL = "http://jishikeji-hospital.oss-cn-shenzhen.aliyuncs.com/";
    public final static String BANNER_PATH = "image/banner/";
    public final static String DOCTOR_PATH = "image/doctor/";

    //初始化ascClient需要的几个参数  阿里大鱼
    public final static String PRODUCT = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    public final static String DOMAIN = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
    public final static String REGION_ID =  "cn-shenzhen";

}

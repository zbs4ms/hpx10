package com.jishi.reservation.util;

/**
 * Created by zbs on 2017/7/25.
 */
public class Constant {

    /****------------   支付类型   ---------------------------
    /** 订单支付类型 --> 1 : 阿里 **/
    public final static String ORDER_PAY_TYPE_AL = "alipay";
    /** 订单支付类型 --> 2 : 微信 **/
    public final static String ORDER_PAY_TYPE_WX = "wechat";
    /****------------   阿里支付   ---------------------------
     /**  阿里网关 **/
    public final static String ALIPAY_GATEWAY = "";
    /**  应用APP_ID **/
    public final static String ALIPAY_APP_ID = "";
    /**  应用私钥 **/
    public final static String ALIPAY_PRIVATE_KEY = "";
    /**  支付宝公钥 **/
    public final static String ALIPAY_PUBLIC_KEY = "";
    /**  签名类型 **/
    public final static String ALIPAY_SIGN_TYPE = "";
    /**  编码格式 **/
    public final static String ALIPAY_CHARSET = "";
    /****------------   微信支付   ---------------------------
     /**  微信网关 **/

    public final static String APPID_WECHAT = "";
    public final static String MCHID_WECHAT = "";
    public final static String APP_NAME_WECHAT = "";

    public final static String HIS_TOKEN="359894CB16E4B68531A11083F2046B0E";
    public final static String HIS_KEYS = "929A715701492111";
    public final static String TOKEN_HEADER = "token_";
    public final static String TOKEN = "token";

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

    public final static String DEFAULT_AVATAR = "http://jishikeji-hospital.oss-cn-shenzhen.aliyuncs.com/image/user/icon.png";
    public final static String ADMIN_TOKEN = "admin_token";



    /**  中联相关 */
    public final static String BASE_URL = "http://hpx10ddns.xicp.io/ExternalServices/ZL_InformationService.asmx/";
    //测试连接的服务
    public final static String TEST_SERVICE = "Basic.MCTest.Query";

    /** 极光相关 */
    public final static String JPush_Appkey  = "0e3fcd7d467475b43a56f2a0";
    public final static String JPush_MASTER_SECRET = "52977f5e4b85a5b2e0e257ae";


    public final static String REGISTER_SUCCESS_MGS = "您已成功预约医生,记得按时就诊哦~";
    public final static String REGISTER_TOMORROW_MSG = "明天预约了医生看诊,记得按时就诊哦~";
    public final static String REGISTER_TODAY_MSG = "今天预约了医生看诊,记得按时就诊哦~";
    /**
     * 一天的毫秒值
     */
    public final static long DAY_MS = 24*60*60*1000L;
    //12345678952
    public final static String DEFAULT_PASSWORD = "def677132701779c74c9bd95dae4de57";
    /**
     * 每个账号最大的绑定个数 5
     */
    public final static int MAX_BINDING_NUM = 5;
    public final static String TEST_PHONE = "13401154497,18349226649,18215603517";


    /** =========================   网易云信相关    ==============================**/
    public static final String IM_NETEASY_APPKEY = "b183b56ab35717b01f2ad925a8e6cc89";
    public static final String IM_NETEASY_APPSECRET = "8071e83e5b74";
    public static final int IM_HISTORY_VISIT_DOCTOR_SIZE = 10;  //用户端默认只返回10条咨询过的医生记录
}

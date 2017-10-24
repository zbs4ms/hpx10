package com.jishi.reservation.util;

/**
 * Created by sloan on 2017/10/16.
 */
public class PayConstant {

    //阿里支付相关

    //APP_ID  锦欣通
    public final static String APP_ID = "2017092508916490";
    //应用私钥
    public final static String APP_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANuHXf6onPItABd9\n" +
            "3MkHo591fO+g6b1HyUPQar3FUEUHp4fAEN1y27IUCOtd/ooxaxwTEa2xHakbNwLO\n" +
            "/bFZh6JOOBkTuvpP2HrbMppmltNrEDD4AuuotQpoezfhv8gXk6TQ4cCrxcD7ISAn\n" +
            "srlAGGkMSuvy9kVgajCqgLBEf/atAgMBAAECgYAFKye8Yi3nycVxyCXVmddxXqvF\n" +
            "XI2ERldnwFVzCF0+ctKv8Ia1IAoqJCydlgXzLPyvmYdWBpCDNb/S0hrI/SmjTnNB\n" +
            "A5DryOerSHRViiqcd2WUUpbs08zdXnkXNROjnGc6rO4SlJiJGFwcEE+SiXSrwaLb\n" +
            "pRaa3+wGexIimBnFxQJBAPu+pDtXF76jchmvyT5xsAKdRPKE1szr9HcWo+OhjK9r\n" +
            "zCOI35aBfrtSZ52OTb2kPoOTORud8sWM1CupyV7kQI8CQQDfPVHeNTS4yNROw7Bj\n" +
            "rpK7kAC7dI4vSiLT81BwdLko+GIGUSmkGKzydY5+lsHxvsprv0iq1usnvUYU4ucw\n" +
            "ifsDAkBR41N80fmxU21cGag2nbiJQSsaUEem5OHikwTAmN4webovnOhOBtV4jXeS\n" +
            "8qUrGPAU0KD08M31oLerdFFCgrDVAkAVKdYKD9c4sWlXMjwVpyYn++Xl343fM9ML\n" +
            "buSwVJB9Ltb0LFubP++daMGihJ5MUb8U6eR0tb1DXjE3p68tLdeLAkB21aSqCSnV\n" +
            "vyQa8N37v3IupessTgy6Kvv3vFrpmM9FdShshkX91syo3unyRFiV7JWpBI4NN6qD\n" +
            "jho+CEaoyXJC";
    public final static String APP_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi2LVKOO2Kx20jYWJUf8yYsmw/QCmjZzwIwb3gYYPMqLAq7jIfZG8PpWpu4tj0TJTA1Nv7a2I0GhmnlGE/9qNOaJtpxoV9Ff5+ZdaPEyt2klq4K7K2y0AMjuQzJR/U5schjs2Z49eVTQ11EL0PCb8yJHy+I7kMLZ0umAlv4FcvId3tIQ8SyQiUcKLd/CHAE75thoMVd1Dy4161RHrEO9oulkaltqeOZQHC98KLHRhy/J8lKC/x7FCOReCxSIufpb69uUtalbkC7TyhdBhdlZTEKK3gRTSjYreDsesZ5sqZkrGcZKWl418l64uFNKWU/VIhf9NwkWso333VrdWF94vfQIDAQAB";


    public final static String SERVER_URL = "https://openapi.alipay.com/gateway.do";
    public final static String DATA_FORMAT = "json";
    public final static String CHARSET = "utf-8";
    public final static String ENCRYPT = "RSA2";
    //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
    public final static String QUICK_MSECURITY_PAY = "QUICK_MSECURITY_PAY";

    //该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
    //注：若为空，则默认为15d。
    public final static String TIME_OUT_EXPRESS = "30m";

}

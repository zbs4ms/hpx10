package com.jishi.reservation.util;

/**
 * Created by sloan on 2017/10/16.
 */
public class PayConstant {

    //阿里支付相关

    //APP_ID  锦欣通
    public final static String APP_ID = "2017092508916490";
    //应用私钥

    public final static String APP_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANuHXf6onPItABd9" +
            "3MkHo591fO+g6b1HyUPQar3FUEUHp4fAEN1y27IUCOtd/ooxaxwTEa2xHakbNwLO" +
            "/bFZh6JOOBkTuvpP2HrbMppmltNrEDD4AuuotQpoezfhv8gXk6TQ4cCrxcD7ISAn" +
            "srlAGGkMSuvy9kVgajCqgLBEf/atAgMBAAECgYAFKye8Yi3nycVxyCXVmddxXqvF" +
            "XI2ERldnwFVzCF0+ctKv8Ia1IAoqJCydlgXzLPyvmYdWBpCDNb/S0hrI/SmjTnNB" +
            "A5DryOerSHRViiqcd2WUUpbs08zdXnkXNROjnGc6rO4SlJiJGFwcEE+SiXSrwaLb" +
            "pRaa3+wGexIimBnFxQJBAPu+pDtXF76jchmvyT5xsAKdRPKE1szr9HcWo+OhjK9r" +
            "zCOI35aBfrtSZ52OTb2kPoOTORud8sWM1CupyV7kQI8CQQDfPVHeNTS4yNROw7Bj" +
            "rpK7kAC7dI4vSiLT81BwdLko+GIGUSmkGKzydY5+lsHxvsprv0iq1usnvUYU4ucw" +
            "ifsDAkBR41N80fmxU21cGag2nbiJQSsaUEem5OHikwTAmN4webovnOhOBtV4jXeS" +
            "8qUrGPAU0KD08M31oLerdFFCgrDVAkAVKdYKD9c4sWlXMjwVpyYn++Xl343fM9ML" +
            "buSwVJB9Ltb0LFubP++daMGihJ5MUb8U6eR0tb1DXjE3p68tLdeLAkB21aSqCSnV" +
            "vyQa8N37v3IupessTgy6Kvv3vFrpmM9FdShshkX91syo3unyRFiV7JWpBI4NN6qD" +
            "jho+CEaoyXJC";

   //应用公钥
    public final static String APP_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbh13+qJzyLQAXfdzJB6OfdXzv" +
            "oOm9R8lD0Gq9xVBFB6eHwBDdctuyFAjrXf6KMWscExGtsR2pGzcCzv2xWYeiTjgZ" +
            "E7r6T9h62zKaZpbTaxAw+ALrqLUKaHs34b/IF5Ok0OHAq8XA+yEgJ7K5QBhpDErr" +
            "8vZFYGowqoCwRH/2rQIDAQAB";

    public final static String SERVER_URL = "https://openapi.alipay.com/gateway.do";
    public final static String DATA_FORMAT = "json";
    public final static String CHARSET = "utf-8";
    public final static String ENCRYPT = "RSA";
    //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
    public final static String QUICK_MSECURITY_PAY = "QUICK_MSECURITY_PAY";

    //该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
    //注：若为空，则默认为15d。
    public final static String TIME_OUT_EXPRESS = "30m";

}

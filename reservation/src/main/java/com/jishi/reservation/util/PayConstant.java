package com.jishi.reservation.util;

/**
 * Created by sloan on 2017/10/16.
 */
public class PayConstant {

    //阿里支付相关

    //APP_ID  锦欣通
    public final static String APP_ID = "2017092508916490";
    //应用私钥
    public final static String APP_PRIVATE_KEY = "MIICWwIBAAKBgQDbh13+qJzyLQAXfdzJB6OfdXzvoOm9R8lD0Gq9xVBFB6eHwBDdctuyFAjrXf6KMWscExGtsR2pGzcCzv2xWYeiTjgZE7r6T9h62zKaZpbTaxAw+ALrqLUKaHs34b/IF5Ok0OHAq8XA+yEgJ7K5QBhpDErr8vZFYGowqoCwRH/2rQIDAQABAoGABSsnvGIt58nFccgl1ZnXcV6rxVyNhEZXZ8BVcwhdPnLSr/CGtSAKKiQsnZYF8yz8r5mHVgaQgzW/0tIayP0po05zQQOQ68jnq0h0VYoqnHdllFKW7NPM3V55FzUTo5xnOqzuEpSYiRhcHBBPkol0q8Gi26UWmt/sBnsSIpgZxcUCQQD7vqQ7Vxe+o3IZr8k+cbACnUTyhNbM6/R3FqPjoYyva8wjiN+WgX67Umedjk29pD6DkzkbnfLFjNQrqcle5ECPAkEA3z1R3jU0uMjUTsOwY66Su5AAu3SOL0oi0/NQcHS5KPhiBlEppBis8nWOfpbB8b7Ka79IqtbrJ71GFOLnMIn7AwJAUeNTfNH5sVNtXBmoNp24iUErGlBHpuTh4pMEwJjeMHm6L5zoTgbVeI13kvKlKxjwFNCg9PDN9aC3q3RRQoKw1QJAFSnWCg/XOLFpVzI8FacmJ/vl5d+N3zPTC27ksFSQfS7W9Cxbmz/vnWjBooSeTFG/FOnkdLW9Q14xN6evLS3XiwJAdtWkqgkp1b8kGvDd+79yLqXrLE4Muir797xa6ZjPRXUobIZF/dbMqN7p8kRYleyVqQSODTeqg44aPghGqMlyQg==";
    //应用公钥
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

package com.jishi.reservation.util;

/**
 * Created by sloan on 2017/10/16.
 */
public class PayConstant {

    //阿里支付相关

    //APP_ID  锦欣通
    public final static String APP_ID = "2017092508916490";
    //应用私钥
    public final static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCLYtUo47YrHbSNhYlR/zJiybD9AKaNnPAjBveBhg8yosCruMh9kbw+lam7i2PRMlMDU2/trYjQaGaeUYT/2o05om2nGhX0V/n5l1o8TK3aSWrgrsrbLQAyO5DMlH9TmxyGOzZnj15VNDXUQvQ8JvzIkfL4juQwtnS6YCW/gVy8h3e0hDxLJCJRwot38IcATvm2GgxV3UPLjXrVEesQ72i6WRqW2p45lAcL3wosdGHL8nyUoL/HsUI5F4LFIi5+lvr25S1qVuQLtPKF0GF2VlMQoreBFNKNit4Ox6xnmypmSsZxkpaXjXyXri4U0pZT9UiF/03CRayjffdWt1YX3i99AgMBAAECggEABrS1jqcN75sZQanIBoLhmLEda76pRKzlPLosFDYv6v67iEd2ldv6E4S9ysEVB06FgeZ0vxmXOHedYEaJwIlyO/Cdl+mAXuYQp7ppY/dIcfUQSEkAh3TtynzlJYKLcalHwQl+P5M9NZoycMjb2T0C8l9bMiwlvFY63XCcwE7n+EX2BhCcyw4n+Upcg/u9fMjkygWK6QCQXiVBW1djRMW1BQY1LhgB534P7lX6cOx6kfrgjebs1gWenzOKSDQcKeYVNdZB+rZZxH9usmxAh9LGt7prnRAdZ4Y9ARdpUZ47WR64ARv8+Dc3L8HldiJFPfHHDQwE2JPsaherSfFI2wUrQQKBgQC+pdQi4XcChwHnaLTECRHQeMVw1Gf1MrsIdQqwm1ryxPMinnPR6j2mFjj6pV595WZJmWhzhoADpTw4OCS3rO+gUg4euIZZnS9qnnnGMGMaLqi/KxF06YA8W+LOS6g+bTAJfOzBBhzP+4E+MMfsu7AX6ctkBd69bzG4NIRP1herTQKBgQC7KpEDmVcjkmCv4rgcPCPx34a1cWzAc9QxNdQwnzvQwO3zEwR5Xyw0mJwCydUO+f9eMZR4/vvEeoLqHWZVxdxdKtnVNnTV3zNZJgXnq1WqL79nCxgxvrOhrApDmPDQQgKIW1tf0oqfik5zqe34k+JhA+FEirshtGmwRCwjtH+c8QKBgQCtSKz4x1YUxgHFBDbJuDSo+Bz94RJcnC/EEB4XSB3ya3FTFhLPyckfHTPNHmI0rD28x+NYnffdWGLT9rPo/xcO8YZEBhSXf3eJwWGN8o0hTyeyGsNTMKmOMzaDq+DQXQ48kX/gMl4d7MwcatpNIot8MNTdpTg/lsHqXFGm/0GkQQKBgCcqZyILqnJlJRppJQGzjAoHLlc1nv4+vr51dyudhpTOanydFCiZw9RVwv/KYG073zZExb70DJc208JN2KZGOzC70M2PEa/XfxU+oamy7sAoMbNMXQm70DcamyM+E9ZgmcsS4vIQSrXL6VLG0mdb+tk2SKNhLHuHFz6tZVcrZDDhAoGAaAS/b8fCBZnYFHJEE49sqjXfhvkpQ+MeZcKcTyl5h55TTdjjwZCG2t+KJazFBqPV4vUvWSNR7Gw00NtSVRZdHTVaTtb9mFBvKs7nJnr6VoFCRD5NNkFhbBJG97tuK8d4nZEZjENq/GTDKzsIQLHwXsKQWr1M8GVEHif4ZclPcQo=";
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

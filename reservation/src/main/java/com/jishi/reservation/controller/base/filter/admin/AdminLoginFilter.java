package com.jishi.reservation.controller.base.filter.admin;

import com.jishi.reservation.controller.base.filter.user.VerifyLoginFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * Created by liangxiong on 2017/11/22.
 */
@WebFilter(filterName = "AdminBaseLoginFilter", urlPatterns = {
      "/ad/*", "/reservation/ad/*"},
      //添加不进行登录验证的url，可以为实际路径或正则表达式，以','分隔
      initParams = {@WebInitParam(name = VerifyLoginFilter.EXCLUDED_PAGES, value=".*/ad/admin/login")})
public class AdminLoginFilter extends AdminBaseLoginFilter {
}

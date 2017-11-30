package com.jishi.reservation.controller.base.filter.user;

import javax.servlet.annotation.WebFilter;

/**
 * Created by liangxiong on 2017/11/21.
 */
@WebFilter(filterName = "DefaultLoginFilter", urlPatterns = {
        //"/doctor_i/*",
        //"/reservation/doctor_i/*",             //医生相关接口
  "/department/*", "/reservation/department/*",         //科室相关接口

  "/his_account/*", "/reservation/his_account/*",       //对接了his系统的账号相关接口
  "/his_doctor/*", "/reservation/his_doctor/*",         //对接了his系统的醫生相关接口
  "/hospitalization/*", "/reservation/hospitalization/*",//住院相关接口
  "/order/*", "/reservation/order/*",                   //订单相关接口
  "/pregnant/*", "/reservation/pregnant/*"})            //孕妇信息相关接口
public class DefaultLoginFilter extends VerifyLoginFilter {
}

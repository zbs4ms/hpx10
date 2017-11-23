package com.jishi.reservation.controller.base.filter.admin;

import com.jishi.reservation.controller.base.filter.BaseFilter;
import com.jishi.reservation.controller.base.filter.user.VerifyLoginFilter;
import com.jishi.reservation.service.ManagerService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.us.base.util.datawapper.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * admin登录验证父类
 * Created by liangxiong on 2017/11/22.
 */
public class AdminBaseLoginFilter extends BaseFilter {

    private Logger logger = LoggerFactory.getLogger(VerifyLoginFilter.class);

    @Autowired
    private ManagerService managerService;

    private static final String ATTR_ADMIN_LOGIN_ACCOUNT_ID = "adminAccountId";

    @Override
    public void executeFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Long adminAccountId = null;
        try {
            adminAccountId = managerService.returnIdByToken((HttpServletRequest) request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (adminAccountId == null || adminAccountId.equals(BaseFilter.NOT_LOGIN_ACCOUNT_ID)) {
            Result result = new Result();
            logger.info("*******验证失败，未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(result.addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode()));
            return;
        }
        logger.info("*******进行Admin登录验证，登录用户: " + adminAccountId + "  uri：" + ((HttpServletRequest) request).getRequestURI());
        request.setAttribute(ATTR_ADMIN_LOGIN_ACCOUNT_ID, adminAccountId);
        filterChain.doFilter(request, response);
    }

}

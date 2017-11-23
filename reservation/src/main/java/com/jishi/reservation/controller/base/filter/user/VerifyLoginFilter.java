package com.jishi.reservation.controller.base.filter.user;

import com.jishi.reservation.controller.base.filter.BaseFilter;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.us.base.util.datawapper.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 用户登录验证父类
 * Created by liangxiong on 2017/11/20.
 */
public class VerifyLoginFilter extends BaseFilter {

    private Logger logger = LoggerFactory.getLogger(VerifyLoginFilter.class);

    @Autowired
    private AccountService accountService;

    private static final String ATTR_LOGIN_ACCOUNT_ID = "accountId";
    private static final List<Long> TEST_ACCOUNT_ID_LIST = Arrays.asList(30L, 24L, 27L);


    @Override
    public void executeFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Long accountId = null;
        try {
            accountId = accountService.returnIdByToken((HttpServletRequest) request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (accountId == null || accountId.equals(BaseFilter.NOT_LOGIN_ACCOUNT_ID)) {
            Long testAccount = getTestAccountId(request);
            if (testAccount == null || testAccount.equals(BaseFilter.NOT_LOGIN_ACCOUNT_ID)) {
                Result result = new Result();
                logger.info("*******验证失败，未登录");
                response.setContentType("application/json;charset=utf-8");

                // TODO 返回状态置为401，未登录，待修改
                ((HttpServletResponse)response).setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                response.getWriter().print(result.addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode()));
                return;
            } else {
                accountId = testAccount;
                logger.info("[ 走测试用户: testAccount:" + testAccount + " ]");
            }
        }

        logger.info("*******进行登录验证，登录用户: " + accountId + "  uri：" + ((HttpServletRequest) request).getRequestURI());
        request.setAttribute(ATTR_LOGIN_ACCOUNT_ID, accountId);
        filterChain.doFilter(request, response);
    }

    //获取测试账号
    private Long getTestAccountId(ServletRequest request) {
        Object testAccountObj = request.getParameter(ATTR_LOGIN_ACCOUNT_ID);
        Long testAccount = -1L;
        if (testAccountObj != null) {
            if (testAccountObj instanceof Long) {
                testAccount = (Long) testAccountObj;
            } else if (testAccountObj instanceof String) {
                try {
                    testAccount = Long.parseLong((String) testAccountObj);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        if (TEST_ACCOUNT_ID_LIST.indexOf(testAccount) > -1) {
            return testAccount;
        }
        return BaseFilter.NOT_LOGIN_ACCOUNT_ID;
    }

}

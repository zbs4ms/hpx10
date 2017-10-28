package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.*;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.*;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.PayEnum;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.his.HisOutpatient;
import com.jishi.reservation.service.his.bean.ConfirmOrder;
import com.jishi.reservation.service.his.bean.ConfirmRegister;
import com.jishi.reservation.service.support.AliOssSupport;
import com.jishi.reservation.service.support.DateSupport;
import com.jishi.reservation.util.Constant;
import com.jishi.reservation.util.Helpers;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/order")
@Slf4j
@Api(description = "订单相关接口")
public class OrderController extends MyBaseController {

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    HisOutpatient hisOutpatient;


    @Autowired
    AccountService accountService;

    @Autowired
    RegisterService registerService;


    @ApiOperation(value = "确认订单 两个参数传递其中一个")
    @RequestMapping(value = "confirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject sureOrder(
            @ApiParam(value = "订单id", required = false) @RequestParam(value = "orderId", required = false) Long orderId,
            @ApiParam(value = "订单编号", required = false) @RequestParam(value = "orderNumber", required = false) String orderNumber
    ) throws Exception {

        //执行his确认订单操作..
        //confirm.modify

        OrderVO orderVO = orderInfoService.queryOrderInfoById(orderId,orderNumber);

        ConfirmRegister confirmRegister = orderInfoService.returnConfirmRegister(orderId);
        log.info("处理his的确认订单接口");
        ConfirmOrder confirmOrder = hisOutpatient.confirmRegister(confirmRegister);
        orderInfoService.confirmOrderHis(orderId,confirmOrder);
        return ResponseWrapper().addData(orderVO).addMessage("确认成功").ExeSuccess(200);

    }



    @ApiOperation(value = "通过订单id查询相关信息",response = OrderVO.class)
    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryOrder(
            @ApiParam(value = "订单ID") @RequestParam(value = "orderId", required = true) Long orderId
            ) throws Exception {

        Preconditions.checkNotNull(orderId,"请传入合适的参数：orderId");

        OrderVO orderVO = orderInfoService.queryOrderInfoById(orderId,null);

        return ResponseWrapper().addData(orderVO).addMessage("查询成功").ExeSuccess(200);

    }


    @ApiOperation(value = "查询订单列表页  全部，1 待支付，2 已取消，0 预约成功 ",response = OrderListVO.class)
    @RequestMapping(value = "queryList", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryOrderList(
            HttpServletRequest request,HttpServletResponse response,
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "状态 全部 不传，1 待支付，2 已取消，0 预约成功 ", required = false)
            @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序 ", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc
    ) throws Exception {

        if (accountId == null) {
            //从登陆信息中获取登陆者ID
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }


        List<OrderListVO> voList = new ArrayList<>();
        PageInfo pageInfo = orderInfoService.queryOrderList(status, EnableEnum.EFFECTIVE.getCode(), Paging.create(pageNum, pageSize, orderBy, desc));
        List<OrderInfo> orderList = pageInfo.getList();
        for (OrderInfo orderInfo : orderList) {
            Register register = registerService.queryByOrderId(orderInfo.getId());
            OrderListVO vo = new OrderListVO();
            vo.setOrderId(orderInfo.getId());
            vo.setAgreeTime(register.getAgreedTime());
            vo.setDepartment(register.getDepartment());
            vo.setStatus(orderInfo.getStatus());
            vo.setDoctorName(register.getDoctorName());
            vo.setPatientName(register.getPatientName());

            voList.add(vo);
        }

        return ResponseWrapper().addData(voList).addMessage("查询成功").ExeSuccess(200);

    }









}

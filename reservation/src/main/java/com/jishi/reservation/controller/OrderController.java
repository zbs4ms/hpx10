package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.DateVO;
import com.jishi.reservation.controller.protocol.DoctorVO;
import com.jishi.reservation.controller.protocol.OrderVO;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.service.DepartmentService;
import com.jishi.reservation.service.DoctorService;
import com.jishi.reservation.service.OrderInfoService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/order")
@Slf4j
@Api(description = "订单相关接口")
public class OrderController extends BaseController{

    @Autowired
    OrderInfoService orderInfoService;

    @ApiOperation(value = "通过订单id查询相关信息")
    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryOrder(
            @ApiParam(value = "订单ID") @RequestParam(value = "orderId", required = true) Long orderId
            ) throws Exception {

        Preconditions.checkNotNull(orderId,"请传入合适的参数：orderId");

        OrderVO orderVO = orderInfoService.queryOrderInfoById(orderId);

        return ResponseWrapper().addData(orderVO).addMessage("查询成功").ExeSuccess(200);

    }







}

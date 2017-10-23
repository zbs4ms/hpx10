package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.OrderVO;
import com.jishi.reservation.dao.mapper.DoctorMapper;
import com.jishi.reservation.dao.mapper.OrderInfoMapper;
import com.jishi.reservation.dao.mapper.RegisterMapper;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.dao.models.OrderInfo;
import com.jishi.reservation.dao.models.Register;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.his.bean.ConfirmRegister;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class OrderInfoService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    RegisterMapper registerMapper;

    public OrderVO queryOrderInfoById(Long orderId) {

        OrderInfo orderInfo = orderInfoMapper.queryById(orderId);
        Register register =  registerMapper.queryByOrderId(orderId);

        OrderVO orderVO = new OrderVO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        orderVO.setOrderNumber(orderInfo.getOrderNumber());
        orderVO.setSerialNumber(register.getSerialNumber());
        orderVO.setDoctorName(register.getDoctorName());
        orderVO.setPayType(orderInfo.getPayType());
        orderVO.setPrice(orderInfo.getPrice());
        orderVO.setPosition("泸州锦欣医院");
        orderVO.setCompletedTime(orderInfo.getPayTime());
        orderVO.setDepartment(register.getDepartment());
        orderVO.setPatientName(register.getPatientName());

        orderVO.setTimeInterval(sdf.format(register.getAgreedTime()).contains("14:00")?"下午":"上午");
        orderVO.setRegisterTime(register.getAgreedTime());

        return orderVO;
    }

    public ConfirmRegister returnConfirmRegister(Long orderId) {

        ConfirmRegister confirmRegister = new ConfirmRegister();
        OrderInfo orderInfo = orderInfoMapper.queryById(orderId);
        Register register = registerMapper.queryByOrderId(orderId);
        confirmRegister.setBrid(orderInfo.getBrId());
        confirmRegister.setJe(String.valueOf(orderInfo.getPrice()));
        confirmRegister.setCzjlid("");
        confirmRegister.setHm(register.getHm());
        confirmRegister.setHx(register.getHx());
        confirmRegister.setHzdw("");   //合作单位 固定传入第三方名称
        confirmRegister.setYyfs("");  //预约方式 固定传入第三方名称
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        confirmRegister.setYysj(sdf.format(register.getAgreedTime()));


        return null;
    }
}

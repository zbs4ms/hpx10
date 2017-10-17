package com.jishi.reservation.controller.protocol;

import lombok.Data;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by sloan on 2017/9/6.
 */

@Data
public class RegisterCompleteVO {


    private Long registerId;
    private String department;
    private String doctor;
    private String patient;
    private String position;
    private Date agreeTime;
    private String timeInterval;
    private Integer payType;  //支付方式
    private BigDecimal price;  //支付金额
    private Date payTime;       //支付时间
    private Date completeTime;  //完成时间
    private String orderCode;     //订单编号
    private Long countDownTime;     //支付倒计时
    private String serialNumber;     //序列号
    private String subject; //商品名称
    private String des;     //商品描述
}

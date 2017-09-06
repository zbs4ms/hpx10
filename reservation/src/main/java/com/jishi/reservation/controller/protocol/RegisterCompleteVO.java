package com.jishi.reservation.controller.protocol;

import lombok.Data;

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
    private Integer timeInterval;
}

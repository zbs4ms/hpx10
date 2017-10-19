package com.jishi.reservation.controller.protocol;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zbs on 2017/10/6.
 */
@Data
public class OutpatientVO {

    @ApiModelProperty("缴费单号")
    String receiptId;
    @ApiModelProperty("单据类型，1-收费单，4-挂号单")
    String receiptType;
    @ApiModelProperty("科室")
    String department;
    @ApiModelProperty("医生姓名")
    String doctorName;
    @ApiModelProperty("开单时间")
    String receiptTime;
    @ApiModelProperty("就诊人姓名")
    String patientName;
    @ApiModelProperty("支付状态，0-待支付，1-已支付，2-已退费")
    String receiptStatus;
    
}

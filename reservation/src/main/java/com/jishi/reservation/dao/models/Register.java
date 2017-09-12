package com.jishi.reservation.dao.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("预约信息")
@Table(name = "register")
public class Register {
    @Id
    @ApiModelProperty("预约ID")
    private Long id;
    @ApiModelProperty("账号ID")
    private Long accountId;
    @ApiModelProperty("病人ID")
    private Long patientinfoId;
    @ApiModelProperty("科室ID")
    private Long departmentId;
    @ApiModelProperty("预约的医生ID")
    private Long doctorId;
    @ApiModelProperty("预约时间")
    private Date agreedTime;
    @ApiModelProperty("状态（0  待支付 1 取消 2 预约成功")
    private Integer status;
    @ApiModelProperty("状态标示:0:正常 1:禁用  99:删除")
    private Integer enable;


    @Transient
    private Integer payType;
    @Transient
    private BigDecimal price;
    @Transient
    private Date payTime;
    @Transient
    private Date completeTime;

}

package com.jishi.reservation.dao.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Id;

import javax.persistence.Table;

@Data
@ApiModel("病人信息")
@Table(name = "patientInfo")
public class PatientInfo {
    @Id
    @ApiModelProperty("病人ID")
    private Long id;
    @ApiModelProperty("账号ID")
    private Long accountId;
    @ApiModelProperty("病人姓名")
    private String name;
    @ApiModelProperty("病人电话")
    private String phone;
    @ApiModelProperty("病人身份证")
    private String idCard;
    @ApiModelProperty("状态标示:0:正常 1:禁用  99:删除")
    private Integer enable;

}
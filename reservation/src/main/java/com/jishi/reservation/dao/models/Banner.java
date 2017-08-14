package com.jishi.reservation.dao.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Id;

import javax.persistence.Table;

@Data
@ApiModel("banner信息")
@Table(name = "banner")
public class Banner {
    @Id
    @ApiModelProperty("banner的ID")
    private Long id;
    @ApiModelProperty("banner的url")
    private String bannerUrl;
    @ApiModelProperty("排序")
    private String orderNumbe;
    @ApiModelProperty("状态标示:0:正常 1:禁用  99:删除")
    private Integer enable;


}
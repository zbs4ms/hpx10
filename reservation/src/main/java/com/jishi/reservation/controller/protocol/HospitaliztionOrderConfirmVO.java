package com.jishi.reservation.controller.protocol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by sloan on 2017/11/3.
 */


@Data
@ApiModel("住院订单确认对象")
public class HospitaliztionOrderConfirmVO {

    @ApiModelProperty("状态  0成功， 1失败")
    private Integer status;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("价格")
    private String price;
}

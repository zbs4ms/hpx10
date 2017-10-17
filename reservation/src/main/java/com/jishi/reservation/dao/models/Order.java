package com.jishi.reservation.dao.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by sloan on 2017/10/17.
 */

@Data
@Table(name = "order")
@ApiModel("订单信息")
public class Order {

    @Id
    private Long id;
    @ApiModelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("his唯一病人id")
    private String brId;
    @ApiModelProperty("我们系统的账号 id")
    private Long accountId;
    @ApiModelProperty("对应的预约 id")
    private Long registerId;
    @ApiModelProperty("商品名称")
    private String subject;
    @ApiModelProperty("商品描述")
    private String des;
    @ApiModelProperty("价格")
    private BigDecimal price;
    @ApiModelProperty("支付方式  1 支付宝；2 微信")
    private Integer payType;
    @ApiModelProperty("订单状态 0：已完成(已付款)； 1：待付款 ；2：已取消")
    private Integer status;
    @ApiModelProperty("是否有效的标志 0：有效 ；1：无效")
    private Integer enable;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("支付时间")
    private Date payTime;



}

package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Credentials;
import com.jishi.reservation.dao.models.OrderInfo;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderInfoMapper extends MyMapper<OrderInfo>{


    @Select({
            "select * from order_info where order_number = #{outTradeNo}"
    })
    OrderInfo queryByOutTradeNo(@Param("outTradeNo") String outTradeNo);


    @Select({
            "select * from order_info where id = #{orderId}"
    })
    OrderInfo queryById(@Param("orderId") Long orderId);


    @Select({
            "<script>" +
                    "select * from order_info where 1 =1 " +
                    " and enable = #{enable}" +
                    "<if test = \"status != null\"> AND status = #{status}</if>" +
                    "</script>"
    })
    List queryOrderList(@Param("status") Integer status,@Param("enable") Integer enable);





    @Select({
            "<script>" +
                    "select * from order_info where 1 =1 " +
                    "<if test = \"orderId != null\"> AND id = #{orderId}</if>" +
                    "<if test = \"orderNumber != null\"> AND order_number = #{orderNumber}</if>" +
                    "</script>"
    })
    OrderInfo queryByIdOrOrderNumber(@Param("orderId") Long orderId,@Param("orderNumber") String orderNumber);


    @Select({
            "select * from order_info where order_number = #{orderNumber}"
    })
    OrderInfo queryByNumber(@Param("orderNumber") String orderNumber);
}

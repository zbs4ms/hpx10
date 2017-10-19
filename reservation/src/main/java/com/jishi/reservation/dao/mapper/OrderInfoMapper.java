package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Credentials;
import com.jishi.reservation.dao.models.OrderInfo;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInfoMapper extends MyMapper<OrderInfo>{


    @Select({
            "select * from order_info where order_number = #{outTradeNo}"
    })
    OrderInfo queryByOutTradeNo(@Param("outTradeNo") String outTradeNo);
}

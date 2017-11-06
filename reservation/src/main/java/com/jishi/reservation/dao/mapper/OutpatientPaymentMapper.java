package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.OutpatientPayment;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liangxiong on 2017/11/6.
 */
@Repository
public interface OutpatientPaymentMapper extends MyMapper<OutpatientPayment> {

    @Select({
      "select * from outpatient_payment where account_id = #{accountId}"
    })
    List<OutpatientPayment> queryByAccountId(@Param("accountId") Long accountId);

    @Select({
      "select * from outpatient_payment where br_id = #{brId}"
    })
    List<OutpatientPayment> queryByBrId(@Param("brId") String brId);

    @Select({
      "select * from outpatient_payment where order_id = #{orderId}"
    })
    OutpatientPayment queryByOrderId(@Param("orderId") Long orderId);

    @Select({
      "select * from outpatient_payment where order_number = #{orderNumber}"
    })
    OutpatientPayment queryByOrderNumber(@Param("orderNumber") String orderNumber);

}

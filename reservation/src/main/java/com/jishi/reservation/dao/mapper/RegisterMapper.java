package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Register;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RegisterMapper extends MyMapper<Register> {

    /**
     * 查找所有预约完成的预约信息..
     * @return
     */
    @Select({
            "select * from register where status = 2"
    })
    List<Register> queryEnableRegister();

    @Select({
            "select * from register where order_id = #{orderId}"
    })
    Register queryByOrderId(@Param("orderId") Long orderId);


    @Select({
            "select * from register"
    })
    List<Register> queryAllRegister();
}

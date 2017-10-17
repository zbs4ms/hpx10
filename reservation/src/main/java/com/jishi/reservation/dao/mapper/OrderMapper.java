package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Credentials;
import com.jishi.reservation.dao.models.Order;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper extends MyMapper<Order>{

}
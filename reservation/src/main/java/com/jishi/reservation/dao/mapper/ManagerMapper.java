package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Manager;
import com.jishi.reservation.dao.models.Pregnant;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Repository
public interface ManagerMapper extends MyMapper<Manager> {



    @Select({
            "select * from manager where account = #{account}"
    })
    Manager findAccountByUserName(@Param("account") String account);
}
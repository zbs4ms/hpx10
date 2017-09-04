package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Account;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountMapper extends MyMapper<Account>{


    @Select({
            "SELECT * FROM account where account = ${account} and passwd = ${password}"
    })
    Account selectByAccountAndPassword(@Param("account") String account,@Param("password") String password);


    @Select({
            "select * from account where account = #{phone}"
    })
    Account queryByTelephone(@Param("phone") String phone);
}

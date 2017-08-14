package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Doctor;
import com.us.base.mybatis.base.MyMapper;
import org.springframework.stereotype.Service;

public interface DoctorMapper extends MyMapper<Doctor> {

   // @Service({" select * from doctor " +
     //       " where 1=1 " +
   //         " <if test='vendorKw != null'> " +
   //         "     AND " +
   //         " </if> " +
   //         " name like '%#{name}%' "})

}

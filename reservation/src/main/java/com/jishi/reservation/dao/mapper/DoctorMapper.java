package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Doctor;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DoctorMapper extends MyMapper<Doctor> {

    @Select({" select * from doctor " +
            " where 1=1 " +
            " <if test='#{name} != null'> " +
            "     AND name like '%#{name}%' " +
            " </if> " +
            " <if test='#{id} != null'> " +
            "     AND id = #{id} " +
            " </if> " +
            " <if test='#{type} != null'> " +
            "     AND type = #{type} " +
            " </if> " +
            " <if test='#{enable} != null'> " +
            "     AND enable = #{enable} " +
            " </if> "})
    List<Doctor> queryByAttr(Doctor doctor);

}

package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.PatientInfo;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientInfoMapper extends MyMapper<PatientInfo> {


    @Select({
            "select count(*) from patientInfo where account_id = #{accountId}"
    })
    Integer findMaxPatientNum(@Param("accountId") Long accountId);

    @Select({
            "select * from patientInfo where id = #{id}"
    })
    PatientInfo queryById(@Param("id") Long id);
}

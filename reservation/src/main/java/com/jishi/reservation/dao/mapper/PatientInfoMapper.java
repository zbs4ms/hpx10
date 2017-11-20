package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.PatientInfo;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


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


    @Select({
            "select br_id from patientInfo where account_id = #{accountId}"
    })
    List<String> queryBrIdByAccountId(@Param("accountId") Long accountId);


    @Select({
            "select * from patientInfo where br_id = #{brId} and account_id = #{accountId}"
    })
    PatientInfo queryByById(@Param("brId") String brId,@Param("accountId") Long accountId);


    @Select({
            "select * from patientInfo where account_id = #{accountId} and name = #{name} and id_card = #{idCard}"
    })
    PatientInfo queryForExist(@Param("accountId") Long accountId,@Param("name") String name,@Param("idCard") String idCard);



    @Select({
            "select account_id from patientInfo where br_id = #{brId} "
    })
    Long queryAccountIdByBrId(@Param("brId") String brId);


    @Select({
            "select * from patientInfo where account_id = #{accountId}"
    })
    List<PatientInfo> queryByAccountId(Long accountId);
}

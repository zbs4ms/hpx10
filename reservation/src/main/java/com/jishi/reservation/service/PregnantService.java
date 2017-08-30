package com.jishi.reservation.service;


import com.google.common.base.Preconditions;
import com.jishi.reservation.dao.mapper.PregnantMapper;

import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.dao.models.Pregnant;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created by csrr on 2017/8/27.
 */
@Service
@Log4j
public class PregnantService {

    @Autowired
    private PregnantMapper pregnantMapper;


    @Autowired
    private AccountService accountService;

    @Autowired
    private PatientInfoService patientInfoService;


    /**
     * 增加孕妇信息
     */
    public void addPregnant(Long accountId, Long patientinfoId, String name, Date birth, String livingAddress, Date lastMenses, String telephone, String husbandName, String husbandTelephone,String remark) throws Exception {

        if(Helpers.isNullOrEmpty(accountId) || accountService.queryAccount(accountId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("账户信息为空.");
        if(Helpers.isNullOrEmpty(patientinfoId)  || patientInfoService.queryPatientInfo(patientinfoId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("就诊人信息为空.");

        Pregnant pregnant = new Pregnant();
        pregnant.setAccountId(accountId);
        pregnant.setPatientId(patientinfoId);
        pregnant.setName(name);
        pregnant.setBirth(birth);
        pregnant.setLivingAddress(livingAddress);
        pregnant.setLastMenses(lastMenses);
        pregnant.setTelephone(telephone);
        pregnant.setHusbandName(husbandName);
        pregnant.setHusbandTelephone(husbandTelephone);
        pregnant.setRemark(remark);
        pregnant.setEnable(EnableEnum.EFFECTIVE.getCode());
        pregnant.setCreateTime(new Date());
        pregnantMapper.insert(pregnant);


    }

    public void updatePregnant(Long pregnantId, String name, Date birth, String livingAddress, Date lastMenses, String telephone, String husbandName, String husbandTelephone,Integer enable) throws Exception {

        log.info("修改孕妇信息  pregnantId:"+pregnantId+",name :"+name+",birth:"+birth.toLocaleString()+",livingAddress:"+livingAddress+
                ",lastMenses:"+lastMenses.toLocaleString()+ ",telephone:"+telephone+",husbandName:"+husbandName+",husbandTelephone:"+husbandTelephone);
        if(Helpers.isNullOrEmpty(pregnantId) || queryPregnant(pregnantId,null,null,EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("孕妇信息为空.");
        Pregnant pregnant = new Pregnant();
        pregnant.setId(pregnantId);
        pregnant.setName(name);
        pregnant.setBirth(birth);
        pregnant.setLivingAddress(livingAddress);
        pregnant.setLastMenses(lastMenses);
        pregnant.setTelephone(telephone);
        pregnant.setHusbandName(husbandName);
        pregnant.setHusbandTelephone(husbandTelephone);

        Preconditions.checkState(pregnantMapper.updateByPrimaryKeySelective(pregnant) == 1,"更新失败");


    }


    public List<Pregnant> queryPregnant(Long pregnantId, Long patientinfoId, String name, Integer enable) {

        log.info("查询孕妇 pregnantId:"+pregnantId+" patientinfoId:"+patientinfoId+" name:"+name +" enable:"+enable);
        Pregnant queryPregnant = new Pregnant();

        queryPregnant.setId(pregnantId);
        queryPregnant.setPatientId(patientinfoId);
        queryPregnant.setName(name);
        queryPregnant.setEnable(enable);

        return pregnantMapper.select(queryPregnant);
    }
}
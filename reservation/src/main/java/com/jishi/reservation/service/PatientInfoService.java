package com.jishi.reservation.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.PatientInfoMapper;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.CheckIdCard;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class PatientInfoService {

    @Autowired
    PatientInfoMapper patientInfoMapper;

    /**
     * 增加就诊人信息
     * @param accountId
     * @param name
     * @param phone
     * @param idCard
     * @throws Exception
     */
    public void addPatientInfo(Long accountId, String name, String phone, String idCard) throws Exception {
        if (Helpers.isNullOrEmpty(accountId))
            throw new Exception("账号ID为空");
        String errorInfo = CheckIdCard.IDCardValidate(idCard);
        if (errorInfo != null && !"".equals(errorInfo)) {
            log.error(errorInfo);
            throw new Exception("无效的身份证信息");
        }
        //判断一个账号最大病号数是否超过5个
        if(!this.checkMaxPatientNum(accountId)){
            throw new Exception("该账号最大病号数已达最大5个");
        }
        PatientInfo newPatientInfo = new PatientInfo();
        newPatientInfo.setAccountId(accountId);
        newPatientInfo.setName(name);
        newPatientInfo.setPhone(phone);
        newPatientInfo.setIdCard(idCard);
        newPatientInfo.setEnable(EnableEnum.EFFECTIVE.getCode());
        patientInfoMapper.insert(newPatientInfo);
    }

    /**
     * 判断账号下病号数是否达到最大值5
     * @param accountId
     * @return
     */
    private boolean checkMaxPatientNum(Long accountId) {
        return patientInfoMapper.findMaxPatientNum(accountId) < 5;
    }


    /**
     * 查询就诊人信息
     * @param patientInfoId
     * @param accountId
     * @param enable
     * @return
     * @throws Exception
     */
    public List<PatientInfo> queryPatientInfo(Long patientInfoId, Long accountId, Integer enable) throws Exception {
        PatientInfo queryPatientInfo = new PatientInfo();
        queryPatientInfo.setId(patientInfoId);
        queryPatientInfo.setAccountId(accountId);
        queryPatientInfo.setEnable(enable);
        return patientInfoMapper.select(queryPatientInfo);
    }

    /**
     * 查询就诊人信息,分页
     * @param patientInfoId
     * @param accountId
     * @param enable
     * @param paging
     * @return
     */
    public PageInfo<PatientInfo> queryPatientInfoPagaInfo(Long patientInfoId, Long accountId, Integer enable, Paging paging) throws Exception {
        if(paging != null)
            PageHelper.startPage(paging.getPageNum(),paging.getPageSize(),paging.getOrderBy());
        return new PageInfo(queryPatientInfo(patientInfoId,accountId,enable));
    }


    /**
     * 修改就诊人信息
     * @param patientInfoId
     * @param name
     * @param phone
     * @param idCard
     * @throws Exception
     */
    public void modifyPatientInfo(Long patientInfoId, String name, String phone, String idCard,Integer enable) throws Exception {
        if (Helpers.isNullOrEmpty(patientInfoId))
            throw new Exception("就诊人ID为空");
        if(queryPatientInfo(patientInfoId,null,null) == null)
            throw new Exception("没有查询到就诊人");
        if(idCard != null){
            String errorInfo = CheckIdCard.IDCardValidate(idCard);
            if (errorInfo != null && !"".equals(errorInfo)) {
                log.error(errorInfo);
                throw new Exception("无效的身份证信息");
            }
        }
        PatientInfo modifyPatientInfo = new PatientInfo();
        modifyPatientInfo.setId(patientInfoId);
        modifyPatientInfo.setName(name);
        modifyPatientInfo.setPhone(phone);
        modifyPatientInfo.setIdCard(idCard);
        modifyPatientInfo.setEnable(enable);
        Preconditions.checkState(patientInfoMapper.updateByPrimaryKey(modifyPatientInfo) == 1,"更新失败!");
    }

    /**
     * 删除就诊人
     * @param patientInfoId
     * @throws Exception
     */
    public void deletePatientInfo(Long patientInfoId) throws Exception {
        if (Helpers.isNullOrEmpty(patientInfoId))
            throw new Exception("就诊人ID为空");
        if(queryPatientInfo(patientInfoId,null,null) == null)
            throw new Exception("没有查询到就诊人");
        patientInfoMapper.deleteByPrimaryKey(patientInfoId);
    }

}

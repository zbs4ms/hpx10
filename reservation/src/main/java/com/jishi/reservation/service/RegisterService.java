package com.jishi.reservation.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.RegisterMapper;
import com.jishi.reservation.dao.models.Register;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.StatusEnum;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class RegisterService {

    @Autowired
    RegisterMapper registerMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    PatientInfoService patientInfoService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    DoctorService doctorService;

    /**
     * 增加一条预约
     * @param accountId
     * @param patientinfoId
     * @param departmentId
     * @param doctorId
     * @param agreedTime
     * @throws Exception
     */
    public void addRegister(Long accountId,Long patientinfoId,Long departmentId,Long doctorId,Date agreedTime) throws Exception {
        if(Helpers.isNullOrEmpty(accountId) || accountService.queryAccount(accountId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("账户信息为空.");
        if(Helpers.isNullOrEmpty(patientinfoId)  || patientInfoService.queryPatientInfo(patientinfoId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("就诊人信息为空.");
        if(Helpers.isNullOrEmpty(departmentId)  || departmentService.queryDepartment(departmentId,null) == null)
            throw new Exception("科室信息为空.");
        if(Helpers.isNullOrEmpty(doctorId)  || doctorService.queryDoctor(doctorId,null,null,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("医生信息为空.");
        Register register = new Register();
        register.setAccountId(accountId);
        register.setDepartmentId(departmentId);
        register.setDoctorId(doctorId);
        register.setPatientinfoId(patientinfoId);
        register.setAgreedTime(agreedTime);
        register.setStatus(StatusEnum.REGISTER_STATUS_NO_PAYMENT.getCode());
        register.setEnable(EnableEnum.EFFECTIVE.getCode());
        registerMapper.insert(register);
    }

    /**
     * 查询预约
     * @param accountId
     * @param registerId
     * @param status
     * @return
     * @throws Exception
     */
    public List<Register> queryRegister(Long registerId,Long accountId ,Integer status,Integer enable) throws Exception {
        if(Helpers.isNullOrEmpty(accountId) && Helpers.isNullOrEmpty(registerId))
            throw new Exception("查询条件不能都为空");
        Register queryRegister = new Register();
        queryRegister.setAccountId(accountId);
        queryRegister.setId(registerId);
        queryRegister.setStatus(status);
        queryRegister.setEnable(enable);
        return registerMapper.select(queryRegister);
    }

    /**
     * 带有分页排序的查询
     * @param registerId
     * @param accountId
     * @param status
     * @param enable
     * @param paging
     * @return
     * @throws Exception
     */
    public PageInfo queryRegisterPageInfo(Long registerId,Long accountId ,Integer status,Integer enable,Paging paging) throws Exception {
        if(!Helpers.isNullOrEmpty(paging))
            PageHelper.startPage(paging.getPageSize(),paging.getPageNum(),paging.getOrderBy());
        return new PageInfo(queryRegister(registerId,accountId,status,enable));
    }

    /**
     * 查询全部预约
     * @return
     * @throws Exception
     */
    public List<Register> queryAllRegister(Integer enable, Paging paging) throws Exception {
        Register queryRegister = new Register();
        queryRegister.setEnable(enable);
        if(!Helpers.isNullOrEmpty(paging))
           PageHelper.startPage(paging.getPageSize(),paging.getPageNum(),paging.getOrderBy());
        return registerMapper.select(queryRegister);
    }

    /**
     * 修改预约信心
     * @param registerId
     * @param accountId
     * @param patientinfoId
     * @param departmentId
     * @param doctorId
     * @param status
     * @param agreedTime
     * @param enable
     * @throws Exception
     */
    public void modifyRegister(Long registerId,Long accountId,Long patientinfoId,Long departmentId,Long doctorId,Integer status,Date agreedTime,Integer enable) throws Exception {
        if(Helpers.isNullOrEmpty(registerId) || queryRegister(registerId,null,null,null) == null)
            throw new Exception("预约信息为空.");
        if(!Helpers.isNullOrEmpty(accountId) && accountService.queryAccount(accountId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("账户信息为空.");
        if(!Helpers.isNullOrEmpty(patientinfoId) && patientInfoService.queryPatientInfo(patientinfoId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("就诊人信息为空.");
        if(!Helpers.isNullOrEmpty(departmentId) && departmentService.queryDepartment(departmentId,null) == null)
            throw new Exception("科室信息为空.");
        if(!Helpers.isNullOrEmpty(doctorId) && doctorService.queryDoctor(doctorId,null,null,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("医生信息为空.");
        Register newRegister = new Register();
        newRegister.setId(registerId);
        newRegister.setAccountId(accountId);
        newRegister.setDepartmentId(departmentId);
        newRegister.setDoctorId(doctorId);
        newRegister.setPatientinfoId(patientinfoId);
        newRegister.setStatus(status);
        newRegister.setAgreedTime(agreedTime);
        newRegister.setStatus(StatusEnum.REGISTER_STATUS_NO_PAYMENT.getCode());
        newRegister.setEnable(enable);
        Preconditions.checkState(registerMapper.updateByPrimaryKeySelective(newRegister) == 1,"更新失败!");
    }

    /**
     * 把就诊信息置为无效
     * @param registerId
     * @throws Exception
     */
    public void failureRegister(Long registerId) throws Exception {
        if(Helpers.isNullOrEmpty(registerId) || queryRegister(registerId,null,null,null) == null)
            throw new Exception("预约信息为空.");
        Register newRegister = new Register();
        newRegister.setId(registerId);
        newRegister.setEnable(EnableEnum.INVALID.getCode());
        registerMapper.updateByPrimaryKeySelective(newRegister);
    }
}

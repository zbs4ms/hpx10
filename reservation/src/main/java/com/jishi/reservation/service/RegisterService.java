package com.jishi.reservation.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.RegisterCompleteVO;
import com.jishi.reservation.dao.mapper.*;
import com.jishi.reservation.dao.models.Department;
import com.jishi.reservation.dao.models.OrderInfo;
import com.jishi.reservation.dao.models.Register;
import com.jishi.reservation.otherService.pay.AlibabaPay;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.OrderStatusEnum;
import com.jishi.reservation.service.enumPackage.PayEnum;
import com.jishi.reservation.service.enumPackage.StatusEnum;
import com.jishi.reservation.service.his.HisOutpatient;
import com.jishi.reservation.service.his.HisUserManager;
import com.jishi.reservation.service.his.bean.LockRegister;
import com.jishi.reservation.util.Helpers;
import com.jishi.reservation.util.NewRandomUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class RegisterService {

    @Autowired
    RegisterMapper registerMapper;

    @Autowired
    DoctorMapper doctorMapper;
    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    PatientInfoMapper patientInfoMapper;
    @Autowired
    AccountService accountService;
    @Autowired
    PatientInfoService patientInfoService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    ScheduledService scheduledService;
    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    HisUserManager hisUserManager;

    @Autowired
    HisOutpatient hisOutpatient;




    /**
     * 增加一条预约
     * @param accountId
     * @param brid
     * @param departmentId
     * @param doctorId
     * @param agreedTime
     * @throws Exception
     */
    @Transactional
    public RegisterCompleteVO addRegister(Long accountId,String brid,Long departmentId,Long doctorId,
                                          Date agreedTime,String timeInterval,String doctorName,
                                          String price,String subject,String brName,String department,String hm) throws Exception {
        if(Helpers.isNullOrEmpty(accountId) || accountService.queryAccount(accountId,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("账户信息为空.");
        if(Helpers.isNullOrEmpty(departmentId)  || departmentService.queryDepartment(departmentId,null) == null)
            throw new Exception("科室信息为空.");
        if(Helpers.isNullOrEmpty(doctorId)  || doctorService.queryDoctor(doctorId,null,null,null, EnableEnum.EFFECTIVE.getCode()) == null)
            throw new Exception("医生信息为空.");


        BigDecimal bd=new BigDecimal(price);

        OrderInfo order = new OrderInfo();
        order.setAccountId(accountId);
        order.setBrId(brid);
        order.setCreateTime(new Date());
        order.setSubject(subject);
        order.setDes(subject);
        order.setPrice(bd);
        order.setEnable(EnableEnum.EFFECTIVE.getCode());
        String orderNumber = AlibabaPay.generateUniqueOrderNumber();
        order.setOrderNumber(orderNumber);
        //order.setRegisterId(register.getId());
        order.setStatus(OrderStatusEnum.WAIT_PAYED.getCode());
        order.setPayType(PayEnum.ALI.getCode());


        Register register = new Register();
        register.setAccountId(accountId);
        register.setDepartmentId(departmentId);
        register.setDoctorId(doctorId);
        register.setBrId(brid);
        register.setDepartment(department);
        register.setPatientName(brName);
        register.setDoctorName(doctorName);
        register.setOrderId(order.getId());
        register.setAgreedTime(agreedTime);
        register.setStatus(StatusEnum.REGISTER_STATUS_PAYMENT.getCode());
        register.setEnable(EnableEnum.EFFECTIVE.getCode());
        register.setCreateTime(new Date());
        register.setHm(hm);
        String serialCode = NewRandomUtil.getRandomNum(4);
        register.setSerialNumber(serialCode);

        registerMapper.insertReturnId(register);
        order.setRegisterId(register.getId());
        orderInfoMapper.insertReturnId(order);
        register.setOrderId(order.getId());
        RegisterCompleteVO completeVO = new RegisterCompleteVO();
        completeVO.setRegisterId(register.getId());
        completeVO.setDoctor(doctorName);
        //Department department = departmentMapper.queryById(departmentId);
        completeVO.setDepartment(department);
        completeVO.setAgreeTime(agreedTime);
        completeVO.setPosition("交子路");
        completeVO.setTimeInterval(timeInterval);
        completeVO.setPatient(brName);


        completeVO.setPayType(PayEnum.ALI.getCode());
        completeVO.setPayTime(new Date());
        completeVO.setCompleteTime(new Date());
        completeVO.setPrice(bd);
        //completeVO.setPrice(BigDecimal.valueOf(0.01));
        completeVO.setCountDownTime(new Date().getTime()+30*60*1000L-new Date().getTime()>0?register.getCreateTime().getTime()+30*60*1000L-new Date().getTime():0);
        completeVO.setOrderCode(orderNumber);
        completeVO.setSerialNumber(serialCode);
        completeVO.setSubject(subject);
        completeVO.setDes(subject);
        completeVO.setOrderId(order.getId());
        //his 锁定号源,返回hx 号序
        String hx = this.lockRegister(hm, agreedTime);

        register.setHx(hx);
        registerMapper.updateByPrimaryKeySelective(register);

        return completeVO;

    }

    private String lockRegister(String hm, Date agreedTime) throws Exception {
        log.info("开始锁定号源");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String timeStr = sdf.format(agreedTime);
        LockRegister lockRegister = hisOutpatient.lockRegister(hm, timeStr, "", "jxyy+zczh");
        return lockRegister.getHx();
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
            PageHelper.startPage(paging.getPageNum(),paging.getPageSize(),paging.getOrderBy());
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
        newRegister.setBrId(String.valueOf(patientinfoId));
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

    public Register queryByOrderId(Long orderId) {

        return registerMapper.queryByOrderId(orderId);
    }
}

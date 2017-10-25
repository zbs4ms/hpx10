package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.RegisterCompleteVO;
import com.jishi.reservation.controller.protocol.RegisterVO;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.*;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.PayEnum;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.support.JpushSupport;
import com.jishi.reservation.util.Constant;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/register")
@Slf4j
@Api(description = "预约相关接口")
public class RegisterController extends MyBaseController {

    @Autowired
    RegisterService registerService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    PatientInfoService patientInfoService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    AccountService accountService;

    @Autowired
    JpushSupport jpushSupport;







    @ApiOperation(value = "增加预约信息")
    @RequestMapping(value = "addRegister", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject addRegister(HttpServletRequest request,
                                  HttpServletResponse response,


                                  @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                                  @ApiParam(value = "价格", required = true) @RequestParam(value = "price", required = true) String price,
                                  @ApiParam(value = "支付名称", required = true) @RequestParam(value = "subject", required = true) String subject,
                                  @ApiParam(value = "病人名称", required = true) @RequestParam(value = "brName", required = true) String brName,
                                  @ApiParam(value = "医生名称", required = true) @RequestParam(value = "doctorName", required = true) String doctorName,
                                  @ApiParam(value = "病人ID", required = true) @RequestParam(value = "brid", required = true) String brid,
                                  @ApiParam(value = "科室ID", required = true) @RequestParam(value = "departmentId", required = true) Long departmentId,
                                  @ApiParam(value = "科室名称", required = true) @RequestParam(value = "department", required = true) String department,
                                  @ApiParam(value = "his的号码 HM", required = true) @RequestParam(value = "hm", required = true) String hm,

            @ApiParam(value = "预约的医生ID", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId,
            @ApiParam(value = "预约的时间段", required = true) @RequestParam(value = "timeInterval", required = true) String timeInterval,
            @ApiParam(value = "预约时间", required = true) @RequestParam(value = "agreedTime", required = true) Long agreedTime
            ) throws Exception {

        Preconditions.checkNotNull(brid,"请传入必须的参数：brid");
        Preconditions.checkNotNull(departmentId,"请传入必须的参数：departmentId");
        Preconditions.checkNotNull(doctorId,"请传入必须的参数：doctorId");
        Preconditions.checkNotNull(timeInterval,"请传入必须的参数：timeInterval");
        Preconditions.checkNotNull(agreedTime,"请传入必须的参数：agreedTime");
        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }



        // 10.17  在此处加入订单。。
        RegisterCompleteVO completeVO = registerService.addRegister(accountId, brid, departmentId, doctorId, new Date(agreedTime),timeInterval,doctorName,price,subject,brName,department,hm);
        if(completeVO == null){
            return ResponseWrapper().addMessage("该医生挂号号源已满，请选择其他医生。").ExeSuccess(ReturnCodeEnum.FAILED.getCode());
        }
        jpushSupport.sendPush(accountService.queryAccountById(accountId).getPushId(), Constant.REGISTER_SUCCESS_MGS);
        return ResponseWrapper().addData(completeVO).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "查询预约信息  --不用", response = RegisterVO.class)
    @RequestMapping(value = "queryRegister", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryRegister(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
                                    @ApiParam(value = "预约ID", required = false) @RequestParam(value = "registerId", required = false) Long registerId,
                                    @ApiParam(value = "状态", required = false) @RequestParam(value = "status", required = false) Integer status,
                                    @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                    @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                    @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
                                    @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception {
        if (accountId == null) {
            //从登陆信息中获取登陆者ID
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        List<RegisterVO> registerVOList = new ArrayList<>();
        PageInfo pageInfo = registerService.queryRegisterPageInfo(registerId, accountId, status, EnableEnum.EFFECTIVE.getCode(), Paging.create(pageNum, pageSize, orderBy, desc));
        List<Register> registerList = pageInfo.getList();


        for (Register register : registerList) {
            RegisterVO registerVO = new RegisterVO();
            List<Doctor> doctors = doctorService.queryDoctor(register.getDoctorId(), null, null,null, null);
            List<Department> departments = departmentService.queryDepartment(register.getDepartmentId(), null);
            List<Account> accounts = accountService.queryAccount(register.getAccountId(), null, null);
            List<PatientInfo> patientInfos = patientInfoService.queryPatientInfo(register.getId(), null, null);
            //todo 还没对接支付，所以就先搞几个假数据，供前段解析展示
            register.setPayType(PayEnum.WEIXIN.getCode());
            register.setPayTime(new Date());
            register.setCompleteTime(new Date());
            register.setPrice(BigDecimal.valueOf(23.88));
            register.setCountDownTime(register.getCreateTime().getTime()+30*60*1000L-new Date().getTime()>0?register.getCreateTime().getTime()+30*60*1000L-new Date().getTime():0);
            register.setOrderCode("wx568254965");


            registerVO.setRegister(register);
            registerVO.setDoctor(doctors.size() > 0 ? doctors.get(0) : null);
            registerVO.setAccount(accounts.size() > 0 ? accounts.get(0) : null);
            registerVO.setDepartment(departments.size() > 0 ? departments.get(0) : null);
            registerVO.setPatientInfo(patientInfos.size() > 0 ? patientInfos.get(0) : null);
            registerVOList.add(registerVO);
        }
        pageInfo.setList(registerVOList);
        return ResponseWrapper().addData(pageInfo).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "修改预约信息")
    @RequestMapping(value = "modifyRegister", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyRegister(HttpServletRequest request,
                                     HttpServletResponse response,

                                     @ApiParam(value = "预约ID", required = true) @RequestParam(value = "registerId", required = true) Long registerId,
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "病人ID", required = false) @RequestParam(value = "patientinfoId", required = false) Long patientinfoId,
            @ApiParam(value = "状态", required = false) @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "科室ID", required = false) @RequestParam(value = "departmentId", required = false) Long departmentId,
            @ApiParam(value = "预约的医生ID", required = false) @RequestParam(value = "doctorId", required = false) Long doctorId,
            @ApiParam(value = "预约时间", required = false) @RequestParam(value = "agreedTime", required = false) String agreedTime) throws Exception {
        Preconditions.checkNotNull(registerId,"请传入必须的参数：registerId");

        if (accountId == null) {
            //从登陆信息中获取登陆者ID
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());
                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        registerService.modifyRegister(registerId, accountId, patientinfoId, departmentId, doctorId, status, new Date(agreedTime), null);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "预约信息置为无效")
    @RequestMapping(value = "failureRegister", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject failureRegister(
            @ApiParam(value = "预约ID", required = true) @RequestParam(value = "registerId", required = true) Long registerId
    ) throws Exception {
        Preconditions.checkNotNull(registerId,"请传入必须的参数：registerId");

        registerService.failureRegister(registerId);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }
}

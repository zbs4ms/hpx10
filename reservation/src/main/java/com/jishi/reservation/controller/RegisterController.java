package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.OrderVO;
import com.jishi.reservation.controller.protocol.RegisterAdminVO;
import com.jishi.reservation.controller.protocol.RegisterCompleteVO;
import com.jishi.reservation.controller.protocol.RegisterVO;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.*;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.PayEnum;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.his.HisOutpatient;
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
import java.text.SimpleDateFormat;
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

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    HisOutpatient hisOutpatient;


    @ApiOperation(value = "根据项目id和病人id brid查询挂号的真实价格")
    @RequestMapping(value = "queryTruePrice", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject addRegister(
                                  @ApiParam(value = "病人id", required = true) @RequestParam(value = "brid", required = true) String brid,
                                  @ApiParam(value = "项目id", required = true) @RequestParam(value = "xmid", required = true) String xmid
    ) throws Exception {


        //String price = hisOutpatient.queryLastPrice(xmid, brid);

        return ResponseWrapper().addMessage("查询成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
        }




    @ApiOperation(value = "增加预约信息",response = RegisterCompleteVO.class)
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
                                  @ApiParam(value = "项目id", required = true) @RequestParam(value = "xmid", required = true) String xmid,


            @ApiParam(value = "预约的医生ID", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId,
            @ApiParam(value = "预约的时间段", required = true) @RequestParam(value = "timeInterval", required = true) String timeInterval,
            @ApiParam(value = "预约时间", required = true) @RequestParam(value = "agreedTime", required = true) Long agreedTime
            ) throws Exception {

        Preconditions.checkNotNull(brid,"请传入必须的参数：brid");
        Preconditions.checkNotNull(departmentId,"请传入必须的参数：departmentId");
        Preconditions.checkNotNull(doctorId,"请传入必须的参数：doctorId");
        Preconditions.checkNotNull(timeInterval,"请传入必须的参数：timeInterval");
        Preconditions.checkNotNull(agreedTime,"请传入必须的参数：agreedTime");

        //验证br_id 是否存在..
        if(patientInfoService.queryByBrId(brid) == null)
            return ResponseWrapper().addMessage("该病人id不存在，请检查").ExeFaild(ReturnCodeEnum.FAILED.getCode());

        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }



        // 10.17  在此处加入订单。。
        RegisterCompleteVO completeVO = registerService.addRegister(accountId, brid, departmentId, doctorId,xmid, new Date(agreedTime),timeInterval,doctorName,price,subject,brName,department,hm);
        if(completeVO == null){
            return ResponseWrapper().addMessage("该医生挂号号源已满，请选择其他医生。").ExeSuccess(ReturnCodeEnum.FAILED.getCode());
        }
        jpushSupport.sendPush(accountService.queryAccountById(accountId).getPushId(), Constant.REGISTER_SUCCESS_MGS);
        return ResponseWrapper().addData(completeVO).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "查询预约信息 ", response = RegisterVO.class)
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
            //List<Doctor> doctors = doctorService.queryDoctor(null, String.valueOf(register.getDoctorId()),null, null,null, null);


            List<Account> accounts = accountService.queryAccount(register.getAccountId(), null, null);

            //OrderVO orderVO = orderInfoService.queryOrderInfoById(register.getOrderId());
            OrderInfo orderInfo = orderInfoService.findOrderById(register.getOrderId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            register.setPayType(orderInfo.getPayType());

            if(orderInfo.getPayTime()!=null) {
                register.setCompleteTime(sdf.parse(orderInfo.getPayTime()));
                register.setPayTime(sdf.parse(orderInfo.getPayTime()));
                }
            register.setPrice(orderInfo.getPrice());
            //register.setCountDownTime(register.getCreateTime().getTime()+30*60*1000L-new Date().getTime()>0?register.getCreateTime().getTime()+30*60*1000L-new Date().getTime():0);
            register.setOrderCode(orderInfo.getOrderNumber());

            Doctor doctor = doctorService.queryDoctorByHid(register.getDoctorId());
            registerVO.setRegister(register);
            registerVO.setDoctor(doctor);
            accounts.get(0).setPasswd(null);
            registerVO.setAccount(accounts.size() > 0 ? accounts.get(0) : null);
            Department department = new Department();
            department.setName(register.getDepartment());
            department.setId(register.getDepartmentId());
            registerVO.setDepartment(department);
            PatientInfo patientInfo = patientInfoService.queryByBrId(register.getBrId());


            registerVO.setPatientInfo(patientInfo);
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


    @ApiOperation(value = "锁定号源")
    @RequestMapping(value = "lock", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject lock(
            @ApiParam(value = "预约ID", required = true) @RequestParam(value = "registerId", required = true) Long registerId
    ) throws Exception {
        Preconditions.checkNotNull(registerId,"请传入必须的参数：registerId");

        registerService.failureRegister(registerId);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }



    @ApiOperation(value = "admin 查询预约信息 ", response = RegisterVO.class)
    @RequestMapping(value = "queryRegisterAdmin", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryRegisterAdmin(HttpServletRequest request,
                                    HttpServletResponse response,
                                         @ApiParam(value = "开始时间", required = false) @RequestParam(value = "startTime", required = false) Long startTime,
                                         @ApiParam(value = "结束时间", required = false) @RequestParam(value = "endTime", required = false) Long endTime,
                                         @ApiParam(value = "查询关键字", required = false) @RequestParam(value = "key", required = false) String key,
                                         @ApiParam(value = "医生id", required = false) @RequestParam(value = "doctorId", required = false) Long doctorId,
                                         @ApiParam(value = "科室id", required = false) @RequestParam(value = "departmentId", required = false) Long departmentId,
                                         @ApiParam(value = "预约状态 过期未到诊 1，正常就诊 2 ，预约就诊 3", required = false) @RequestParam(value = "status", required = false) Integer status,
                                        @ApiParam(value = "页数", required = false) @RequestParam(value = "startPage", defaultValue = "1") Integer startPage,
                                    @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws Exception {

        PageInfo<RegisterAdminVO> page  = registerService.queryRegisterAdmin(key,startTime,endTime,doctorId,departmentId,status,startPage,pageSize);
        return ResponseWrapper().addMessage("查询成功").addData(page).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());


    }
}

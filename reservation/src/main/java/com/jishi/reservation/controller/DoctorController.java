package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.DoctorVO;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.service.DepartmentService;
import com.jishi.reservation.service.DoctorService;
import com.jishi.reservation.service.PatientInfoService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/doctor")
@Slf4j
@Api(description = "医生相关接口")
public class DoctorController extends BaseController{

    @Autowired
    DoctorService doctorService;
    @Autowired
    DepartmentService departmentService;

    @ApiOperation(value = "增加医生")
    @RequestMapping(value = "addDoctor", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject addDoctor(
            @ApiParam(value = "医生名称", required = true) @RequestParam(value = "doctorName", required = true) String doctorName,
            @ApiParam(value = "类型（0 普通医生 1 专家", required = true) @RequestParam(value = "type", required = true) String type,
            @ApiParam(value = "头像", required = true) @RequestParam(value = "headPortrait", required = true) String headPortrait,
            @ApiParam(value = "科室", required = true) @RequestParam(value = "departmentIds", required = true) String departmentIds,
            @ApiParam(value = "医生简介", required = false) @RequestParam(value = "about", required = false) String about,
            @ApiParam(value = "职称", required = false) @RequestParam(value = "title", required = false) String title,
            @ApiParam(value = "毕业学校", required = false) @RequestParam(value = "school", required = false) String school,
            @ApiParam(value = "擅长的介绍", required = false) @RequestParam(value = "goodDescribe", required = false) String goodDescribe) throws Exception {
        doctorService.addDoctor(doctorName,type,headPortrait,departmentIds,about,title,school,goodDescribe);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "查询全部医生",response=DoctorVO.class)
    @RequestMapping(value = "queryAllDoctor", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllDoctor(
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception {
        List<DoctorVO> doctorVOList = new ArrayList<>();
        List<Doctor> doctors = doctorService.queryAllDoctor(Paging.create(pageNum,pageSize,orderBy,desc));
        for(Doctor doctor : doctors){
            DoctorVO doctorVO = new DoctorVO();
            doctorVO.setDoctor(doctor);
            doctorVO.setDepartmentList(departmentService.batchQueryDepartment(doctor.getDepartmentIds().split(",")));
            doctorVOList.add(doctorVO);
        }
        return ResponseWrapper().addData(doctorVOList).ExeSuccess();
    }

    @ApiOperation(value = "查询医生",response=DoctorVO.class)
    @RequestMapping(value = "queryDoctor", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryDoctor(
            @ApiParam(value = "医生ID", required = false) @RequestParam(value = "doctorId", required = false) Long doctorId,
            @ApiParam(value = "医生名称", required = false) @RequestParam(value = "doctorName", required = false) String doctorName,
            @ApiParam(value = "类型（0 普通医生 1 专家", required = false) @RequestParam(value = "type", required = false) String type) throws Exception {
        DoctorVO doctorVO = new DoctorVO();
        List<Doctor> doctors = doctorService.queryDoctor(doctorId,doctorName,null,EnableEnum.EFFECTIVE.getCode());
        if(doctors.size()>0){
            doctorVO.setDoctor(doctors.get(0));
            doctorVO.setDepartmentList(departmentService.batchQueryDepartment(doctors.get(0).getDepartmentIds().split(",")));
        }
        return ResponseWrapper().addData(doctorVO).ExeSuccess();
    }

    @ApiOperation(value = "修改医生信息")
    @RequestMapping(value = "modifyDoctor", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyDoctor(
            @ApiParam(value = "医生ID", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId,
            @ApiParam(value = "医生名称", required = false) @RequestParam(value = "doctorName", required = false) String doctorName,
            @ApiParam(value = "类型（0 普通医生 1 专家", required = false) @RequestParam(value = "type", required = false) String type,
            @ApiParam(value = "头像", required = false) @RequestParam(value = "headPortrait", required = false) String headPortrait,
            @ApiParam(value = "医生简介", required = false) @RequestParam(value = "describe", required = false) String describe,
            @ApiParam(value = "职称", required = false) @RequestParam(value = "title", required = false) String title,
            @ApiParam(value = "毕业学校", required = false) @RequestParam(value = "school", required = false) String school,
            @ApiParam(value = "擅长的介绍", required = false) @RequestParam(value = "goodDescribe", required = false) String goodDescribe) throws Exception {
        doctorService.modifyDoctor(doctorId,doctorName,type,headPortrait,describe,title,school,goodDescribe,null);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "失效医生")
    @RequestMapping(value = "failureDoctor", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject failureDoctor(
            @ApiParam(value = "医生ID", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId
    ) throws Exception {
        doctorService.modifyDoctor(doctorId,null,null,null,null,null,null,null, EnableEnum.INVALID.getCode());
        return ResponseWrapper().addData("ok").ExeSuccess();
    }
}
package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.PatientInfoService;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.Helpers;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */

@RestController
@RequestMapping("/patientInfo")
@Slf4j
@Api(description = "就诊人相关接口")
public class PatientInfoController extends BaseController {

    @Autowired
    PatientInfoService patientInfoService;

    @Autowired
    AccountService accountService;

    @ApiOperation(value = "增加就诊人信息  8月30号提出 一个账号最多有5个")
    @RequestMapping(value = "addPatientInfo", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject addPatientInfo(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "就诊人名称", required = true) @RequestParam(value = "name", required = true) String name,
            @ApiParam(value = "病人电话", required = true) @RequestParam(value = "phone", required = true) String phone,
            @ApiParam(value = "病人身份证", required = true) @RequestParam(value = "idCard", required = true) String idCard) throws Exception {
        if (accountId == null) {
            //todo:从登陆信息中获取登陆者ID
            accountId = accountId;
        }
        patientInfoService.addPatientInfo(accountId, name, phone, idCard);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "查询就诊人信息", response = PatientInfo.class)
    @RequestMapping(value = "queryPatientInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPatientInfo(
            @ApiParam(value = "账号ID", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "就诊人ID", required = false) @RequestParam(value = "patientInfoId", required = false) Long patientInfoId) throws Exception {
        if (accountId == null) {
            //todo:从登陆信息中获取登陆者ID
            accountId = accountId;
        }
        if (Helpers.isNullOrEmpty(patientInfoId) && Helpers.isNullOrEmpty(accountId))
            throw new Exception("查询条件不能都为空");
        List<PatientInfo> patientInfos = patientInfoService.queryPatientInfo(accountId, patientInfoId, null);
        return ResponseWrapper().addData(patientInfos).ExeSuccess();
    }

    @ApiOperation(value = "查询全部就诊人信息", response = PatientInfo.class)
    @RequestMapping(value = "queryAllPatientInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllPatientInfo(
            @ApiParam(value = "有效否(为空 查询全部  0 正常 1 禁用 99 删除)", required = false) @RequestParam(value = "enable", required = false) Integer enable,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception {
        PageInfo<PatientInfo> patientInfo = patientInfoService.queryPatientInfoPagaInfo(null, null, enable, Paging.create(pageNum,pageSize,orderBy,desc));
        return ResponseWrapper().addData(patientInfo).ExeSuccess();
    }

    @ApiOperation(value = "app 通过token查询该用户所有就诊人信息", response = PatientInfo.class)
    @RequestMapping(value = "queryPatientInfoByToken", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPatientInfoByToken(HttpServletRequest request,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception {
        PageInfo<PatientInfo> patientInfo = patientInfoService.queryPatientInfoPagaInfo(null, accountService.returnIdByToken(request), EnableEnum.EFFECTIVE.getCode(), Paging.create(pageNum,pageSize,orderBy,desc));
        return ResponseWrapper().addData(patientInfo).ExeSuccess();
    }


    @ApiOperation(value = "修改就诊人信息")
    @RequestMapping(value = "modifyPatientInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyPatientInfo(
            @ApiParam(value = "就诊人ID", required = true) @RequestParam(value = "patientInfoId", required = true) Long patientInfoId,
            @ApiParam(value = "就诊人名称", required = true) @RequestParam(value = "name", required = true) String name,
            @ApiParam(value = "病人电话", required = true) @RequestParam(value = "phone", required = true) String phone,
            @ApiParam(value = "病人身份证", required = true) @RequestParam(value = "idCard", required = true) String idCard) throws Exception {
        patientInfoService.modifyPatientInfo(patientInfoId, name, phone, idCard, null);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "删除就诊人")
    @RequestMapping(value = "failurePatientInfo", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject deletePatientInfo(
            @ApiParam(value = "就诊人ID", required = true) @RequestParam(value = "patientInfoId", required = true) Long patientInfoId
    ) throws Exception {
        patientInfoService.deletePatientInfo(patientInfoId);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }
}

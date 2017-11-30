package com.jishi.reservation.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.models.Banner;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.HomeService;
import com.jishi.reservation.service.PatientInfoService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.enumPackage.ReturnMessageEnum;
import com.jishi.reservation.service.support.AliOssSupport;
import com.jishi.reservation.service.support.FileSupport;
import com.jishi.reservation.util.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/ad/patientInfo")
@Slf4j
@Api(description = "后台管理平台的的首页(banner)接口")
public class AdminPatientController extends MyBaseController {


    @Autowired
    PatientInfoService patientInfoService;



    @ApiOperation(value = "admin 根据用户id查询对应的就诊人",response = PatientInfo.class)
    @RequestMapping(value = "queryForAdmin", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPatientForAdmin(
            @ApiParam(value = "账号id", required = true) @RequestParam(value = "accountId", required = true) Long accountId
    ) throws Exception {


        List<PatientInfo> list = patientInfoService.queryPatientInfo(null, accountId, 0);
        return ResponseWrapper().addData("ok").addData(list).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }



}

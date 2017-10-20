package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Constant;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.protocol.LoginData;
import com.jishi.reservation.controller.protocol.PatientHisVO;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.his.HisOutpatient;
import com.jishi.reservation.service.his.HisUserManager;
import com.jishi.reservation.service.his.bean.Credentials;
import com.jishi.reservation.service.his.bean.DepartmentList;
import com.jishi.reservation.service.his.bean.PatientsList;
import com.jishi.reservation.service.his.bean.RegisteredNumberInfo;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/his_doctor")
@Slf4j
@Api(description = "对接了his系统的醫生相关接口")
public class HisDoctorController extends BaseController{

    @Autowired
    HisOutpatient hisOutpatient;


    @Autowired
    HisUserManager hisUserManager;




    @ApiOperation(value = "查询指定天数内的可挂号科室列表",response = DepartmentList.DepartmentHis.class)
    @RequestMapping(value = "queryDepartment", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryDepartment(
            @ApiParam(value = "查询天数 7") @RequestParam(value = "cxts",required = true) String cxts

    ) throws Exception {

        DepartmentList departmentList = hisOutpatient.selectDepartments("", cxts, "");
        log.info(JSONObject.toJSONString(departmentList));

        return ResponseWrapper().addData(departmentList).addMessage("查询成功").ExeSuccess(200);
    }




    @ApiOperation(value = "获取挂号号源",response = Doctor.class)
    @RequestMapping(value = "queryRegister", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryRegister(

            @ApiParam(value = "科室id") @RequestParam(value = "ksid",defaultValue = "") String ksid,
            @ApiParam(value = "医生id") @RequestParam(value = "ysid",defaultValue = "") String ysid,
            @ApiParam(value = "医生姓名") @RequestParam(value = "name",defaultValue = "") String name,

            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) throws Exception {

        RegisteredNumberInfo info = hisOutpatient.queryRegisteredNumber("", "", "", ksid, ysid, name, "", "");
        List<RegisteredNumberInfo.Hb> hbList = info.getGroup().getHblist().get(0).getHbList();
        PageInfo<Doctor> pageInfo = new PageInfo<>();
        List<Doctor> doctorList = new ArrayList<>();
        Integer startIndex = (pageNum - 1)*pageSize;
        Integer endIndex = pageNum*pageSize;
        for(int i = startIndex;i<endIndex;i++){
            Doctor doctor = new Doctor();
            RegisteredNumberInfo.Hb hb = hbList.get(i);
            doctor.setName(hb.getYs());
            //TODO his返回的信息没有头像
            doctor.setHeadPortrait("http://jishikeji-hospital.oss-cn-shenzhen.aliyuncs.com/image/doctor/hack.png");
            doctor.setHymc(hb.getHymc());
            doctor.setDj(hb.getDj());
            doctor.setGoodDescribe("医生擅长各种疑难杂症");
            doctor.setYsid(hb.getYsid());
            doctor.setKsmc(hb.getKsmc());

            doctorList.add(doctor);

        }
        pageInfo.setList(doctorList);
        pageInfo.setTotal(hbList.size());
        pageInfo.setPages(hbList.size()/pageSize +1);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);

        log.info(JSONObject.toJSONString(info));
        return ResponseWrapper().addData(pageInfo).addMessage("查询成功").ExeSuccess(200);
    }




}

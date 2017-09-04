package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.dao.models.Department;
import com.jishi.reservation.service.DepartmentService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/department")
@Slf4j
@Api(description = "科室相关接口")
public class DepartmentController extends BaseController{

    @Autowired
    DepartmentService departmentService;

    @ApiOperation(value = "增加科室")
    @RequestMapping(value = "addDepartment", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject addDepartment(
            @ApiParam(value = "科室名称", required = true) @RequestParam(value = "departmentName", required = true) String departmentName) throws Exception {
        departmentService.addDepartment(departmentName);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "查询全部科室",response=Department.class)
    @RequestMapping(value = "queryAllDepartment", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllDepartment(
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception {
        List<Department> departmentList = departmentService.queryAllDepartment();
        return ResponseWrapper().addData(departmentList).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "修改科室")
    @RequestMapping(value = "modifyDepartment", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyDepartment(
            @ApiParam(value = "科室ID", required = true) @RequestParam(value = "departmentId", required = true) Long departmentId,
            @ApiParam(value = "科室名称", required = true) @RequestParam(value = "departmentName", required = true) String departmentName) throws Exception {
        departmentService.modifyDepartment(departmentId,departmentName,null);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "失效科室")
    @RequestMapping(value = "failureDepartment", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject failureDepartment(
            @ApiParam(value = "科室ID", required = true) @RequestParam(value = "departmentId", required = true) Long departmentId
    ) throws Exception {
        departmentService.failureDepartment(departmentId);
        return ResponseWrapper().addData("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

}

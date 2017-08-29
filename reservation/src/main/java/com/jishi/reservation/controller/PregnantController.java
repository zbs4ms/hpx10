package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.RegisterVO;
import com.jishi.reservation.dao.models.*;
import com.jishi.reservation.service.*;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/pregnant")
@Slf4j
@Api(description = "孕妇信息相关接口")
public class PregnantController extends BaseController {

    @Autowired
    PregnantService pregnantService;

    @ApiOperation(value = "添加孕妇信息")
    @RequestMapping(value = "addPregnant", method ={RequestMethod.PUT,RequestMethod.POST} )
    @ResponseBody
    public JSONObject addPregnant(
            @ApiParam(value = "账号ID", required = true) @RequestParam(value = "accountId", required = true) Long accountId,
            @ApiParam(value = "病人ID", required = true) @RequestParam(value = "patientinfoId", required = true) Long patientinfoId,
            @ApiParam(value = "孕妇姓名", required = true) @RequestParam(value = "name", required = true) String  name,
            @ApiParam(value = "出生年月", required = true) @RequestParam(value = "birth", required = true) Long birth,
            @ApiParam(value = "现居地址", required = true) @RequestParam(value = "livingAddress", required = true) String livingAddress,
            @ApiParam(value = "末次月经时间", required = true) @RequestParam(value = "lastMenses", required = true) Long lastMenses,
            @ApiParam(value = "联系电话", required = true) @RequestParam(value = "telephone", required = true) String telephone,
            @ApiParam(value = "丈夫姓名", required = true) @RequestParam(value = "husbandName", required = true) String  husbandName,
            @ApiParam(value = "丈夫电话", required = true) @RequestParam(value = "husbandTelephone", required = true) String husbandTelephone,
            @ApiParam(value = "备注", required = false) @RequestParam(value = "remark", required = false) String remark
            ) throws Exception {

        pregnantService.addPregnant(accountId,patientinfoId,name,new Date(birth),livingAddress,new Date(lastMenses),telephone,husbandName,husbandTelephone,remark);

        return  ResponseWrapper().addMessage("孕妇信息添加成功.").ExeSuccess();
    }


    @ApiOperation(value = "更新孕妇信息")
    @RequestMapping(value = "updatePregnant", method =RequestMethod.POST )
    @ResponseBody
    public JSONObject updatePregnant(
            @ApiParam(value = "孕妇信息ID", required = true) @RequestParam(value = "pregnantId", required = true) Long pregnantId,
            @ApiParam(value = "孕妇姓名", required = true) @RequestParam(value = "name", required = true) String  name,
            @ApiParam(value = "出生年月", required = true) @RequestParam(value = "birth", required = true) Long birth,
            @ApiParam(value = "现居地址", required = true) @RequestParam(value = "livingAddress", required = true) String livingAddress,
            @ApiParam(value = "末次月经时间", required = true) @RequestParam(value = "lastMenses", required = true) Long lastMenses,
            @ApiParam(value = "联系电话", required = true) @RequestParam(value = "telephone", required = true) String telephone,
            @ApiParam(value = "丈夫姓名", required = true) @RequestParam(value = "husbandName", required = true) String  husbandName,
            @ApiParam(value = "丈夫电话", required = true) @RequestParam(value = "husbandTelephone", required = true) String husbandTelephone
    ) throws Exception {

        pregnantService.updatePregnant(pregnantId,name,new Date(birth),livingAddress,new Date(lastMenses),telephone,husbandName,husbandTelephone,EnableEnum.EFFECTIVE.getCode());

        return  ResponseWrapper().addMessage("孕妇信息修改成功.").ExeSuccess();
    }

    @ApiOperation(value = "查询指定孕妇信息 还没看到姓名的模糊搜索要求，所以是精确匹配姓名",response = Pregnant.class)
    @RequestMapping(value = "queryPregnant", method =RequestMethod.GET )
    @ResponseBody
    public JSONObject queryPregnant(
            @ApiParam(value = "孕妇信息ID", required = false) @RequestParam(value = "pregnantId", required = false) Long pregnantId,
            @ApiParam(value = "病人ID", required = false) @RequestParam(value = "patientinfoId", required = false) Long patientinfoId,
            @ApiParam(value = "孕妇姓名", required = false) @RequestParam(value = "name", required = false) String  name
    ) throws Exception {

        List<Pregnant> list = pregnantService.queryPregnant(pregnantId,patientinfoId,name,EnableEnum.EFFECTIVE.getCode());

        return  ResponseWrapper().addMessage("查询成功.").addData(list).ExeSuccess();
    }


    @ApiOperation(value = "(软)删除孕妇  enable 设置为1")
    @RequestMapping(value = "deletePregnant", method ={RequestMethod.POST,RequestMethod.DELETE} )
    @ResponseBody
    public JSONObject deletePregnant(
            @ApiParam(value = "孕妇信息ID", required = true) @RequestParam(value = "pregnantId", required = true) Long pregnantId
    ) throws Exception {

        pregnantService.updatePregnant(pregnantId,null,null,null,null,null,null,null,EnableEnum.DELETE.getCode());

        return  ResponseWrapper().addMessage("操作成功.").ExeSuccess();
    }



}

package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.DateVO;
import com.jishi.reservation.dao.models.Banner;
import com.jishi.reservation.dao.models.Diary;
import com.jishi.reservation.otherService.pay.AlibabaPay;
import com.jishi.reservation.service.DiaryService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.support.DateSupport;
import com.jishi.reservation.util.Common;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/diray")
@Slf4j
@Api(description = "日记接口")
public class DiaryController extends BaseController {



    @Autowired
    private DiaryService diaryService;

    @ApiOperation(value = "查询日记列表  分页")
    @RequestMapping(value = "query", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject query(
            @ApiParam(value = "名称 关键字", required = false) @RequestParam(value = "query", required = false) String query,
            @ApiParam(value = "审核状态 0审核通过 1等待审核  2审核拒绝", required = false) @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "开始时间", required = false) @RequestParam(value = "startTime", required = false) Long startTime,
            @ApiParam(value = "结束时间", required = false) @RequestParam(value = "endTime", required = false) Long endTime,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,

            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序 sort create_time", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc
    ){
        PageInfo<Diary> diaryPageInfo = diaryService.queryDiaryPageInfo(query,status,startTime,endTime, Paging.create(pageNum,pageSize,orderBy,desc));
        return ResponseWrapper().addMessage("查询成功").addData(diaryPageInfo).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }

    @ApiOperation(value = "审核日记")
    @RequestMapping(value = "verify", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject verify(
            @ApiParam(value = "日记 的 id") @RequestParam(value = "id") Long id,
            @ApiParam(value = "审核的结果 0审核通过 2审核拒绝") @RequestParam(value = "status")Integer status
                ){
        diaryService.verify(id, status);
        return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }

    @ApiOperation(value = "上下架 日记")
    @RequestMapping(value = "show", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject show(
            @ApiParam(value = "日记的id") @RequestParam(value = "id") Long id
    ){
        diaryService.show(id);
        return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }


    @ApiOperation(value = "置顶 日记")
    @RequestMapping(value = "top", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject top(
            @ApiParam(value = "日记的id") @RequestParam(value = "id") Long id
    ){
        diaryService.top(id);
        return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }




}

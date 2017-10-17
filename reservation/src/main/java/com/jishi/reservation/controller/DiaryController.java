package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.otherService.pay.AlibabaPay;
import com.jishi.reservation.service.DiaryService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


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


}
package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.github.pagehelper.PageInfo;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.models.Banner;
import com.jishi.reservation.service.HomeService;
import com.jishi.reservation.service.support.AliOssSupport;
import com.jishi.reservation.util.Common;
import com.us.base.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/home")
@Slf4j
@Api(description = "首页相关接口")
public class HomeController extends BaseController {

    @Autowired
    HomeService homeService;

    @Autowired
    AliOssSupport ossSupport;

    //*******************  banner  *********************************
    @ApiOperation(value = "增加banner")
    @RequestMapping(value = "addBanner", method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject addBanner(
            @ApiParam(value = "banner 名称", required = true) @RequestParam(value = "name", required = true) String name,
            @ApiParam(value = "banner 图片"  )@RequestParam(value = "file")MultipartFile file,
            @ApiParam(value = "跳转的url", required = true) @RequestParam(value = "jumpUrl", required = true) String jumpUrl,
            @ApiParam(value = "序号", required = true) @RequestParam(value = "orderNumber", required = true) String orderNumber
             ) throws Exception {

        String fileUrl = ossSupport.uploadImage(file,Common.BANNER_PATH);
        homeService.addBanner(name,fileUrl, jumpUrl,orderNumber);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "修改banner")
    @RequestMapping(value = "modifyBanner", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject modifyBanner(
            @ApiParam(value = "banner的ID", required = true) @RequestParam(value = "bannerId", required = true) Long bannerId,
            @ApiParam(value = "banner 名称", required = true) @RequestParam(value = "name", required = true) String name,
            @ApiParam(value = "跳转的url", required = true) @RequestParam(value = "jumpUrl", required = true) String jumpUrl,
            @ApiParam(value = "banner 图片"  )@RequestParam(value = "file")MultipartFile file,
            @ApiParam(value = "序号", required = false) @RequestParam(value = "orderNumber", required = false) String orderNumber) throws Exception {
        String fileUrl = ossSupport.uploadImage(file,Common.BANNER_PATH);
        homeService.modifyBanner(bannerId, name,fileUrl, jumpUrl,orderNumber);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "查询单个banner", response = Banner.class)
    @RequestMapping(value = "queryBanner", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryBanner(
            @ApiParam(value = "有效否(为空 查询全部  0 正常 1 禁用 99 删除)", required = false) @RequestParam(value = "enable", required = false) Integer enable,
            @ApiParam(value = "banner的ID", required = true) @RequestParam(value = "bannerId", required = true) Long bannerId) throws Exception {
        List<Banner> bannerList = homeService.queryBanner(bannerId,null,enable);
        Banner banner = bannerList ==null || bannerList.size() == 0 ? null : bannerList.get(0);
        return ResponseWrapper().addData(banner).ExeSuccess();
    }

    @ApiOperation(value = "查询全部banner", response = Banner.class)
    @RequestMapping(value = "queryAllBanner", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllBanner(
            @ApiParam(value = "名称 关键字", required = false) @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "有效否(为空 查询全部  0 正常 1 禁用 99 删除)", required = false) @RequestParam(value = "enable", required = false) Integer enable,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "排序", required = false) @RequestParam(value = "orderBy", required = false) String orderBy,
            @ApiParam(value = "是否是倒排序", required = false) @RequestParam(value = "desc", required = false) Boolean desc) throws Exception

    {
        PageInfo<Banner> bannerList = homeService.queryBannerPageInfo(null,name,enable, Paging.create(pageNum,pageSize,orderBy,desc));
        return ResponseWrapper().addData(bannerList).ExeSuccess();
    }

    @ApiOperation(value = "删除单个banner")
    @RequestMapping(value = "deleteBanner", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject deleteBanner(
            @ApiParam(value = "banner的图片ID", required = true) @RequestParam(value = "bannerId", required = true) Long bannerId) throws Exception {
        homeService.deleteBanner(bannerId);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "隐藏/显示 单个banner")
    @RequestMapping(value = "hideOrShowBanner", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hideOrShowBanner(
            @ApiParam(value = "banner的图片ID", required = true) @RequestParam(value = "bannerId", required = true) Long bannerId) throws Exception {
        homeService.hideOrShowBanner(bannerId);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }

    @ApiOperation(value = "批量删除banner")
    @RequestMapping(value = "deleteBannerBatch", method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject deleteBannerBatch(
            @ApiParam(value = "banner的ID  ','分隔", required = true) @RequestParam(value = "bannerIdList", required = true) String bannerIdList) throws Exception {
        homeService.deleteBannerBatch(bannerIdList);
        return ResponseWrapper().addData("ok").ExeSuccess();
    }
}

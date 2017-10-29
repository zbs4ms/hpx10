package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;

import com.jishi.reservation.controller.protocol.DiaryContentVO;
import com.jishi.reservation.dao.models.Diary;

import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.DiaryService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;

import com.us.base.util.tool.IpTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by zbs on 2017/8/10.
 */
@RestController
@RequestMapping("/diray")
@Slf4j
@Api(description = "日记接口")
public class DiaryController extends MyBaseController {


    @Autowired
    private AccountService accountService;


    @Autowired
    private DiaryService diaryService;

    @ApiOperation(value = "admin 查询日记列表  分页")
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

    @ApiOperation(value = "admin 审核日记")
    @RequestMapping(value = "verify", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject verify(
            @ApiParam(value = "日记 的 id") @RequestParam(value = "id") Long id,
            @ApiParam(value = "审核的结果 0审核通过 1dengdai shenhe  2审核拒绝") @RequestParam(value = "status")Integer status
                ){
        log.info("执行日记审核操作。id:"+id+",status:"+status);
        diaryService.verify(id, status);
        return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }

    @ApiOperation(value = "admin 上下架 日记")
    @RequestMapping(value = "show", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject show(
            @ApiParam(value = "日记的id") @RequestParam(value = "id") Long id
    ){
        log.info("执行日记上下架操作。id:"+id);

        diaryService.show(id);
        return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }




    @ApiOperation(value = "admin 置顶 日记")
    @RequestMapping(value = "top", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject top(
            @ApiParam(value = "日记的id") @RequestParam(value = "id") Long id
    ){

        log.info("执行日记置顶操作。id:"+id);

        diaryService.top(id);
        return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }

    @ApiOperation(value = "app 查看 日记",response = DiaryContentVO.class)
    @RequestMapping(value = "scan", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject scan(
            @ApiParam(value = "日记的id") @RequestParam(value = "id") Long id
    ){

        log.info("查看日记 id:"+id);

        Diary diary = diaryService.queryById(id);
        return ResponseWrapper().addMessage("查询成功").addData(diary).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }









    @ApiOperation(value = "app 用户发布日记/支持修改 传diaryId就是修改")
    @RequestMapping(value = "publish", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject top(
            HttpServletRequest request,HttpServletResponse response,
            @ApiParam(value = "accountId  token") @RequestParam(value = "accountId",required = false) Long accountId,
            @ApiParam(value = "日记的标题") @RequestParam(value = "title",required = false) String title,
            @ApiParam(value = "日记的简介",required = true) @RequestParam(value = "brief",required = true) String brief,
            @ApiParam(value = "日记的内容 json格式保存   eg:[{\"fontName\":\"宋体\",\"lineSpace\":10,\"fontSize\":10,\"text\":\"我是文字\",\"type\":1,\"textColor\":\"red\"},{\"width\":200,\"type\":0,\"url\":\"http://jishikeji-hospital.oss-cn-shenzhen.aliyuncs.com/image/doctor/WechatIMG198.jpg\",\"height\":200}]",required = true)
            @RequestParam(value = "content") String content,
            @ApiParam(value = "日记的id") @RequestParam(value = "diaryId",required = false) Long diaryId,
            @ApiParam(value = "日记是否锁定  0锁定，只有自己能看  1不锁定 大家都能看") @RequestParam(value = "isLock") Integer isLock
            ) throws Exception {


        if (accountId == null) {
            accountId = accountService.returnIdByToken(request);
            if(accountId.equals(-1L)){
                response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }
        if(diaryId ==null ){
            diaryService.publish(accountId,title,brief,content,isLock);

        }else {

            Preconditions.checkNotNull(diaryService.queryById(diaryId),"该id没有对应的日记");
            diaryService.update(diaryId,title,brief,content,isLock);
        }



        return ResponseWrapper().addMessage("添加成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }




    @ApiOperation(value = "app app的日记列表  日记圈/我的日记")
    @RequestMapping(value = "queryPage", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPage(
            HttpServletRequest request,HttpServletResponse response,

            @ApiParam(value = "是否查\"我的日记\" 0 查，1 不查", required = false) @RequestParam(value = "isMy", defaultValue = "1") Integer isMy,

            @ApiParam(value = "页数", required = false) @RequestParam(value = "startPage", defaultValue = "1") Integer startPage,

            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize


    ) throws Exception {
        Long accountId = null;
        if(isMy != 1){
            accountId = isMy == 0?accountService.returnIdByToken(request):null;
            if(accountId == -1L) {
                return ResponseWrapper().addMessage("请登录").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
            }
        }

        PageInfo<Diary> page = diaryService.queryPage(accountId,startPage,pageSize);


        return ResponseWrapper().addMessage("查询成功").addData(page).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }



    @ApiOperation(value = "app 给日记点赞/取消点赞")
    @RequestMapping(value = "likeDiary", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject likeDiary(
            HttpServletRequest request,HttpServletResponse response,

            @ApiParam(value = "日记的id", required = true) @RequestParam(value = "diaryId") Long diaryId


    ) throws Exception {
        Long accountId = accountService.returnIdByToken(request);
        if(accountId.equals(-1L)){
            response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

            return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
        }


        Integer integer = diaryService.likeDiary(diaryId, accountId);


        return ResponseWrapper().addMessage("操作成功").addData(integer).ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }



    @ApiOperation(value = "app 给日记增加浏览次数")
    @RequestMapping(value = "addScanNum", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addScanNum(
            HttpServletRequest request,HttpServletResponse response,
            //@ApiParam(value = "accountId  token") @RequestParam(value = "accountId") Long accountId,

            @ApiParam(value = "日记的id", required = true) @RequestParam(value = "diaryId") Long diaryId


    ) throws Exception {
        Long accountId = accountService.returnIdByToken(request);


        String ip = IpTool.getIp(request);
        diaryService.addScanNum(diaryId,ip,accountId);


        return ResponseWrapper().addMessage("增加成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());

    }



    @ApiOperation(value = "app 删除 日记 token传递，如果不是发布者删除，会提示错误信息")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject delete(
            HttpServletRequest request,HttpServletResponse response,
            @ApiParam(value = "日记的id ",required = true) @RequestParam(value = "diaryId") Long diaryId
    ) throws Exception {
        Long accountId = accountService.returnIdByToken(request);
        if(accountId.equals(-1L)){
            response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

            return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
        }
        Integer state = diaryService.delete(diaryId,accountId);
        switch (state){
            case 0:
                return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
            case 1:
                return ResponseWrapper().addMessage("您无权执行该操作").ExeSuccess(ReturnCodeEnum.FAILED.getCode());

        }


        return null;

    }


    @ApiOperation(value = "app 上锁/解锁 日记 token传递，如果不是发布者上锁，会提示错误信息")
    @RequestMapping(value = "lock", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject lock(
            HttpServletRequest request,HttpServletResponse response,
            @ApiParam(value = "日记的id") @RequestParam(value = "diaryId") Long diaryId
    ) throws Exception {
        Long accountId = accountService.returnIdByToken(request);
        if(accountId.equals(-1L)){
            response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

            return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
        }
        Integer state = diaryService.lock(diaryId,accountId);
        switch (state){
            case 0:
                return ResponseWrapper().addMessage("操作成功").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
            case 1:
                return ResponseWrapper().addMessage("您无权执行该操作").ExeSuccess(ReturnCodeEnum.FAILED.getCode());

        }


        return null;

    }

}

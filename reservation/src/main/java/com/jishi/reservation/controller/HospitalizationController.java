package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jishi.reservation.controller.base.MyBaseController;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.HospitalizationInfoVO;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.AccountService;
import com.jishi.reservation.service.HospitalizationService;
import com.jishi.reservation.service.PatientInfoService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.his.bean.DepositBalanceDetail;
import com.jishi.reservation.util.DateTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/hospitalization")
@Slf4j
@Api(description = "住院相关接口")
public class HospitalizationController extends MyBaseController {

    @Autowired
    HospitalizationService hospitalizationService;

    @Autowired
    AccountService accountService;

    @Autowired
    PatientInfoService patientInfoService;

    @ApiOperation(value = "查询住院人历史的住院信息 分页  by token", response = HospitalizationInfoVO.class)
    @RequestMapping(value = "queryAllInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllInfo(
            HttpServletRequest request,HttpServletResponse response,
            @ApiParam(value = "状态 1 查询未支付订单 0 查询所有订单", required = false) @RequestParam(value = "status",required = false) Integer status,
            @ApiParam(value = "accountId 通过token找到", required = false) @RequestParam(value = "accountId", required = false) Long accountId,
            @ApiParam(value = "页数", required = false) @RequestParam(value = "startPage", defaultValue = "1") Integer startPage,
            @ApiParam(value = "每页多少条", required = false) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws Exception {
        List<HospitalizationInfoVO> list = new ArrayList<>();
        PageInfo<HospitalizationInfoVO> page = new PageInfo<>();

        if(startPage == 0){
            log.info("处理分页参数");
            startPage =1;

        }
        if(pageSize ==0 ){
            pageSize = 100;
        }
        //供测试数据使用。。。
        if(accountId == 3987){
            List<DepositBalanceDetail> depositBalanceDetails = hospitalizationService.queryAllInfo("3987");
            if (depositBalanceDetails == null)
                return ResponseWrapper().addData(null).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
            for (DepositBalanceDetail depositBalanceDetail : depositBalanceDetails) {
                list.add(getHospitalizationInfoVO(depositBalanceDetail));
            }


        }else {
            if (accountId == null) {
                accountId = accountService.returnIdByToken(request);
                if(accountId.equals(-1L)){
                    response.setStatus(ReturnCodeEnum.NOT_LOGIN.getCode());

                    return ResponseWrapper().addMessage("登陆信息已过期，请重新登陆").ExeFaild(ReturnCodeEnum.NOT_LOGIN.getCode());
                }
            }

            List<String> brIdList = patientInfoService.queryBrIdByAccountId(accountId);
            log.info("该账号拥有的病人id:"+JSONObject.toJSONString(brIdList));
            for (String brId : brIdList) {
                List<DepositBalanceDetail> depositBalanceDetails = hospitalizationService.queryAllInfo(brId);
                if (depositBalanceDetails == null)
                    return ResponseWrapper().addData(null).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
                for (DepositBalanceDetail depositBalanceDetail : depositBalanceDetails) {
//
//                    if(status == 1){
//                        if(depositBalanceDetail.getZyzt())
//                    }
                    list.add(getHospitalizationInfoVO(depositBalanceDetail));
                }
            }
        }



        page = patientInfoService.wrapListToPage(list,startPage,pageSize);
        return ResponseWrapper().addData(page).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "查询住院人住院信息", response = HospitalizationInfoVO.class)
    @RequestMapping(value = "queryInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryInfo(@ApiParam(value = "brId", required = false) @RequestParam(value = "brId", required = false) String brId,
                                @ApiParam(value = "入院的次数", required = false) @RequestParam(value = "rycs", required = false) Integer rycs) throws Exception {
        DepositBalanceDetail depositBalanceDetail = hospitalizationService.queryInfo(brId, rycs, null);
        if (depositBalanceDetail == null)
            return ResponseWrapper().addData(null).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
        HospitalizationInfoVO vo = getHospitalizationInfoVO(depositBalanceDetail);
        return ResponseWrapper().addData(vo).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }


    private HospitalizationInfoVO getHospitalizationInfoVO(DepositBalanceDetail depositBalanceDetail) throws Exception {
        HospitalizationInfoVO hospitalizationInfoVO = new HospitalizationInfoVO();
        hospitalizationInfoVO.setState(depositBalanceDetail.getZyzt());
        hospitalizationInfoVO.setName(depositBalanceDetail.getJbxx().getXm());
        hospitalizationInfoVO.setKs(depositBalanceDetail.getJbxx().getZyks());
        hospitalizationInfoVO.setZzys(depositBalanceDetail.getJbxx().getZzys());
        hospitalizationInfoVO.setRysj(DateTool.format(depositBalanceDetail.getJbxx().getRysj(), "yyyy-MM-dd"));
        hospitalizationInfoVO.setCysj(DateTool.format(depositBalanceDetail.getJbxx().getCysj(), "yyyy-MM-dd"));
        hospitalizationInfoVO.setSyje(depositBalanceDetail.getFyxx().getWjfy().getSyje());
        hospitalizationInfoVO.setYjje(depositBalanceDetail.getFyxx().getWjfy().getYjye());
        hospitalizationInfoVO.setYyje(depositBalanceDetail.getFyxx().getYjfy().getZyfy());
        hospitalizationInfoVO.setRycs(depositBalanceDetail.getZycs());
        hospitalizationInfoVO.setYujiaojine(depositBalanceDetail.getYujiaojine());
        return hospitalizationInfoVO;
    }

}

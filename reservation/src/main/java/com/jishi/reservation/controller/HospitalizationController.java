package com.jishi.reservation.controller;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.protocol.HospitalizationInfoVO;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.HospitalizationService;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.jishi.reservation.service.his.bean.DepositBalanceDetail;
import com.us.base.common.controller.BaseController;
import com.jishi.reservation.util.DateTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/hospitalization")
@Slf4j
@Api(description = "住院相关接口")
public class HospitalizationController extends BaseController {

    @Autowired
    HospitalizationService hospitalizationService;

    @ApiOperation(value = "查询住院人历史的住院信息",response = HospitalizationInfoVO.class)
    @RequestMapping(value = "queryAllInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryAllInfo(@ApiParam(value = "brId", required = false) @RequestParam(value = "brId", required = false) Long brId) throws Exception {



        List<DepositBalanceDetail> depositBalanceDetails = hospitalizationService.queryAllInfo(brId);
        if(depositBalanceDetails == null)
            return ResponseWrapper().addData(null).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
        List<HospitalizationInfoVO> list = new ArrayList<>();
        for(DepositBalanceDetail depositBalanceDetail : depositBalanceDetails){
            list.add(getHospitalizationInfoVO(depositBalanceDetail));
        }
        return ResponseWrapper().addData(list).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }

    @ApiOperation(value = "查询住院人住院信息",response = HospitalizationInfoVO.class)
    @RequestMapping(value = "queryInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryInfo(@ApiParam(value = "brId", required = false) @RequestParam(value = "brId", required = false) Long brId,
                                   @ApiParam(value = "入院的次数", required = false) @RequestParam(value = "rycs", required = false) Integer rycs) throws Exception {
        DepositBalanceDetail depositBalanceDetail = hospitalizationService.queryInfo(brId,rycs,null);
        if(depositBalanceDetail == null)
            return ResponseWrapper().addData(null).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
        HospitalizationInfoVO vo = getHospitalizationInfoVO(depositBalanceDetail);
        return ResponseWrapper().addData(vo).addMessage("ok").ExeSuccess(ReturnCodeEnum.SUCCESS.getCode());
    }





    private  HospitalizationInfoVO getHospitalizationInfoVO(DepositBalanceDetail depositBalanceDetail) throws Exception {
        HospitalizationInfoVO hospitalizationInfoVO = new HospitalizationInfoVO();
        hospitalizationInfoVO.setState(depositBalanceDetail.getZyzt());
        hospitalizationInfoVO.setName(depositBalanceDetail.getJbxx().getXm());
        hospitalizationInfoVO.setKs(depositBalanceDetail.getJbxx().getZyks());
        hospitalizationInfoVO.setZzys(depositBalanceDetail.getJbxx().getZzys());
        hospitalizationInfoVO.setRysj(DateTool.format(depositBalanceDetail.getJbxx().getRysj(),"yyyy-MM-dd"));
        hospitalizationInfoVO.setCysj(DateTool.format(depositBalanceDetail.getJbxx().getCysj(),"yyyy-MM-dd"));
        hospitalizationInfoVO.setSyje(depositBalanceDetail.getFyxx().getWjfy().getSyje());
        hospitalizationInfoVO.setYjje(depositBalanceDetail.getFyxx().getWjfy().getYjye());
        hospitalizationInfoVO.setYyje(depositBalanceDetail.getFyxx().getYjfy().getZyfy());
        hospitalizationInfoVO.setRycs(depositBalanceDetail.getZycs());
        hospitalizationInfoVO.setYujiaojine(depositBalanceDetail.getYujiaojine());
        return hospitalizationInfoVO;
    }

}

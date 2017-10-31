package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.protocol.OrderVO;
import com.jishi.reservation.dao.mapper.PrePaymentMapper;
import com.jishi.reservation.dao.models.OrderInfo;
import com.jishi.reservation.dao.models.PrePayment;
import com.jishi.reservation.service.enumPackage.OrderStatusEnum;
import com.jishi.reservation.service.his.HisHospitalization;
import com.jishi.reservation.service.his.bean.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/10/5.
 */
@Service
@Slf4j
public class HospitalizationService {

    @Autowired
    HisHospitalization hisHospitalization;


    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    PrePaymentMapper prePaymentMapper;

    /**
     * 获取用户历史的住院详情信息
     * @param brId
     * @return
     * @throws Exception
     */
    public List<DepositBalanceDetail> queryAllInfo(String brId) throws Exception {
        //获取所有的历史住院信息
        DepositBalanceHistoryDetail depositBalanceHistoryDetail =  hisHospitalization.selectHistoryDetail(String.valueOf(brId),"1","1000","1");
        if(depositBalanceHistoryDetail == null)
            return null;
        List<DepositBalanceDetail> depositBalanceDetails = new ArrayList<>();
        if(depositBalanceHistoryDetail.getGroup().getItemList()!=null && depositBalanceHistoryDetail.getGroup().getItemList().size() !=0){
            for(DepositBalanceHistoryDetail.Item item : depositBalanceHistoryDetail.getGroup().getItemList()){
                depositBalanceDetails.add(queryInfo(brId,Integer.valueOf(item.getZycs()),item.getZyzt()));
            }
        }
        return depositBalanceDetails;

    }

    /**
     * 获取用户住院详情信息
     * @param brId 患者ID
     * @param zycs 住院次数
     * @param zyzt 住院状态 可为空
     * @return
     * @throws Exception
     */
    public DepositBalanceDetail queryInfo(String brId,Integer zycs,String zyzt) throws Exception {
        DepositBalanceDetail depositBalanceDetail = hisHospitalization.selectDetail(brId,String.valueOf(zycs));
        String depositBalance = hisHospitalization.selectDepositBalance(brId, String.valueOf(zycs));
        //写入住院状态参数
        depositBalanceDetail.setYujiaojine(depositBalance);
        depositBalanceDetail.setZyzt(zyzt);
        depositBalanceDetail.setZycs(String.valueOf(zycs));
        return depositBalanceDetail;
    }


    /**
     * 获取用户住院費用清單
     * @param brId 患者ID
     * @param zycs 住院次数
     * @return
     * @throws Exception
     */
    public  List<PayItem> queryPrepayDetail(String brId, Integer zycs) throws Exception {
        TotalDepositBalancePayDetail payDetail = hisHospitalization.selectTotalPayDetail(brId, String.valueOf(zycs), "2");  //2是按日期查
        List<TotalDepositBalancePayDetail.Item> list = payDetail.getItemList();
        log.info(JSONObject.toJSONString(list));
        List<PayItem> voList = new ArrayList<>();
        for (TotalDepositBalancePayDetail.Item item : list) {
            //JSONObject.toJSONString("遍历的对象"+item);
            DepositBalanceDailyPayDetail detail = hisHospitalization.selectDailyPayDetail(brId, item.getLbmc(), String.valueOf(zycs));
            //item.setDetail(detail);
            //log.info("detail:"+JSONObject.toJSONString(detail));
            PayItem vo = new PayItem();
            vo.setFyje(item.getFyje());
            vo.setLbmc(item.getLbmc());
            vo.setDetail(detail);

            voList.add(vo);
        }
        return voList;
    }

    public PageInfo<PayItem> wrapPage(List<PayItem> list, Integer startPage, Integer pageSize) {
        PageInfo<PayItem>  page = new PageInfo<>();
        List<PayItem> result = new ArrayList<>();
        int startRow = (startPage - 1)*pageSize;
        int endRow = list.size()<startPage*pageSize-1?list.size():startPage*pageSize-1;
        if(startPage == endRow)
            endRow+=pageSize;
        if(endRow == 0)
            endRow+=pageSize;

        log.info("endRow :"+endRow);
        if(list.size()<endRow){
            endRow = list.size();
        }
        for(int i = startRow;i<endRow;i++){
            result.add(list.get(i));
        }
        page.setTotal(list.size());
        page.setList(result);
        Integer pages = (list.size()-1)/pageSize+1;
        page.setPages(pages);
        page.setPageNum(startPage);

        return page;

    }

    public String queryPrePayment(String brId, Integer rycs) throws Exception {

        return hisHospitalization.selectDepositBalance(brId, String.valueOf(rycs));
    }

    public DepositBalanceLog queryPaymentRecord(String brId) throws Exception {

        return hisHospitalization.selectDepositBalanceLog(brId);


    }

    public Integer confirmPrePayment(String orderNumber, Long accountId) throws Exception {


        OrderInfo orderInfo = orderInfoService.queryOrderByOrderNumber(orderNumber);
        Preconditions.checkState(orderInfo.getAccountId().equals(accountId),"该用户无权执行此操作");
        PrePayment prePayment =  prePaymentMapper.queryByOrderId(orderInfo.getId());
        String pay = hisHospitalization.pay
                (orderInfo.getBrId(),String.valueOf(prePayment.getRycs()), "", String.valueOf(orderInfo.getPrice()),
                        orderInfo.getThirdOrderNumber(), "支付账号", "支付姓名");
        if(pay!=null && !"".equals(pay)){
            log.info("更新预交订单状态...");
            prePayment.setYjdh(pay);
            return prePaymentMapper.updateByPrimaryKeySelective(prePayment);
        }else {
            return 0;

        }

    }


    @Data
    @ApiModel("住院清单对象")
    public class PayItem{
        @ApiModelProperty(name = "费用金额")
        String fyje;
        @ApiModelProperty(name = "类别名称  日期/收费类型")

        String lbmc;
        DepositBalanceDailyPayDetail detail;
    }

}

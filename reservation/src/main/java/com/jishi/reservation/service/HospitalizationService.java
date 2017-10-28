package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jishi.reservation.controller.protocol.HospitalizationInfoVO;
import com.jishi.reservation.service.his.HisHospitalization;
import com.jishi.reservation.service.his.bean.DepositBalanceDailyPayDetail;
import com.jishi.reservation.service.his.bean.DepositBalanceDetail;
import com.jishi.reservation.service.his.bean.DepositBalanceHistoryDetail;
import com.jishi.reservation.service.his.bean.TotalDepositBalancePayDetail;
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
        //log.info(JSONObject.toJSONString(list));
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
            endRow+=1;
        if(endRow == 0)
            endRow+=1;

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


    @Data
    public class PayItem{

        String fyje;
        String lbmc;
        DepositBalanceDailyPayDetail detail;
    }

}

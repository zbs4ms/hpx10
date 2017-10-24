package com.jishi.reservation.service;

import com.jishi.reservation.dao.config.DataConfig;
import com.jishi.reservation.service.his.HisHospitalization;
import com.jishi.reservation.service.his.bean.DepositBalanceDetail;
import com.jishi.reservation.service.his.bean.DepositBalanceHistoryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/10/5.
 */
@Service
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

}

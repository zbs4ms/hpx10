package com.jishi.reservation.service;

import com.jishi.reservation.controller.protocol.*;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.his.HisOutpatient;
import com.jishi.reservation.service.his.bean.OutpatientPaymentInfo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by liangxiong on 2017/10/25.
 */
@Service
@Log4j
public class OutpatientService {

    @Autowired
    private PatientInfoService patientInfoService;

    @Autowired
    private HisOutpatient hisOutpatient;

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 获取用户门诊缴费列表
     * @param accountId
     * @return
     * @throws Exception
     */
    public List<OutpatientPaymentInfoVO> queryOutpatientPamentInfo(long accountId) throws Exception {
        List<PatientInfo> patientInfoList = patientInfoService.queryPatientInfo(null,accountId, 0);
        if (patientInfoList == null || patientInfoList.isEmpty()) {
            return Collections.emptyList();
        }
        List<OutpatientPaymentInfoVO> paymentInfoList = new ArrayList<OutpatientPaymentInfoVO>();
        for (PatientInfo info : patientInfoList) {
            // TODO 结算卡类别和站点暂时传null
            OutpatientPaymentInfo data = hisOutpatient.queryPayReceipt(info.getBrId(), "", "", "");
            List<OutpatientPaymentInfo.Ghlist> ghList = data.getGhlist();
            if (ghList == null || ghList.isEmpty()) {
              continue;
            }
            for (OutpatientPaymentInfo.Ghlist gh : ghList) {
                OutpatientPaymentInfoVO paymentInfo = new OutpatientPaymentInfoVO();
                paymentInfo.setBrid(info.getBrId());
                paymentInfo.setDocumentNum(gh.getGh().getDjh());
                paymentInfo.setRegisterType(gh.getGh().getHl());
                paymentInfo.setRegisterDate(formatDate(gh.getGh().getYysj()));
                paymentInfo.setDepartment(gh.getGh().getKdks());
                paymentInfo.setExeStatus(gh.getGh().getZxzt());
                paymentInfo.setDocumentType(Integer.parseInt(gh.getGh().getDjlx()));
                paymentInfo.setPaymentStatus(Integer.parseInt(gh.getGh().getZfzt()));
                paymentInfo.setHasRegister(Integer.parseInt(gh.getGh().getSfyy()));
                paymentInfo.setPaymentAmount(Double.parseDouble(gh.getGh().getJe()));
                paymentInfo.setDockerId(gh.getGh().getYsid());
                paymentInfo.setDockerName(gh.getGh().getYsxm());
                paymentInfo.setHasPayCard(Integer.parseInt(gh.getGh().getSfjsk()));
                List<OutpatientPaymentInfo.Yzlist> yzLists = gh.getGh().getYzlists();
                if (yzLists == null || yzLists.isEmpty()) {
                    continue;
                }

                // 医嘱列表
                List<OutpatientAdviceVO> adviceList = new ArrayList<OutpatientAdviceVO>();
                for (OutpatientPaymentInfo.Yzlist yz : yzLists) {
                    OutpatientAdviceVO advice = new OutpatientAdviceVO();
                    advice.setAdviceId(yz.getYz().getYzid());
                    advice.setAdviceName(yz.getYz().getYzmc());
                    advice.setAdviceType(yz.getYz().getYzlx());

                    // 费目列表
                    List<OutpatientPaymentInfo.Fmlist> fmlists = yz.getYz().getFmlists();
                    if (fmlists != null && !fmlists.isEmpty()) {
                        List<OutpatientFeeVO> feeList = new ArrayList<OutpatientFeeVO>();
                        for (OutpatientPaymentInfo.Fmlist fm : fmlists) {
                            OutpatientFeeVO fee = new OutpatientFeeVO();
                            fee.setFeeName(fm.getFm().getMc());
                            fee.setFeeAmount(Double.parseDouble(fm.getFm().getJe()));
                            fee.setFeeStatus(Integer.parseInt(fm.getFm().getZfzt()));

                            // 费目明细列表
                            List<OutpatientPaymentInfo.Mxlist> mxlists = fm.getFm().getMxlists();
                            if (mxlists!= null && !mxlists.isEmpty()) {
                                List<OutpatientFeeVO.OutpatientFeeItemVO> itemList = new ArrayList<OutpatientFeeVO.OutpatientFeeItemVO>();
                                for (OutpatientPaymentInfo.Mxlist mx : mxlists) {
                                    OutpatientFeeVO.OutpatientFeeItemVO item = new OutpatientFeeVO.OutpatientFeeItemVO();
                                    item.setItemName(mx.getMx().getMc());
                                    item.setItemAdvance(Double.parseDouble(mx.getMx().getDj()));
                                    item.setItemFormat(mx.getMx().getGg());
                                    item.setItemNumber(Integer.parseInt(mx.getMx().getSl()));
                                    item.setItemUnit(mx.getMx().getDw());

                                    itemList.add(item);
                                }

                                fee.setFeeItemList(itemList);
                            }

                            feeList.add(fee);
                        }
                        advice.setFeeList(feeList);
                    }

                    // 单据列表
                    List<OutpatientPaymentInfo.Djlist> djLists = yz.getYz().getDjlists();
                    if (djLists != null && !djLists.isEmpty()) {
                        List<OutpatientFeeDocVO> feeDocList = new ArrayList<OutpatientFeeDocVO>();
                        for (OutpatientPaymentInfo.Djlist dj : djLists) {
                            OutpatientFeeDocVO doc = new OutpatientFeeDocVO();
                            doc.setDocumentNum(dj.getDj().getDjh());
                            doc.setDocumentDate(formatDate(dj.getDj().getKdsj()));
                            doc.setDocumentType(dj.getDj().getDjlx());
                            doc.setHasPayCard(Integer.parseInt(dj.getDj().getSfjsk()));
                            doc.setPayStatus(Integer.parseInt(dj.getDj().getZfzt()));
                            String ytje = dj.getDj().getYtje();
                            double returnNum = (ytje == null || ytje.isEmpty()) ? 0.0 : Double.parseDouble(ytje);
                            doc.setReturnNumber(returnNum);

                            feeDocList.add(doc);
                        }
                        advice.setFeeDocList(feeDocList);
                    }

                    adviceList.add(advice);
                }
                paymentInfo.setAdviceList(adviceList);
                paymentInfoList.add(paymentInfo);
            }
        }
        if (paymentInfoList != null) {
            Collections.sort(paymentInfoList);
        }
        return paymentInfoList;
    }

    public boolean batchpayConfirm(String docIds, String brId, double zje, double jsje, int sfghd, long orderId) throws Exception {
        OrderVO order = orderInfoService.queryOrderInfoById(orderId);
        // TODO 校验总金额和支付结果
        // TODO 第三方名称, 暂时传空, 交易流水号 交易内容
        String czsj = hisOutpatient.batchPayModify(docIds, brId, zje, jsje, sfghd, null, null, null);
        return czsj != null && !czsj.isEmpty();
    }

    private Date formatDate(String str) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.parse(str);
    }
}

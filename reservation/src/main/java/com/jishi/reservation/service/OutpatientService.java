package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.protocol.*;
import com.jishi.reservation.dao.models.PatientInfo;
import com.jishi.reservation.service.his.HisOutpatient;
import com.jishi.reservation.service.his.bean.OutpatientPaymentInfo;
import com.jishi.reservation.service.his.bean.OutpatientVisitPrescription;
import com.jishi.reservation.service.his.bean.OutpatientVisitReceipt;
import com.jishi.reservation.service.his.bean.OutpatientVisitRecord;
import com.jishi.reservation.util.Helpers;
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

    private static final int OUTPATIENT_PAMENT_INFO_QUERY_DAY = 30;

    /**
     * 获取用户门诊缴费列表
     * @param accountId
     * @return
     * @throws Exception
     */
    public List<OutpatientPaymentInfoVO> queryOutpatientPamentInfo(long accountId) throws Exception {
        List<PatientInfo> patientInfoList = patientInfoService.queryPatientInfo(null,accountId, 0);
        if (patientInfoList == null || patientInfoList.isEmpty()) {
            log.info(accountId + ": 病人列表为空");
            return Collections.emptyList();
        }
        List<OutpatientPaymentInfoVO> paymentInfoList = new ArrayList<OutpatientPaymentInfoVO>();
        for (PatientInfo info : patientInfoList) {
            // TODO 结算卡类别和站点暂时传null
            OutpatientPaymentInfo data = hisOutpatient.queryPayReceipt(info.getBrId(), "", OUTPATIENT_PAMENT_INFO_QUERY_DAY, "");
            List<OutpatientPaymentInfo.Ghlist> ghList = data.getGhlist();
            if (ghList == null || ghList.isEmpty()) {
              log.info(info.getBrId() + ": 门诊缴费列表为空");
              continue;
            }
            log.info(info.getBrId() + "的his门诊缴费列表返回值：" + JSONObject.toJSONString(ghList));
            for (OutpatientPaymentInfo.Ghlist gh : ghList) {
                if (gh == null || gh.getGh() == null) {
                    continue;
                }
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
        OrderVO order = orderInfoService.queryOrderVoById(orderId,null);
        log.info("batchpayConfirm => OrderNumber: " + order.getOrderNumber() + "病人Id" + brId + "单据号" + docIds);
        // TODO 校验总金额和支付结果
        // TODO 第三方名称, 暂时传空, 交易流水号 交易内容
        String czsj = hisOutpatient.batchPayModify(docIds, brId, zje, jsje, sfghd, null, null, null);
        log.info(czsj + " batchpayConfirm => 病人ID: " + brId + "单据号：" + docIds + "总/结算金额: " + zje + jsje);
        return czsj != null && !czsj.isEmpty();
    }

    /**
     * @description
     * @param accountId 用户账号
     * @param pageNo 当前页数
     * @param pageSize 每页记录数
     * @throws
    **/
    public List<OutpatientVisitRecordVO> queryVisitRecord(long accountId, int pageNo, int pageSize) throws Exception {
        List<PatientInfo> patientInfoList = patientInfoService.queryPatientInfo(null,accountId, 0);
        if (patientInfoList == null || patientInfoList.isEmpty()) {
          log.info(accountId + ": 病人列表为空");
          return Collections.emptyList();
        }
        if (pageSize == 0) {
            pageSize = Integer.MAX_VALUE;
            pageNo = 1;
        }
        pageNo = pageNo < 0 ? 0 : pageNo;
        List<OutpatientVisitRecordVO> recordVOList = new ArrayList<OutpatientVisitRecordVO>();
        // TODO 确认his当前页数是否从1开始， 站点暂时传null
        for (PatientInfo patientInfo : patientInfoList) {
            OutpatientVisitRecord visitRecordData = hisOutpatient.queryOutpatientVisitRecord(patientInfo.getBrId(), pageNo, pageSize, null);
            if (visitRecordData.getInfolist() == null || visitRecordData.getInfolist().getList() == null
                      || visitRecordData.getInfolist().getList().isEmpty()) {
                continue;
            }
            for (OutpatientVisitRecord.VisitRecord record : visitRecordData.getInfolist().getList()) {
                OutpatientVisitRecordVO recordVO = new OutpatientVisitRecordVO();
                recordVO.setBrid(patientInfo.getBrId());
                recordVO.setRgisterNum(record.getGhdh());
                recordVO.setAmount(Double.parseDouble(record.getJe()));
                recordVO.setDate(formatDate(record.getRq()));
                recordVO.setDepartment(record.getJzks());
                recordVO.setDepartmentId(record.getJzksid());
                recordVO.setDoctor(record.getYs());
                recordVO.setZdxx(record.getZdxx());

                List<OutpatientVisitRecord.RecordMX> mxList = record.getMxlist().getList();
                if (mxList != null && !mxList.isEmpty()) {
                    List<OutpatientVisitRecordVO.RecordMX> recordMXVOList = new ArrayList<OutpatientVisitRecordVO.RecordMX>();
                        for (OutpatientVisitRecord.RecordMX mx : mxList) {
                            OutpatientVisitRecordVO.RecordMX mxVO = new OutpatientVisitRecordVO.RecordMX();
                            mxVO.setDocName(mx.getMc());
                            mxVO.setDocNum(Integer.parseInt(mx.getSl()));
                            recordMXVOList.add(mxVO);
                        }
                    recordVO.setDocList(recordMXVOList);
                }
                recordVOList.add(recordVO);
              }
          }

        return recordVOList;
    }

    /**
     * @description 获取指定就诊中的单据信息
     * @param registerNum 挂号单号
     * @throws Exception
    **/
    public List<OutpatientVisitPrescriptionVO> queryVisitPrescription(String registerNum) throws Exception {
        if (Helpers.isNullOrEmpty(registerNum)) {
          log.info("queryVisitPrescription: 参数registerNum不能为空");
          return Collections.emptyList();
        }
        OutpatientVisitPrescription queryData = hisOutpatient.queryOutpatientVisitPrescription(registerNum);
        if (queryData == null || queryData.getPrescriptionList() == null || queryData.getPrescriptionList().isEmpty()) {
          log.info("就诊单据信息为空, 挂号单号(registerNum): " + registerNum);
          return Collections.emptyList();
        }

        List<OutpatientVisitPrescriptionVO> prescriptionVOList = new ArrayList<OutpatientVisitPrescriptionVO>();
        for (OutpatientVisitPrescription.Prescription prescription : queryData.getPrescriptionList()) {
            OutpatientVisitPrescriptionVO prescriptionVO = new OutpatientVisitPrescriptionVO();
            prescriptionVO.setDate(formatDate(prescription.getRq()));
            prescriptionVO.setDocAmount(Double.parseDouble(prescription.getZfy()));
            prescriptionVO.setDocNumber(prescription.getDjh());
            prescriptionVO.setDoctor(prescription.getYs());
            prescriptionVO.setInfo(prescription.getZd());

            // 类别
            if (prescription.getItemList() != null && prescription.getItemList().size() > 0) {
                List<OutpatientVisitPrescriptionVO.PrescriptionDocment> docList = new ArrayList<OutpatientVisitPrescriptionVO.PrescriptionDocment>();
                for (OutpatientVisitPrescription.PrescriptionItem item : prescription.getItemList()) {
                    OutpatientVisitPrescriptionVO.PrescriptionDocment docment = new OutpatientVisitPrescriptionVO.PrescriptionDocment();
                    docment.setType(item.getLb());

                    // 明细和用法
                    if (item.getGroup() != null && !item.getGroup().isEmpty()) {
                        List<OutpatientVisitPrescriptionVO.PrescriptionMX> mxList = new ArrayList<OutpatientVisitPrescriptionVO.PrescriptionMX>();
                        for (OutpatientVisitPrescription.PrescriptionYF yf : item.getGroup()) {
                            if (yf.getMxList() != null && !yf.getMxList().isEmpty()) {

                                //明细
                                for (OutpatientVisitPrescription.PrescriptionMX mx : yf.getMxList()) {
                                    OutpatientVisitPrescriptionVO.PrescriptionMX mxOV = new OutpatientVisitPrescriptionVO.PrescriptionMX();
                                    mxOV.setUsage(yf.getYf());
                                    mxOV.setName(mx.getMc());
                                    mxOV.setFormat(mx.getGg());
                                    mxOV.setMedicalRecordId(mx.getBlid());
                                    mxOV.setReportSource(Integer.parseInt(mx.getBgly()));
                                    mxOV.setReportSourceNote(mx.getBglysm());
                                    mxOV.setUnit(mx.getDw());
                                    mxOV.setSingleValue(Double.parseDouble(mx.getDl()));
                                    mxOV.setUsageValue(Double.parseDouble(mx.getYl()));
                                    mxList.add(mxOV);
                                }
                            }
                        }
                        docment.setMxList(mxList);
                    }
                  docList.add(docment);
                }
                prescriptionVO.setDocList(docList);
            }
            prescriptionVOList.add(prescriptionVO);
        }
        return prescriptionVOList;
    }

    /**
     * @description 获取指定就诊的费用信息
     * @param registerNum 挂号单号
     * @throws Exception
    **/
    public List<OutpatientVisitReceiptVO> queryVisitReceipt(String registerNum) throws Exception {
        if (Helpers.isNullOrEmpty(registerNum)) {
            log.info("queryVisitReceipt: 参数registerNum不能为空");
            return Collections.emptyList();
        }
        OutpatientVisitReceipt queryData = hisOutpatient.queryOutpatientVisitReceipt(registerNum);
        if (queryData == null || queryData.getReceiptList() == null || queryData.getReceiptList().isEmpty()) {
            log.info("就诊费用信息为空, 挂号单号(registerNum): " + registerNum);
            return Collections.emptyList();
        }
        List<OutpatientVisitReceiptVO> receiptVOList = new ArrayList<OutpatientVisitReceiptVO>();
        for (OutpatientVisitReceipt.Receipt receipt : queryData.getReceiptList()) {
            OutpatientVisitReceiptVO receiptVO = new OutpatientVisitReceiptVO();
            receiptVO.setDocNumber(receipt.getDjh());
            receiptVO.setDate(formatDate(receipt.getSj()));
            receiptVO.setDocAmount(Double.parseDouble(receipt.getFy()));
            receiptVO.setDoctor(receipt.getYS());
            receiptVO.setPaymentType(receipt.getZffs());

            //费目
            if (receipt.getItemList() != null && !receipt.getItemList().isEmpty()) {
                List<OutpatientVisitReceiptVO.ReceiptItem> itemVOLIst = new ArrayList<OutpatientVisitReceiptVO.ReceiptItem>();
                for (OutpatientVisitReceipt.ReceiptItem item : receipt.getItemList()) {
                    OutpatientVisitReceiptVO.ReceiptItem itemVO = new OutpatientVisitReceiptVO.ReceiptItem();
                    itemVO.setFm(item.getFm());

                    //明细
                    if (item.getMxList() != null && !item.getMxList().isEmpty()) {
                        List<OutpatientVisitReceiptVO.ReceiptMX> mxVOList = new ArrayList<OutpatientVisitReceiptVO.ReceiptMX>();
                        for (OutpatientVisitReceipt.ReceiptMX mx : item.getMxList()) {
                            OutpatientVisitReceiptVO.ReceiptMX mxVO = new OutpatientVisitReceiptVO.ReceiptMX();
                            mxVO.setName(mx.getMc());
                            mxVO.setAmount(mx.getJe());
                            mxVO.setFormat(mx.getGg());
                            mxVO.setNumber(mx.getSl());
                            mxVO.setUnit(mx.getDw());
                            mxVO.setUnitPtrice(mx.getDj());
                            mxVOList.add(mxVO);
                        }

                        itemVO.setMxList(mxVOList);
                    }
                    itemVOLIst.add(itemVO);
                }

                receiptVO.setItemLIst(itemVOLIst);
            }
            receiptVOList.add(receiptVO);
        }
        return receiptVOList;
    }

    private Date formatDate(String str) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formater.parse(str);
    }
}

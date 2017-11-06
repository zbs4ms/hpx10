package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.controller.protocol.*;
import com.jishi.reservation.dao.mapper.OutpatientPaymentMapper;
import com.jishi.reservation.dao.models.OrderInfo;
import com.jishi.reservation.dao.models.OutpatientPayment;
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

import java.math.BigDecimal;
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

    @Autowired
    private OutpatientPaymentMapper outpatientPaymentMapper;

    private static final int OUTPATIENT_PAMENT_INFO_QUERY_DAY = 30;

      /**
       * 获取用户门诊缴费列表
       * @param accountId
       * @throws Exception
       */
    public List<OutpatientPaymentInfoVO> queryOutpatientPamentInfo(long accountId, Integer paymentStatus, Integer pageNo, Integer pageSize) throws Exception {

        List<PatientInfo> patientInfoList = patientInfoService.queryPatientInfo(null,accountId, 0);
        if (patientInfoList == null || patientInfoList.isEmpty()) {
            log.info(accountId + ": 病人列表为空");
            return Collections.emptyList();
        }
        List<OutpatientPaymentInfoVO> paymentInfoList = new ArrayList<OutpatientPaymentInfoVO>();
        for (PatientInfo info : patientInfoList) {
            // TODO 结算卡类别和站点暂时传null
            OutpatientPaymentInfo data = hisOutpatient.queryPayReceipt(info.getBrId(), "", String.valueOf(OUTPATIENT_PAMENT_INFO_QUERY_DAY), "");
            if (data == null) {
                continue;
            }
            List<OutpatientPaymentInfo.Gh> ghList = data.getGhlist();
            if (ghList == null || ghList.isEmpty()) {
              log.info(info.getBrId() + ": 门诊缴费列表为空");
              continue;
            }
            log.info(info.getBrId() + "的his门诊缴费列表返回值：" + JSONObject.toJSONString(ghList));
            for (OutpatientPaymentInfo.Gh gh : ghList) {
                if (gh == null) {
                    continue;
                }
                double unpaidAmount = 0.0;
                String unpaidDocIds = "";
                OutpatientPaymentInfoVO paymentInfo = new OutpatientPaymentInfoVO();
                paymentInfo.setBrid(info.getBrId());
                paymentInfo.setPatientName(info.getName());
                paymentInfo.setDocumentNum(gh.getDjh());
                paymentInfo.setRegisterType(gh.getHl());
                paymentInfo.setRegisterDate(formatDate(gh.getYysj()));
                paymentInfo.setDepartment(gh.getKdks());
                paymentInfo.setExeStatus(gh.getZxzt());
                //paymentInfo.setDocumentType(Integer.parseInt(gh.getDjlx()));
                paymentInfo.setPaymentStatus(Integer.parseInt(gh.getZfzt()));
                paymentInfo.setHasRegister(Integer.parseInt(gh.getSfyy()));
                paymentInfo.setPaymentAmount(Double.parseDouble(gh.getJe()));
                paymentInfo.setDoctorId(gh.getYsid());
                paymentInfo.setDoctorName(gh.getYsxm());
                paymentInfo.setHasPayCard(Integer.parseInt(gh.getSfjsk()));
                List<OutpatientPaymentInfo.Yz> yzLists = gh.getYzlists();
                if (yzLists == null || yzLists.isEmpty()) {
                    continue;
                }

                // 医嘱列表
                List<OutpatientAdviceVO> adviceList = new ArrayList<OutpatientAdviceVO>();
                for (OutpatientPaymentInfo.Yz yz : yzLists) {
                    OutpatientAdviceVO advice = new OutpatientAdviceVO();
                    advice.setAdviceId(yz.getYzid());
                    advice.setAdviceName(yz.getYzmc());
                    advice.setAdviceType(yz.getYzlx());

                    // 费目列表
                    List<OutpatientPaymentInfo.Fm> fmlists = yz.getFmlists();
                    if (fmlists != null && !fmlists.isEmpty()) {
                        List<OutpatientFeeVO> feeList = new ArrayList<OutpatientFeeVO>();
                        for (OutpatientPaymentInfo.Fm fm : fmlists) {
                            OutpatientFeeVO fee = new OutpatientFeeVO();
                            fee.setFeeName(fm.getMc());
                            fee.setFeeAmount(Double.parseDouble(fm.getJe()));
                            fee.setFeeStatus(Integer.parseInt(fm.getZfzt()));

                            /** 暂时不提供明细
                            // 费目明细列表
                            List<OutpatientPaymentInfo.Mx> mxlists = fm.getMxlists();
                            if (mxlists!= null && !mxlists.isEmpty()) {
                                List<OutpatientFeeVO.OutpatientFeeItemVO> itemList = new ArrayList<OutpatientFeeVO.OutpatientFeeItemVO>();
                                for (OutpatientPaymentInfo.Mx mx : mxlists) {
                                    OutpatientFeeVO.OutpatientFeeItemVO item = new OutpatientFeeVO.OutpatientFeeItemVO();
                                    item.setItemName(mx.getMc());
                                    item.setItemAdvance(Double.parseDouble(mx.getDj()));
                                    item.setItemFormat(mx.getGg());
                                    item.setItemNumber(Integer.parseInt(mx.getSl()));
                                    item.setItemUnit(mx.getDw());

                                    itemList.add(item);
                                }

                                fee.setFeeItemList(itemList);
                            }**/

                            feeList.add(fee);
                        }
                        advice.setFeeList(feeList);
                    }

                    // 单据列表
                    List<OutpatientPaymentInfo.Dj> djLists = yz.getDjlists();
                    if (djLists != null && !djLists.isEmpty()) {
                        List<OutpatientFeeDocVO> feeDocList = new ArrayList<OutpatientFeeDocVO>();
                        for (OutpatientPaymentInfo.Dj dj : djLists) {
                            OutpatientFeeDocVO doc = new OutpatientFeeDocVO();
                            doc.setDocumentNum(dj.getDjh());
                            doc.setDocumentAmount(Double.parseDouble(dj.getJe()));
                            doc.setDocumentDate(formatDate(dj.getKdsj()));
                            doc.setDocumentType(Integer.parseInt(dj.getDjlx()));
                            doc.setHasPayCard(Integer.parseInt(dj.getSfjsk()));
                            doc.setPayStatus(Integer.parseInt(dj.getZfzt()));
                            String ytje = dj.getYtje();
                            double returnNum = (ytje == null || ytje.isEmpty()) ? 0.0 : Double.parseDouble(ytje);
                            doc.setReturnNumber(returnNum);

                            //默认处理收费单(单据类型，1-收费单，4-挂号单)，挂号单不处理
                            if (doc.getPayStatus() == 0 && doc.getDocumentType() == 1 && !unpaidDocIds.contains(doc.getDocumentNum())) {
                                unpaidAmount += doc.getDocumentAmount();
                                unpaidDocIds += doc.getDocumentNum() + ",";
                            }

                            feeDocList.add(doc);
                        }
                        //advice.setFeeDocList(feeDocList);
                    }

                    adviceList.add(advice);
                }
                paymentInfo.setAdviceList(adviceList);
                paymentInfo.setUnpaidAmount(unpaidAmount);
                if (unpaidDocIds != null && !unpaidDocIds.isEmpty()) {
                    unpaidDocIds = unpaidDocIds.substring(0, unpaidDocIds.length() - 1);
                }
                paymentInfo.setUnpaidDocIds(unpaidDocIds);
                //不知道his返回的挂号单下面的支付状态是不是挂号单的支付状态，每次返回1，这里通过未支付单据进行处理，后期有需要再调整
                //  1 已支付， 0 待支付
                int PaymentStatus = unpaidDocIds == null || unpaidDocIds.isEmpty() ? 1 : 0;
                paymentInfo.setPaymentStatus(PaymentStatus);
                paymentInfo.setDocumentType(1);   //默认处理收费单(单据类型，1-收费单，4-挂号单)，挂号单不处理

                if (PaymentStatus == paymentStatus || paymentStatus == 1) {
                    paymentInfoList.add(paymentInfo);
                }
            }
        }
        if (paymentInfoList != null) {
            Collections.sort(paymentInfoList);
            if (paymentStatus != 0 && pageSize > 0) {
                int beginIndex = (pageNo - 1) * pageSize;
                int endIndex = beginIndex + pageSize - 1;
                endIndex = endIndex > paymentInfoList.size() ? paymentInfoList.size() : pageSize;
                if (beginIndex < paymentInfoList.size()) {
                    return paymentInfoList.subList(beginIndex, endIndex);
                } else {
                    return Collections.emptyList();
                }
            }
        }
        return paymentInfoList;
    }

    /**
     * @description
     * @param accountId
     * @param brId his系统病人id
     * @param subject 预交的名称
     * @param price 交易的金额
     * @param docIds 单据ID，可以多个单据，以','分隔
     * @param docmentType 单据类型，1-收费单，4-挂号单
     * @throws Exception
    **/
    public OrderInfo generatePaymentOrder(Long accountId, String brId, String subject, BigDecimal price, String docIds, Integer docmentType) throws Exception {
        OrderInfo orderInfo = orderInfoService.generateOutpatient(accountId, brId, subject, price);
        log.info(" generatePaymentOrder =>  订单号: " + orderInfo.getOrderNumber());
        OutpatientPayment payment = new OutpatientPayment();
        payment.setAccountId(accountId);
        payment.setBrId(brId);
        payment.setDocmentId(docIds);
        payment.setDocmentType(docmentType);
        payment.setStatus(0); //创建订单，初始化为未支付
        payment.setOrderId(orderInfo.getId());
        payment.setOrderNumber(orderInfo.getOrderNumber());
        payment.setCreateTime(new Date());

        outpatientPaymentMapper.insert(payment);
        log.info(" generatePaymentOrder => 门诊订单信息，病人id: " + payment.getBrId() + " 单据号：" + payment.getDocmentId());
        return orderInfo;
    }

    /**
     * @description 门诊缴费确认(单个)
     * @param orderNumber 订单号
     * @throws Exception
    **/
    public OrderVO payConfirm(String orderNumber) throws Exception {
        OrderInfo order = orderInfoService.queryOrderByOrderNumber(orderNumber);
        OutpatientPayment payment = outpatientPaymentMapper.queryByOrderNumber(orderNumber);
        log.info("payConfirm => OrderNumber: " + order.getOrderNumber() + " 病人Id: " + payment.getBrId() + " 单据号：" + payment.getDocmentId() + " 金额：" + order.getPrice());
        if (order == null || !order.getBrId().equals(payment.getBrId())) {
            return null;
        }
        // 订单状态0：已支付，是否有效的标志0：有效，订单类型3：门诊订单
        if (order.getStatus() != 0 || order.getEnable() != 0 || order.getType() != 3) {
            return null;
        }
        // TODO 第三方名称（结算卡类别）暂时传空
        String paymentContent = order.getBuyerId() + "|" + order.getSubject();
        //documentType: 单据类型，1-收费单，4-挂号单    isRegisterDoc: 是否挂号单，0-收费单，1-挂号单
        int isRegisterDoc = payment.getDocmentType() == 4 ? 1 : 0;
        String jzid = hisOutpatient.payModify(payment.getBrId(), payment.getDocmentId(), order.getPrice(), order.getPrice(),
                  isRegisterDoc, order.getThirdOrderNumber(), paymentContent, null);
        log.info(" payConfirm => 就诊ID: " + jzid);
        boolean rslt = jzid != null && !jzid.isEmpty();
        if (rslt) {
            payment.setStatus(3);
            outpatientPaymentMapper.updateByPrimaryKeySelective(payment);
        }
        return rslt ? toOrderVO(order) : null;
    }

    /**
     * @description 门诊缴费确认(可多个单据)
     * @param orderNumber 订单号
     * @throws Exception
     **/
    public OrderVO batchpayConfirm(String orderNumber) throws Exception {
        OrderInfo order = orderInfoService.queryOrderByOrderNumber(orderNumber);
        OutpatientPayment payment = outpatientPaymentMapper.queryByOrderNumber(orderNumber);
        log.info("batchpayConfirm => OrderNumber: " + order.getOrderNumber() + " 病人Id: " + payment.getBrId() + " 单据号: " + payment.getDocmentId() + " 金额：" + order.getPrice());
        if (order == null || !order.getBrId().equals(payment.getBrId())) {
            return null;
        }
        // 订单状态0：已支付，是否有效的标志0：有效，订单类型3：门诊订单
        if (order.getStatus() != 0 || order.getEnable() != 0 || order.getType() != 3) {
            return null;
        }
        String paymentContent = order.getBuyerId() + "|" + order.getSubject();
        //documentType: 单据类型，1-收费单，4-挂号单    isRegisterDoc: 是否挂号单，0-收费单，1-挂号单
        int isRegisterDoc = payment.getDocmentType() == 4 ? 1 : 0;
        String czsj = hisOutpatient.batchPayModify(payment.getBrId(), payment.getDocmentId(), order.getPrice(), order.getPrice(),
                  isRegisterDoc, order.getThirdOrderNumber(), paymentContent, null);
        log.info(" batchpayConfirm => 操作时间: " + czsj);
        boolean rslt = czsj != null && !czsj.isEmpty();
        if (rslt) {
            payment.setStatus(3);
            outpatientPaymentMapper.updateByPrimaryKeySelective(payment);
        }
        return rslt ? toOrderVO(order) : null;
    }

    /**
     * @description
     * @param accountId 用户账号
     * @param pageNo 当前页数
     * @param pageSize 每页记录数
     * @throws Exception
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
            if (visitRecordData == null || visitRecordData.getInfolist() == null || visitRecordData.getInfolist() == null
                      || visitRecordData.getInfolist().isEmpty()) {
                continue;
            }
            for (OutpatientVisitRecord.VisitRecord record : visitRecordData.getInfolist()) {
                OutpatientVisitRecordVO recordVO = new OutpatientVisitRecordVO();
                recordVO.setBrid(patientInfo.getBrId());
                recordVO.setRgisterNum(record.getGhdh());
                recordVO.setAmount(Double.parseDouble(record.getJe()));
                recordVO.setDate(formatDate(record.getRq()));
                recordVO.setDepartment(record.getJzks());
                recordVO.setDepartmentId(record.getJzksid());
                recordVO.setDoctor(record.getYs());
                recordVO.setZdxx(record.getZdxx());

                List<OutpatientVisitRecord.RecordMX> mxList = record.getMxlist();
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
                                    mxOV.setReportSource(parseInt(mx.getBgly()));
                                    mxOV.setReportSourceNote(mx.getBglysm());
                                    mxOV.setUnit(mx.getDw());
                                    mxOV.setSingleValue(mx.getDl());
                                    mxOV.setUsageValue(parseDouble(mx.getYl()));
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


    private int parseInt(String str) throws ParseException {
        int rslt = 0;
        if (str != null && !str.isEmpty()){
            rslt = Integer.parseInt(str);
        }
        return rslt;
    }

    private double parseDouble(String str) throws ParseException {
        double rslt = 0.0;
        if (str != null && !str.isEmpty()){
            rslt = Double.parseDouble(str);
        }
        return rslt;
    }

    private OrderVO toOrderVO(OrderInfo order) {
        if (order == null) {
            return null;
        }
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNumber(order.getOrderNumber());
        orderVO.setPayType(order.getPayType());
        orderVO.setPrice(order.getPrice());
        return orderVO;
    }
}

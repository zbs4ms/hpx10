package com.jishi.reservation.service.his;

import com.jishi.reservation.mypackage.*;
import com.jishi.reservation.service.his.bean.*;
import lombok.extern.log4j.Log4j;
import org.apache.axis.message.MessageElement;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

/**
 * 门诊业务
 * Created by wang on 2017/9/28.
 */
@Log4j
@Service
public class HisOutpatient {

    /**
     * 	获取挂号号源
     * @param brid 病人ID
     * @param rq 日期，格式 (YYYY-MM-DD HH24:MI:SS),为空表示当天
     * @param zd 站点
     * @param ksid 科室ID
     * @param ysid 医生ID
     * @param ysxm 医生姓名
     * @param hzdw 合作单位，固定传入第三方名称
     * @param sjjg 时间段间隔,单位:分钟。传入此节点时，按此间隔返回时间段；不传入此接点时，按HIS中设置的号源时间段返回
     * @return
     * @throws Exception
     */
    public RegisteredNumberInfo queryRegisteredNumber(String brid, String rq, String zd,String ksid,String ysid,String ysxm,String hzdw,String sjjg) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<ZD>").append(zd).append("</ZD>");
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<RQ>").append(rq).append("</RQ>");
        sb.append("<KSID>").append(ksid).append("</KSID>");
        sb.append("<YSID>").append(ysid).append("</YSID>");
        sb.append("<YSXM>").append(ysxm).append("</YSXM>");
        sb.append("<HZDW>").append(hzdw).append("</HZDW>");
        sb.append("<SJJG>").append(sjjg).append("</SJJG>");
        String reData = HisTool.toXMLString("Register.SignalSource.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Register.SignalSource.Query");
            return (RegisteredNumberInfo) HisTool.toBean(RegisteredNumberInfo.class, xml);
        }
        return null;
    }


    /**
     * 	获取挂号项目最后得金额
     * @param brid 病人ID
     * @param xmid 项目id
     * @return
     * @throws Exception
     */
    public LastPrice  queryLastPrice(String xmid,String brid) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<XMID>").append(xmid).append("</XMID>");
        sb.append("<BRID>").append(brid).append("</BRID>");

        String reData = HisTool.toXMLString("Register.Preferential.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Register.Preferential.Query");
            return (LastPrice)HisTool.toBean(LastPrice.class,xml);
        }
        return null;
    }





    /**
     * 获取挂号限制条件
     * @param brid 病人id
     * @param ghhm 挂号号码
     * @return
     * @throws Exception
     */
    public Boolean checkIsValid(String brid,String ghhm) throws Exception {


        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<GHHM>").append(ghhm).append("</GHHM>");

        String reData = HisTool.toXMLString("Register.LimitInfo.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            HisTool.getHisDataparam(me,"Register.LimitInfo.Query");
            return true;
        }
        return false;
    }


    /**
     * 锁定号源 返回号序  之后的confirm.modify会用到这个hm
     * @param hm 号码
     * @param yysj
     * @param hzdw
     * @param jqm
     * @return
     * @throws Exception
     */
    public LockRegister lockRegister(
            String hm,String yysj,String hzdw,String jqm
    ) throws Exception {

        StringBuffer sb = new StringBuffer();
        sb.append("<HM>").append(hm).append("</HM>");
        sb.append("<YYSJ>").append(yysj).append("</YYSJ>");
        sb.append("<CZ>").append("1").append("</CZ>");
        sb.append("<HZDW>").append(hzdw).append("</HZDW>");
        sb.append("<JQM>").append(jqm).append("</JQM>");

        String reData = HisTool.toXMLString("Register.Lock.Modify", sb.toString());


        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            log.info(me.getAsString());
            String xml = HisTool.getHisDataparam(me,"Register.Lock.Modify");
            return (LockRegister)HisTool.toBean(LockRegister.class,xml);
        }

        return null;
    }

    /**
     * 取消预约
     * @param ghdh  挂号单号
     * @param jsklb  结算卡类别，固定传入第三方名称
     * @param yyfs  预约方式，固定传入第三方名称
     * @return
     * @throws Exception
     */
    public boolean cancelSubscribeRegister(String ghdh, String jsklb, String yyfs) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<GHDH>").append(ghdh).append("</GHDH>");
        sb.append("<JSKLB>").append(jsklb).append("</JSKLB>");
        sb.append("<JCFP>").append("").append("</JCFP>");
        sb.append("<GHJE>").append("").append("</GHJE>");
        sb.append("<LSH>").append("").append("</LSH>");
        sb.append("<SFYY>").append("1").append("</SFYY>");
        sb.append("<YYFS>").append(yyfs).append("</YYFS>");
        String reData = HisTool.toXMLString("Register.SubScribeCancel.Modify", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            HisTool.getHisDataparam(me,"Register.SubScribeCancel.Modify");
            return true;
        }
        return false;
    }

    /**
     * 预约(未缴费)
     * @param confirmRegister
     * @return
     * @throws Exception
     */
    public String subscribeRegister(ConfirmRegister confirmRegister) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<SFYY>").append("1").append("</SFYY>");
        sb.append("<CZFS>").append("3").append("</CZFS>");
        sb.append("<CZJLID>").append(confirmRegister.getCzjlid()).append("</CZJLID>");
        sb.append("<HM>").append(confirmRegister.getHm()).append("</HM>");
        sb.append("<HX>").append(confirmRegister.getHx()).append("</HX>");
        sb.append("<HZDW>").append(confirmRegister.getHzdw()).append("</HZDW>");
        sb.append("<YYFS>").append(confirmRegister.getYyfs()).append("</YYFS>");
        sb.append("<FB>").append(confirmRegister.getFb()).append("</FB>");
        sb.append("<YYSJ>").append(confirmRegister.getYysj()).append("</YYSJ>");
        sb.append("<JE>").append(confirmRegister.getJe()).append("</JE>");
        sb.append("<SM>").append(confirmRegister.getSm()).append("</SM>");
        sb.append("<BRID>").append(confirmRegister.getBrid()).append("</BRID>");
        sb.append("<BRLX>").append(confirmRegister.getBrlx()).append("</BRLX>");
        sb.append("<JQM>").append(confirmRegister.getJqm()).append("</JQM>");
        sb.append("<JSLIST><JS>");
        sb.append("<JSKLB>").append(confirmRegister.getJsklb()).append("</JSKLB>");
        sb.append("<JSKH>").append(confirmRegister.getJskh()).append("</JSKH>");
        sb.append("<JSFS>").append(confirmRegister.getJsfs()).append("</JSFS>");
        sb.append("<JSJE>").append(confirmRegister.getJsje()).append("</JSJE>");
        sb.append("<JYLSH>").append(confirmRegister.getJylsh()).append("</JYLSH>");
        sb.append("<EXPENDLIST><EXPEND>");
        sb.append("<JYMC>").append(confirmRegister.getJymc()).append("</JYMC>");
        sb.append("<JYLR>").append(confirmRegister.getJylr()).append("</JYLR>");
        sb.append("</EXPEND></EXPENDLIST>");
        sb.append("</JS></JSLIST>");
        String reData = HisTool.toXMLString("Register.Subscribe.Modify", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Register.Subscribe.Modify");
            return HisTool.getXmlAttribute(xml,"GHDH");
        }
        return null;
    }



    /**
     * 	挂号
     * @param confirmRegister
     * @return
     * @throws Exception
     */
    public ConfirmOrder confirmRegister(ConfirmRegister confirmRegister) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<SFYY>").append("0").append("</SFYY>");
        sb.append("<CZFS>").append("3").append("</CZFS>");
        sb.append("<CZJLID>").append(confirmRegister.getCzjlid()).append("</CZJLID>");
        sb.append("<HM>").append(confirmRegister.getHm()).append("</HM>");
        sb.append("<HX>").append(confirmRegister.getHx()).append("</HX>");
        sb.append("<HZDW>").append(confirmRegister.getHzdw()).append("</HZDW>");
        sb.append("<YYFS>").append(confirmRegister.getYyfs()).append("</YYFS>");
        sb.append("<FB>").append(confirmRegister.getFb()).append("</FB>");
        sb.append("<YYSJ>").append(confirmRegister.getYysj()).append("</YYSJ>");
        sb.append("<JE>").append(confirmRegister.getJe()).append("</JE>");
        sb.append("<SM>").append(confirmRegister.getSm()).append("</SM>");
        sb.append("<BRID>").append(confirmRegister.getBrid()).append("</BRID>");
        sb.append("<BRLX>").append(confirmRegister.getBrlx()).append("</BRLX>");
        sb.append("<JQM>").append(confirmRegister.getJqm()).append("</JQM>");
        sb.append("<JSLIST><JS>");
        sb.append("<JSKLB>").append(confirmRegister.getJsklb()).append("</JSKLB>");
        sb.append("<JSKH>").append(confirmRegister.getJskh()).append("</JSKH>");
        sb.append("<JSFS>").append(confirmRegister.getJsfs()).append("</JSFS>");
        sb.append("<JSJE>").append(confirmRegister.getJsje()).append("</JSJE>");
        sb.append("<JYLSH>").append(confirmRegister.getJylsh()).append("</JYLSH>");
        sb.append("<EXPENDLIST><EXPEND>");
        sb.append("<JYMC>").append(confirmRegister.getJymc()).append("</JYMC>");
        sb.append("<JYLR>").append(confirmRegister.getJylr()).append("</JYLR>");
        sb.append("</EXPEND></EXPENDLIST>");
        sb.append("</JS></JSLIST>");

        log.info("請求的數據：\n"+sb.toString());
        String reData = HisTool.toXMLString("Register.Confirm.Modify", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Register.Confirm.Modify");
            if(xml == null || "".equals(xml))
                return null;
            //失败返回null
            return (ConfirmOrder) HisTool.toBean(ConfirmOrder.class,xml);
        }
        return null;
    }


    /**
     * 查询指定天数内的可挂号科室列表
     * @param hzdw 合作单位
     * @param cxts 查询天数
     * @param zd  站点
     */
    public DepartmentList selectDepartments(String hzdw,String cxts,String zd) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<HZDW>").append(hzdw).append("</HZDW>");
        sb.append("<CXTS>").append(cxts).append("</CXTS>");
        sb.append("<ZD>").append(zd).append("</ZD>");
        String reData = HisTool.toXMLString("Register.Depart.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            log.info(me.getAsString());
            String xml = HisTool.getHisDataparam(me,"Register.Depart.Query");
            return (DepartmentList)HisTool.toBean(DepartmentList.class,xml);
        }
        return null;
    }









    /**
     * 获取未缴费/已缴费的挂号单据信息。对于已缴费挂号单仅返回指定第三方缴费的挂号单
     *
     * @param brid
     * @param cxts
     * @return
     * @throws Exception
     */
    public RegisterRegReceiptInfo queryRegisterRegReceipt(String brid, String cxts) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<CXTS>").append(cxts).append("</CXTS>");
        String reData = HisTool.toXMLString("Register.RegReceipt.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Register.RegReceipt.Query");
            return (RegisterRegReceiptInfo)HisTool.toBean(RegisterRegReceiptInfo.class,xml);
        }
        return null;
    }







    /**
     * 获取缴费单据信息
     * @param brid 病人ID
     * @param jsklb 结算卡类别，固定传入第三方名称
     * @param cxts 查询天数，查询N天内的缴费记录。N由第三方后台设置
     * @param zd 站点（用于区分多院区）
     * @return
     * @throws Exception
     */
    public OutpatientPaymentInfo queryPayReceipt(String brid, String jsklb, String cxts, String zd) throws Exception {
        StringBuffer sb = new StringBuffer();
        zd = zd == null ? "" : zd;
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<CXTS>").append(cxts).append("</CXTS>");
        sb.append("<JSKLB>").append(jsklb).append("</JSKLB>");
        sb.append("<ZD>").append(zd).append("</ZD>");
        String reData = HisTool.toXMLString("Payment.PayReceipt.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Payment.PayReceipt.Query");
            return (OutpatientPaymentInfo)HisTool.toBean(OutpatientPaymentInfo.class,xml);
        }
        return null;
    }

    /**
     * @description 对一个单据进行交费
     * @param docmentId 收费单据号
     * @param brId 病人ID
     * @param price 总金额
     * @param payPrice 结算金额
     * @param isRegisterDoc 是否挂号单，0-收费单，1-挂号单
     * @param thirdOrderNumber 交易流水号
     * @param paymentContent 交易内容，传“支付帐号|姓名”
     * @param jsklb 结算卡类别，固定传入第三方名称
     * @throws Exception
     * @date 2017/10/26
     **/
    public String payModify(String brId, String docmentId, BigDecimal price, BigDecimal payPrice, int isRegisterDoc,
                              String thirdOrderNumber, String paymentContent, String jsklb) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<DJH>").append(docmentId).append("</DJH>");
        sb.append("<JE>").append(String.valueOf(price.stripTrailingZeros())).append("</JE>");
        sb.append("<SFGH>").append(isRegisterDoc).append("</SFGH>");
        sb.append("<BRID>").append(brId).append("</BRID>");
        sb.append("<JSLIST>");
        sb.append("<JS>");
        sb.append("<JSKLB>").append(jsklb).append("</JSKLB>");
        sb.append("<JSKH>").append("</JSKH>");
        sb.append("<JSFS>").append("</JSFS>");
        sb.append("<JSJE>").append(String.valueOf(payPrice.stripTrailingZeros())).append("</JSJE>");
        sb.append("<JYLSH>").append(thirdOrderNumber).append("</JYLSH>");
        sb.append("<EXPENDLIST>");
        sb.append("<EXPEND>");
        sb.append("<JYMC>").append("交易信息").append("</JYMC>");
        sb.append("<JYLR>").append(paymentContent).append("</JYLR>");
        sb.append("</EXPEND>");
        sb.append("</EXPENDLIST>");
        sb.append("</JS>");
        sb.append("</JSLIST>");
        log.info("Payment.Pay.Modify: " + sb.toString());
        String reData = HisTool.toXMLString("Payment.Pay.Modify", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Payment.Pay.Modify");
            return HisTool.getXmlAttribute(xml,"JZID");
        }
        return null;
    }

    /**
     * @description 门诊缴费多张单据一次支付
     * @param brId 病人ID
     * @param docIds 收费单据号串，逗号分隔多个单据号
     * @param price 总金额
     * @param payPrice 结算金额
     * @param isRegisterDoc 是否挂号单，0-收费单，1-挂号单
     * @param thirdOrderNumber 交易流水号
     * @param paymentContent 交易内容，传“支付帐号|姓名”
     * @param jsklb 结算卡类别，固定传入第三方名称
     * @throws Exception
     * @date 2017/10/26
     **/
    public String batchPayModify(String brId, String docIds, BigDecimal price, BigDecimal payPrice, int isRegisterDoc, String thirdOrderNumber, String paymentContent, String jsklb) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<DJH>").append(docIds).append("</DJH>");
        sb.append("<JE>").append(String.valueOf(price.stripTrailingZeros())).append("</JE>");
        sb.append("<SFGH>").append(isRegisterDoc).append("</SFGH>");
        sb.append("<BRID>").append(brId).append("</BRID>");
        sb.append("<JSLIST>");
        sb.append("<JS>");
        sb.append("<JSKLB>").append(jsklb).append("</JSKLB>");
        sb.append("<JSKH>").append("</JSKH>");
        sb.append("<JSFS>").append("</JSFS>");
        sb.append("<JSJE>").append(String.valueOf(payPrice.stripTrailingZeros())).append("</JSJE>");
        sb.append("<JYLSH>").append(thirdOrderNumber).append("</JYLSH>");
        sb.append("<EXPENDLIST>");
        sb.append("<EXPEND>");
        sb.append("<JYMC>").append("交易信息").append("</JYMC>");
        sb.append("<JYLR>").append(paymentContent).append("</JYLR>");
        sb.append("</EXPEND>");
        sb.append("</EXPENDLIST>");
        sb.append("</JS>");
        sb.append("</JSLIST>");
        log.info("Payment.BatchPay.Modify: " + sb.toString());
        String reData = HisTool.toXMLString("Payment.BatchPay.Modify", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me,"Payment.BatchPay.Modify");
            return HisTool.getXmlAttribute(xml,"CZSJ");
        }
        return null;
    }

    /**
     * @description 获取病人的门诊就诊记录
     * @param brId 病人ID
     * @param dqys 当前页数
     * @param jlts 记录条数
     * @param zd 站点（用于区分多院区）
     * @throws Exception
    **/
    public OutpatientVisitRecord queryOutpatientVisitRecord(String brId, Integer dqys, Integer jlts, String zd) throws Exception {
        StringBuffer sb = new StringBuffer();
        zd = zd == null ? "" : zd;
        sb.append("<BRID>").append(brId).append("</BRID>");
        sb.append("<DQYS>").append(dqys).append("</DQYS>");
        sb.append("<JLTS>").append(jlts).append("</JLTS>");
        sb.append("<ZD>").append(zd).append("</ZD>");
        String reData = HisTool.toXMLString("Visit.Record.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            log.info(me.getAsString());
            String xml = HisTool.getHisDataparam(me, "Visit.Record.Query");
            return (OutpatientVisitRecord)HisTool.toBean(OutpatientVisitRecord.class, xml);
        }
        return null;
    }

    /**
     * @description 获取指定就诊的单据信息
     * @param ghdh 挂号单号
     * @throws Exception
    **/
    public OutpatientVisitPrescription queryOutpatientVisitPrescription(String ghdh) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<GHDH>").append(ghdh).append("</GHDH>");
        String reData = HisTool.toXMLString("Visit.Prescription.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            log.info(me.getAsString());
            String xml = HisTool.getHisDataparam(me, "Visit.Prescription.Query");
            return (OutpatientVisitPrescription)HisTool.toBean(OutpatientVisitPrescription.class, xml);
        }
        return null;
    }

    /**
     * @description 获取指定就诊的费用信息
     * @param ghdh 挂号单号
     * @throws Exception
     **/
    public OutpatientVisitReceipt queryOutpatientVisitReceipt(String ghdh) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<GHDH>").append(ghdh).append("</GHDH>");
        String reData = HisTool.toXMLString("Visit.Receipt.Query", sb.toString());
        OutPatientResponseOutPatientResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            log.info(me.getAsString());
            String xml = HisTool.getHisDataparam(me, "Visit.Receipt.Query");
            return (OutpatientVisitReceipt)HisTool.toBean(OutpatientVisitReceipt.class, xml);
        }
        return null;
    }


    private OutPatientResponseOutPatientResult execute(String reData) throws RemoteException, ServiceException {
        ZL_InformationServiceLocator locator = new ZL_InformationServiceLocator();
        ZL_InformationServiceSoap_PortType service = locator.getZL_InformationServiceSoap();
        return service.outPatient(reData);
    }

}

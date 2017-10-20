package com.jishi.reservation.service.his;

import com.jishi.reservation.mypackage.HospitalizationResponseHospitalizationResult;
import com.jishi.reservation.mypackage.ZL_InformationServiceLocator;
import com.jishi.reservation.mypackage.ZL_InformationServiceSoap_PortType;
import com.jishi.reservation.service.his.bean.*;
import lombok.extern.log4j.Log4j;
import org.apache.axis.message.MessageElement;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

/**
 * 住院
 * Created by zbs on 2017/10/5.
 */
@Log4j
@Service
public class HisHospitalization {

    /**
     * 获取预交款余额
     *
     * @param brid 病人ID
     * @param zycs 住院次数
     * @return
     * @throws Exception
     */
    public String selectDepositBalance(String brid, String zycs) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<ZYCS>").append(zycs).append("</ZYCS>");
        String reData = HisTool.toXMLString("PrePayment.Balance.Query", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return HisTool.getXmlAttribute(xml, "FYYE");
        }
        return null;
    }

    /**
     * 获取病人预交款的缴款明细
     *
     * @param brid 病人ID
     * @return
     * @throws Exception
     */
    public DepositBalanceLog selectDepositBalanceLog(String brid) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        String reData = HisTool.toXMLString("PrePayment.Record.Query", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return (DepositBalanceLog) HisTool.toBean(DepositBalanceLog.class, xml);
        }
        return null;
    }

    /**
     * 获取总费用清单
     *
     * @param brid 病人ID
     * @param zycs 住院次数
     * @param fllx 分类类型，1-按费目分组，2-按日期分组
     * @return
     * @throws Exception
     */
    public TotalDepositBalancePayDetail selectTotalPayDetail(String brid, String zycs, String fllx) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<ZYCS>").append(zycs).append("</ZYCS>");
        sb.append("<FLLX>").append(fllx).append("</FLLX>");
        String reData = HisTool.toXMLString("Information.PayDetail.Query", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return (TotalDepositBalancePayDetail) HisTool.toBean(TotalDepositBalancePayDetail.class, xml);
        }
        return null;
    }

    /**
     * 获取日费用清单
     *
     * @param brid 病人ID
     * @param rq   日期
     * @param zycs 住院次数
     * @return
     * @throws Exception
     */
    public DepositBalanceDailyPayDetail selectDailyPayDetail(String brid, String rq, String zycs) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<RQ>").append(rq).append("</RQ>");
        sb.append("<ZYCS>").append(zycs).append("</ZYCS>");
        String reData = HisTool.toXMLString("Information.DailyPayDetail.Query", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return (DepositBalanceDailyPayDetail) HisTool.toBean(DepositBalanceDailyPayDetail.class, xml);
        }
        return null;
    }

    /**
     * 获取住院详情
     *
     * @param brid 病人ID
     * @param zycs 住院次数
     * @return
     * @throws Exception
     */
    public DepositBalanceDetail selectDetail(String brid, String zycs) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<ZYCS>").append(zycs).append("</ZYCS>");
        String reData = HisTool.toXMLString("Information.Detail.Query", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return (DepositBalanceDetail) HisTool.toBean(DepositBalanceDetail.class, xml);
        }
        return null;
    }

    /**
     * 获取历史住院详情
     *
     * @param brid 病人ID
     * @param dqys 当前页数
     * @param jlts 每页记录条数
     * @param zd 站点
     * @return
     * @throws Exception
     */
    public DepositBalanceHistoryDetail selectHistoryDetail(String brid, String dqys, String jlts, String zd) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<DQYS>").append(dqys).append("</DQYS>");
        sb.append("<JLTS>").append(jlts).append("</JLTS>");
        sb.append("<ZD>").append(zd).append("</ZD>");
        String reData = HisTool.toXMLString("Information.Record.Query", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return (DepositBalanceHistoryDetail) HisTool.toBean(DepositBalanceHistoryDetail.class, xml);
        }
        return null;
    }

    /**
     * 进行预交款缴款
     * @param brid 病人ID
     * @param zycs 住院次数
     * @param jsklb 结算卡类别，固定传入第三方名称
     * @param jsje 结算金额
     * @param jylsh 交易流水号
     * @param zfbzh 支付帐号
     * @param zfbxm 支付姓名
     * @return
     * @throws Exception
     */
    public String  pay(String brid,String zycs,String jsklb,String jsje,String jylsh,String zfbzh,String zfbxm) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<BRID>").append(brid).append("</BRID>");
        sb.append("<ZYCS>").append(zycs).append("</ZYCS>");
        sb.append("<SFMZ></SFMZ>");
        sb.append("<JSLIST><JS>");
        sb.append("<JSKLB>").append(jsklb).append("</JSKLB>");
        sb.append("<JSKH></JSKH><JSFS></JSFS>");
        sb.append("<JSJE>").append(jsje).append("</JSJE>");
        sb.append("<JYLSH>").append(jylsh).append("</JYLSH>");
        sb.append("<EXPENDLIST><EXPEND>");
        sb.append("<JYMC>").append("交易信息").append("</JYMC>");
        sb.append("<JYLR>").append(zfbzh+"|"+zfbxm).append("</JYLR>");
        sb.append("</EXPEND></EXPENDLIST>");
        sb.append("</JS></JSLIST>");
        String reData = HisTool.toXMLString("PrePayment.Pay.Modify", sb.toString());
        HospitalizationResponseHospitalizationResult result = execute(reData);
        for (MessageElement me : result.get_any()) {
            String xml = HisTool.getHisDataparam(me);
            return HisTool.getXmlAttribute(xml,"YJDH");
        }
        return null;
    }


//    public static void main(String[] args) throws Exception {
//        HisHospitalization hisHospitalization = new HisHospitalization();
//        String l1 = hisHospitalization.selectDepositBalance("264", "1");
//        System.out.println("费用余额" + l1);
//        DepositBalanceLog l = hisHospitalization.selectDepositBalanceLog("264");
//        for (DepositBalanceLog.DB3 db3 : l.getGroup().getItem()) {
//            System.out.println(db3.getJksh());
//            System.out.println(db3.getLx());
//            System.out.println(db3.getJe());
//        }
//
//        TotalDepositBalancePayDetail totalDepositBalancePayDetail = hisHospitalization.selectTotalPayDetail("264","1","1");
//        System.out.println(totalDepositBalancePayDetail.toString());
//        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = dateFormat1.parse("2015-10-25");
//        long time = date.getTime();
//        for(int i = 26 ;i<=30;i++) {
//
//                System.out.println("\n----------------    2015-10-" + i + " --------------------");
//                hisHospitalization.selectDailyPayDetail("264", "2015-10-" + i, "1");
//
//
//        }
//
//
     //  DepositBalanceHistoryDetail depositBalanceHistoryDetail = hisHospitalization.selectHistoryDetail("3987", "1","100","1");
      //  System.out.println(depositBalanceHistoryDetail);
//       hisHospitalization.pay("3987","3","支付宝","1000","111111","1234567","杨杨");
//        hisHospitalization.selectDepositBalanceLog("3987");
//    }

    private HospitalizationResponseHospitalizationResult execute(String reData) throws RemoteException, ServiceException {
        ZL_InformationServiceLocator locator = new ZL_InformationServiceLocator();
        ZL_InformationServiceSoap_PortType service = locator.getZL_InformationServiceSoap();
        return service.hospitalization(reData);
    }
}

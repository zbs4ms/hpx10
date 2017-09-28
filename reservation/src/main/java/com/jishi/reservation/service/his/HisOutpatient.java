package com.jishi.reservation.service.his;

import com.jishi.reservation.mypackage.*;
import com.jishi.reservation.service.his.bean.DepartmentList;
import com.jishi.reservation.service.his.bean.PatientsList;
import lombok.extern.log4j.Log4j;
import org.apache.axis.message.MessageElement;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

/**
 * Created by wang on 2017/9/28.
 */
@Log4j
@Service
public class HisOutpatient {

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
            String xml = HisTool.toString(me);
            return (DepartmentList)HisTool.toBean(DepartmentList.class,xml);
        }
        return null;
    }


    private OutPatientResponseOutPatientResult execute(String reData) throws RemoteException, ServiceException {
        ZL_InformationServiceLocator locator = new ZL_InformationServiceLocator();
        ZL_InformationServiceSoap_PortType service = locator.getZL_InformationServiceSoap();
        return service.outPatient(reData);
    }

    public static void main(String[] args) throws Exception {
        HisOutpatient outpatient = new HisOutpatient();
        System.out.print(outpatient.selectDepartments("","",""));
    }
}

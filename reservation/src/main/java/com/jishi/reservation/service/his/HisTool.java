package com.jishi.reservation.service.his;

import com.jishi.reservation.service.his.bean.PatientsList;
import com.jishi.reservation.util.Codec;
import com.jishi.reservation.util.Common;
import com.thoughtworks.xstream.XStream;
import lombok.extern.log4j.Log4j;
import org.apache.axis.message.MessageElement;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by wang on 2017/9/28.
 */
@Log4j
public class HisTool {

    public static Object toBean(Class type,String xml){
        XStream xstream = new XStream();
        xstream.processAnnotations(type);//显示声明使用注解
        xstream.autodetectAnnotations(true);
        return xstream.fromXML(xml);
    }

    public static String toString(MessageElement messageElement) throws Exception {
        Document document = DocumentHelper.parseText(messageElement.getAsString());
        Element root = document.getRootElement();
        String state = root.elementTextTrim("STATE");
        String req = null;
        if ("T".equals(state)) {
            req = Codec.Decrypt(root.elementTextTrim("DATAPARAM"), Common.HIS_KEYS);
        }
        log.info(req);
        return req;
    }

    public static String toXMLString(String serviceName, String data) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<ROOT>");
        sb.append("<TOKEN>");
        sb.append("<![CDATA[");
        sb.append(Codec.Encrypt(Common.HIS_TOKEN, Common.HIS_KEYS));
        sb.append("]]></TOKEN>");
        sb.append("<SERVICE>");
        sb.append("<![CDATA[");
        sb.append(serviceName);
        sb.append("]]></SERVICE>");
        sb.append("<INSIDEKEY>");
        sb.append("<![CDATA[");
        sb.append("]]></INSIDEKEY>");
        sb.append("<DATAPARAM>");
        sb.append("<![CDATA[");
        sb.append(Codec.Encrypt(data, Common.HIS_KEYS));
        sb.append("]]></DATAPARAM>");
        sb.append("</ROOT>");
        log.info(sb.toString());
        return sb.toString();
    }

}

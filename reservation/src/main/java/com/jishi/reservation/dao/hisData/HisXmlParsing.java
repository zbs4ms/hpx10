package com.jishi.reservation.dao.hisData;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Created by zbs on 2017/9/18.
 */
public class HisXmlParsing {

    public void parsing(String xmlStr) throws DocumentException {
        Document document = DocumentHelper.parseText(xmlStr);
        //获取根节点
        Element root = document.getRootElement();
        Element student1Node = root.element("student1");
    }
}

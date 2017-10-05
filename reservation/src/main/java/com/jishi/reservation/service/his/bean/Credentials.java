package com.jishi.reservation.service.his.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * Created by wang on 2017/9/28.
 */
@Data
public class Credentials {
    //类别
    @XStreamAlias("LB")
    String idType;
    //卡号
    @XStreamAlias("KH")
    String idNumber;
    //his唯一ID
    @XStreamAlias("BRID")
    String BRID;
    //病人门诊号
    @XStreamAlias("MZH")
    String MZH;
}

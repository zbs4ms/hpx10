package com.jishi.reservation.service.his.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * Created by wang on 2017/9/28.
 */
@XStreamAlias("LIST")
@Data
public class PatientsList {

    @XStreamImplicit(itemFieldName="JZK")
    List<Credentials> jzkList;
}

package com.jishi.reservation.service.his.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * Created by wang on 2017/9/28.
 */

@XStreamAlias("KSLIST")
@Data
public class DepartmentList {

    @XStreamImplicit(itemFieldName="KS")
    List<Department> list ;
}

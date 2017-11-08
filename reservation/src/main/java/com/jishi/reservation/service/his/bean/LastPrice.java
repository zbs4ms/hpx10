package com.jishi.reservation.service.his.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * Created by sloan on 2017/11/6.
 */

@XStreamAlias("ROOT")
@Data
public class LastPrice {


    @XStreamAlias("je")
    String je;

    @XStreamAlias("YHJE")
    String yhje;
}

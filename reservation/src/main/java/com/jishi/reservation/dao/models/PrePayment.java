package com.jishi.reservation.dao.models;

import lombok.Data;

import javax.persistence.Id;

/**
 * Created by sloan on 2017/10/29.
 */


@Data
public class PrePayment {

    @Id
    private Long id;

    private Long accountId;
    private Long orderId;
    private String yjdh;  //预交单号
    private String brId;
    private Integer rycs;
}

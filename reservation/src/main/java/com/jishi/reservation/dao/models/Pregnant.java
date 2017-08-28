package com.jishi.reservation.dao.models;

/**
 * Created by sloan on 2017/8/27.
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 孕妇信息 (扑来个男的)
 */

@Table(name = "pregnant")
@ApiModel("孕妇信息")
@Data
public class Pregnant {

    @Id
    private Long id;  //主键id
    private Long accountId;
    private Long patientId;   //关联的patientInfo表的id
    private String name;
    private Date birth;
    private String livingAddress;
    private Date lastMenses;
    private String telephone;
    private String husbandName;
    private String husbandTelephone;
    private String remark;
    private Integer enable;
    private Date createTime;


}

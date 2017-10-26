package com.jishi.reservation.dao.models;

import com.jishi.reservation.controller.protocol.DiaryContentVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by sloan on 2017/10/17.
 */

@Data
@ApiModel("日记表")
@Table(name = "diary")
public class Diary {

    @Id
    private Long id;
    private Long accountId;
    private String nick;
    private String url;
    private String title;
    private Date createTime;
    private Integer status;
    private Integer enable;
    private Integer sort;
    private String content;
    private String bigImgUrl;  //封面url
    private Integer height;     //高度
    private Integer width;      //宽度


    @Transient
    private Boolean isTop;

    @Transient
    private List<DiaryContentVO> contentVOList;

}

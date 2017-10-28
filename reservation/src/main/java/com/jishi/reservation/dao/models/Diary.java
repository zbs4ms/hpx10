package com.jishi.reservation.dao.models;

import com.jishi.reservation.controller.protocol.DiaryContentVO;
import com.jishi.reservation.controller.protocol.ImageVO;
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
    private Integer lock;  //是否锁定 锁定0 只有自己能看  ，没有锁定1 公布



    @Transient
    private Boolean isTop;

    @Transient
    private List<DiaryContentVO> contentVOList;

    @Transient
    private String avatar;   //用户头像

    @Transient
    private Integer scanNum;   //浏览次数
    @Transient
    private Integer likedNum;      //点赞次数

    @Transient
    private List<ImageVO> imgList;   //首页列表展示的图片list

}

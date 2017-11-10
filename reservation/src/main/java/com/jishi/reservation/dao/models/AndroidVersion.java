package com.jishi.reservation.dao.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by sloan on 2017/11/9.
 */

@Data
@ApiModel("安卓版本更新对象")
@Table(name = "android_version")
public class AndroidVersion {

    @Id
    @ApiModelProperty("主键id")
    private Long id;
    @ApiModelProperty("版本号")
    private String version;
    @ApiModelProperty("版本名称")
    private String versionName;
    @ApiModelProperty("更新内容")
    private String updateContent;
    @ApiModelProperty("apk下载地址")
    private String apkUrl;
    @ApiModelProperty("更新时间")
    private Date updateTime;
}

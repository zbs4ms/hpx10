package com.jishi.reservation.controller.base;

import com.alibaba.fastjson.JSONObject;
import com.jishi.reservation.service.enumPackage.ReturnCodeEnum;
import com.us.base.common.controller.BaseController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by sloan on 2017/10/25.
 */
public class MyBaseController extends BaseController {

    @Override
    @ExceptionHandler
    @ResponseBody
    public JSONObject exceptionHandle(Exception e) {
        return e == null?this.ResponseWrapper().addMessage("系统错误!").ExeFaild(ReturnCodeEnum.ERR.getCode()):(e.getMessage() != null && e.getMessage().length() <= 30?this.ResponseWrapper().addError(e).addMessage(e.getMessage()).ExeFaild(ReturnCodeEnum.ERR.getCode()):this.ResponseWrapper().addError(e).addMessage("系统错误!").ExeFaild(ReturnCodeEnum.ERR.getCode()));
    }


}

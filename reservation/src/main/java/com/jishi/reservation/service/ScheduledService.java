package com.jishi.reservation.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.RegisterMapper;
import com.jishi.reservation.dao.mapper.ScheduledMapper;
import com.jishi.reservation.dao.models.Register;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.enumPackage.StatusEnum;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class ScheduledService {

    @Autowired
    private ScheduledMapper scheduledMapper;



}

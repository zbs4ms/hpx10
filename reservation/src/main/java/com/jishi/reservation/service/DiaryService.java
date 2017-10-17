package com.jishi.reservation.service;

import com.jishi.reservation.dao.mapper.DiaryMapper;
import com.jishi.reservation.dao.mapper.OrderMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class DiaryService {

    @Autowired
    DiaryMapper diaryMapper;
}

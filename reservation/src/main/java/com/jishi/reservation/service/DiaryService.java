package com.jishi.reservation.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.DiaryMapper;
import com.jishi.reservation.dao.models.Diary;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class DiaryService {

    @Autowired
    DiaryMapper diaryMapper;

    public PageInfo<Diary> queryDiaryPageInfo(String name,Integer status, Long startTime,Long endTime,Paging paging) {
        if(paging != null)
            PageHelper.startPage(paging.getPageNum(),paging.getPageSize(),paging.getOrderBy());
        return new PageInfo(queryDiary(name,status, startTime, endTime));
    }

    private List<Diary> queryDiary(String name,Integer status,Long startTime,Long endTime) {

        List<Diary> diaryList = diaryMapper.queryDiary(name, status, startTime, endTime);
        Diary topDiary = diaryMapper.queryTopDiary();
        for (Diary diary : diaryList) {
                if(Objects.equals(diary.getId(), topDiary.getId())){
                    diary.setIsTop(true);
                }else {
                    diary.setIsTop(false);
                }
        }

        return diaryList;
    }

    public void verify(Long id, Integer status) {
        Diary diary = diaryMapper.queryById(id);
        Preconditions.checkNotNull(diary,"该id没有对应的日记信息");
        diary.setStatus(status);
        diaryMapper.updateByPrimaryKeySelective(diary);
    }

    public void show(Long id) {
        Diary diary = diaryMapper.queryById(id);
        Preconditions.checkNotNull(diary,"该id没有对应的日记信息");
        diary.setEnable(diary.getEnable() == 1?EnableEnum.EFFECTIVE.getCode():EnableEnum.INVALID.getCode());
        diaryMapper.updateByPrimaryKeySelective(diary);
    }

    public void top(Long id) {
        Diary diary = diaryMapper.queryById(id);
        Preconditions.checkNotNull(diary,"该id没有对应的日记信息");
        Integer maxSort =  diaryMapper.queryMaxSort();
        diary.setSort(maxSort+1);
        diaryMapper.updateByPrimaryKeySelective(diary);

    }
}

package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.controller.protocol.DiaryContentVO;
import com.jishi.reservation.controller.protocol.ImageVO;
import com.jishi.reservation.dao.mapper.AccountMapper;
import com.jishi.reservation.dao.mapper.DiaryLikedMapper;
import com.jishi.reservation.dao.mapper.DiaryMapper;
import com.jishi.reservation.dao.mapper.DiaryScanMapper;
import com.jishi.reservation.dao.models.Diary;
import com.jishi.reservation.dao.models.DiaryLiked;
import com.jishi.reservation.dao.models.DiaryScan;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class DiaryService {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    DiaryMapper diaryMapper;

    @Autowired
    DiaryScanMapper diaryScanMapper;

    @Autowired
    DiaryLikedMapper diaryLikedMapper;

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


    public void update(Long id, String title, String content,Integer lock) {


        Diary diary = diaryMapper.queryById(id);
        diary.setTitle(title);
        Gson gson = new Gson();


        List<DiaryContentVO> contentList = gson.fromJson(content,
                new TypeToken<List<DiaryContentVO>>() {
                }.getType());

        diary.setContent(JSONObject.toJSONString(contentList));
        diary.setIsLock(lock);
        diaryMapper.updateByPrimaryKeySelective(diary);
    }


    public void publish(Long accountId,String title, String content,Integer lock) {

        Gson gson = new Gson();


        List<DiaryContentVO> contentList = gson.fromJson(content,
                new TypeToken<List<DiaryContentVO>>() {
                }.getType());

        Diary diary = new Diary();

        diary.setTitle(title);

        diary.setContent(JSONObject.toJSONString(contentList));
        diary.setEnable(0);
        diary.setAccountId(accountId);
        diary.setNick(accountMapper.queryById(accountId).getNick());
        diary.setUrl("");
        diary.setStatus(1);
        diary.setSort(0);
        diary.setIsLock(lock);
        diary.setCreateTime(new Date());
        diaryMapper.insertReturnId(diary);

    }

    public Diary queryById(Long id) {

        Diary diary = diaryMapper.queryById(id);

        String content = diary.getContent();

        Gson gson = new Gson();


        List<DiaryContentVO> contentList = gson.fromJson(content,
                new TypeToken<List<DiaryContentVO>>() {
                }.getType());
        diary.setContentVOList(contentList);
        diary.setContent(null);
        diary.setScanNum(diaryScanMapper.queryCountByDiaryId(diary.getId()));
        diary.setLikedNum(diaryLikedMapper.queryCountByDiaryId(diary.getId()));
        diary.setAvatar(accountMapper.queryById(diary.getAccountId()).getHeadPortrait());

        return diary;
    }

    public PageInfo<Diary> queryPage(Long accountId,Integer startPage, Integer pageSize) {

        Gson gson = new Gson();
        PageHelper.startPage(startPage,pageSize).setOrderBy("create_time desc");
        List<Diary> list =  diaryMapper.queryEnableAndVerified(accountId);
        for (Diary diary : list) {
            diary.setScanNum(diaryScanMapper.queryCountByDiaryId(diary.getId()));
            diary.setLikedNum(diaryLikedMapper.queryCountByDiaryId(diary.getId()));
            diary.setAvatar(accountMapper.queryById(diary.getAccountId()).getHeadPortrait());
            List<ImageVO> paramList = new ArrayList<>();
            List<DiaryContentVO> contentList = gson.fromJson(diary.getContent(),
                    new TypeToken<List<DiaryContentVO>>() {
                    }.getType());
            int i = 0;
            for (DiaryContentVO diaryContentVO : contentList) {
                if(i == 4)
                    break;
                if(diaryContentVO.getType() == 0){
                    ImageVO vo = new ImageVO();
                    vo.setUrl(diaryContentVO.getUrl());
                    vo.setHeight(diaryContentVO.getHeight());
                    vo.setWidth(diaryContentVO.getWidth());

                    paramList.add(vo);
                }
                i++;
            }
            diary.setImgList(paramList);
            diary.setContent(null);

        }

        PageInfo<Diary> pageInfo = new PageInfo<>(list);
        return  pageInfo;
    }

    public Integer likeDiary(Long diaryId, Long accountId) {

        DiaryLiked param = new DiaryLiked();
        param.setDiaryId(diaryId);
        param.setAccountId(accountId);

        DiaryLiked liked = diaryLikedMapper.selectOne(param);
        if(liked == null){
            Preconditions.checkState(diaryLikedMapper.insert(param) == 1,"评论点赞失败");
            return 1;
        }else {
            Preconditions.checkState(diaryLikedMapper.delete(param) == 1,"取消点赞失败");
            return 0;
        }


    }

    public void addScanNum(Long diaryId, String ip, Long accountId) {

        DiaryScan scan = new DiaryScan();
        if(accountId != -1L) {
            scan.setAccountId(String.valueOf(accountId));
        }else{
            scan.setAccountId(ip);
        }
        scan.setDiaryId(diaryId);
        if(diaryScanMapper.selectOne(scan) == null) {
            diaryScanMapper.insert(scan);
        }
    }

    public Integer delete(Long diaryId, Long accountId) {

        Diary diary = diaryMapper.queryById(diaryId);
        Preconditions.checkNotNull(diary,"该id没有对应的日记信息");
        if(!diary.getAccountId().equals(accountId))
            return 1;

        diary.setEnable(EnableEnum.DELETE.getCode());
        diaryMapper.updateByPrimaryKeySelective(diary);
        return 0;
    }

    public Integer lock(Long diaryId, Long accountId) {
        Diary diary = diaryMapper.queryById(diaryId);
        Preconditions.checkNotNull(diary,"该id没有对应的日记信息");
        if(!diary.getAccountId().equals(accountId))
            return 1;
        diary.setIsLock(diary.getIsLock()==1?0:1);
        diaryMapper.updateByPrimaryKeySelective(diary);
        return 0;

    }


//    public static void main(String[] args) {
//
//        List<DiaryContentVO> list = new ArrayList<>();
//        DiaryContentVO vo1 = new DiaryContentVO();
//        vo1.setFontName("宋体");
//        vo1.setFontSize(10);
//        vo1.setLineSpace(10);
//        vo1.setType(1);
//        vo1.setTextColor("red");
//        vo1.setText("我是文字");
//
//        DiaryContentVO vo2 = new DiaryContentVO();
//        vo2.setUrl("http://jishikeji-hospital.oss-cn-shenzhen.aliyuncs.com/image/doctor/WechatIMG198.jpg");
//        vo2.setHeight(200);
//        vo2.setWidth(200);
//        vo2.setType(0);
//
//        list.add(vo1);
//        list.add(vo2);
//
//        System.out.println(JSONObject.toJSON(list));
//
//    }
}

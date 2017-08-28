package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.DoctorMapper;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.util.Helpers;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbs on 2017/8/10.
 */
@Service
@Log4j
public class DoctorService {

    @Autowired
    DoctorMapper doctorMapper;

    /**
     * 增加科室
     * @param doctorName
     * @param type
     * @param headPortrait
     * @param departmentIds
     * @param about
     * @param title
     * @param school
     * @param goodDescribe
     */
    public void addDoctor(String doctorName, String type,String headPortrait, String departmentIds,String about, String title,String school,String goodDescribe){
        log.info("增加科室 doctorName:"+doctorName+" type:"+type+" headPortrait:"+headPortrait+" about:"+about+" title:"+title+" school:"+school+" goodDescribe:"+goodDescribe);
        Doctor newDoctor = new Doctor();
        List<String> list = new ArrayList<>();
        if(departmentIds!=null && departmentIds.length() != 0){
            String[] split = departmentIds.split(",");
            for (String s : split) {
                list.add(s);
            }
            log.info("转化后的部门id:"+JSONObject.toJSONString(list));
        }
        newDoctor.setName(doctorName);
        newDoctor.setType(type);
        newDoctor.setHeadPortrait(headPortrait);
        newDoctor.setDepartmentIds(JSONObject.toJSONString(list));
        newDoctor.setAbout(about);
        newDoctor.setTitle(title);
        newDoctor.setSchool(school);
        newDoctor.setGoodDescribe(goodDescribe);
        newDoctor.setEnable(EnableEnum.EFFECTIVE.getCode());
        doctorMapper.insert(newDoctor);
    }

    /**
     * 查询科室
     * @param doctorId
     * @param doctorName
     * @param type
     */
    public List<Doctor> queryDoctor(Long doctorId, String doctorName, String departmentId,String type,Integer enable) throws Exception {
        log.info("查询科室 doctorId:"+doctorId+" doctorName:"+doctorName+" type:"+type +" enable:"+enable);
        Doctor queryDoctor = new Doctor();
        queryDoctor.setType(type);
        if(departmentId!=null && !"".equals(departmentId)){
            queryDoctor.setDepartmentIds("\""+departmentId+"\"");
        }
        queryDoctor.setName(doctorName);
        queryDoctor.setId(doctorId);
        queryDoctor.setEnable(enable);
        log.info("查询条件："+JSONObject.toJSONString(queryDoctor));
        List<Doctor> list = doctorMapper.queryByAttr(queryDoctor);
        log.info("查询结果："+JSONObject.toJSONString(list));

        //todo  queryByAttrc查询无结果...
        return list;
    }

    /**
     * 查询全部科室
     */
    public PageInfo<Doctor> queryDoctorPageInfo(Long doctorId, String doctorName,String departmentId, String type,Integer enable,Paging paging) throws Exception {
        log.info("查询全部科室.");
        if(!Helpers.isNullOrEmpty(paging))
            PageHelper.startPage(paging.getPageNum(),paging.getPageSize(),paging.getOrderBy());
        return new PageInfo(queryDoctor(doctorId,doctorName,departmentId,type,enable));
    }

    /**
     * 修改科室信息
     * @param doctorId
     * @param doctorName
     * @param type
     * @param headPortrait
     * @param about
     * @param title
     * @param school
     * @param goodDescribe
     * @param enable
     */
    public void modifyDoctor(Long doctorId,String doctorName, String type,String headPortrait, String about, String title,String school,String goodDescribe,Integer enable) throws Exception {
        log.info("修改科室信息 doctorId:"+doctorId+" doctorName:"+doctorName+" type:"+type+" headPortrait:"+headPortrait+" about:"+about+" title:"+title+" school:"+school+" goodDescribe:"+goodDescribe);
        if(Helpers.isNullOrEmpty(doctorId))
            throw new Exception("医生ID不能为空.");
        if(this.queryDoctor(doctorId,null,null,null, EnableEnum.EFFECTIVE.getCode()).size()==0)
            throw new Exception("没有查询到医生");
        Doctor modifyDoctor = new Doctor();
        modifyDoctor.setId(doctorId);
        modifyDoctor.setName(doctorName);
        modifyDoctor.setType(type);
        modifyDoctor.setHeadPortrait(headPortrait);
        modifyDoctor.setAbout(about);
        modifyDoctor.setTitle(title);
        modifyDoctor.setSchool(school);
        modifyDoctor.setGoodDescribe(goodDescribe);
        modifyDoctor.setEnable(enable);
        Preconditions.checkState(doctorMapper.updateByPrimaryKeySelective(modifyDoctor) == 1,"更新失败!");
    }

    public List<Doctor> queryDoctorByDepartment(Long departmentId, Integer enable) {

        Doctor param = new Doctor();
        param.setEnable(enable);
        List<Doctor> allDoctorList = doctorMapper.select(param);
        List<Doctor> list = new ArrayList<>();
        for (Doctor doctor : allDoctorList) {
            String[] split =  doctor.getDepartmentIds().split(",");
            for (String existDepartment : split) {
                if(Long.valueOf(existDepartment).equals(departmentId)){
                    list.add(doctor);
                }
            }
        }

        return  list;
    }
}

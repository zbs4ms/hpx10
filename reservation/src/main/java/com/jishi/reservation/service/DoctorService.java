package com.jishi.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.jishi.reservation.controller.base.Paging;
import com.jishi.reservation.dao.mapper.DoctorMapper;
import com.jishi.reservation.dao.models.Banner;
import com.jishi.reservation.dao.models.Doctor;
import com.jishi.reservation.service.enumPackage.EnableEnum;
import com.jishi.reservation.service.his.bean.RegisteredNumberInfo;
import com.jishi.reservation.util.Constant;
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
    public List<Doctor> queryDoctor(Long doctorId, String hDoctorId,String doctorName, String departmentId,String type,Integer enable) throws Exception {
        log.info("查询医生 doctorId:"+doctorId+" doctorName:"+doctorName+" type:"+type +" enable:"+enable);
        Doctor queryDoctor = new Doctor();
        queryDoctor.setType(type);
        if(departmentId!=null &&!"null".equals(departmentId)&& !"".equals(departmentId)){
            queryDoctor.setDepartmentIds("\""+departmentId+"\"");
        }
        queryDoctor.setName(doctorName);
        queryDoctor.setId(doctorId);
        queryDoctor.setEnable(enable);
        queryDoctor.setHId(hDoctorId);
        List<Doctor> list = doctorMapper.select(queryDoctor);
        log.info("查询结果："+JSONObject.toJSONString(list));

        return list;
    }

    /**
     * 查询全部科室
     */
    public PageInfo<Doctor> queryDoctorPageInfo(Long doctorId, String doctorName,String departmentId, String type,Integer enable,Paging paging) throws Exception {
        log.info("查询全部科室.");
        if(!Helpers.isNullOrEmpty(paging))
            PageHelper.startPage(paging.getPageNum(),paging.getPageSize(),paging.getOrderBy());
        return new PageInfo(queryDoctor(doctorId,null,doctorName,departmentId,type,enable));
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

        Doctor oldDoctor = doctorMapper.queryById(doctorId);
        Doctor modifyDoctor = new Doctor();
        modifyDoctor.setId(doctorId);
        modifyDoctor.setName(doctorName!=null?doctorName:oldDoctor.getName());
        modifyDoctor.setType(type!=null?type:oldDoctor.getType());
        modifyDoctor.setHeadPortrait(headPortrait!=null?headPortrait:oldDoctor.getHeadPortrait());
        modifyDoctor.setAbout(about!=null?about:oldDoctor.getAbout());
        modifyDoctor.setTitle(title!=null?title:oldDoctor.getTitle());
        modifyDoctor.setSchool(school!=null?school:oldDoctor.getSchool());
        modifyDoctor.setGoodDescribe(goodDescribe!=null?goodDescribe:oldDoctor.getGoodDescribe());
        modifyDoctor.setEnable(enable!=null?enable:oldDoctor.getEnable());
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

    public void topDoctor(Long doctorId) throws Exception {
        if(Helpers.isNullOrEmpty(doctorId))
            throw new Exception("医生ID不能为空.");
        List<Doctor> doctorList = this.queryDoctor(doctorId, null,null, null, null, EnableEnum.EFFECTIVE.getCode());
        if(doctorList.size()==0)
            throw new Exception("没有查询到医生");


        Integer maxOrderNumber = doctorMapper.queryMaxOrderNumber();
        doctorList.get(0).setOrderNumber(doctorList.get(0).getOrderNumber().equals(0)?maxOrderNumber+1:0);

        doctorMapper.updateByPrimaryKeySelective(doctorList.get(0));

    }

    public void getDoctorFromHis(List<RegisteredNumberInfo.Hb> hbList) {

        List<Doctor> list = new ArrayList<>();
        for (RegisteredNumberInfo.Hb hb : hbList) {
            Doctor doctor = new Doctor();
            doctor.setName(hb.getYs());
            doctor.setHId(hb.getYsid());
            doctor.setDepartmentId(hb.getKsid());
            doctor.setKsmc(hb.getKsmc());
            doctor.setHeadPortrait(Constant.DEFAULT_AVATAR);
            doctor.setOrderNumber(0);
            doctor.setEnable(0);
            doctor.setType("0");
            if(!isExist(hb.getYsid())){
                list.add(doctor);

            }

        }
        doctorMapper.insertList(list);
    }

    private boolean isExist(String hId) {
        return doctorMapper.queryByHid(hId) !=null;
    }
}

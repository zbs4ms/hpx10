package com.jishi.reservation.service;

import com.google.common.base.Preconditions;
import com.jishi.reservation.dao.mapper.DepartmentMapper;
import com.jishi.reservation.dao.models.Department;
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
public class DepartmentService {

    @Autowired
    DepartmentMapper departmentMapper;

    /**
     * 增加科室
     * @param departmentName
     * @throws Exception
     */
    public void addDepartment(String departmentName) throws Exception {
        log.info("增加科室 departmentName:"+departmentName);
        if(queryDepartment(null,departmentName).size()>0)
            throw new Exception("科室名称重复!");
        Department department = new Department();
        department.setName(departmentName);
        department.setEnable(EnableEnum.EFFECTIVE.getCode());
        departmentMapper.insert(department);
    }

    /**
     * 查询科室
     * @param departmentId
     * @param departmentName
     * @return
     */
    public List<Department> queryDepartment(Long departmentId, String departmentName){
        log.info("查询科室 departmentId:"+departmentId+" departmentName:"+departmentName);
        Department queryDepartment = new Department();
        queryDepartment.setId(departmentId);
        queryDepartment.setName(departmentName);
        return departmentMapper.select(queryDepartment);
    }

    /**
     * 通过ID号批量查询科室
     * @param departmentIds
     * @return
     * @throws Exception
     */
    public List<Department> batchQueryDepartment(String ... departmentIds) throws Exception {
        List<Department> departmentIdList = new ArrayList<>();
        for(String id : departmentIds)
            departmentIdList.addAll(queryDepartment(Long.valueOf(id),null));
        return departmentIdList;
    }

    /**
     * 查询全部科室
     * @return
     */
    public List<Department>  queryAllDepartment(){
        log.info("查询全部科室");
        return departmentMapper.selectAll();
    }

    /**
     * 修改科室
     * @param departmentId
     * @param departmentName
     * @param enable
     * @throws Exception
     */
    public void modifyDepartment(Long departmentId,String departmentName,Integer enable) throws Exception {
        log.info("修改科室 departmentId:"+departmentId+" departmentName:"+departmentName+" enable:"+enable);
        if(Helpers.isNullOrEmpty(departmentId))
            throw new Exception("科室ID不能为空!");
        if(queryDepartment(departmentId,null).size()==0)
            throw new Exception("科室不存在!");
        Department newDepartment = new Department();
        newDepartment.setName(departmentName);
        newDepartment.setId(departmentId);
        newDepartment.setEnable(enable);
        Preconditions.checkState(departmentMapper.updateByPrimaryKeySelective(newDepartment) == 1,"更新失败");
    }

    /**
     * 把科室置为无效
     * @param departmentId
     * @throws Exception
     */
    public void failureDepartment(Long departmentId) throws Exception {
        log.info("把科室置为无效 departmentId:"+departmentId);
        if(queryDepartment(departmentId,null).size()==0)
            throw new Exception("科室不存在!");
        modifyDepartment(departmentId,null, EnableEnum.INVALID.getCode());
    }
}

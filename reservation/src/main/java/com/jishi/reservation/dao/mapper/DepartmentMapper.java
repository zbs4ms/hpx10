package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Department;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Repository
public interface DepartmentMapper extends MyMapper<Department> {



    @Select({
            "select * from department where id = #{id}"
    })
    Department queryById(@Param("id") Long id);
}

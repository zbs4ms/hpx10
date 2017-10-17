package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Diary;
import com.jishi.reservation.dao.models.Permission;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionMapper extends MyMapper<Permission>{



}
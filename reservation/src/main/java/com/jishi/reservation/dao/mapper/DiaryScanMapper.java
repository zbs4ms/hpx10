package com.jishi.reservation.dao.mapper;

import com.jishi.reservation.dao.models.Diary;
import com.jishi.reservation.dao.models.DiaryScan;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryScanMapper extends MyMapper<DiaryScan>{



    @Select({
            "select count(*) from diary_scan where diary_id = #{diaryId}"
    })
    Integer queryCountByDiaryId(@Param("diaryId") Long id);
}

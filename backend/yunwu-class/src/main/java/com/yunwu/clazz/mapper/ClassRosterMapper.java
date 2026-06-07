package com.yunwu.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.clazz.entity.ClassRoster;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClassRosterMapper extends BaseMapper<ClassRoster> {
    @Select("SELECT * FROM class_rosters WHERE class_id = #{classId} AND left_at IS NULL")
    List<ClassRoster> selectActiveByClass(@Param("classId") Long classId);
    @Select("SELECT COUNT(*) FROM class_rosters WHERE class_id = #{classId} AND left_at IS NULL")
    int countActive(@Param("classId") Long classId);
}

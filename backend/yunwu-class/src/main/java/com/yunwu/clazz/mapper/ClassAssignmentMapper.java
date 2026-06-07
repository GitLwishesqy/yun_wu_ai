package com.yunwu.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.clazz.entity.ClassAssignment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClassAssignmentMapper extends BaseMapper<ClassAssignment> {
    @Select("SELECT * FROM class_assignments WHERE class_id = #{classId} AND deleted_at IS NULL ORDER BY created_at DESC")
    List<ClassAssignment> selectByClass(@Param("classId") Long classId);
}

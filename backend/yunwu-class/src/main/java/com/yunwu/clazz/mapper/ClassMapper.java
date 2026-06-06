package com.yunwu.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.clazz.entity.ClassEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<ClassEntity> {
    @Select("SELECT * FROM classes WHERE teacher_id = #{teacherId} AND deleted_at IS NULL ORDER BY created_at DESC")
    List<ClassEntity> selectByTeacher(@Param("teacherId") Long teacherId);
}

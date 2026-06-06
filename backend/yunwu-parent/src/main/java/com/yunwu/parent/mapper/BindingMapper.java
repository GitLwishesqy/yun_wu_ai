package com.yunwu.parent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.parent.entity.ParentStudentBinding;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BindingMapper extends BaseMapper<ParentStudentBinding> {
    @Select("SELECT * FROM parent_student_bindings WHERE parent_id = #{parentId} AND binding_status = 'ACTIVE'")
    List<ParentStudentBinding> selectActiveByParent(@Param("parentId") Long parentId);

    @Select("SELECT * FROM parent_student_bindings WHERE student_id = #{studentId} AND binding_status = 'PENDING'")
    List<ParentStudentBinding> selectPendingByStudent(@Param("studentId") Long studentId);

    @Select("SELECT * FROM parent_student_bindings WHERE parent_id = #{parentId} AND student_id = #{studentId}")
    ParentStudentBinding findByPair(@Param("parentId") Long parentId, @Param("studentId") Long studentId);
}

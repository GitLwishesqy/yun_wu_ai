package com.yunwu.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.clazz.entity.AssignmentSubmission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AssignmentSubmissionMapper extends BaseMapper<AssignmentSubmission> {
    @Select("SELECT * FROM assignment_submissions WHERE assignment_id = #{assignmentId}")
    List<AssignmentSubmission> selectByAssignment(@Param("assignmentId") Long assignmentId);
}

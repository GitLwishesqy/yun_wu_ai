package com.yunwu.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.coach.entity.CoachMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CoachMessageMapper extends BaseMapper<CoachMessage> {

    @Select("SELECT * FROM coach_messages WHERE session_id = #{sessionId} ORDER BY sequence_num ASC")
    List<CoachMessage> selectBySessionId(@Param("sessionId") Long sessionId);

    @Select("SELECT MAX(sequence_num) FROM coach_messages WHERE session_id = #{sessionId}")
    Integer maxSequenceNum(@Param("sessionId") Long sessionId);
}

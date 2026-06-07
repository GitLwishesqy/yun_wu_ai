package com.yunwu.coach.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.coach.entity.Correction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CoachCorrectionMapper extends BaseMapper<Correction> {

    @Select("SELECT * FROM corrections WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<Correction> selectBySessionId(@Param("sessionId") Long sessionId);

    @Select("SELECT * FROM corrections WHERE message_id = #{messageId}")
    List<Correction> selectByMessageId(@Param("messageId") Long messageId);
}

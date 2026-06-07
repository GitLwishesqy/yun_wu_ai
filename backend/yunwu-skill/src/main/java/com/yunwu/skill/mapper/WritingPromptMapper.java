package com.yunwu.skill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.skill.entity.WritingPrompt;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface WritingPromptMapper extends BaseMapper<WritingPrompt> {
    @Select("<script>SELECT * FROM writing_prompts WHERE is_published = TRUE AND deleted_at IS NULL"
            + "<if test='gradeLevel != null'> AND grade_level = #{gradeLevel}</if>"
            + "<if test='difficulty != null'> AND difficulty = #{difficulty}</if>"
            + " ORDER BY difficulty ASC LIMIT #{limit} OFFSET #{offset}</script>")
    List<WritingPrompt> selectPublished(@Param("gradeLevel") String gradeLevel, @Param("difficulty") Integer difficulty,
                                         @Param("limit") int limit, @Param("offset") int offset);
}

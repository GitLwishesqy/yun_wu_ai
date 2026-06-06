package com.yunwu.skill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.skill.entity.ReadingPassage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ReadingPassageMapper extends BaseMapper<ReadingPassage> {
    @Select("<script>SELECT * FROM reading_passages WHERE is_published = TRUE AND deleted_at IS NULL"
            + "<if test='gradeLevel != null'> AND grade_level = #{gradeLevel}</if>"
            + "<if test='difficulty != null'> AND difficulty = #{difficulty}</if>"
            + " ORDER BY difficulty ASC LIMIT #{limit} OFFSET #{offset}</script>")
    List<ReadingPassage> selectPublished(@Param("gradeLevel") String gradeLevel, @Param("difficulty") Integer difficulty,
                                          @Param("limit") int limit, @Param("offset") int offset);
}

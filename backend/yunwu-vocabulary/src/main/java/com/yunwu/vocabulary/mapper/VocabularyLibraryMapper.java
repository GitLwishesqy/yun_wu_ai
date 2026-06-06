package com.yunwu.vocabulary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.vocabulary.entity.VocabularyLibrary;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VocabularyLibraryMapper extends BaseMapper<VocabularyLibrary> {

    @Select("SELECT * FROM vocabulary_library WHERE word_lower = #{wordLower}")
    VocabularyLibrary findByWord(@Param("wordLower") String wordLower);

    @Select("<script>SELECT * FROM vocabulary_library WHERE 1=1"
            + "<if test='cefrLevel != null'> AND cefr_level = #{cefrLevel}</if>"
            + "<if test='q != null and q != \"\"'> AND (word LIKE CONCAT('%',#{q},'%') OR translation LIKE CONCAT('%',#{q},'%'))</if>"
            + "ORDER BY difficulty ASC LIMIT #{limit} OFFSET #{offset}</script>")
    List<VocabularyLibrary> search(@Param("q") String q, @Param("cefrLevel") String cefrLevel, @Param("limit") int limit, @Param("offset") int offset);
}

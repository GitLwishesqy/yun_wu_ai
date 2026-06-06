package com.yunwu.scene.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.scene.entity.SceneTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SceneTemplateMapper extends BaseMapper<SceneTemplate> {

    @Select("SELECT * FROM scene_templates WHERE is_published = TRUE AND deleted_at IS NULL ORDER BY difficulty ASC")
    List<SceneTemplate> selectAllPublished();

    @Select("SELECT * FROM scene_templates WHERE is_published = TRUE AND deleted_at IS NULL " +
            "AND (#{gradeLevel} IS NULL OR grade_level = #{gradeLevel}) " +
            "AND (#{category} IS NULL OR category = #{category}) " +
            "AND (#{minDifficulty} IS NULL OR difficulty >= #{minDifficulty}) " +
            "AND (#{maxDifficulty} IS NULL OR difficulty <= #{maxDifficulty}) " +
            "ORDER BY difficulty ASC")
    List<SceneTemplate> selectPublishedWithFilter(
            @Param("gradeLevel") String gradeLevel,
            @Param("category") String category,
            @Param("minDifficulty") Integer minDifficulty,
            @Param("maxDifficulty") Integer maxDifficulty);

    @Select("SELECT DISTINCT category FROM scene_templates WHERE is_published = TRUE AND deleted_at IS NULL")
    List<String> selectAllCategories();
}

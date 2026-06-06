package com.yunwu.skill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.skill.entity.ContentAsset;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ContentAssetMapper extends BaseMapper<ContentAsset> {
    @Select("<script>SELECT * FROM content_assets WHERE is_published = TRUE AND deleted_at IS NULL AND asset_type = #{assetType}"
            + "<if test='gradeLevel != null'> AND grade_level = #{gradeLevel}</if>"
            + "<if test='difficulty != null'> AND difficulty = #{difficulty}</if>"
            + " ORDER BY difficulty ASC LIMIT #{limit} OFFSET #{offset}</script>")
    List<ContentAsset> selectPublished(@Param("assetType") String assetType, @Param("gradeLevel") String gradeLevel,
                                        @Param("difficulty") Integer difficulty, @Param("limit") int limit, @Param("offset") int offset);
}

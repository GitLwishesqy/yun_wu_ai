package com.yunwu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户 (排除已删除)
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND deleted_at IS NULL")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 更新最后登录时间和 IP
     */
    @Update("UPDATE users SET last_login_at = NOW(), last_login_ip = #{ip} WHERE id = #{userId}")
    int updateLastLogin(@Param("userId") Long userId, @Param("ip") String ip);

    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE id = #{userId}")
    int updateStatus(@Param("userId") Long userId, @Param("status") String status);
}

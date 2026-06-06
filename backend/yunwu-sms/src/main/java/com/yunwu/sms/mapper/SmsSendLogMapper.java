package com.yunwu.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunwu.sms.entity.SmsSendLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 短信发送日志 Mapper
 *
 * @author YunWu Team
 * @since 1.0.0
 */
@Mapper
public interface SmsSendLogMapper extends BaseMapper<SmsSendLog> {

    /**
     * 统计指定手机号今日发送次数
     */
    @Select("SELECT COUNT(*) FROM sms_send_logs " +
            "WHERE phone = #{phone} AND created_at >= CURRENT_DATE")
    int countTodayByPhone(@Param("phone") String phone);

    /**
     * 统计指定 IP 今日发送次数
     */
    @Select("SELECT COUNT(*) FROM sms_send_logs " +
            "WHERE ip_address = #{ip} AND created_at >= CURRENT_DATE")
    int countTodayByIp(@Param("ip") String ip);
}

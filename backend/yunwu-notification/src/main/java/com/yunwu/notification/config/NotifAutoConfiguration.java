package com.yunwu.notification.config;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject; import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.ComponentScan;
import java.time.LocalDateTime;

@AutoConfiguration @ComponentScan("com.yunwu.notification") @MapperScan("com.yunwu.notification.mapper")
public class NotifAutoConfiguration {
    @Bean public MetaObjectHandler notifMetaHandler() {
        return new MetaObjectHandler() {
            @Override public void insertFill(MetaObject m) { this.strictInsertFill(m,"createdAt",LocalDateTime.class,LocalDateTime.now()); }
            @Override public void updateFill(MetaObject m) {}
        };
    }
}

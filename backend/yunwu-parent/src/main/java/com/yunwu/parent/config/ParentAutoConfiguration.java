package com.yunwu.parent.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import java.time.LocalDateTime;

@AutoConfiguration @MapperScan("com.yunwu.parent.mapper")
public class ParentAutoConfiguration {
    @Bean public MetaObjectHandler parentMetaHandler() {
        return new MetaObjectHandler() {
            @Override public void insertFill(MetaObject m) { this.strictInsertFill(m,"createdAt",LocalDateTime.class,LocalDateTime.now()); this.strictInsertFill(m,"updatedAt",LocalDateTime.class,LocalDateTime.now()); }
            @Override public void updateFill(MetaObject m) { this.strictUpdateFill(m,"updatedAt",LocalDateTime.class,LocalDateTime.now()); }
        };
    }
}

package com.yunwu.report.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import java.time.LocalDateTime;

@AutoConfiguration @MapperScan("com.yunwu.report.mapper")
public class ReportAutoConfiguration {
    @Bean
    public MetaObjectHandler reportMetaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "generatedAt", LocalDateTime.class, LocalDateTime.now());
            }
            @Override public void updateFill(MetaObject metaObject) {}
        };
    }
}

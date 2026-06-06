package com.yunwu.evaluation.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@AutoConfiguration
@ComponentScan(basePackages = "com.yunwu.evaluation")
@MapperScan("com.yunwu.evaluation.mapper")
public class EvaluationAutoConfiguration {
    @Bean
    public MetaObjectHandler evalMetaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
            }
            @Override public void updateFill(MetaObject metaObject) {}
        };
    }
}

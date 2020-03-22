package com.xchinfo.erp.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.yecat.mybatis.handler.AuditMetaObjectHandler;
import org.yecat.mybatis.incrementer.MysqlKeyGenerator;
import org.yecat.mybatis.interceptor.DataFilterInterceptor;

/**
 * @author roman.li
 * @date 2017/10/9
 * @update
 */
/*@EnableTransactionManagement*/
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置数据权限
     */
    @Bean
    @Order(1)
    public DataFilterInterceptor dataFilterInterceptor() {
        return new DataFilterInterceptor();
    }

    /**
     * mybatis-plus分页插件
     */
    @Bean
    @Order(0)
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 公共字段填充插件
     *
     * @return
     */
    @Bean
    @Order(3)
    public AuditMetaObjectHandler auditMetaObjectHandler(){
        return new AuditMetaObjectHandler();
    }

    /**
     * 乐观锁机制
     *
     * @return
     */
    @Bean
    @Order(2)
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }

    /**
     * 自定义主键生成器
     *
     * @return
     */
    @Bean
    public MysqlKeyGenerator mysqlKeyGenerator(){
        return new MysqlKeyGenerator();
    }

    /**
     * SQL执行效率插件
     */
    @Bean
    @Profile({"dev","test","prod"})// 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
}

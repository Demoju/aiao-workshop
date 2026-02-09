package com.tableorder.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.tableorder.admin.mapper", "com.tableorder.customer.mapper"})
public class MyBatisConfig {
}

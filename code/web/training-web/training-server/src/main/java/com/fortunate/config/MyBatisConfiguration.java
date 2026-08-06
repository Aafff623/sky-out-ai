package com.fortunate.config;

import com.fortunate.plugins.MyBatisSqlInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description MyBatis配置类
 */
@Configuration
public class MyBatisConfiguration {
  
  @Bean
  public ConfigurationCustomizer mybatisConfigurationCustomizer() {
    return configuration -> {
      configuration.addInterceptor(new MyBatisSqlInterceptor());
    };
  }
}

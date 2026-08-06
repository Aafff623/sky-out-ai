package com.fortunate.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "training.jwt")
public class JwtProperties {
  
  // 后台管理token配置
  private String storeSecretKey;
  private long storeTtl;
  private String storeTokenName;
  
  // 小程序token配置
  private String clientSecretKey;
  private long clientTtl;
  private String clientTokenName;
}

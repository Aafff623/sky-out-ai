package com.fortunate.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "training.alioss")
public class AliOssProperties {
  
  /** OSS服务的访问端点 */
  private String endpoint;
  
  /** 访问阿里云 API 的 Access Key ID，用于进行身份验证 */
  private String accessKeyId;
  
  /** 表示访问阿里云 API 的 Access Key Secret。与 Access Key ID 配对使用，用于身份验证的私钥。确保这个值不被泄露 */
  private String accessKeySecret;
  
  /** 表示 OSS 存储桶（Bucket）的名称 */
  private String bucketName;
}

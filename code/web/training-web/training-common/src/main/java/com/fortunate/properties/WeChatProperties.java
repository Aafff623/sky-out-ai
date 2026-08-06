package com.fortunate.properties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description
 */
@Data
@Component
@ApiModel(description = "微信小程序属性配置类")
@ConfigurationProperties(prefix = "training.wechat")
public class WeChatProperties {
  
  @ApiModelProperty("小程序的appid")
  private String appid;
  
  @ApiModelProperty("小程序的秘钥")
  private String secret;
}

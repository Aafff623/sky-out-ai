package com.fortunate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description
 */
@Data
@Builder
@ApiModel(description = "菜品")
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Serializable {
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty(value = "菜品id")
  private Long id;
  
  @ApiModelProperty("菜品名称")
  private String name;
  
  @ApiModelProperty("菜品分类id")
  private Long categoryId;
  
  @ApiModelProperty("菜品价格")
  private BigDecimal price;
  
  @ApiModelProperty("图片url")
  private String image;
  
  @ApiModelProperty("描述信息")
  private String description;
  
  @ApiModelProperty("菜品状态：0-停售、1-起售")
  private Integer status;

  /**
   * 用户每购买一次这个菜品，热度加1
   */
  @ApiModelProperty("菜品热度")
  private Long hotSpot;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;
  
  private Long createUser;
  
  private Long updateUser;
}

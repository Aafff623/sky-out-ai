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
 * @description 套餐
 */
@Data
@Builder
@ApiModel(description = "套餐实体类")
@NoArgsConstructor
@AllArgsConstructor
public class Setmeal implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @ApiModelProperty("套餐id")
  private Long id;
  
  @ApiModelProperty("分类id")
  private Long categoryId;

  @ApiModelProperty("分类名称")
  private String categoryName;
  
  @ApiModelProperty("套餐名称")
  private String name;
  
  @ApiModelProperty("套餐价格")
  private BigDecimal price;
  
  @ApiModelProperty("状态 0:停用 1:启用")
  private Integer status;
  
  @ApiModelProperty("描述信息")
  private String description;
  
  @ApiModelProperty("图片")
  private String image;

  /**
   * 用户每购买一次这个套餐，热度加1
   */
  @ApiModelProperty("套餐热度")
  private Long hotSpot;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;
  
  private Long createUser;
  
  private Long updateUser;
}

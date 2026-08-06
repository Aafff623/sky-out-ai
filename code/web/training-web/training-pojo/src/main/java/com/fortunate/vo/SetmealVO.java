package com.fortunate.vo;

import com.fortunate.entity.SetmealDish;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 套餐总览
 */
@Data
@Builder
@ApiModel(description = "套餐总览")
@NoArgsConstructor
@AllArgsConstructor
public class SetmealVO implements Serializable {
  
  @ApiModelProperty("套餐id")
  private Long id;
  
  @ApiModelProperty("分类id")
  private Long categoryId;
  
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
  
  @ApiModelProperty("更新时间")
  private LocalDateTime updateTime;
  
  @ApiModelProperty("分类名称")
  private String categoryName;
  
  @ApiModelProperty("套餐和菜品的关联关系")
  private List<SetmealDish> setmealDishes = new ArrayList<>();
}

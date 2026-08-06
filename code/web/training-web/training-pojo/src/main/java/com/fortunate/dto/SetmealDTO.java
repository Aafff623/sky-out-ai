package com.fortunate.dto;

import com.fortunate.entity.SetmealDish;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 套餐dto
 */
@Data
public class SetmealDTO implements Serializable {
  
  @ApiModelProperty("套餐id")
  private Long id;
  
  @ApiModelProperty("该套餐所属分类id")
  private Long categoryId;
  
  @ApiModelProperty("套餐名称")
  private String name;
  
  @ApiModelProperty("套餐价格")
  private BigDecimal price;
  
  @ApiModelProperty("状态 0:停用 1:启用")
  private Integer status;
  
  @ApiModelProperty("描述信息")
  private String description;
  
  @ApiModelProperty("上传到阿里云oss的图片路径")
  private String image;
  
  @ApiModelProperty("套餐菜品关系")
  private List<SetmealDish> setmealDishes = new ArrayList<>();
  
}

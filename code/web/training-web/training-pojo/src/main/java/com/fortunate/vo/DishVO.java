package com.fortunate.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fortunate.entity.DishFlavor;
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
 * @description
 */
@Data
@Builder
@ApiModel(description = "菜品视图展示对象")
@NoArgsConstructor
@AllArgsConstructor
public class DishVO implements Serializable {
  
  @ApiModelProperty("菜品id")
  private Long id;
  
  @ApiModelProperty("菜品名称")
  private String name;
  
  @ApiModelProperty("菜品分类id")
  private Long categoryId;
  
  @ApiModelProperty("菜品价格")
  private BigDecimal price;
  
  @ApiModelProperty("菜品封面图片")
  private String image;
  
  @ApiModelProperty("菜品描述信息")
  private String description;
  
  @ApiModelProperty("菜品状态：0 停售 1 起售")
  private Integer status;

  @ApiModelProperty("热度")
  private Long hotSpot;

  @ApiModelProperty("分类名称")
  private String categoryName;

  @ApiModelProperty("更新时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  @ApiModelProperty("菜品关联的口味")
  private List<DishFlavor> flavors = new ArrayList<>();
}

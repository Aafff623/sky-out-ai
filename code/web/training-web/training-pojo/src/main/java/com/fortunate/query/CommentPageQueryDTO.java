package com.fortunate.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "评论分页查询数据传输对象")
public class CommentPageQueryDTO extends BasePageQuery implements Serializable {
    @ApiModelProperty("菜品id")
    private Integer dishId;

    @ApiModelProperty("套餐id")
    private Integer setmealId;
}

package com.fortunate.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BasePageQuery implements Serializable {

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页记录数")
    private int pageSize;
}

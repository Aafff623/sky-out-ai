package com.fortunate.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@ApiModel(description = "敏感词分页查询参数DTO")
@NoArgsConstructor
@AllArgsConstructor
public class SensitivePageQueryDTO extends BasePageQuery implements Serializable {

    @ApiModelProperty("敏感词")
    private String sensitive;
}

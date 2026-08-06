package com.fortunate.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentDTO {

    /**
     * 菜品id
     */
    private Long dishId;

    /***
     * 套餐id
     */
    private Long setmealId;

    /**
     * 订单号
     */
    private List<String> orderNumber;

    /**
     *分页条件 最小时间
     */
    private Date minDate;

    /**
     * 分页参数
     */
    private Integer size;
}

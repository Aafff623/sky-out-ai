package com.fortunate.entity;

import lombok.Data;

@Data
public class ItemView {
    /**
     * id
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 菜品ID 或 套餐ID
     */
    private Long itemId;
    /**
     * "dish" 或 "setmeal"
     */
    private String itemType;
    /**
     * 浏览次数
     */
    private Integer viewCount;
}
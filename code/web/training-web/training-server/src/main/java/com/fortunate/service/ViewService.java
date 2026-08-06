package com.fortunate.service;

public interface ViewService {
    /**
     * 菜品后套餐浏览数加1
     * @param dishOrSetmealId
     * @param type
     */
    void incrementViewCount(Long dishOrSetmealId, String type,Long userId);
}

package com.fortunate.constant;

/**
 * @description 缓存前缀常量类
 */
public class CachePrefixConstant {
  
  /** 缓存菜品前缀key */
  public static final String DISH_KEY = "dish_";
  
  /** 缓存套餐前缀key */
  public static final String SETMEAL_KEY = "setmealCache";

  /**
   * 缓存推荐菜品前缀key
   */
  public static final String RECOMMEND_DISH_KEY = "recommendDish_";

  /**
   * 缓存用户订单前缀key
   */
  public static final String USER_ORDER = "user:orders:";

  /**
   * 全部菜品缓存key 0
   */
  public static final Integer ALL_DISH = 0;

  /**
   * 人气最高菜品缓存key 1
   */
  public static final Integer HOT_DISH = 1;
}

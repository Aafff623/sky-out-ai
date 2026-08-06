package com.fortunate.mapper;

import com.fortunate.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 套餐菜品Mapper
 */
@Mapper
public interface SetmealDishMapper {
  
  /**
   * 根据菜品id查询套餐
   * @param ids 菜品id列表
   * @return 套餐id列表
   * @example select `setmeal_id` from `setmeal_dish` where `dish_id` in (1,2,3)
   */
  List<Long> getSetmealDishByDishIds(@Param("dishIds") List<Long> ids);
  
  /**
   * 批量保存套餐和菜品的关联关系
   * @param setmealDishes 套餐菜品关联关系列表
   */
  void insertBatch(@Param("setmealDishesList") List<SetmealDish> setmealDishes);
  
  /**
   * 根据套餐ids集合批量删除套餐和菜品的关联关系
   * @param ids 套餐id列表
   */
  void deleteBySetmealIds(@Param("setmealIds") List<Long> ids);
  
  /**
   * 根据套餐id删除套餐和菜品的关联关系
   * @param setmealId 套餐id
   */
  @Delete("delete from `setmeal_dish` where `setmeal_id` = #{setmealId}")
  void deleteBySetmealId(@Param("setmealId") Long setmealId);
}

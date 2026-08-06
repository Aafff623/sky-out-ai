package com.fortunate.mapper;

import com.fortunate.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description 菜品口味mapper
 */
@Mapper
public interface DishFlavorMapper {
  
  /**
   * 批量插入菜品口味数据
   * @param flavors 菜品口味数据列表
   */
  void insertBatch(@Param("flavorsList") List<DishFlavor> flavors);
  
  /**
   * 根据菜品id集合删除菜品口味数据
   * @param ids 菜品id集合
   */
  void deleteByDishIds(@Param("dishIds") List<Long> ids);
  
  /**
   * 根据菜品id查询菜品口味表中对应的口味数据
   * @param id 菜品id
   * @return
   */
  @Select("select * from `dish_flavor` where `dish_id` = #{dishId}")
  List<DishFlavor> getFlavorsByDishId(@Param("dishId") Long id);
  
  /**
   * 根据菜品id集合查询菜品口味表中对应的口味数据（新增）
   * @param ids 菜品id集合
   * @return
   */
  List<DishFlavor> getFlavorsByDishIds(@Param("dishIds") List<Long> ids);
  
  /**
   * 根据菜品id删除关联的口味数据
   * @param id
   */
  @Delete("delete from `dish_flavor` where `dish_id` = #{dishId}")
  void deleteByDishId(@Param("dishId") Long id);
}

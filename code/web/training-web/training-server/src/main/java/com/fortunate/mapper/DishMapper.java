package com.fortunate.mapper;

import com.fortunate.annotation.AutoFill;
import com.fortunate.entity.Dish;
import com.fortunate.enumeration.OperationType;
import com.fortunate.query.DishPageQueryDTO;
import com.fortunate.vo.DishVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description
 */
@Mapper
public interface DishMapper {
  
  /**
   * 根据分类id查询菜品数量
   * @param categoryId
   * @return 菜品数量
   */
  @Select("select count(`id`) from `dish` where `category_id` = #{categoryId}")
  Integer countByCategoryId(@Param("categoryId") Long categoryId);
  
  /**
   * 新增菜品
   * @param dish 菜品实体对象
   */
  @AutoFill(OperationType.INSERT)
  void insert(Dish dish);
  
  /**
   * 分页查询菜品数据
   * @param dishPageQueryDTO 菜品分页查询数据传输对象
   * @return 分页查询结果
   */
  Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
  
  /**
   * 根据id集合查询菜品
   * @param ids 菜品id集合
   * @return 菜品id集合
   */
  List<Dish> getDishByIds(@Param("dishIds") List<Long> ids);
  
  /**
   * 根据id集合批量删除菜品
   * @param ids 菜品id集合
   */
  void deleteByIds(@Param("dishIds") List<Long> ids);
  
  /**
   * 根据id查询菜品
   * @param id 菜品id
   * @return 菜品实体对象
   */
  @Select("select * from `dish` where `id` = #{dishId}")
  Dish getDishById(@Param("dishId") Long id);
  
  /**
   * 更新菜品信息
   * @param dish 菜品实体对象
   */
  @AutoFill(OperationType.UPDATE)
  void update(Dish dish);
  
  /**
   * 根据套餐id查询与中国套餐关联的所有菜品
   * @param id 套餐id
   * @return 套餐下所有菜品
   */
  @Select("select d.* from `dish` d left join `setmeal_dish` sd on d.`id` = sd.`dish_id` where sd.`setmeal_id` = " +
          "#{setmealId}")
  List<Dish> getBySetmealId(@Param("setmealId") Long id);
  
  /**
   * 动态条件查询菜品
   * @param dish 菜品实体对象
   * @return 菜品实体对象集合
   */
  List<Dish> list(Dish dish);
  
  /**
   * 获取所有菜品
   * @return 所有菜品列表
   */
  @Select("select * from `dish`")
  List<Dish> getAllDishes();

  /**
   * 获取所有启用菜品（按状态启用，随机排序）
   * @return 菜品列表
   */
  @Select("select * from `dish` where `status` = 1")
  List<Dish> getAllEnabledDishes();

  /**
   * 菜品热度增加
   * @param dishId
   */
  void updateHotDish(Long dishId);
}

package com.fortunate.service;

import com.fortunate.dto.DishDTO;
import com.fortunate.entity.Dish;
import com.fortunate.query.DishPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.vo.DishVO;

import java.util.List;

/**
 * @description
 */
public interface DishService {
  
  /**
   * 保存菜品
   * @param dishDTO 菜品数据传输对象
   */
  void saveWithFlavors(DishDTO dishDTO);
  
  /**
   * 分页查询菜品数据
   * @param dishPageQueryDTO 菜品分页查询数据传输对象
   * @return 分页查询结果
   */
  PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
  
  /**
   * 批量删除菜品
   * @param ids 菜品id列表
   */
  void deleteBatch(List<Long> ids);
  
  /**
   * 根据id获取菜品数据
   * @param id 菜品id
   * @return 菜品数据
   */
  DishVO getByIdWithFlavor(Long id);
  
  /**
   * 更新菜品
   * @param dishDTO 菜品数据传输对象
   */
  void updateWithFlavors(DishDTO dishDTO);
  
  /**
   * 菜品起售停售
   * @param status 菜品状态，1表示起售，0表示停售
   * @param id 菜品id
   */
  void startOrStop(Integer status, Long id);
  
  /**
   * 根据id获取菜品数据以及对应的口味数据
   * @param dish 菜品数据
   * @return 菜品数据
   */
  List<DishVO> listWithFlavors(Dish dish);
}

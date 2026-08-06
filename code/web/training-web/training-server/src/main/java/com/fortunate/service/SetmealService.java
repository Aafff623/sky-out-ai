package com.fortunate.service;



import com.fortunate.dto.SetmealDTO;
import com.fortunate.entity.Setmeal;
import com.fortunate.query.SetmealPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.vo.DishItemVO;
import com.fortunate.vo.SetmealVO;

import java.util.List;

/**
 * @description 套餐服务
 */
public interface SetmealService {
  
  /**
   * 动态条件查询套餐
   * @param setmeal 套餐实体类
   * @return 套餐集合
   */
  List<Setmeal> list(Setmeal setmeal);
  
  /**
   * 根据套餐id查询其中包含的菜品项
   * @param id 套餐id
   * @return 菜品项集合
   */
  List<DishItemVO> getDishItemBySetmealId(Long id);
  
  /**
   * 新增套餐，同时需要保存套餐和菜品的关联关系
   * @param setmealDTO 套餐dto类
   */
  void saveWithDish(SetmealDTO setmealDTO);
  
  /**
   * 分页查询套餐
   * @param setmealPageQueryDTO 套餐分页查询dto类
   * @return 分页结果
   */
  PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
  
  /**
   * 批量删除套餐
   * @param ids 套餐id集合，以逗号分隔，例如：1,2,3
   */
  void deleteBatch(List<Long> ids);
  
  /**
   * 根据套餐id查询套餐以及这个套餐包含的菜品
   * @param id 套餐id
   * @return 套餐vo类
   */
  SetmealVO getSetmealByIdWithDish(Long id);
  
  /**
   * 更新套餐
   * @param setmealDTO 套餐dto类
   */
  void update(SetmealDTO setmealDTO);
  
  /**
   * 套餐起售停售
   * @param id 套餐id
   * @param status 状态，1：起售，0：停售
   */
  void startOrStop(Long id, Integer status);
}

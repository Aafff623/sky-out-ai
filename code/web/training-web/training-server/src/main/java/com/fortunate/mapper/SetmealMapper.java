package com.fortunate.mapper;


import com.fortunate.annotation.AutoFill;
import com.fortunate.entity.Setmeal;
import com.fortunate.enumeration.OperationType;
import com.fortunate.query.SetmealPageQueryDTO;
import com.fortunate.vo.DishItemVO;
import com.fortunate.vo.SetmealVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @description 套餐Mapper
 */
@Mapper
public interface SetmealMapper {
  /**
   * 根据分类id查询套餐的数量
   * @param id 分类id
   * @return 套餐数量
   */
  @Select("select count(`id`) from `setmeal` where `category_id` = #{categoryId}")
  Integer countByCategoryId(@Param("categoryId") Long id);
  
  /**
   * 动态条件查询套餐
   * @param setmeal 套餐对象
   * @return 套餐列表
   */
  List<Setmeal> list(Setmeal setmeal);
  
  /**
   * 根据太迟id查询里面包含的菜品项
   * @param id 套餐id
   * @return 菜品项列表
   */
  @Select("select sd.name, sd.copies, d.image, d.description from setmeal_dish sd left join dish d on sd.dish_id" +
          " = d.id where sd.setmeal_id = #{setmealId}")
  List<DishItemVO> getDishItemBySetmealId(@Param("setmealId") Long id);
  
  /**
   * 新增套餐
   * @param setmeal 套餐实体对象
   */
  @AutoFill(OperationType.INSERT)
  void insert(Setmeal setmeal);
  
  /**
   * 分页查询套餐
   * @param setmealPageQueryDTO 套餐分页查询对象
   * @return 分页套餐列表
   */
  Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
  
  /**
   * 根据套餐id集合获取套餐列表
   * @param ids 套餐id集合，用逗号分隔，如：1,2,3
   * @return 套餐列表
   */
  List<Setmeal> getSetmealByIds(@Param("setmealIds") List<Long> ids);
  
  /**
   * 根据套餐ids集合批量删除套餐
   * @param ids 套餐id集合
   */
  void deleteBatchByIds(@Param("ids") List<Long> ids);
  
  /**
   * 根据id查询套餐和套餐菜品关系
   * @param id 套餐id
   * @return 套餐对象
   */
  SetmealVO getSetmealByIdWithDish(Long id);
  
  /**
   * 根据id修改套餐
   * @param setmeal 套餐对象
   */
  @AutoFill(OperationType.UPDATE)
  void update(Setmeal setmeal);
  
  /**
   * 批量更新套餐起售停售状态（原因：当一个菜品停售时，包含这个菜品的套餐也需要被停售）
   * @param setmealIds 套餐id集合
   * @param status 套餐状态，0：停售，1：起售
   */
  void batchUpdateStatus(@Param("setmealIds") List<Long> setmealIds, @Param("status") Integer status);
  
  /**
   * 根据id查询套餐
   * @param id 套餐id
   * @return 套餐对象
   */
  @Select("select * from `setmeal` where `id` = #{setmealId}")
  Setmeal getSetmealById(@Param("setmealId") Long id);

  /**
   * 更新套餐热度
   * @param setmealId
   */
  void updateHotSetmeal(Long setmealId);

}

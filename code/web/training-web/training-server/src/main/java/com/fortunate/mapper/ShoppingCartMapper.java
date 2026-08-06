package com.fortunate.mapper;

import com.fortunate.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @description
 */
@Mapper
public interface ShoppingCartMapper {
  
  /**
   * 动态条件查询购物车商品
   * @param shoppingCart 购物车实体对象
   */
  @Options(useGeneratedKeys = true, keyProperty = "id")
  List<ShoppingCart> list(ShoppingCart shoppingCart);
  
  /**
   * 根据用户id清空购物车商品
   * @param id 用户id
   */
  @Delete("delete from `shopping_cart` where `user_id` = #{userId}")
  void deleteByUserId(@Param("userId") Long id);
  
  /**
   * 批量插入购物车商品
   * @param shoppingCartList 购物车商品列表
   */
  void insertBatch(@Param("shoppingCartList") List<ShoppingCart> shoppingCartList);
  
  /**
   * 更新指定商品数量
   * @param cart 购物车实体对象
   */
  @Update("update `shopping_cart` set `number` = #{number} where `id` = #{id}")
  void updateNumberById(ShoppingCart cart);
  
  /**
   * 将商品添加到购物车商品
   * @param shoppingCart 购物车实体对象
   */
  @Insert("insert into shopping_cart (`name`, `user_id`, `dish_id`, `setmeal_id`, `dish_flavor`, `number`, `amount`, "
          + "`image`, `create_time`) values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number}," +
          "#{amount},#{image},#{createTime})")
  void insert(ShoppingCart shoppingCart);
  
  /**
   * 根据购物车id删除购物车商品
   * @param id 购物车id
   */
  @Delete("delete from `shopping_cart` where `id` = #{id}")
  void deleteById(@Param("id") Long id);
}

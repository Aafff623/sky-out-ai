package com.fortunate.service;

import com.fortunate.dto.ShoppingCartDTO;
import com.fortunate.entity.ShoppingCart;

import java.util.List;

/**
 * @description
 */
public interface ShoppingCartService {
  
  /**
   * 添加购物车
   * @param shoppingCartDTO 购物车DTO
   */
  void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
  
  /**
   * 查看购物车列表
   * @return 购物车列表
   */
  List<ShoppingCart> showShoppingCart();
  
  /**
   * 清空当前微信用户购物车
   */
  void cleanShoppingCart();
  
  /**
   * 删除购物车中的一个商品
   * @param shoppingCartDTO 购物车DTO
   */
  void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
  
  /**
   * 更新购物车中的商品数量
   */
  void updateShoppingCartNumber(ShoppingCartDTO shoppingCartDTO);
}

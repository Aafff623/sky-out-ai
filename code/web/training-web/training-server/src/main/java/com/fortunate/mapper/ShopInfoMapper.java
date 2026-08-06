package com.fortunate.mapper;

import com.fortunate.annotation.AutoFill;
import com.fortunate.entity.ShopInfo;
import com.fortunate.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @description
 */
@Mapper
public interface ShopInfoMapper {
  
  @Select("SELECT * FROM `shop_info` LIMIT 1")
  ShopInfo selectDefaultShop();
  
  /**
   * 更新店铺信息
   * @param shopInfo 店铺信息
   */
  @AutoFill(OperationType.UPDATE)
  void update(ShopInfo shopInfo);
}

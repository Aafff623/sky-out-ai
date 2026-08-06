package com.fortunate.service;

import com.fortunate.dto.ShopInfoDTO;
import com.fortunate.vo.ShopInfoVO;

/**
 * @description
 */
public interface ShopInfoService {
  
  /**
   * 获取商家店铺信息
   */
  ShopInfoVO getShopInfo();
  
  /**
   * 更新商家店铺信息
   * @param shopInfoDTO 商家店铺信息
   */
  void updateShopInfo(ShopInfoDTO shopInfoDTO);
}

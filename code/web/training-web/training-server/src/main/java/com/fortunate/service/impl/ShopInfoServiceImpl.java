package com.fortunate.service.impl;

import com.fortunate.constant.MessageConstant;
import com.fortunate.dto.ShopInfoDTO;
import com.fortunate.entity.ShopInfo;
import com.fortunate.mapper.ShopInfoMapper;
import com.fortunate.service.ShopInfoService;
import com.fortunate.vo.ShopInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "shop")
public class ShopInfoServiceImpl implements ShopInfoService {
  
  private final ShopInfoMapper shopInfoMapper;
  
  @Override
  @Cacheable(key = "'info'")
  public ShopInfoVO getShopInfo() {
    ShopInfo shopInfo = shopInfoMapper.selectDefaultShop();
    return convertToVO(shopInfo);
  }
  
  @Override
  @CacheEvict(key = "'info'")
  public void updateShopInfo(ShopInfoDTO shopInfoDTO) {
    ShopInfo shopInfo = new ShopInfo();
    BeanUtils.copyProperties(shopInfoDTO, shopInfo);
    shopInfo.setId(1L);
    shopInfoMapper.update(shopInfo);
  }
  
  private ShopInfoVO convertToVO(ShopInfo shopInfo) {
    if (shopInfo == null) {
      throw new RuntimeException(MessageConstant.SHOP_INFO_NOT_FOUND);
    }
    ShopInfoVO vo = new ShopInfoVO();
    BeanUtils.copyProperties(shopInfo, vo);
    return vo;
  }
}

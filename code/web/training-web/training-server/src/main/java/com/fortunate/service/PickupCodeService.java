package com.fortunate.service;

import org.springframework.stereotype.Service;

/**
 * @description
 */
@Service
public interface PickupCodeService {
  
  /**
   * 生成堂食取餐号
   */
  String generatePickupCode();
}

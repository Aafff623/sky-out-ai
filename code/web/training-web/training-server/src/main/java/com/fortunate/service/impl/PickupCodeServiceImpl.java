package com.fortunate.service.impl;

import com.fortunate.mapper.PickupCodeMapper;
import com.fortunate.service.PickupCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PickupCodeServiceImpl implements PickupCodeService {
  
  private static final String REDIS_KEY_PREFIX = "pickup:codes:";
  private static final int MAX_ATTEMPTS = 50;
  private static final Random RANDOM = new Random();
  
  private final PickupCodeMapper pickupCodeMapper;
  private final RedisTemplate<String, String> redisTemplate;
  
  @Override
  public String generatePickupCode() {
    LocalDate today = LocalDate.now();
    String redisKey = REDIS_KEY_PREFIX + today;
    
    // 获取当天已存在的取餐码集合
    Set<String> existingCodes = getExistingCodes(redisKey);
    
    for (int i = 0; i < MAX_ATTEMPTS; i++) {
      String code = generateRandomCode();
      
      // 先检查Redis缓存
      if (!existingCodes.contains(code)) {
        // 二次检查数据库
        if (!pickupCodeMapper.exists(today, code)) {
          // 保存到数据库和Redis
          if (saveCode(today, code, redisKey)) {
            return code;
          }
        }
        existingCodes.add(code);
      }
    }
    throw new RuntimeException("无法生成唯一取餐码，请稍后重试");
  }
  
  private String generateRandomCode() {
    char letter = (char) ('A' + RANDOM.nextInt(26));
    int number = RANDOM.nextInt(1000);
    return String.format("%c%03d", letter, number);
  }
  
  private Set<String> getExistingCodes(String redisKey) {
    // 从Redis获取当天所有取餐码
    Set<String> codes = redisTemplate.opsForSet().members(redisKey);
    return codes != null ? codes : Collections.emptySet();
  }
  
  private boolean saveCode(LocalDate date, String code, String redisKey) {
    int affected = pickupCodeMapper.insert(date, code);
    if (affected > 0) {
      // Redis添加并设置过期时间
      redisTemplate.opsForSet().add(redisKey, code);
      redisTemplate.expire(redisKey, 32, TimeUnit.HOURS); // 保留32小时
      return true;
    }
    return false;
  }
}

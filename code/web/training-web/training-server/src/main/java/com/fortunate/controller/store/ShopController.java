package com.fortunate.controller.store;

import com.fortunate.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @description
 */
@Slf4j
@Api(tags = "店铺相关接口")
@RestController("storeShopController")
@RequestMapping("/store/shop")
@RequiredArgsConstructor
public class ShopController {
  
  private static final String KEY = "SHOP_STATUS";
  
  private final RedisTemplate<String, String> redisTemplate;
  
  /**
   * 设置店铺状态
   * @param status 店铺状态，0：营业中，1：休息中
   */
  @PutMapping("/status/{status}")
  @ApiOperation("设置店铺状态")
  public Result<?> setShopStatus(@PathVariable Integer status) {
    log.info("设置店铺状态，status==> {}", status == 1 ? "营业中" : "打烊中");
    // 将店铺状态存入到redis中
    redisTemplate.opsForValue()
            .set(KEY, String.valueOf(status));
    return Result.success();
  }
  
  /**
   * 获取店铺的营业状态
   */
  @GetMapping("/status")
  @ApiOperation("获取店铺的营业状态")
  public Result<Integer> getStatus() {
    int status = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue()
                                                                 .get(KEY)));
    log.info("获取到店铺的营业状态为：{}", status == 1 ? "营业中" : "打烊中");
    return Result.success(status);
  }
}

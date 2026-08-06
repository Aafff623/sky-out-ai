package com.fortunate.controller.client;

import com.fortunate.result.Result;
import com.fortunate.service.ShopInfoService;
import com.fortunate.vo.ShopInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description
 */
@Slf4j
@RestController
@Api(tags = "C端-商家信息接口")
@RequestMapping("/client/shopInfo")
@RequiredArgsConstructor
public class ShopInfoController {
  private final ShopInfoService shopInfoService;
  
  @GetMapping
  @ApiOperation("获取商家信息")
  public Result<ShopInfoVO> getShopInfo() {
    return Result.success(shopInfoService.getShopInfo());
  }
}

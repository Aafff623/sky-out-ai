package com.fortunate.controller.store;

import com.fortunate.dto.ShopInfoDTO;
import com.fortunate.result.Result;
import com.fortunate.service.ShopInfoService;
import com.fortunate.vo.ShopInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @description
 */
@Slf4j
@RestController("storeShopInfoController")
@Api(tags = "管理端-商家管理接口")
@RequestMapping("/store/shopInfo")
@RequiredArgsConstructor
public class ShopInfoController {
  private final ShopInfoService shopInfoService;
  
  @GetMapping
  @ApiOperation("获取商家信息")
  public Result<ShopInfoVO> getShopInfo() {
    return Result.success(shopInfoService.getShopInfo());
  }
  
  @PutMapping
  @ApiOperation("更新商家信息")
  public Result<?> updateShopInfo(@Valid @RequestBody ShopInfoDTO shopInfoDTO) {
    shopInfoService.updateShopInfo(shopInfoDTO);
    return Result.success();
  }
}

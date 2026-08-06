package com.fortunate.controller.client;

import com.fortunate.dto.ShoppingCartDTO;
import com.fortunate.entity.ShoppingCart;
import com.fortunate.result.Result;
import com.fortunate.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description
 */
@Slf4j
@Api(tags = "C端购物车相关接口")
@RestController
@RequestMapping("/client/shoppingCart")
@RequiredArgsConstructor
public class ShoppingCartController {
  
  private final ShoppingCartService shoppingCartService;
  
  /**
   * 添加购物车
   * @param shoppingCartDTO 购物车DTO
   */
  @PostMapping("/add")
  @ApiOperation("添加购物车")
  public Result<?> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
    log.info("添加购物车，商品信息为==> {}", shoppingCartDTO);
    shoppingCartService.addShoppingCart(shoppingCartDTO);
    return Result.success();
  }
  
  /**
   * 查看购物车
   */
  @GetMapping("/list")
  @ApiOperation("查看购物车")
  public Result<List<ShoppingCart>> list() {
    List<ShoppingCart> list = shoppingCartService.showShoppingCart();
    return Result.success(list);
  }
  
  /**
   * 更新购物车中商品的数量
   */
  @PutMapping("/update")
  @ApiOperation("更新购物车中商品的数量")
  public Result<?> update(@RequestBody ShoppingCartDTO shoppingCartDTO) {
    log.info("更新购物车中商品的数量");
    shoppingCartService.updateShoppingCartNumber(shoppingCartDTO);
    return Result.success();
  }
  
  /**
   * 清空购物车
   */
  @DeleteMapping("/clean")
  @ApiOperation("清空购物车")
  public Result<?> clean() {
    shoppingCartService.cleanShoppingCart();
    return Result.success();
  }
  
  /**
   * 删除购物车中一个商品
   * @param shoppingCartDTO 购物车DTO
   */
  @PostMapping("/sub")
  @ApiOperation("删除购物车中一个商品")
  public Result<?> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
    shoppingCartService.subShoppingCart(shoppingCartDTO);
    return Result.success();
  }
}

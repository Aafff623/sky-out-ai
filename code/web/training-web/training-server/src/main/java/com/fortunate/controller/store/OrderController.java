package com.fortunate.controller.store;

import com.fortunate.dto.OrdersCancelDTO;
import com.fortunate.dto.OrdersRejectionDTO;
import com.fortunate.query.OrdersPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.OrderService;
import com.fortunate.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @description
 */
@Slf4j
@Api(tags = "订单管理接口")
@RestController("storeOrderController")
@RequestMapping("/store/order")
@RequiredArgsConstructor
public class OrderController {
  
  private final OrderService orderService;
  
  /**
   * 订单搜索
   */
  @GetMapping("/conditionSearch")
  @ApiOperation("订单搜索")
  public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
    PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
    return Result.success(pageResult);
  }
  
  /**
   * 根据id查询订单详情
   * @param id 订单id
   */
  @GetMapping("/details")
  @ApiOperation("根据id查询订单详情")
  public Result<OrderVO> details(@RequestParam Long id) {
    OrderVO orderVO = orderService.details(id);
    return Result.success(orderVO);
  }
  
  /**
   * 商家接单
   * @param id 订单id
   */
  @PutMapping("/confirm/{id}")
  @ApiOperation("商家接单")
  public Result<?> confirm(@PathVariable Long id) {
    orderService.confirm(id);
    return Result.success();
  }
  
  /**
   * 商家拒单
   * @param ordersRejectionDTO 订单拒单DTO
   */
  @PutMapping("/rejection")
  @ApiOperation("商家拒单")
  public Result<?> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
    orderService.rejection(ordersRejectionDTO);
    return Result.success();
  }
  
  /**
   * 商家取消订单
   */
  @PutMapping("/cancel")
  @ApiOperation("商家取消订单")
  public Result<?> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
    orderService.cancel(ordersCancelDTO);
    return Result.success();
  }
  
  /**
   * 商家派送订单
   * @param id 订单id
   */
  @PutMapping("/delivery/{id}")
  @ApiOperation("商家派送订单")
  public Result<?> delivery(@PathVariable Long id) {
    orderService.delivery(id);
    return Result.success();
  }
  
  /**
   * 完成订单
   * @param id 订单id
   */
  @PutMapping("/complete/{id}")
  @ApiOperation("完成订单")
  public Result<?> complete(@PathVariable("id") Long id) {
    orderService.complete(id);
    return Result.success();
  }
}

package com.fortunate.controller.store;

import com.fortunate.result.Result;
import com.fortunate.service.WorkspaceService;
import com.fortunate.vo.BusinessDataVO;
import com.fortunate.vo.DishOverViewVO;
import com.fortunate.vo.OrderOverViewVO;
import com.fortunate.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @description
 */
@Slf4j
@Api(tags = "工作台相关接口")
@RestController
@RequestMapping("/store/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {
  
  private final WorkspaceService workspaceService;
  
  /**
   * 工作台今日数据查询
   */
  @GetMapping("/businessData")
  @ApiOperation("工作台今日数据查询")
  public Result<BusinessDataVO> businessData() {
    // 今天的开始时间，例如：2024-08-02 00:00:00
    LocalDateTime begin = LocalDateTime.now()
            .with(LocalTime.MIN);
    // 今天的结束时间，例如：2024-08-02 23:59:59
    LocalDateTime end = LocalDateTime.now()
            .with(LocalTime.MAX);
    
    BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
    return Result.success(businessDataVO);
  }
  
  /**
   * 订单管理数据查询
   */
  @GetMapping("/overviewOrders")
  @ApiOperation("查询订单管理数据")
  public Result<OrderOverViewVO> orderOverView() {
    OrderOverViewVO orderOverView = workspaceService.getOrderOverView();
    return Result.success(orderOverView);
  }
  
  /**
   * 菜品管理数据查询
   */
  @GetMapping("/overviewDishes")
  @ApiOperation("查询菜品总览")
  public Result<DishOverViewVO> dishOverView() {
    DishOverViewVO dishOverView = workspaceService.getDishOverView();
    return Result.success(dishOverView);
  }
  
  /**
   * 套餐管理数据查询
   */
  @GetMapping("/overviewSetmeals")
  @ApiOperation("查询套餐总览")
  public Result<SetmealOverViewVO> setmealOverView() {
    SetmealOverViewVO setmealOverView = workspaceService.getSetmealOverView();
    return Result.success(setmealOverView);
  }
}

package com.fortunate.service;

import com.fortunate.vo.*;

import java.time.LocalDateTime;

/**
 * @description
 */
public interface WorkspaceService {
  /**
   * 根据时间段统计营业数据
   * @param begin 开始时间
   * @param end 结束时间
   * @return 工作台今日数据查询
   */
  BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);
  
  /**
   * 查询订单管理数据
   * @return 订单管理数据
   */
  OrderOverViewVO getOrderOverView();
  
  /**
   * 查询菜品总览数据
   * @return 菜品总览数据
   */
  DishOverViewVO getDishOverView();
  
  /**
   * 查询套餐总览
   * @return 套餐总览数据
   */
  SetmealOverViewVO getSetmealOverView();
  
  /**
   * 查询指定时间段的营业数据
   * @param beginTime 开始时间
   * @param endTime 结束时间
   * @return 运营数据报表-明细数据导出excel业务数据
   */
  BusinessDataByMapVO findRangeBusinessData(LocalDateTime beginTime, LocalDateTime endTime);
}

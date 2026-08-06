package com.fortunate.service;


import com.fortunate.vo.OrderReportVO;
import com.fortunate.vo.SalesTop10ReportVO;
import com.fortunate.vo.TurnoverReportVO;
import com.fortunate.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

/**
 * @description 数据统计服务接口
 */
public interface ReportService {
  
  /**
   * 获取营业额统计数据
   * @param begin 开始日期
   * @param end 结束日期
   * @return 营业额统计数据
   */
  TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
  
  /**
   * 获取用户统计数据
   * @param begin 开始日期
   * @param end 结束日期
   * @return 用户统计数据
   */
  UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
  
  /**
   * 订单统计数据
   * @param begin 开始日期
   * @param end 结束日期
   * @return 订单统计数据
   */
  OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
  
  /**
   * 统计指定时间区间内的销量排名前10
   * @param begin 开始日期
   * @param end 结束日期
   * @return 销量排名前10的商品
   */
  SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
  
  /**
   * 导出运营数据报表
   * @param response HTTP响应对象
   */
  void exportBusinessData(HttpServletResponse response);
}

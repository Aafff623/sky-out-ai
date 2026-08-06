package com.fortunate.controller.store;


import com.fortunate.result.Result;
import com.fortunate.service.ReportService;
import com.fortunate.vo.OrderReportVO;
import com.fortunate.vo.SalesTop10ReportVO;
import com.fortunate.vo.TurnoverReportVO;
import com.fortunate.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

/**
 * @description 数据统计相关接口
 */
@Slf4j
@Api(tags = "数据统计相关接口")
@RestController
@RequestMapping("/store/report")
@RequiredArgsConstructor
public class ReportController {
  
  private final ReportService reportService;
  
  /**
   * 营业额统计
   * @param begin 开始日期
   * @param end 结束日期
   */
  @GetMapping("/turnoverStatistics")
  @ApiOperation("营业额统计")
  public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("营业额数据统计 begin:{}, end:{}", begin, end);
    TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);
    return Result.success(turnoverReportVO);
  }
  
  /**
   * 用户统计
   * @param begin 开始日期
   * @param end 结束日期
   */
  @GetMapping("/userStatistics")
  @ApiOperation("用户统计")
  public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("用户数据统计 begin:{}, end:{}", begin, end);
    UserReportVO userReportVO = reportService.getUserStatistics(begin, end);
    return Result.success(userReportVO);
  }
  
  /**
   * 订单统计
   * @param begin 开始日期
   * @param end 结束日期
   */
  @GetMapping("/ordersStatistics")
  @ApiOperation("订单统计")
  public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("订单数据统计 begin:{}, end:{}", begin, end);
    OrderReportVO orderReportVO = reportService.getOrderStatistics(begin, end);
    return Result.success(orderReportVO);
  }
  
  /**
   * 销量排名top10
   * @param begin 开始日期
   * @param end 结束日期
   * @return
   */
  @GetMapping("/top10")
  @ApiOperation("销量排名top10")
  public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("销量排名top10 begin:{}, end:{}", begin, end);
    SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10(begin, end);
    return Result.success(salesTop10ReportVO);
  }
  
  /**
   * 导出运营数据报表
   * @param response HTTP响应对象
   */
  @GetMapping("/export")
  @ApiOperation("导出运营数据报表")
  public void export(HttpServletResponse response) {
    reportService.exportBusinessData(response);
  }
}

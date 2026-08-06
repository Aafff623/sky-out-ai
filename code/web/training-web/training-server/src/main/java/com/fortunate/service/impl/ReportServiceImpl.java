package com.fortunate.service.impl;


import com.fortunate.dto.GoodsSalesDTO;
import com.fortunate.entity.Orders;
import com.fortunate.entity.User;
import com.fortunate.mapper.OrderMapper;
import com.fortunate.mapper.UserMapper;
import com.fortunate.service.ReportService;
import com.fortunate.service.WorkspaceService;
import com.fortunate.vo.*;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 数据统计服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {
  
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private UserMapper userMapper;
  @Resource
  private WorkspaceService workspaceService;
  
  @Override
  public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = this.getDateList(begin, end);
    
    /* 查询整个日期范围内的所有订单数据（开始时间的最小值和结束时间的最大值） */
    LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN); // 开始时间，例如2024-07-26T00:00
    LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1),
                                             LocalTime.MAX); // 结束时间，例如2024-08-02T23:59:999999999
    
    Map<String, Object> map = new HashMap<>();
    map.put("begin", beginTime);
    map.put("end", endTime);
    map.put("status", Orders.COMPLETED); // 只查询订单状态为`已完成`的订单
    
    /* 获取整个日期范围内的所有订单数据，原型：select * from orders where order_time between beginTime and endTime and status = 5 */
    List<Orders> orders = orderMapper.findOrdersByMap(map);
    
    /* 按日期分组统计营业额，格式例子：map{ 2024-07-26=18.0, 2024-07-27=0.0, ... } */
    Map<LocalDate, Double> turnoverMap = orders.stream()
            .collect(Collectors.groupingBy(order -> order.getOrderTime()
                    .toLocalDate(), Collectors.summingDouble(order -> order.getAmount()
                    .doubleValue())));
    
    // 声明一个列表，用于存放每一条的营业额数据
    List<Double> turnoverList = new ArrayList<>();
    for (LocalDate date : dateList) {
      // 如果没有该日期的营业额数据，则默认为0.0
      Double turnover = turnoverMap.getOrDefault(date, 0.0);
      turnoverList.add(turnover);
    }
    
    // 封装返回结果
    return TurnoverReportVO.builder()
            // 日期列表，用逗号分隔，返回格式例如：2024-07-26,2024-07-27,2024-07-28,2024-07-29,2024-07-30,2024-07-31,2024-08-01,2024-08-02
            .dateList(StringUtils.join(dateList, ","))
            // 对应每个日期的营业额列表，用逗号分隔，返回格式例如：18.0,0.0,0.0,0.0,72.0,0.0,0.0,0.0（每个逗号隔开代表一个日期）
            .turnoverList(StringUtils.join(turnoverList, ","))
            .build();
  }
  
  @Override
  public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = this.getDateList(begin, end);
    
    LocalDateTime beginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
    LocalDateTime endTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);
    
    Map<String, Object> map = new HashMap<>();
    map.put("begin", beginTime);
    map.put("end", endTime);
    
    /* 查询指定日期范围内的所有用户 */
    List<User> users = userMapper.findUsersByMap(map);
    
    /* 按日期分组统计每天新增的用户数量和总用户数量，格式例子：map{ 2024-07-26=10, 2024-07-27=0, ... } */
    Map<LocalDate, Long> newUserMap = users.stream()
            .collect(Collectors.groupingBy(user -> user.getCreateTime()
                    .toLocalDate(), Collectors.counting()));
    
    /* 存放每个日期对应的总用户数量，格式例子：map{ 2024-07-26=10, 2024-07-27=0, ... } */
    Map<LocalDate, Long> totalUserMap = new HashMap<>();
    long totalUsers = 0;
    
    // 计算总用户数量
    for (LocalDate date : dateList) {
      totalUsers += newUserMap.getOrDefault(date, 0L);
      totalUserMap.put(date, totalUsers);
    }
    
    /* 存放每天新增的用户数量 */
    List<Integer> newUserList = dateList.stream()
            .map(date -> newUserMap.getOrDefault(date, 0L)
                    .intValue())
            .collect(Collectors.toList());
    
    /* 存放每天对应的总用户数量 */
    List<Integer> totalUserList = dateList.stream()
            .map(date -> totalUserMap.get(date)
                    .intValue())
            .collect(Collectors.toList());
    
    // 封装返回结果
    return UserReportVO.builder()
            .dateList(StringUtils.join(dateList, ","))
            .totalUserList(StringUtils.join(totalUserList, ","))
            .newUserList(StringUtils.join(newUserList, ","))
            .build();
  }
  
  @Override
  public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = this.getDateList(begin, end);
    
    LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
    LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
    
    Map<String, Object> map = new HashMap<>();
    map.put("begin", beginTime);
    map.put("end", endTime);
    
    /* 查询指定范围能的所有订单 */
    List<Orders> orders = orderMapper.findOrdersByMap(map);
    
    /* 存放每天的订单总数map */
    Map<LocalDate, Long> orderCountMap = orders.stream()
            .collect(Collectors.groupingBy(order -> order.getOrderTime()
                    .toLocalDate(), Collectors.counting()));
    
    /* 存放每天的有效订单数map */
    Map<LocalDate, Long> validOrderCountMap = orders.stream()
            .filter(order -> Orders.COMPLETED.equals(order.getStatus())) // 订单状态为已完成的订单叫有效订单
            .collect(Collectors.groupingBy(order -> order.getOrderTime()
                    .toLocalDate(), Collectors.counting()));
    
    /* 计算总订单数和有效订单数 */
    long totalOrderCount = orders.size(); // 总订单数
    long validOrderCount = orders.stream()
            .filter(order -> Orders.COMPLETED.equals(order.getStatus()))
            .count(); // 有效订单数
    
    /* 计算订单完成率 = (有效订单数 / 总订单数 * 100%) */
    double orderCompleteRate = (totalOrderCount != 0) ? (double) validOrderCount / totalOrderCount : 0.0;
    
    /* 存放每天的订单总数和有效订单数 */
    List<Integer> orderCountList = dateList.stream()
            .map(date -> orderCountMap.getOrDefault(date, 0L)
                    .intValue())
            .collect(Collectors.toList());
    
    List<Integer> validOrderCountList = dateList.stream()
            .map(date -> validOrderCountMap.getOrDefault(date, 0L)
                    .intValue())
            .collect(Collectors.toList());
    
    // 封装返回结果
    return OrderReportVO.builder()
            .dateList(StringUtils.join(dateList, ","))
            .orderCountList(StringUtils.join(orderCountList, ",")) // 每日订单数列表，使用逗号分隔
            .validOrderCountList(StringUtils.join(validOrderCountList, ",")) // 每日有效订单数列表，使用逗号分隔
            .totalOrderCount((int) totalOrderCount) // 总订单数量
            .validOrderCount((int) validOrderCount) // 有效订单数量
            .orderCompletionRate(orderCompleteRate) // 订单完成率
            .build();
  }
  
  @Override
  public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
    LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
    LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
    
    List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
    
    List<String> names = salesTop10.stream()
            .map(GoodsSalesDTO::getName)
            .collect(Collectors.toList());
    
    List<Integer> numbers = salesTop10.stream()
            .map(GoodsSalesDTO::getNumber)
            .collect(Collectors.toList());
    
    return SalesTop10ReportVO.builder()
            .nameList(StringUtils.join(names, ","))
            .numberList(StringUtils.join(numbers, ","))
            .build();
  }
  
  @Override
  public void exportBusinessData(HttpServletResponse response) {
    // 查询数据库，获取营业数据（查询最近前30天的运营数据）
    LocalDateTime beginTime = LocalDateTime.of(LocalDate.now()
                                                       .minusDays(30),
                                               LocalTime.MIN); // minusDays(30)表示今天日期减30天，也就是30天前的日期
    LocalDateTime endTime = LocalDateTime.of(LocalDate.now()
                                                     .minusDays(1),
                                             LocalTime.MAX); // minusDays(1)表示今天日期减1天，也就是昨天的日期，因为有可能今天还没结束，数据不完全
    
    /* 根据时间段统计营业数据：营业额、订单完成率、新增用户数、有效订单、平均客单价 */
    BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);
    
    /* 通过POI将数据写入到Excel文件中 */
    InputStream inputStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream("template/运营数据报表模板.xlsx");
    assert inputStream != null;
    
    try {
      // 基于模版文件创建一个新的Excel文件
      XSSFWorkbook excel = new XSSFWorkbook(inputStream);
      
      // 获取表格文件的Sheet页对象
      XSSFSheet sheet = excel.getSheetAt(0);
      
      // 填充数据（获取第1行第2列的单元格对象，并设置值） --> 时间
      sheet.getRow(1)
              .getCell(1)
              .setCellValue("时间：" + beginTime.toLocalDate() + " 至 " + endTime.toLocalDate());
      
      // 填充数据（获取第4行第2、4、6列的单元格对象，并设置值） --> 营业额、订单完成率、新增用户数
      XSSFRow row = sheet.getRow(3);
      row.getCell(2)
              .setCellValue(businessDataVO.getTurnover()); // 营业额
      // row.getCell(4).setCellValue(Math.round(businessDataVO.getOrderCompletionRate() * 10000) / 100.0 + "%"); //
      // 订单完成率
      row.getCell(4)
              .setCellValue(businessDataVO.getOrderCompletionRate()); // 订单完成率（excel会帮我们自动格式化为百分比）
      row.getCell(6)
              .setCellValue(businessDataVO.getNewUsers()); // 新增用户数
      
      // 填充数据（获取第5行第2、4、6列的单元格对象，并设置值） --> 有效订单、平均客单价
      row = sheet.getRow(4);
      row.getCell(2)
              .setCellValue(businessDataVO.getValidOrderCount()); // 有效订单
      row.getCell(4)
              .setCellValue(businessDataVO.getUnitPrice()); // 平均客单价
      
      /* 填充明细数据 */
      BusinessDataByMapVO businessData = workspaceService.findRangeBusinessData(beginTime, endTime);
      
      for (int i = 0; i < 30; i++) {
        LocalDate date = beginTime.toLocalDate()
                .plusDays(i);
        
        // 依次获取某一行，从第7行开始（具体从第几行开始请参照模板文件）
        row = sheet.getRow(7 + i);
        row.getCell(1)
                .setCellValue(date.toString()); // 日期
        row.getCell(2)
                .setCellValue(businessData.getTurnoverMap()
                                      .getOrDefault(date, 0.0)); // 营业额
        row.getCell(3)
                .setCellValue(businessData.getValidOrderCountMap()
                                      .getOrDefault(date, 0L)); // 有效订单数
        row.getCell(4)
                .setCellValue(businessData.getOrderCompletionRateMap()
                                      .getOrDefault(date, 0.0)); // 订单完成率
        row.getCell(5)
                .setCellValue(businessData.getUnitPriceMap()
                                      .getOrDefault(date, 0.0)); // 平均客单价
        row.getCell(6)
                .setCellValue(businessData.getNewUsersMap()
                                      .getOrDefault(date, 0L)); // 新增用户数
      }
      
      /* 通过输出流将Excel文件下载到客户端浏览器 */
      ServletOutputStream out = response.getOutputStream();
      excel.write(out);
      
      // 关闭资源
      excel.close();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 获取指定日期范围内的日期列表
   * @param begin 开始日期
   * @param end 结束日期
   * @return 日期列表
   */
  private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
    // 声明一个列表，用于存放从`begin`到`end`期间的每一天日期
    List<LocalDate> dateList = new ArrayList<>();
    // 追加第一天
    dateList.add(begin);
    
    while (begin.isBefore(end)) {
      // 日期计算，计算指定日期的后一天对应的日期
      begin = begin.plusDays(1); // 追加一天
      dateList.add(begin);
    }
    return dateList;
  }
}

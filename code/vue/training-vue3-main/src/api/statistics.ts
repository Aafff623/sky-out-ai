import request from "@/utils/request";

const STATISTICS_BASE_URL = "/store/report";
export const statisticsUrls = {
  turnoverStatistics: `${STATISTICS_BASE_URL}/turnoverStatistics`,
  userStatistics: `${STATISTICS_BASE_URL}/userStatistics`,
  ordersStatistics: `${STATISTICS_BASE_URL}/ordersStatistics`,
  top10: `${STATISTICS_BASE_URL}/top10`,
  export: `${STATISTICS_BASE_URL}/export`,
};

class StatisticsAPI {
  /**
   * 营业额统计
   * @param begin 开始日期 (YYYY-MM-DD)
   * @param end 结束日期 (YYYY-MM-DD)
   */
  static turnoverStatistics(begin: string, end: string) {
    return request<any, ReqResultType<TurnoverReportVO>>({
      url: statisticsUrls.turnoverStatistics,
      method: "get",
      params: { begin, end },
    });
  }

  /**
   * 用户统计
   * @param begin 开始日期 (YYYY-MM-DD)
   * @param end 结束日期 (YYYY-MM-DD)
   */
  static userStatistics(begin: string, end: string) {
    return request<any, ReqResultType<UserReportVO>>({
      url: statisticsUrls.userStatistics,
      method: "get",
      params: { begin, end },
    });
  }

  /**
   * 订单统计
   * @param begin 开始日期 (YYYY-MM-DD)
   * @param end 结束日期 (YYYY-MM-DD)
   */
  static ordersStatistics(begin: string, end: string) {
    return request<any, ReqResultType<OrderReportVO>>({
      url: statisticsUrls.ordersStatistics,
      method: "get",
      params: { begin, end },
    });
  }

  /**
   * 销量排名TOP10
   * @param begin 开始日期 (YYYY-MM-DD)
   * @param end 结束日期 (YYYY-MM-DD)
   */
  static top10(begin: string, end: string) {
    return request<any, ReqResultType<SalesTop10ReportVO>>({
      url: statisticsUrls.top10,
      method: "get",
      params: { begin, end },
    });
  }

  /**
   * 导出运营数据报表
   * @param begin 开始日期 (YYYY-MM-DD)
   * @param end 结束日期 (YYYY-MM-DD)
   */
  static exportReport(begin: string, end: string) {
    return request<any, ReqResultType<Blob>>({
      url: statisticsUrls.export,
      method: "get",
      params: { begin, end },
      responseType: "blob",
    });
  }
}

/**
 * 营业额统计返回值
 */
export interface TurnoverReportVO {
  /** 日期列表（逗号分隔） */
  dateList?: string;
  /** 营业额列表（逗号分隔） */
  turnoverList?: string;
}

/**
 * 用户统计返回值
 */
export interface UserReportVO {
  /** 日期列表（逗号分隔） */
  dateList?: string;
  /** 用户总量列表（逗号分隔） */
  totalUserList?: string;
  /** 新增用户列表（逗号分隔） */
  newUserList?: string;
}

/**
 * 订单统计返回值
 */
export interface OrderReportVO {
  /** 日期列表（逗号分隔） */
  dateList?: string;
  /** 每日订单数列表（逗号分隔） */
  orderCountList?: string;
  /** 有效订单数列表（逗号分隔） */
  validOrderCountList?: string;
  /** 订单总数 */
  totalOrderCount?: number;
  /** 有效订单数 */
  validOrderCount?: number;
  /** 订单完成率 */
  orderCompletionRate?: number;
}

/**
 * 销量排名TOP10返回值
 */
export interface SalesTop10ReportVO {
  /** 商品名称列表（逗号分隔） */
  nameList?: string;
  /** 销量列表（逗号分隔） */
  numberList?: string;
}

export default StatisticsAPI;

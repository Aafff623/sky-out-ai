import {
  type TurnoverReportVO,
  type UserReportVO,
  type OrderReportVO,
  type SalesTop10ReportVO,
} from "@/api/statistics";

// 时间范围类型
export type DateRangeType =
  | "yesterday"
  | "7days"
  | "30days"
  | "currentWeek"
  | "currentMonth";

export type StatisticsRequestMap = {
  turnoverStatistics: TurnoverReportVO;
  userStatistics: UserReportVO;
  ordersStatistics: OrderReportVO;
  top10: SalesTop10ReportVO;
};

export type TransformFunctions = {
  [K in keyof StatisticsRequestMap]: (
    res: ReqResultType<StatisticsRequestMap[K]>["data"]
  ) => EChartsCoreOption;
};

export type ModuleState<T extends Record<string, any>> = {
  [K in keyof T]: {
    data: T[K] | null;
    options: EChartsCoreOption;
    loading: boolean;
  };
};

export type ChartsList = Array<{
  apiKey: keyof StatisticsRequestMap;
  cardProps?: Partial<BaseCardType>;
  chartsProps?: Partial<BaseEchartsType>;
}>;

export type ModuleStates = ModuleState<StatisticsRequestMap>;

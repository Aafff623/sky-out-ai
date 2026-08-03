import type { XAXisComponentOption } from "echarts";

/**
 * 智能X轴配置生成器
 * @param dateList 日期数据数组
 * @param options 配置选项
 */
export const createXAxis = (
  dateList: string[],
  options?: {
    defaultRotate?: number; // 默认旋转角度（默认45）
    maxLabels?: number; // 最大显示标签数（默认15）
    forceRotate?: boolean; // 强制旋转
    showFullYear?: boolean; // 是否显示完整年份
    formatter?: (date: string) => string; // 自定义格式化函数
  }
): XAXisComponentOption => {
  const dataLength = dateList.length;
  const {
    maxLabels = 15,
    forceRotate,
    showFullYear = true,
    formatter,
  } = options || {};

  // 计算显示间隔
  const interval =
    dataLength <= maxLabels ? 0 : Math.floor(dataLength / maxLabels);

  // 自动旋转逻辑
  const autoRotate =
    forceRotate !== undefined ? forceRotate : dataLength > maxLabels;

  // 默认格式化函数
  const defaultFormatter = (date: string) => {
    if (dataLength <= maxLabels || showFullYear) return date;
    return date.slice(date.indexOf("-") + 1); // 显示 MM-DD
  };

  return {
    type: "category",
    data: dateList,
    axisLabel: {
      interval,
      rotate: autoRotate ? 90 : options?.defaultRotate || 45,
      formatter: formatter || defaultFormatter,
      margin: 10, // 增加标签间距
    },
    axisTick: {
      alignWithLabel: true,
      interval,
    },
    axisLine: {
      onZero: false,
    },
  };
};

/**
 * 智能响应式配置
 * @param chartRef echarts实例引用
 * @param dateList 日期数据数组
 * @param maxLabels 最大标签数
 */
export const useSmartAxis = (
  chartRef: Ref<echarts.ECharts | undefined>,
  dateList: string[],
  maxLabels = 15
) => {
  const updateAxis = () => {
    if (!chartRef.value) return;

    const options = chartRef.value.getOption();
    const newAxis = createXAxis(dateList, { maxLabels });

    chartRef.value.setOption(
      {
        xAxis: { ...options.xAxis!, ...newAxis },
      },
      true // 是否合并选项
    );
  };

  updateAxis();
  chartRef.value?.resize();
};

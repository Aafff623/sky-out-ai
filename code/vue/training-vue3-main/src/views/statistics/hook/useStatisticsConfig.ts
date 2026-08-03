import * as StatisticsTypes from "../types";

export function useStatisticsConfig() {
  const selectedRange = ref<StatisticsTypes.DateRangeType>("7days");
  const chartsList = ref<StatisticsTypes.ChartsList>([
    {
      apiKey: "turnoverStatistics",
      cardProps: {
        title: "营业额统计",
      },
    },
    {
      apiKey: "userStatistics",
      cardProps: {
        title: "新增用户统计",
      },
    },
    {
      apiKey: "ordersStatistics",
      cardProps: {
        title: "订单统计",
      },
    },
    {
      apiKey: "top10",
      cardProps: {
        title: "销量排名TOP10",
      },
    },
  ]);
  const moduleStates = reactive<StatisticsTypes.ModuleStates>({
    turnoverStatistics: { data: null, options: {}, loading: true },
    userStatistics: { data: null, options: {}, loading: true },
    ordersStatistics: { data: null, options: {}, loading: true },
    top10: { data: null, options: {}, loading: true },
  });

  const timeRanges = ref([
    { label: "近7日", value: "7days" },
    { label: "近30天", value: "30days" },
    { label: "昨日", value: "yesterday" },
    { label: "本周", value: "currentWeek" },
    { label: "本月", value: "currentMonth" },
  ]);

  // 显示日期范围文本
  const displayDateRange = computed(() => {
    return `${dateRange.value.begin} 至 ${dateRange.value.end}`;
  });

  // 计算开始结束日期
  const dateRange = computed(() => {
    const now = new Date();
    const endDate = new Date(now);
    let startDate = new Date(now);

    switch (selectedRange.value) {
      case "yesterday":
        startDate.setDate(now.getDate() - 1);
        endDate.setDate(now.getDate() - 1);
        break;
      case "7days":
        startDate.setDate(now.getDate() - 6);
        break;
      case "30days":
        startDate.setDate(now.getDate() - 29);
        break;
      case "currentWeek":
        startDate = getWeekStart(new Date(now));
        break;
      case "currentMonth":
        startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        break;
    }

    startDate.setHours(0, 0, 0, 0);
    endDate.setHours(23, 59, 59, 999);

    return {
      begin: formatDate(startDate),
      end: formatDate(endDate),
    };
  });

  // 新增智能间隔计算函数
  const calculateSmartInterval = (dataLength: number) => {
    if (dataLength <= 20) return 0; // 10个以内全显示
    if (dataLength <= 30) return 1; // 20个数据时每隔1个显示
    return Math.floor(dataLength / 10); // 超过20个按每10%显示
  };

  // 新增智能旋转计算
  const calculateRotation = (dataLength: number) => {
    return dataLength > 15 ? 90 : dataLength <= 3 ? 0 : 45;
  };

  const transformData = computed<StatisticsTypes.TransformFunctions>(() => {
    return {
      turnoverStatistics(res) {
        const dateList = res?.dateList?.split(",") || [];
        const dataLength = dateList.length;

        return {
          title: [
            {
              subtext: "直观展示营业额最新统计结果",
              subTextStyle: {
                fontSize: 16,
              },
            },
            {
              subtext: "营业额（元）",
              left: "center",
              bottom: 0,
              subtextStyle: {},
            },
          ],
          tooltip: {
            trigger: "axis",
            axisPointer: {
              type: "cross",
              crossStyle: {
                width: 1,
                color: getCssVarValue("--el-color-primary"),
              },
            },
          },
          // grid 可以·设置整个图表容器的距离
          grid: {
            left: "1%",
            right: "-5%",
            bottom: "10%",
            top: "60px",
            containLabel: true, // 包含标签的绘图区域
          },
          xAxis: {
            type: "category",
            data: dateList,
            axisLabel: {
              // 此处偷懒，直接写了
              interval: calculateSmartInterval(dataLength),
              rotate: calculateRotation(dataLength),
              formatter: (value: string) => value,
            },
            axisTick: {
              alignWithLabel: true, // 保证刻度线与标签对齐
            },
          },
          yAxis: {
            type: "value",
            axisLabel: {
              formatter: (val: number) => {
                return val;
              },
            },
          },
          series: [
            {
              name: "营业额",
              type: "line",
              smooth: true,
              stack: "Total",
              data: res?.turnoverList?.split(",").map(Number) || [],
            },
          ],
        };
      },

      userStatistics(res) {
        const dateList = res?.dateList?.split(",") || [];
        const dataLength = dateList.length;
        return {
          title: {
            subtext: "周期内新增用户数量趋势概览",
            subTextStyle: {
              fontSize: 16,
            },
          },
          tooltip: {
            trigger: "axis",
            axisPointer: {
              type: "cross",
              crossStyle: {
                width: 1,
                color: getCssVarValue("--el-color-primary"),
              },
            },
          },
          legend: {
            bottom: 0,
            data: ["总用户数（个）", "新增用户（个）"],
          },
          grid: {
            left: "1%",
            right: "-5%",
            bottom: "10%",
            top: "60px",
            containLabel: true,
          },
          xAxis: {
            type: "category",
            data: dateList,
            axisLabel: {
              interval: calculateSmartInterval(dataLength),
              rotate: calculateRotation(dataLength),
              formatter: (value: string) => value,
            },
            axisTick: {
              alignWithLabel: true, // 保证刻度线与标签对齐
            },
          },
          yAxis: {
            type: "value",
            axisLabel: {
              formatter: (val: number) => val,
            },
          },
          series: [
            {
              name: "总用户数（个）",
              type: "line",
              smooth: true,
              stack: "Total",
              data: res?.totalUserList?.split(",").map(Number) || [],
            },
            {
              name: "新增用户（个）",
              type: "line",
              smooth: true,
              stack: "Total",
              data: res?.newUserList?.split(",").map(Number) || [],
            },
          ],
        };
      },

      ordersStatistics(res) {
        const top = 5; // 距离顶部的距离
        const offsetTop = top + 7; // 在距离顶部的值基础上再往下偏移的距离，最终结果为 top + offsetYTop
        const symbolOffsetTop = 9; // 符号距离顶部的距离
        const left = 0;
        const offsetLeft = left + 90;
        const leftDifferenceValue = offsetLeft - 20; // 差值
        const latestTotal = res?.totalOrderCount || 0;
        const latestValid = res?.validOrderCount || 0;
        const completionRate = formatPercentage(res?.orderCompletionRate || 0);
        const dateList = res?.dateList?.split(",") || [];
        const dataLength = dateList.length;

        return {
          graphic: {
            elements: [
              // 订单完成率
              {
                type: "text",
                left: left, // 改为像素值更精准
                top: `${top}%`,
                style: {
                  text: "订单完成率",
                  fontSize: 12,
                  fill: getCssVarValue("--el-text-color-regular"),
                },
              },
              // 订单完成率-百分比数值
              {
                type: "text",
                left: left,
                top: `${offsetTop}%`,
                style: {
                  text: completionRate,
                  fontSize: 15,
                  fontWeight: "bold",
                  fill: getCssVarValue("--el-text-color-regular"),
                },
              },
              // 有效订单
              {
                type: "text",
                left: offsetLeft,
                top: `${top}%`,
                style: {
                  text: "有效订单",
                  fontSize: 12,
                  fill: getCssVarValue("--el-text-color-regular"),
                },
              },
              // 有效订单-数值
              {
                type: "text",
                left: offsetLeft,
                top: `${offsetTop}%`,
                style: {
                  text: latestValid.toString(),
                  fontSize: 15,
                  fontWeight: "bold",
                  fill: getCssVarValue("--el-text-color-regular"),
                },
              },
              // 等号
              {
                type: "text",
                left: leftDifferenceValue,
                top: `${symbolOffsetTop}%`,
                style: {
                  text: "=",
                  fontSize: 15,
                  fill: getCssVarValue("--el-text-color-secondary"),
                },
              },
              // 订单总数
              {
                type: "text",
                left: offsetLeft * 2,
                top: `${top}%`,
                style: {
                  text: "订单总数",
                  fontSize: 12,
                  fill: getCssVarValue("--el-text-color-regular"),
                },
              },
              // 订单总数-数值
              {
                type: "text",
                left: offsetLeft * 2,
                top: `${offsetTop}%`,
                style: {
                  text: latestTotal.toString(),
                  fontSize: 15,
                  fontWeight: "bold",
                  fill: getCssVarValue("--el-text-color-regular"),
                },
              },
              // 斜杠
              {
                type: "text",
                left: leftDifferenceValue * 2 + 10,
                top: `${symbolOffsetTop}%`,
                style: {
                  text: "/",
                  fontSize: 15,
                  fill: getCssVarValue("--el-text-color-secondary"),
                },
              },
            ],
          },
          tooltip: {
            trigger: "axis",
            axisPointer: {
              type: "cross",
              crossStyle: {
                width: 1,
                color: getCssVarValue("--el-color-primary"),
              },
            },
          },
          legend: {
            bottom: 0,
            data: ["订单总数（个）", "有效订单（个）"],
          },
          grid: {
            left: "0%",
            right: "-5%",
            bottom: "5%",
            top: "23%",
            containLabel: true,
          },
          xAxis: {
            type: "category",
            data: dateList,
            axisLabel: {
              interval: calculateSmartInterval(dataLength),
              rotate: calculateRotation(dataLength),
              formatter: (value: string) => value,
            },
            axisTick: {
              alignWithLabel: true, // 保证刻度线与标签对齐
            },
          },
          yAxis: {
            type: "value",
            axisLabel: {
              formatter: (val: number) => val,
            },
          },
          series: [
            {
              name: "订单总数（个）",
              type: "line",
              smooth: true,
              stack: "Total",
              data: res?.orderCountList?.split(",").map(Number) || [],
            },
            {
              name: "有效订单（个）",
              type: "line",
              smooth: true,
              stack: "Total",
              data: res?.validOrderCountList?.split(",").map(Number) || [],
            },
          ],
        };
      },

      top10(res) {
        return {
          title: {
            subtext: "畅销商品销量 TOP10 榜单",
            subTextStyle: {
              fontSize: 16,
            },
          },
          tooltip: {
            trigger: "axis",
            axisPointer: {
              type: "shadow",
              shadowStyle: {
                width: 1,
                color: getCssVarValue("--el-color-primary-light-6"),
              },
            },
          },
          legend: {
            top: 0,
            left: "right",
            data: ["销量份数（份）"],
          },
          grid: {
            left: "1%",
            right: "-5%",
            bottom: "10%",
            top: "60px",
            containLabel: true,
          },
          xAxis: {
            show: true,
            type: "value",
            axisLabel: {
              formatter: (val: number) => val,
            },
          },
          yAxis: {
            type: "category",
            data: res?.nameList?.split(",") || [],
            axisLabel: {
              interval: 0,
              rotate: 90,
            },
          },
          series: [
            {
              name: "销量份数（份）",
              type: "bar",
              barWidth: "60%", // 柱条的宽度
              barMaxWidth: "60%",
              itemStyle: {
                color: getCssVarValue("--el-color-primary"),
              },
              data: res?.numberList?.split(",").map(Number) || [],
            },
          ],
        };
      },
    };
  });

  // 获取css变量值
  function getCssVarValue(varName: string) {
    return getComputedStyle(document.documentElement).getPropertyValue(varName);
  }

  // 格式化百分比
  function formatPercentage(value: number, precision: number = 2): string {
    if (Number.isNaN(value) || !Number.isFinite(value)) {
      return "0%";
    }

    const percentage = (value * 100).toFixed(precision);
    return `${parseFloat(percentage)}%`;
  }

  // 日期格式化工具函数
  function formatDate(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  // 获取本周第一天（周一）
  function getWeekStart(date: Date) {
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // 调整周一为每周第一天
    return new Date(date.setDate(diff));
  }

  return {
    moduleStates,
    selectedRange,
    chartsList,
    displayDateRange,
    timeRanges,
    dateRange,
    transformData,
  };
}

<script lang="ts" setup>
import StatisticsAPI from "@/api/statistics";
import { StatisticsRequestMap } from "./types";
import { useStatisticsConfig } from "./hook/useStatisticsConfig";

const {
  moduleStates,
  selectedRange,
  chartsList,
  displayDateRange,
  timeRanges,
  dateRange,
  transformData,
} = useStatisticsConfig();

const handleExport = async () => {
  const { begin, end } = dateRange.value;
  ElMessageBox.confirm(`导出${begin}-${end}的运营数据？`, "数据导出", {
    confirmButtonText: "确 定",
    cancelButtonText: "取 消",
    type: "warning",
  })
    .then(async () => {
      const res = await StatisticsAPI.exportReport(begin, end);
      // 创建下载链接
      const blob = new Blob([res.data]);
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = `运营数据_${begin}_${end}.xlsx`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      ElMessage.success("导出成功");
    })
    .catch(() => {});
};

// 获取图表数据
async function fetchChartData<K extends keyof StatisticsRequestMap>(apiKey: K) {
  try {
    moduleStates[apiKey].loading = true;
    const res = await StatisticsAPI[apiKey](
      dateRange.value.begin,
      dateRange.value.end
    );
    if (res?.code === 200) {
      // 保存源数据
      moduleStates[apiKey].data = res.data;
      moduleStates[apiKey].options = transformData.value[apiKey](res.data);
    }
  } catch (err) {
    console.error(`${apiKey}数据获取失败==>`, err);
  } finally {
    moduleStates[apiKey].loading = false;
  }
}

watch(
  dateRange,
  async () => {
    await Promise.all([
      fetchChartData("turnoverStatistics"),
      fetchChartData("userStatistics"),
      fetchChartData("ordersStatistics"),
      fetchChartData("top10"),
    ]);
  },
  { immediate: true }
);

defineOptions({
  name: "Statistics",
});
</script>

<template>
  <div class="statistics">
    <el-row v-stagger class="select-time-range-row mb-4">
      <el-col :xl="18" :lg="18" :md="18" :sm="24" :xs="24">
        <div class="select-time-range flex items-center gap-4">
          <el-radio-group v-model="selectedRange">
            <el-radio-button
              v-for="range in timeRanges"
              :key="range.value"
              :label="range.label"
              :value="range.value"
            />
          </el-radio-group>

          <div class="text-sm text-[var(--el-text-color-primary)]">
            已选时间：{{ displayDateRange }}
          </div>
        </div>
      </el-col>

      <el-col :xl="6" :lg="6" :md="6" :sm="24" :xs="24" class="text-right">
        <el-button @click="handleExport"
          ><el-icon><Printer /></el-icon>
          <span>数据导出</span>
        </el-button>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="gap-y-20px">
      <el-col
        v-for="(chart, index) in chartsList"
        :key="chart.apiKey"
        :span="12"
        :xl="12"
        :md="12"
        :lg="12"
        :sm="12"
        :xs="24"
      >
        <BaseCard
          v-stagger:[index]
          v-bind="chart.cardProps"
          headerClass="mb-0!"
        >
          <div
            style="height: 18.75rem"
            element-loading-text="🦀图表加载中..."
            v-loading="moduleStates[chart.apiKey].loading"
          >
            <BaseEcharts
              v-bind="chart.chartsProps"
              :options="moduleStates[chart.apiKey].options"
            />
          </div>
        </BaseCard>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.statistics {
  .select-time-range-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

@media (max-width: 1090px) {
  .statistics {
    .select-time-range-row {
      gap: 10px;
      flex-direction: column-reverse;
      align-items: flex-start;

      .select-time-range {
        flex-direction: column-reverse;
        align-items: flex-start;
      }
    }
  }
}
</style>

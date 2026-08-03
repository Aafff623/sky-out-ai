<script lang="ts" setup>
import { CSSProperties } from "vue";
import { useEcharts } from "@/hooks/useEcharts";
import { useSettingsStore } from "@/store/modules/settings";

const props = defineProps({
  autoTheme: { type: Boolean, default: false },
  autoResize: { type: Boolean, default: true },
  width: { type: [Number, String], default: "100%" },
  height: { type: [Number, String], default: "100%" },
  options: { type: Object as PropType<EChartsCoreOption> },
  themeColors: { type: Array as PropType<string[]>, default: () => [] },
  showEmptyTip: { type: Boolean, default: true },
  emptyTipProps: {
    type: Object as PropType<BaseEmptyTipType>,
    default: () => ({
      emptyText: "🐱‍🐉图标数据为空哦~",
    }),
  },
});

const attrs = useAttrs();
const { theme } = storeToRefs(useSettingsStore());
const JEcharts = ref<HTMLElement>();
const tempChartOptions = ref<EChartsCoreOption>();

const { isInitialized, setOptions, initCharts, resize } = useEcharts(
  JEcharts,
  props.options!
);

const isEmptySeries = computed(() => {
  const series = props.options?.series;
  return !series || (Array.isArray(series) && series.length === 0);
});

const chartSizes = computed(() => ({
  width: typeof props.width === "number" ? `${props.width}px` : props.width,
  height: typeof props.height === "number" ? `${props.height}px` : props.height,
}));

const chartStyle = computed(() => ({
  width: chartSizes.value.width,
  height: chartSizes.value.height,
  ...(attrs.style as CSSProperties),
}));

watch(
  () => props.options,
  async (nVal) => {
    let targetOptions: EChartsCoreOption = {};
    if (props.themeColors && props.themeColors.length > 0) {
      targetOptions = { ...nVal };
      targetOptions.color = props.themeColors;
    } else {
      targetOptions = { ...nVal };
    }

    await nextTick();
    initData(); // 重新初始化图表
    setOptions({ ...targetOptions });
    tempChartOptions.value = { ...targetOptions };
  },
  { deep: true, immediate: true }
);

function initData() {
  if (isInitialized.value || isEmptySeries.value) return;
  initCharts(props.autoTheme ? theme.value : undefined);
}

const handleResize = (size: ResizeCallbackParams) => {
  props.autoResize &&
    resize({
      width: size.width,
      height: size.height,
      animation: {
        easing: "cubicOut",
        duration: 300,
      },
    });
};

onMounted(initData);

defineExpose({ resize });

defineOptions({
  name: "BaseEcharts",
});
</script>

<template>
  <div
    v-if="!isEmptySeries"
    ref="JEcharts"
    v-bind="$attrs"
    class="base-echarts"
    :style="chartStyle"
    v-resize="handleResize"
  />

  <BaseEmptyTip
    v-bind="emptyTipProps"
    v-else-if="!isEmptySeries && showEmptyTip"
  />
</template>

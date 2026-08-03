import { isEmpty } from "lodash-es";
import echarts from "@/plugins/echarts";

/**
 * echarts hooks
 * @param elRef dom ref
 * @param options echarts options
 */
const useEcharts = (
  elRef: Ref<HTMLElement | undefined>,
  options: EChartsCoreOption
) => {
  const isInitialized = ref(false);
  const charts = shallowRef<echarts.ECharts | null>();

  const setOptions = (options: EChartsCoreOption) => {
    if (isEmpty(options) || !charts.value) return;
    charts.value.setOption(options);
  };

  // 初始化
  const initCharts = (theme?: string, otherOptions?: EChartsCoreOption) => {
    const el = unref(elRef);
    if (!el || !unref(el) || isInitialized.value) return;

    charts.value = echarts.init(el, theme);
    if (otherOptions?.color) {
      options.color = otherOptions?.color;
    }
    isInitialized.value = true;
    setOptions(options);
  };

  // 销毁
  const dispose = () => {
    charts.value?.dispose?.();
    isInitialized.value = false;
    charts.value = null;
  };

  // 判断是否有实例
  const getInstance = (target?: HTMLElement) => {
    if (!charts.value || !target) return null;

    return echarts?.getInstanceByDom?.(target);
  };

  onUnmounted(dispose);

  // 重新窗口变化时，重新计算
  const resize = (options?: echarts.ResizeOpts) => {
    charts.value?.resize?.(options);
  };

  return {
    isInitialized,
    initCharts,
    setOptions,
    resize,
    dispose,
    getInstance,
  };
};

export { useEcharts };

import type { App } from "vue";
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口
import * as echarts from "echarts/core";
// 引入内置组件，组件后缀都为Component
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  PolarComponent,
  AriaComponent,
  ParallelComponent,
  LegendComponent,
  RadarComponent,
  ToolboxComponent,
  DatasetComponent, // 数据集组件
  DataZoomComponent,
  VisualMapComponent,
  TimelineComponent,
  CalendarComponent,
  GraphicComponent,
  TransformComponent, // 数据转换器组件(filter, sort)
} from "echarts/components";
// 引入渲染器：echarst默认使用canvas渲染，引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import { CanvasRenderer, SVGRenderer } from "echarts/renderers";
// 标签自动布局、全局过渡动画等特性
import { LabelLayout, UniversalTransition } from "echarts/features";
// 引入图表类型，后缀都为Chart
import {
  BarChart,
  LineChart,
  PieChart,
  MapChart,
  RadarChart,
  PictorialBarChart,
} from "echarts/charts";

echarts.use([
  // 内置组件
  TitleComponent,
  TooltipComponent,
  GridComponent,
  PolarComponent,
  AriaComponent,
  ParallelComponent,
  LegendComponent,
  RadarComponent,
  ToolboxComponent,
  DatasetComponent,
  DataZoomComponent,
  VisualMapComponent,
  TimelineComponent,
  CalendarComponent,
  GraphicComponent,
  TransformComponent,
  // 渲染器
  CanvasRenderer,
  SVGRenderer,
  // 特性
  LabelLayout,
  UniversalTransition,
  // 图表
  BarChart,
  LineChart,
  PieChart,
  MapChart,
  RadarChart,
  PictorialBarChart,
]);

// 全局注册echarts
export function setupEcharts(app: App) {
  app.config.globalProperties.$echarts = echarts;
}

export default echarts;

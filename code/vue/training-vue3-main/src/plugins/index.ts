import type { App } from "vue";
import { setupStore } from "@/store";
import { setupRouter } from "@/router";
import { setupI18n } from "@/lang";
import { setupElIcons } from "./icons";
import { setupPermission } from "./permission";
import { setupDirective } from "@/directive";
import { setupEcharts } from "./echarts";
// import { setupBaseComponent } from "@/components/Base";

export default {
  install(app: App<Element>) {
    // 注册 store
    setupStore(app);
    // 注册路由
    setupRouter(app);
    // 国际化
    setupI18n(app);
    // Element-Plus 图标
    setupElIcons(app);
    // 路由守卫
    setupPermission();
    // echarts 图表
    setupEcharts(app);
    // 基础异步组件
    // setupBaseComponent(app);
    // 自定义指令
    setupDirective(app);
  },
};

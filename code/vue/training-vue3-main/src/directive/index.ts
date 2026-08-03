import type { App } from "vue";
import { slideIn } from "./slideIn";
import { resize } from "./resize";
import { stagger } from "./stagger";

// 全局注册 directive
export function setupDirective(app: App<Element>) {
  // 使 v-slide-in 在所有组件中都可用
  app.directive("slide-in", slideIn);
  app.directive("resize", resize);
  app.directive("stagger", stagger);
}

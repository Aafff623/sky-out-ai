import type { App } from "vue";
import { stagger } from "./stagger";

// 全局注册 directive
export function setupDirective(app: App<Element>) {
  app.directive("stagger", stagger);
}

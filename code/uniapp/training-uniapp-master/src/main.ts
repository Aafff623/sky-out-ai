import { createSSRApp } from "vue";
import setupPlugins from "@/plugins";
import { store as piniaStore } from "@/store";
import App from "./App.vue";
import uviewPlus from "uview-plus";

import "virtual:uno.css"; // 使用虚拟模块引入样式

export function createApp() {
  const app = createSSRApp(App);
  app.use(setupPlugins);
  app.use(uviewPlus);
  return {
    app,
    piniaStore, // 此处必须将 Pinia 返回
  };
}

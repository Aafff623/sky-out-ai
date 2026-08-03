import type { App } from "vue";
import { setupStore } from "@/store";
import { setupGlobalMethods } from "./methods";
import { setupDirective } from "@/directive";

export default {
  install(app: App<Element>) {
    // 注册 store
    setupStore(app);
    // 批量注册全局函数
    setupGlobalMethods(app);
    // 自定义指令
    setupDirective(app);
  },
};

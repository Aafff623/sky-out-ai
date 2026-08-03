import type { App } from "vue";
import { createPinia } from "pinia";
import { createUnistorage } from "pinia-plugin-unistorage";

const store = createPinia();

// 使用 pinia-plugin-unistorage 插件进行本地数据持久化
store.use(createUnistorage());

export function setupStore(app: App<Element>) {
  app.use(store);
}

export * from "./modules/user";
export * from "./modules/shop";
export * from "./modules/order-countdown";
export * from "./modules/shopping-cart";

export { store };

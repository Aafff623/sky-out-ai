import { App } from "vue";

// 动态导入所有Base目录下的.vue文件
const components = import.meta.glob('../Base/**/*.vue');

export function setupBaseComponent(app: App<Element>) {
  Object.keys(components).forEach((key) => {
    // 动态生成组件名称
    const componentName = key
      .replace("./", "Base/") // 所有动态异步组件都以Base开头
      .replace("/index.vue", "")
      .replace(".vue", "")
      .replace(/\//g, "");

    // 注册异步组件
    app.component(
      componentName, 
      defineAsyncComponent(components[key] as () => Promise<any>)
    );
  });
}

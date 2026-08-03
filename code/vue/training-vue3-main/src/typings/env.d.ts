/// <reference types="vite/client" />
import * as echarts from "echarts/core";

// 解决ts无法识别.vue文件
declare module "*.vue" {
  import { DefineComponent } from "vue";
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

interface ImportMetaEnv {
  /* 应用标题 */
  VITE_APP_TITLE: string;
  /* 应用端口 */
  VITE_APP_PORT: number;
  /* API基础路径(反向代理) */
  VITE_APP_BASE_API: string;
}

// 声明 env 为环境变量类型 ImportMetaEnv
interface ImportMeta {
  readonly env: ImportMetaEnv;
}

// 全局类型定义，就是挂载到app.config.globalProperties上的类型
declare module "vue" {
  interface ComponentCustomProperties {
    /** 全局echarts */
    $echarts: typeof echarts;
  }
}

/**
 * 平台的名称、版本、运行所需的`node`版本、依赖、构建时间的类型提示
 */
// declare const __APP_INFO__: {
//   pkg: {
//     name: string;
//     version: string;
//     engines: {
//       node: string;
//     };
//     dependencies: Record<string, string>;
//     devDependencies: Record<string, string>;
//   };
//   buildTimestamp: number;
// };

declare module "lodash-es";

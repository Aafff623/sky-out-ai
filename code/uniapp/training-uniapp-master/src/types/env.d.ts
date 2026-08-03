/// <reference types="vite/client" />

// 识别 .vue 文件
declare module "*.vue" {
  import { DefineComponent } from "vue";
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

// 地址解析工具（js库，无类型声明文件）
declare module "address-parse" {
  /** 解析地址 */
  function parse(address: string): AddressParseData[];
}

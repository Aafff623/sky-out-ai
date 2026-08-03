/// <reference types='@dcloudio/types' />

// uni-app 类型声明文件 
declare module "@vue/runtime-core" {
  type Hooks = App.AppInstance & Page.PageInstance;
  interface ComponentCustomOptions extends Hooks {}
}

export {};

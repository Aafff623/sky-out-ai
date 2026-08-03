import { ComponentCustomProperties } from "vue";
import type { NavType } from "@/core/app";

// 全局方法类型定义
declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    /** 显示纯文字提示框 */
    $toast: (message: any, duration?: number, mask?: boolean) => void;
    /** 跳转到指定页面url */
    $navTo: <T extends Record<string, any>>(
      url: string,
      query?: T = {} as T,
      modo?: NavType
    ) => boolean;
    /** uview-plus 实例对象 */
    $u: any;
    $request: any;
    /** 登录状态检查 */
    $isLogin: () => Promise<boolean>;
  }
}

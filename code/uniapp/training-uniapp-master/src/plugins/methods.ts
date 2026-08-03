import type { App } from "vue";
import {
  navTo,
  showToast,
  showSuccess,
  showError,
  checkLogin,
  isLogin
} from "@/core/app";

// 声明全局方法对象类型
interface GlobalMethods {
  $navTo: typeof navTo;
  $toast: typeof showToast;
  $showSuccess: typeof showSuccess;
  $showError: typeof showError;
  $checkLogin: typeof checkLogin;
  $isLogin: typeof isLogin;
}

// 导出类型声明
export type { GlobalMethods };

export function setupGlobalMethods(app: App) {
  // 定义全局函数对象
  const globalMethods = {
    $toast: showToast,
    $navTo: navTo,
    $showSuccess: showSuccess,
    $showError: showError,
    $checkLogin: checkLogin,
    $isLogin: isLogin
  };

  // 批量挂载到全局
  Object.entries(globalMethods).forEach(([key, method]) => {
    app.config.globalProperties[key] = method;
  });
}

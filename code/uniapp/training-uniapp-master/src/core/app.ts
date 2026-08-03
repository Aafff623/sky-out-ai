import * as util from "@/utils/util";

/**
 * 显示成功提示框
 */
export const showSuccess = (msg: any, callback: Function) => {
  uni.showToast({
    title: msg,
    icon: "success",
    mask: true,
    duration: 1500,
    success() {
      callback && callback();
    },
  });
};

/**
 * 显示失败提示框
 */
export const showError = (msg: any, callback: Function) => {
  uni.showModal({
    title: "友情提示",
    content: msg,
    showCancel: false,
    success() {
      callback && callback();
    },
  });
};

/**
 * 显示纯文字提示框
 */
export const showToast = (msg: any, duration = 1500, mask = true) => {
  uni.showToast({
    title: msg, // 提示的内容
    icon: "none",
    mask, // 是否显示透明蒙层，防止触摸穿透 (支付宝小程序不支持)
    duration, // 提示的延迟时间，单位毫秒，默认：1500
  });
};

/**
 * 验证是否已登录（通过用户登录的userId）
 */
export const checkLogin = () => {
  return !!uni.getStorageSync("token");
};

/**
 * 校验是否登录
 */
export function isLogin(): Promise<boolean> {
  return new Promise((resolve, reject) => {
    if (checkLogin()) {
      resolve(true);
    } else {
      // 没有登录，延迟 500 毫秒后提示用户登录
      setTimeout(() => {
        uni.showModal({
          title: "🦀温馨提示",
          confirmText: "登录",
          content: "此时此刻，您需要先进行登录哦",
          success(res) {
            if (res.confirm) {
              navTo("pages/login/index");
            } else if (res.cancel && getCurrentPages().length > 1) {
              uni.navigateBack();
            }
            reject(false);
          },
        });
      }, 500);
    }
  });
}

/**
 * tabBar页面路径列表 (用于链接跳转时判断)
 * tabBarLinks为常量, 无需修改
 */
export const getTabBarLinks = () => {
  const tabBarLinks = ["pages/index/index", "pages/message/index", "pages/user/index"];
  return tabBarLinks;
};

export type NavType = "navigateTo" | "redirectTo";
/**
 * 跳转到指定页面url
 * 支持tabBar页面
 * @param {string}  url   页面路径
 * @param {object}  query 页面参数
 * @param {string}  modo  跳转类型(默认navigateTo)
 */
export const navTo = <T extends Record<string, any>>(
  url: string,
  query: T = {} as T,
  modo: NavType = "navigateTo"
) => {
  if (!url || url.length == 0) {
    return false;
  }
  // tabBar页面, 使用switchTab
  if (util.inArray(url, getTabBarLinks())) {
    uni.switchTab({
      url: `/${url}`,
    });
    return true;
  }
  // 生成query参数
  const queryStr = !util.isEmpty(query) ? "?" + util.urlEncode(query) : "";
  // 普通页面, 使用navigateTo
  modo === "navigateTo" &&
    uni.navigateTo({
      url: `/${url}${queryStr}`,
    });
  // 特殊指定, 使用redirectTo
  modo === "redirectTo" &&
    uni.redirectTo({
      url: `/${url}${queryStr}`,
    });
  return true;
};

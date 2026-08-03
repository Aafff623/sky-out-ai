import request from "./core/request";
import type { RequestError } from "./core/types";
import { useUserStoreHook } from "@/store";

const userStore = ref();

setTimeout(() => {
  userStore.value = useUserStoreHook();
}, 2000);

// 后端api地址
const apiUrl = import.meta.env.VITE_APP_API_URL;

const $http = new request({
  baseUrl: apiUrl,
  header: {
    "content-type": "application/json;charset=utf-8",
  },
  timeout: 30000, // 增加到30秒，适应AI接口响应时间
  // 默认配置（可不写）
  config: {
    // 是否自动提示错误
    isPrompt: true,
    // 是否显示加载动画
    load: true,
    // 是否使用数据工厂
    isFactory: true,
  },
});

// 当前接口请求数
let requestNum = 0;

// 请求拦截器
$http.requestStart = (options) => {
  if (options.load) {
    if (requestNum <= 0) {
      // 打开加载动画
      uni.showLoading({
        title: "🐠加载中",
        mask: true,
      });
    }
    requestNum += 1;
  }

  options.header["authentication"] = uni.getStorageSync("token");
  // return false; // 表示请求拦截，不会继续请求
  return options;
};

// 请求结束
$http.requestEnd = (options) => {
  // 判断当前接口是否需要加载动画
  if (options.load) {
    requestNum = requestNum - 1;
    if (requestNum <= 0) {
      uni.hideLoading();
    }
  }
};

// 当前是否显示modal
let loginModal = false;

$http.dataFactory = async (res) => {
  const whiteCodeList = [404, 401];
  const { code } = res.response;

  if (!code || (code !== 200 && !whiteCodeList.includes(code))) {
    // 返回错误的结果(catch接受数据)
    return Promise.reject({
      code: code || 0,
      data: null,
      msg: res.response.msg || "http状态码错误【code为undefined】",
    });
  }

  let responseData = res.response.data;
  // console.log("响应数据", responseData);

  if (typeof responseData === "string") {
    try {
      responseData = JSON.parse(responseData);
    } catch (error) {
      return Promise.reject({
        code,
        msg: "尝试JSON.parse响应数据失败",
      } as ResponseData);
    }
  }

  if (code === 200) {
    // 返回正确的结果(then接受数据)
    return Promise.resolve(responseData);
  }

  // 判断是否需要登录
  if (code === 401) {
    // 401也有可能是后端登录态到期, 所以要清空本地的登录状态
    setTimeout(() => userStore.value?.logout(), 2100);

    // 防止重复弹窗
    if (!loginModal) {
      loginModal = true;
      // 弹窗告诉用户去登录
      uni.showModal({
        title: "🐔温馨提示",
        content: "此时此刻需要您登录喔~",
        confirmText: "去登录",
        cancelText: "再逛会",
        success: (res) => {
          if (res.confirm) {
            uni.navigateTo({
              url: "/pages/login/index",
            });
          }
          if (res.cancel && getCurrentPages().length > 1) {
            uni.navigateBack();
          }
          loginModal = false;
        },
      });
    }
    // 返回错误的结果(catch接受数据)
    return Promise.reject({
      code: 0,
      msg: res.response.msg,
      data: responseData,
    } as ResponseData);
  }

  // 其他错误提示
  if (code === 500) {
    if (res.isPrompt) {
      setTimeout(() => {
        uni.showToast({
          title: res.response.msg || "未知错误",
          icon: "none",
          duration: 2500,
        });
      }, 10);
    }
    // 返回错误的结果(catch接受数据)
    return Promise.reject({
      statusCode: 0,
      errMsg: res.response.msg || "未知错误",
      result: res.data,
    });
  }

  // 其他错误提示
  return Promise.reject({
    code: 0,
    msg: "未知错误",
    data: responseData,
  } as ResponseData);
};

// 错误回调
$http.requestError = (e: any) => {
  if (e.statusCode === 0 || e.code === 0) {
    throw e;
  } else {
    setTimeout(() => showRequestError(e), 10);
  }
};

// 显示请求错误信息
const showRequestError = (e: RequestError) => {
  let errMsg = `请求出错：${e.msg || e.errMsg || "未知错误"}`;
  // #ifdef MP-WEIXIN
  if (e.errMsg?.includes("request:fail url not in domain list")) {
    errMsg = "当前API域名未添加到微信小程序授权名单 " + e.errMsg;
  }
  // #endif
  if (e.errMsg?.includes("request:fail")) {
    errMsg = "网络请求错误：请检查api地址能否访问正常";
  }
  uni.showToast({
    title: errMsg,
    icon: "none",
    duration: 3500,
  });
};

export default $http;

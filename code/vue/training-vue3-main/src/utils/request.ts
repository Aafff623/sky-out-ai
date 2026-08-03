import qs from "qs";
import { debounce } from "lodash-es";
import { ElNotification } from "element-plus";
import axios, { InternalAxiosRequestConfig, AxiosResponse } from "axios";
import { TOKEN_KEY } from "@/enums/CacheEnum";
import { useUserStoreHook } from "@/store/modules/user";

const debounceFn = debounce(() => {
  ElNotification({
    title: "提示",
    message: "登录失效，请重新登录",
    type: "error",
  });
  useUserStoreHook()
    .resetToken()
    .then(() => location.reload());
}, 500);

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
  // 对象参数序列化，例如：{ a: 1, b: 2 } 会被转换为 a=1&b=2
  paramsSerializer: (params) => {
    return qs.stringify(params);
  },
});

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem(TOKEN_KEY);
    // 是否存在token
    if (accessToken) {
      // 存在token，则在请求头中添加token
      config.headers.token = accessToken;
    }
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 处理 blob 类型数据
    if (response.config.responseType === "blob") {
      // 检查是否为错误响应（根据content-type判断）
      if (response.headers["content-type"]?.includes("application/json")) {
        return new Promise((_, reject) => {
          const reader = new FileReader();
          reader.onload = () => {
            try {
              const errorData = JSON.parse(reader.result as string);
              ElMessage.error(errorData.msg || "导出失败");
              reject(new Error(errorData.msg));
            } catch (e) {
              ElMessage.error("导出错误");
              reject(new Error("导出错误"));
            }
          };
          reader.readAsText(response.data);
        });
      }

      return response;
    }

    const { code, msg } = response.data as ResponseData;

    if (code === 200) {
      return response.data;
    }

    ElMessage.error(msg || "未知异常");
    return Promise.reject(new Error(msg || "Error"));
  },
  (error: any) => {
    if (error.response.data) {
      const { code, msg } = error.response.data;
      if (code === 401) {
        debounceFn();
      } else {
        ElMessage.error(msg || "未知异常");
      }
    }
    return Promise.reject(error.message);
  }
);

// 导出 axios 实例
export default service;

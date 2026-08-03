import EmployeeAPI, { LoginData, UserInfo, } from "@/api/employee";
import { resetRouter } from "@/router";
import { store } from "@/store";
import { TOKEN_KEY } from "@/enums/CacheEnum";
import { EmployeeEnum } from "@/enums/EmployeeEnum"

export const useUserStore = defineStore("user", () => {
  // const userInfo = ref<any>(null);
  /** 用户登录 */
  function login(loginData: LoginData) {
    return new Promise<void>((resolve, reject) => {
      EmployeeAPI.login(loginData)
        .then((res) => {
          const { token } = res.data as any;
          token && localStorage.setItem(TOKEN_KEY, token);
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /** 用户退出 */
  function logout() {
    return new Promise<void>((resolve, reject) => {
      EmployeeAPI.logout()
        .then(() => {
          localStorage.setItem(TOKEN_KEY, "");
          localStorage.removeItem(EmployeeEnum.employeeName);
          location.reload(); // 清空路由
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /** 移除token */
  function resetToken() {
    console.log("resetToken");
    return new Promise<void>((resolve) => {
      localStorage.setItem(TOKEN_KEY, "");
      resetRouter();
      resolve();
    });
  }

  function getInfo() {
    return new Promise<UserInfo>((resolve, reject) => {
      EmployeeAPI.getInfo()
        .then((res) => {
          const { name } = res.data;
          name && localStorage.setItem(EmployeeEnum.employeeName, name);
          resolve(res.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  return {
    login,
    logout,
    resetToken,
    getInfo,
    // userInfo,
  };
},);

export function useUserStoreHook() {
  return useUserStore(store);
}

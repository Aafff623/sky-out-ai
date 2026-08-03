import { store } from "@/store";
import UserAPI, { type UserVO, UserLoginVO } from "@/api/user";
import ShopAPI, { type ShopInfoVO } from "@/api/shop";

export const useUserStore = defineStore("user", () => {
  // 用户认证token
  const token = ref<string>();
  // 用户ID
  const userId = ref<number | null>();
  const userInfo = ref<UserVO>();
  const shopInfo = ref<ShopInfoVO>({});

  const getShopRecord = computed(() => {
    return uni.getStorageSync("shopInfo") as ShopInfoVO;
  });

  /**
   * 微信授权登录
   * @param code 微信登录code
   */
  async function loginMpWx(code: string): Promise<ResponseData<UserLoginVO>> {
    try {
      const loginRes = await UserAPI.login(code, { isPrompt: false });

      // 存储基础登录信息
      token.value = loginRes.data.token;
      userId.value = loginRes.data.id;
      uni.setStorageSync("token", token.value);
      uni.setStorageSync("userId", userId.value);

      // 并行获取用户信息和商家信息
      await Promise.all([getUserInfo(), getShopInfo()]);

      return loginRes;
    } catch (error) {
      await logout();
      throw error;
    }
  }

  /**
   * 获取商家信息
   * @returns 商家信息
   */
  async function getShopInfo() {
    try {
      const res = await ShopAPI.getShopInfo();
      shopInfo.value = res.data;
      uni.setStorageSync("shopInfo", shopInfo.value);
    } catch (error) {
      throw new Error("商家信息获取失败");
    }
  }

  /**
   * 强制刷新商家信息（清除缓存后重新获取）
   */
  async function refreshShopInfo() {
    try {
      // 清除本地缓存
      uni.removeStorageSync("shopInfo");
      // 重新获取
      await getShopInfo();
    } catch (error) {
      throw new Error("刷新商家信息失败");
    }
  }

  /**
   * 获取用户信息
   * @returns 用户信息
   */
  function getUserInfo() {
    return new Promise<ResponseData<UserVO>>((resolve, reject) => {
      UserAPI.info()
        .then((res) => {
          userInfo.value = res.data;
          uni.setStorageSync("userInfo", userInfo.value);
          resolve(res);
        })
        .catch(reject);
    });
  }

  // 退出登录方法
  async function logout() {
    try {
      // 清除本地存储
      ["token", "cart", "userId", "userInfo", "shopInfo"].forEach((key) => {
        uni.removeStorageSync(key);
      });

      // 重置状态
      token.value = "";
      userId.value = null;
      userInfo.value = undefined;
    } catch (error) {
      throw error;
    }
  }

  return {
    token,
    userId,
    getShopRecord,
    logout,
    loginMpWx,
    getShopInfo,
    getUserInfo,
  };
});

export function useUserStoreHook() {
  return useUserStore(store);
}

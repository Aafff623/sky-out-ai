import { store } from "@/store";
import ShopAPI from "@/api/shop";
import { ShopStatusEnum } from "@/enums/StatusEnum";

export const useShopStore = defineStore(
  "shop",
  () => {
    // 开始营业时间
    const startTime = ref<string>("08:00");
    // 结束营业时间
    const endTime = ref<string>("21:00");
    // 店铺营业状态
    const shopStatus = ref<ShopStatusEnum>(ShopStatusEnum.RESTAURANT);

    /** 是否正在营业中 */
    const isOperating = computed(() => {
      return shopStatus.value === ShopStatusEnum.IN_OPERATION;
    });

    /** 是否在营业时间内 */
    const isInOperatingTime = computed(() => {
      const currentTime = new Date();
      const currentHours = currentTime.getHours();
      const currentMinutes = currentTime.getMinutes();

      // 将 startTime 和 endTime 转换为小时和分钟
      const [startHours, startMinutes] = startTime.value.split(":").map(Number);
      const [endHours, endMinutes] = endTime.value.split(":").map(Number);

      // 比较当前时间是否在营业时间内
      const isAfterStartTime =
        currentHours > startHours ||
        (currentHours === startHours && currentMinutes >= startMinutes);
      const isBeforeEndTime =
        currentHours < endHours ||
        (currentHours === endHours && currentMinutes <= endMinutes);

      return isAfterStartTime && isBeforeEndTime;
    });

    /**
     * 获取店铺营业状态
     * @returns 店铺营业状态: 1-营业中 0-打样中
     */
    function getShopStatus() {
      return new Promise<ResponseData<ShopStatusEnum>>((resolve, reject) => {
        ShopAPI.getStatus()
          .then((res) => {
            shopStatus.value = res.data;
            resolve(res);
          })
          .catch((err) => {
            shopStatus.value = ShopStatusEnum.RESTAURANT;
            reject(err);
          });
      });
    }

    return {
      startTime,
      endTime,
      isOperating,
      shopStatus,
      isInOperatingTime,
      getShopStatus,
    };
  },
  {
    // unistorage: true, // 开启后对 state 的数据读写都将持久化（配置了这句，下面的代码其实已经是多余的了...）
    // 持久化配置
    unistorage: {
      key: "shopStore", // 本地存储的 key 值
      storage: {
        // 使用uniapp方式的本地存储
        getItem: (key) => uni.getStorageSync(key), // 获取key使用uniapp方式的本地存储
        setItem: (key, value) => uni.setStorageSync(key, value), // 设置key使用uniapp方式的本地存储
      },
      paths: ["shopStatus", "startTime", "endTime"], // 需要持久化的字段
    },
  }
);

export function useShopStoreHook() {
  return useShopStore(store);
}

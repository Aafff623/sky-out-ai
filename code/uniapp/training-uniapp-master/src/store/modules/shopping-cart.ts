import { store } from "@/store";
import ShoppingCartAPI, {
  ShoppingCart,
  ShoppingCartDTO,
} from "@/api/shopping-cart";
import { shopInfo } from "@/settings";
import { onShow } from "@dcloudio/uni-app";
import { isEqual } from "lodash";
import { DishVO } from "@/api/dish";
import { CategoryTypeEnum } from "@/enums/CategoryEnum";

const CART_KEY = "cart";

export const useShoppingCartStore = defineStore("shoppingCart", () => {
  // 购物车列表
  const list = ref<ShoppingCart[]>(uni.getStorageSync(CART_KEY) || []);
  // 总商品数量
  const totalNum = ref<number>(0);
  // 总价格
  const totalPrice = ref<number>(0);

  // 购物车中是否有商品
  const isCartNotEmpty = computed(() => list.value.length > 0);

  // 基础总价
  const baseTotalPrice = computed(() => {
    return totalPrice.value.toFixed(2);
  });

  // 精确的总价格
  const exactTotalPrice = computed(() => {
    return (
      Math.round(
        (totalPrice.value +
          shopInfo.balingCharges +
          shopInfo.deliveryCharges +
          Number.EPSILON) *
        100
      ) / 100
    ).toFixed(2);
  });

  onShow(() => {
    updateCart(list.value);
  });

  watch(
    () => list.value,
    (newVal, oldVal) => {
      if (!isEqual(newVal, oldVal)) {
        updateCart(newVal);
      }
    },
    { deep: true, immediate: true }
  );

  function updateTotalPrice(newPrice: number) {
    totalPrice.value = newPrice;
  }

  /**
   * 添加到购物车
   * @param 添加到购物车的商品信息
   */
  function addCart(item: ShoppingCartDTO) {
    return new Promise<ResponseData<null>>((resolve, reject) => {
      // 发起网络请求，添加到购物车
      ShoppingCartAPI.add({ ...item })
        .then((res) => {
          // 获取本地购物车列表，如果为空则初始化为空数组
          list.value = (uni.getStorageSync(CART_KEY) || []) as ShoppingCart[];

          // 检查购物车是否为空
          if (list.value.length === 0) {
            // 如果购物车为空，直接添加商品
            list.value.push(item);
            uni.setStorageSync(CART_KEY, list.value);
            resolve(res);
            return;
          }

          const itemId = item.dishId ?? item.setmealId;
          // 查找购物车中是否已经存在该商品
          const i = list.value.findIndex(
            (v) => itemId === v.dishId && item.dishFlavor === v.dishFlavor
          );

          if (i >= 0) {
            // 如果商品已存在，则更新商品数量
            list.value[i].number = item.number;
            // 如果数量小于等于0，则从购物车移除该商品
            if (list.value[i].number! <= 0) {
              list.value.splice(i, 1);
            }
          } else {
            // 如果商品不存在，且数量大于0，则添加到购物车列表
            if (item.number! > 0) {
              list.value.push(item);
            }
          }

          // 同步更新到本地存储
          uni.setStorageSync(CART_KEY, list.value);
          resolve(res);
        })
        .catch((error) => {
          // 错误处理，打印日志并且返回 reject
          console.error("添加到购物车失败：", error);
          reject(error);
        });
    });
  }

  /**
   * 更新购物车数据
   * @param {ShoppingCart[]} cart 购物车对象
   */
  function updateCart(cart: ShoppingCart[]) {
    if (cart.length) {
      totalNum.value = cart.reduce((s, c) => {
        return s + (c.number || 0);
      }, 0);

      totalPrice.value = cart.reduce((t, c) => {
        const number = (c.number ?? 0) as number;
        const amount = (c.amount ?? 0) as number;
        return t + number * amount;
      }, 0);
    } else {
      totalNum.value = 0;
      totalPrice.value = 0;
    }
  }

  /**
   * 获取购物车列表
   */
  function getCartlist() {
    return new Promise<ResponseData<ShoppingCart[]>>(async (resolve) => {
      const res = await ShoppingCartAPI.list();

      // 每次请求购物车列表成功时，同步本地数据
      if (res.code === 200 && res.data.length) {
        list.value = res.data;
        uni.setStorageSync(CART_KEY, res.data);
      }
      resolve(res);
    });
  }

  /**
   * 更新购物车中商品的数量
   */
  function updateShoppingCartNumber(item: ShoppingCartDTO) {
    return new Promise<ResponseData<null>>((resolve, reject) => {
      ShoppingCartAPI.update(item)
        .then((res) => {
          // 获取本地购物车列表
          list.value = uni.getStorageSync(CART_KEY) || [];

          const itemId = item.dishId ?? item.setmealId;
          const itemKey = item.dishId ? "dishId" : "setmealId";
          // 查找购物车中是否已有该商品
          const index = list.value.findIndex(
            (v) => v[itemKey] === itemId && v.dishFlavor === item.dishFlavor
          );

          if (index >= 0) {
            // 如果找到该商品，更新数量
            if (item.number! > 0) {
              list.value[index].number = item.number!;
            } else {
              // 如果数量为 0 或不存在，移除该商品
              list.value.splice(index, 1);
            }
          } else if (item.number! > 0) {
            // 如果商品不存在并且传入的数量大于 0，则将商品添加到购物车
            list.value.push({
              ...item,
              number: item.number!,
            });
          }

          // 同步更新本地存储
          uni.setStorageSync(CART_KEY, list.value);

          // 更新购物车总数量和总价
          updateCart(list.value);

          resolve(res);
        })
        .catch((error) => {
          console.error("更新购物车商品数量失败：", error);
          reject(error);
        });
    });
  }

  /**
   * 清空购物车
   */
  function cleanCart() {
    return new Promise<void>((resolve, reject) => {
      ShoppingCartAPI.clean()
        .then(() => {
          // 清空本地存储数据
          clearStorageCart();
          resolve();
        })
        .catch(reject);
    });
  }

  /**
   * 删除购物车中一个商品
   */
  function subCart(data: DishVO) {
    return new Promise<ResponseData<null>>((resolve, reject) => {
      const itemId =
        data.type === CategoryTypeEnum.DISH ? "dishId" : "setmealId";
      ShoppingCartAPI.sub({ [itemId]: data.id })
        .then((res) => {
          // 更新本地购物车列表
          list.value = uni.getStorageSync(CART_KEY) || [];

          // 查找购物车中该商品的索引
          const index = list.value.findIndex((v) => v[itemId] === data.id);

          if (index >= 0) {
            // 商品数量减一
            const item = list.value[index];
            item.number = (item.number || 1) - 1; // 默认1，避免出现负数

            if (item.number <= 0) {
              // 如果数量减到0，移除该商品
              list.value.splice(index, 1);
            }

            // 同步更新到本地存储
            uni.setStorageSync(CART_KEY, list.value);

            // 更新购物车的数量和总价
            // updateCart(list.value);
          }

          // 处理完成
          resolve(res);
        })
        .catch((error) => {
          console.error("从购物车中减少商品失败==>", error);
          reject(error);
        });
    });
  }

  /**
   * 清空本地存储数据
   */
  function clearStorageCart() {
    return new Promise<void>((resolve) => {
      // 重置数据
      list.value = [];
      totalNum.value = 0;
      totalPrice.value = 0;
      uni.removeStorageSync(CART_KEY);
      resolve();
    });
  }

  /**
   * 获取购物车列表（本地存储数据）
   */
  async function getCartStorage(): Promise<ShoppingCart[]> {
    return new Promise(async (resolve, reject) => {
      try {
        const data = uni.getStorageSync(CART_KEY) as ShoppingCart[];
        if (data.length) {
          return resolve(data);
        }
        if (list.value.length) {
          return resolve(list.value);
        }
        // 如果本地存储和数据都为空，请求一次购物车列表数据
        const res = await getCartlist();
        if (res.code === 200 && res.data.length) {
          return resolve(res.data);
        }
        return resolve([]);
      } catch (error) {
        reject(error);
      }
    });
  }

  /** 获取购物车列表 */
  function getList() {
    return list.value || [];
  }

  /**
   * 根据 dishId 或 setmealId 获取对应的总数量（角标）
   * @param targetId 目标菜品ID或套餐ID
   * @returns 对应项的总数量
   */
  function getBadgeByItemId(targetId?: number) {
    return getList().reduce((total, item) => {
      if (item.dishId === targetId || item.setmealId === targetId) {
        return total + (item.number || 0);
      }
      return total;
    }, 0);
  }

  return {
    isCartNotEmpty,
    totalNum,
    totalPrice,
    exactTotalPrice,
    baseTotalPrice,
    updateTotalPrice,
    getList,
    cleanCart,
    clearStorageCart,
    updateCart,
    subCart,
    addCart,
    getCartStorage,
    getCartlist,
    getBadgeByItemId,
    updateShoppingCartNumber,
  };
});

export function useShoppingCartStoreHook() {
  return useShoppingCartStore(store);
}

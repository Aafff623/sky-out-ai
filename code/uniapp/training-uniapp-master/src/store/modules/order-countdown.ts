import mitt from "mitt";
import * as _ from "lodash";
import { store } from "@/store";
import OrderAPI, { OrderVO } from "@/api/order";
import { shopInfo } from "@/settings";
import { OrderStatus } from "@/enums/OrderEnum";

type CancelCallback = (orderId: OrderVO["id"]) => void;

/** 订单倒计时任务对象 */
interface TimerTask {
  /** 订单对象 */
  order: OrderVO;
  /** 倒计时定时器id */
  intervalId: ReturnType<typeof setInterval>;
  /** 订单剩余时间 */
  remainingSeconds: number;
  /** 重试次数 */
  retryCount: number;
  /** 取消事件回调函数 */
  callbacks: CancelCallback[];
}

// 事件总线
const emitter = mitt<{ "order:cancelled": OrderVO["id"] }>();
const MAX_RETRY_COUNT = 2;
const ORDER_AUTO_CANCEL_REASON = "订单超时，已自动取消！";
// 定义超时时间
const TOTAL_SECONDS = shopInfo.remainingPaymentTime * 60;

/**
 * 订单倒计时管理store
 * @example 使用示例
 * const orderCountdownStore = useOrderCountdownStore()
 * onMounted(() => {
 *   if (props.order.status === OrderStatus.PENDING_PAYMENT) {
 *     orderCountdownStore.addOrder(props.order)
 *     // 内部监听状态变化
 *     orderCountdownStore.watchOrderStatus(
 *       props.order.id,
 *       () => props.order.status
 *     )
 *   }
 * })
 */
export const useOrderCountdownStore = defineStore("order-countdown", () => {
  // 订单倒计时任务列表
  const tasks = ref<TimerTask[]>([]);

  // 获取剩余秒数
  const getRemainingSeconds = computed(
    () => (orderId: OrderVO["id"]) =>
      getTaskByOrderId(orderId)?.remainingSeconds ?? 0
  );

  /** 获取格式化时间，例如：03:30 */
  const getFormattedTime = computed(() => (orderId: OrderVO["id"]) => {
    const seconds = getRemainingSeconds.value(orderId);

    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${String(minutes).padStart(2, "0")}:${String(
      remainingSeconds
    ).padStart(2, "0")}`;
  });

  // 根据订单id获取指定订单倒计时任务
  const getTaskByOrderId = (orderId: OrderVO["id"]) => {
    if (tasks.value.length === 0 || !orderId) return void 0;
    return tasks.value.find((task) => task.order.id === orderId);
  };

  // 是否应该跳过添加订单到倒计时
  function shouldSkipAdd(order: OrderVO) {
    return (
      _.isEmpty(order) ||
      order.status !== OrderStatus.PENDING_PAYMENT ||
      tasks.value.some((task) => task.order.id === order.id)
    );
  }

  /**
   * 添加订单到倒计时管理
   * @param order 订单对象
   * @param onCancel 订单超时自动取消成功回调
   */
  function addOrder(order: OrderVO, onCancel?: CancelCallback) {
    if (!order.id || shouldSkipAdd(order)) return;

    // 计算订单剩余时间
    const remainingSeconds = calculateRemainingSeconds(order);

    // 订单超时自动取消
    if (remainingSeconds <= 0) {
      handleAutoCancel(order.id);
      return;
    }

    // 创建对应订单id的定时器
    const intervalId = createCountdownInterval(order.id);
    // 注册新任务
    registerNewTask(order, intervalId, remainingSeconds, onCancel);
  }

  // 创建倒计时
  function createCountdownInterval(orderId: OrderVO["id"]) {
    return setInterval(() => {
      const task = getTaskByOrderId(orderId);
      if (!task) return;

      task.remainingSeconds--;
      if (task.remainingSeconds <= 0) {
        handleAutoCancel(orderId);
      }
    }, 1000);
  }

  // 新增回调注册方法
  function registerCallback(orderId: OrderVO["id"], callback: CancelCallback) {
    const task = getTaskByOrderId(orderId);
    if (task) {
      // 注册回调
      task.callbacks.push(callback);

      // 返回一个函数用来取消注册（将当前回调函数从列表中移除）
      return () => {
        task.callbacks = task.callbacks.filter((cb) => cb !== callback);
      };
    }
  }

  // 注册新任务
  function registerNewTask(
    order: OrderVO,
    intervalId: number,
    remainingSeconds: number,
    onCancel?: CancelCallback
  ) {
    const callbacks = onCancel ? [onCancel] : [];
    tasks.value.push(
      reactive({
        order: _.cloneDeep(order),
        intervalId,
        remainingSeconds,
        retryCount: 0,
        callbacks,
      })
    );
  }

  // 剩余时间计算
  function calculateRemainingSeconds(order: OrderVO) {
    const orderTime = new Date(order.orderTime!).getTime();
    const now = Date.now();
    const elapsed = Math.floor((now - orderTime) / 1000);
    return Math.max(TOTAL_SECONDS - elapsed, 0);
  }

  // 处理自动取消
  async function handleAutoCancel(orderId: OrderVO["id"]) {
    const task = getTaskByOrderId(orderId);
    if (!task) return;

    try {
      await OrderAPI.cancel(
        {
          id: orderId,
          cancelReason: ORDER_AUTO_CANCEL_REASON,
        },
        { load: false } // 超时自动取消的请求不用加载loading动画
      );

      // 更新本地订单状态（已取消），-> 无用待定删除
      task.order.status = OrderStatus.CANCELED;

      // 触发订单超时自动取消全局回调
      task.callbacks.forEach((cb) => cb(orderId));
      emitter.emit("order:cancelled", orderId);
    } catch (error) {
      console.error("自动取消订单失败==>", error);
      // 取消订单失败，自动重试
      retryCancelOrder(orderId);
    } finally {
      removeOrder(orderId); // 无论成功失败都移除
    }
  }

  // 重试取消订单
  function retryCancelOrder(orderId: OrderVO["id"]) {
    const task = getTaskByOrderId(orderId);
    if (!task) return;

    if (task.retryCount < MAX_RETRY_COUNT) {
      task.retryCount++;
      const delay = Math.pow(2, task.retryCount) * 1000; // 根据delay来决定重试间隔
      setTimeout(() => handleAutoCancel(orderId), delay);
    } else {
      console.error(`订单 ${orderId} 自动取消已达最大重试次数`);
      removeOrder(orderId);
    }
  }

  // 移除订单倒计时（只在取消或完成后调用）
  function removeOrder(orderId: OrderVO["id"]) {
    const taskIndex = tasks.value.findIndex(
      (task) => task.order.id === orderId
    );
    if (taskIndex === -1) return;

    const task = tasks.value[taskIndex];
    task.callbacks = [];
    clearInterval(task.intervalId);
    tasks.value.splice(taskIndex, 1);
  }

  // 清理所有任务的逻辑
  function clearAll() {
    tasks.value.forEach((task) => clearInterval(task.intervalId));
    tasks.value = [];
  }

  // 初始化时加载待支付订单
  function initialize(orders: OrderVO[]) {
    orders.forEach((order) => {
      if (order.status === OrderStatus.PENDING_PAYMENT) {
        addOrder(order);
      }
    });
  }

  function getCurTime() {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(
      2,
      "0"
    )}-${String(now.getDate()).padStart(2, "0")} ${String(
      now.getHours()
    ).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}:${String(
      now.getSeconds()
    ).padStart(2, "0")}`;
  }

  return {
    /** 超时时间 */
    TOTAL_SECONDS,
    ORDER_AUTO_CANCEL_REASON,
    shopTitle: shopInfo.title,
    tasks,
    initialize,
    addOrder,
    clearAll,
    removeOrder,
    registerCallback,
    getTaskByOrderId,
    getCurTime,
    /** 获取格式化时间，例如：03:30 */
    getFormattedTime,
    /** 获取剩余秒数 */
    getRemainingSeconds,
  };
});

// emitter事件总线订阅方法
export const orderCountdownEvents = {
  /**
   * 订阅订单超时全局回调
   * @param callback 订单超时自动取消全局回调
   * @returns 取消全局事件总线事件函数
   */
  subscribe: (callback: (orderId: OrderVO["id"]) => void) => {
    emitter.on("order:cancelled", callback); // 订阅订单超时全局回调
    return () => emitter.off("order:cancelled", callback); // 返回一个函数用来取消订阅
  },
};

export function useOrderCountdownStoreHook() {
  return useOrderCountdownStore(store);
}

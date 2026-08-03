import { ref, onUnmounted } from "vue";

/**
 * @param {number} minutes - 倒计时的分钟数。
 * @param {() => void} onEnd - 倒计时结束时要执行的回调函数。
 * @returns timeLeft 是剩余的秒数，formattedTime 是格式化为 "MM" 的时间字符串
 * @example const { formattedTime } = useCountdown(5); // 设置倒计时5分钟
 */
export function useCountdown(minutes: number, onEnd?: () => void) {
  const timeLeft = ref(0);
  let interval: ReturnType<typeof setInterval> | null = null;
  const COUNTDOWN_KEY = "orderCountdownEndTime"; // 本地存储的键名

  /**
   * 初始化倒计时，计算并设置剩余时间
   */
  const initializeCountdown = () => {
    const endTime = uni.getStorageSync(COUNTDOWN_KEY);
    const currentTime = Math.floor(Date.now() / 1000); // 获取当前时间戳（秒）

    if (endTime) {
      // 结束时间减去当前时间，得到剩余时间（秒）
      const remainingTime = parseInt(endTime) - currentTime;
      timeLeft.value = remainingTime > 0 ? remainingTime : 0;

      // 如果剩余时间小于等于0，直接触发倒计时结束逻辑
      if (remainingTime <= 0) {
        clearCountdownData();
        onEnd?.();
      } else {
        startCountdown();
      }
    } else {
      // 如果本地存储没有数据，初始化倒计时
      timeLeft.value = minutes * 60;
      startCountdown();
      saveEndTime();
    }
  };

  /**
   * 启动倒计时计时器函数，每秒减少一次时间。
   * 当时间到达 0 时，调用 onEnd 回调函数并停止计时器。
   */
  const startCountdown = () => {
    interval = setInterval(() => {
      if (timeLeft.value > 0) {
        timeLeft.value--;
      } else {
        stopCountdown(); // 时间到达 0 时自动停止计时
        clearCountdownData(); // 移除本地倒计时
        onEnd?.(); // 调用倒计时结束时的回调函数
      }
    }, 1000);
  };

  /**
   * 手动停止倒计时的函数，并清除计时器。
   */
  const stopCountdown = () => {
    if (interval) {
      clearInterval(interval);
      interval = null;
    }
  };

  /**
   * 保存倒计时结束时间到本地存储
   */
  const saveEndTime = () => {
    const endTime = Math.floor(Date.now() / 1000) + timeLeft.value; // 结束时间戳
    uni.setStorageSync(COUNTDOWN_KEY, endTime.toString());
  };

  /**
   * 清除本地倒计时数据
   */
  const clearCountdownData = () => {
    uni.removeStorageSync(COUNTDOWN_KEY);
  };

  /**
   * 将剩余时间格式化为 "MM:SS" 的字符串，用于显示。
   */
  const formattedTime = computed(() => {
    const mins = Math.floor(timeLeft.value / 60);
    const secs = timeLeft.value % 60;
    return `${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
  });

  /**
   * 是否有本地存储的倒计时数据
   */
  const hasCountdownData = computed(() => {
    return uni.getStorageSync(COUNTDOWN_KEY) !== undefined;
  });

  onUnmounted(() => {
    stopCountdown();
  });

  return {
    timeLeft,
    formattedTime,
    hasCountdownData,
    initializeCountdown,
    stopCountdown,
    clearCountdownData,
  };
}

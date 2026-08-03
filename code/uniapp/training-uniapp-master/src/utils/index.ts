/**
 * 根据指定的时间段和间隔计算时间区间范围
 * @param startTime 营业时间开始（格式：'08:00'）
 * @param endTime 营业时间结束（格式：'21:00'）
 * @param interval 间隔分钟数（如20分钟）
 * @returns string[] 时间段数组 ['08:00-08:20', '08:20-08:40', ..., '20:40-21:00']
 * @example 示例1（传入字符串）：
 * const intervals = calculateTimeIntervals('08:00', '21:00', 20);
 * console.log(intervals); // 结果输出：['08:00-08:20', '08:20-08:40', ..., '20:40-21:00']
 * @example 示例2（传入Date对象）：
 * const startTime = new Date();
 * startTime.setHours(14, 0); // 设置起始时间为14:00
 * const endTime = new Date();
 * endTime.setHours(18, 0); // 设置结束时间为18:00
 * const intervalsFromDate = calculateTimeIntervals(startTime, endTime, 60);
 * console.log(intervalsFromDate); // 输出: ["08:00-08:20", "08:20-08:40", ..., "20:40-21:00"]
 */
export function calculateTimeIntervals(
  startTime: string | Date = "08:00",
  endTime: string | Date = "21:00",
  interval: number = 20
): string[] {
  const result: string[] = [];

  // 创建时间的辅助函数
  const createDate = (time: string | Date): Date => {
    if (time instanceof Date) {
      return new Date(time.getTime());
    } else {
      const [hours, minutes] = time.split(":").map(Number);
      const date = new Date();
      date.setHours(hours, minutes, 0, 0);
      return date;
    }
  };

  let currentTime = createDate(startTime);
  const closingTime = createDate(endTime);

  // 辅助函数：格式化时间为'HH:mm'字符串
  const formatTime = (date: Date): string => {
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");
    return `${hours}:${minutes}`;
  };

  while (currentTime < closingTime) {
    const nextTime = new Date(currentTime.getTime() + interval * 60000);
    // 保证最后一个时间段的结束时间不能超过关门时间
    const formattedEndTime =
      nextTime > closingTime ? formatTime(closingTime) : formatTime(nextTime);
    result.push(`${formatTime(currentTime)}-${formattedEndTime}`);
    currentTime = nextTime;
  }

  return result;
}

type DateFormat =
  | "yyyy-MM-dd HH:mm:ss"
  | "yyyy/MM/dd HH:mm"
  | "MM-dd-yyyy HH:mm"
  | "MM/dd/yyyy HH:mm";

/**
 * 获取预计送达时间【当前时间往后推迟30分钟】
 * @param inputTime 可选参数，传入的日期 (格式：'YYYY-MM-DD' 或 JavaScript Date 对象)，默认是当前时间
 * @param format 可选参数，日期格式，默认是'yyyy-MM-dd HH:mm:ss'
 * @returns string 预计送达时间，例如：'2024-10-01 12:30:00'
 */
export function getEstimatedDeliveryTime(
  inputTime?: Date | string,
  format: DateFormat = "yyyy-MM-dd HH:mm:ss"
): string {
  let now: Date;

  // 处理传入的时间参数
  if (typeof inputTime === "string") {
    now = new Date(inputTime);
  } else if (inputTime instanceof Date) {
    now = new Date(inputTime);
  } else {
    // 如果没有传入参数，默认为当前时间
    now = new Date();
  }

  // 推迟30分钟
  now.setMinutes(now.getMinutes() + 30);

  // 获取日期和时间的各个部分
  let year = now.getFullYear();
  let month = (now.getMonth() + 1).toString().padStart(2, "0");
  let day = now.getDate().toString().padStart(2, "0");
  let hours = now.getHours().toString().padStart(2, "0");
  let minutes = now.getMinutes().toString().padStart(2, "0");
  let seconds = now.getSeconds().toString().padStart(2, "0");

  // 根据指定的格式返回结果
  switch (format) {
    case "yyyy-MM-dd HH:mm:ss":
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    case "yyyy/MM/dd HH:mm":
      return `${year}/${month}/${day} ${hours}:${minutes}`;
    case "MM-dd-yyyy HH:mm":
      return `${month}-${day}-${year} ${hours}:${minutes}`;
    case "MM/dd/yyyy HH:mm":
      return `${month}/${day}/${year} ${hours}:${minutes}`;
    default:
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
}

/**
 * 获取预计送达时间范围
 * @param startTime 指定开始的时间，例如：22:12
 * @param delayMinutes 推迟的时间间隔，单位：分钟，默认值为 30 分钟
 * @returns {string[]} 送达时间范围，例如：['22:12', '22:42']
 */
export function getDeliveryTimeRange(
  startTime: string | Date = new Date(), // 默认当前时间
  delayMinutes: number = 30 // 默认延迟 30 分钟
): string[] {
  let startDate: Date;

  // 如果 startTime 是字符串，则将其转换为 Date
  if (typeof startTime === "string") {
    startDate = new Date(startTime);
    if (isNaN(startDate.getTime())) {
      throw new Error("Invalid date string"); // 处理无效的日期字符串
    }
  } else {
    startDate = startTime;
  }

  // 格式化开始时间
  const startHours: string = startDate.getHours().toString().padStart(2, "0");
  const startMinutes: string = startDate
    .getMinutes()
    .toString()
    .padStart(2, "0");

  // 计算延迟后的时间
  const endTime: Date = new Date(startDate.getTime() + delayMinutes * 60000);
  const endHours: string = endTime.getHours().toString().padStart(2, "0");
  const endMinutes: string = endTime.getMinutes().toString().padStart(2, "0");

  // 返回结果数组
  return [`${startHours}:${startMinutes}`, `${endHours}:${endMinutes}`];
}

/**
 * 获取今天是星期几
 * @returns string 今天是星期几（如：'周一'）
 */
export function getTodayWeekday(): string {
  const weekdays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
  const today = new Date();
  return weekdays[today.getDay()];
}

/**
 * 计算传入日期的明天是星期几
 * @param date 可选参数，传入的日期 (格式：'YYYY-MM-DD' 或 JavaScript Date 对象)，默认是今天
 * @returns string 明天是星期几（如：'周二'）
 */
export function getTomorrowWeekday(date: string | Date = new Date()): string {
  const weekdays = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];

  // 将字符串日期转换为Date对象
  const inputDate = typeof date === "string" ? new Date(date) : date;

  // 获取明天的日期
  const tomorrow = new Date(inputDate);
  tomorrow.setDate(tomorrow.getDate() + 1); // 获取明天的日期

  // 返回明天是星期几
  return weekdays[tomorrow.getDay()];
}

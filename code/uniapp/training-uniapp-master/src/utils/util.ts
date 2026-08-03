/**
 * 格式化日期格式 (用于兼容ios Date对象)
 */
export const formatDate = (time: string) => {
  // 将xxxx-xx-xx的时间格式，转换为 xxxx/xx/xx的格式
  return time.replace(/\-/g, "/");
};

/**
 * 获取明天是星期几
 * @param {string} date 可选参数，传入的日期 (格式：'YYYY-MM-DD' 或 JavaScript Date 对象)，默认是今天
 * @returns {string} 明天是星期几（如：'周二'）
 */
export const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))

  if (minutes < 1) return "刚刚"
  if (minutes < 60) return `${minutes}分钟前`

  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`

  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`

  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

/**
 * 将时间毫秒值转换为 "YYYY-MM-DD HH:MM:SS" 格式的字符串
 * @param {number} timestamp - 时间的毫秒值
 * @returns {string} - 格式化后的时间字符串
 */
export const formatTimestamp = (timestamp: number) => {
  const date = new Date(timestamp);
  // console.log(date)
  const Y = date.getFullYear();
  const M = (date.getMonth() + 1).toString().padStart(2, '0');
  const D = date.getDate().toString().padStart(2, '0');
  const H = date.getHours().toString().padStart(2, '0');
  const m = date.getMinutes().toString().padStart(2, '0');
  const S = date.getSeconds().toString().padStart(2, '0');
  const result = `${Y}-${M}-${D} ${H}:${m}:${S}`
  // console.log(result)
  return result;
};


/**
 * 对象转URL参数
 * @param {object} obj
 */
export const urlEncode = (obj: any = {}) => {
  const result = [];
  for (const key in obj) {
    const item = obj[key];
    if (!item) {
      continue;
    }
    if (isArray(item)) {
      item.forEach((val: any) => {
        result.push(key + "=" + val);
      });
    } else {
      result.push(key + "=" + item);
    }
  }
  return result.join("&");
};

/**
 * URL参数转对象
 * @param {string} queryStr
 */
export const urlDecode = (queryStr: string = "") => {
  var newObj = new Object();
  if (queryStr) {
    var strs = queryStr.split("&");
    for (var i = 0; i < strs.length; i++) {
      (newObj as any)[strs[i].split("=")[0]] = strs[i].split("=")[1] || "";
    }
  }
  return newObj;
};

/**
 * 遍历对象
 */
export const objForEach = (obj: any, callback: Function) => {
  Object.keys(obj).forEach((key) => {
    callback(obj[key], key);
  });
};

/**
 * 是否在数组内
 */
export const inArray = (search: any, array: any) => {
  for (var i in array) {
    if (array[i] == search) return true;
  }
  return false;
};

/**
 * 对Date的扩展，将 Date 转化为指定格式的String
 * 月(Y)、月(m)、日(d)、小时(H)、分(M)、秒(S) 可以用 1-2 个占位符，
 * 例子：
 * dateFormat('YYYY-mm-dd HH:MM:SS', new Date()) ==> 2020-01-01 08:00:00
 */
export const dateFormat = (fmt: any, date: any) => {
  const opt = {
    "Y+": date.getFullYear().toString(), // 年
    "m+": (date.getMonth() + 1).toString(), // 月
    "d+": date.getDate().toString(), // 日
    "H+": date.getHours().toString(), // 时
    "M+": date.getMinutes().toString(), // 分
    "S+": date.getSeconds().toString(), // 秒
    // 有其他格式化字符需求可以继续添加，必须转化成字符串
  };
  let ret: any;
  for (let k in opt) {
    ret = new RegExp("(" + k + ")").exec(fmt);
    if (ret) {
      fmt = fmt.replace(
        ret[1],
        ret[1].length == 1
          ? (opt as any)[k]
          : (opt as any)[k].padStart(ret[1].length, "0")
      );
    }
  }
  return fmt;
};

/**
 * 判断是否为空对象
 * @param {*} object 源对象
 */
export const isEmptyObject = (object: any) => {
  return Object.keys(object).length === 0;
};

/**
 * 判断是否为对象
 * @param {*} object
 */
export const isObject = (object: any) => {
  return Object.prototype.toString.call(object) === "[object Object]";
};

/**
 * 判断是否为数组
 * @param {*} array
 */
export const isArray = (array: any) => {
  return Object.prototype.toString.call(array) === "[object Array]";
};

/**
 * 判断是否为空
 * @param {*} value 源对象
 */
export const isEmpty = (value: any) => {
  if (isArray(value)) {
    return value.length === 0;
  }
  if (isObject(value)) {
    return isEmptyObject(value);
  }
  return !value;
};

/**
 * 判断对象中是否存在某个属性
 */
export const hasOwnProperty = (object: any, key: any) => {
  return isObject(object) && object[key] !== undefined;
};

/**
 * 对象深拷贝
 * @param {*} obj 源对象
 */
export const cloneObj = (obj: any) => {
  let newObj = isArray(obj) ? [] : {};
  if (typeof obj !== "object") {
    return;
  }
  for (let i in obj) {
    (newObj as any)[i] = typeof obj[i] === "object" ? cloneObj(obj[i]) : obj[i];
  }
  return newObj;
};

/**
 * 数组交集
 * @param {Array} array1 数组1
 * @param {Array} array2 数组2
 */
export const arrayIntersect = (array1: any, array2: any) => {
  return array1.filter((val: any) => array2.indexOf(val) > -1);
};

/**
 * 获取当前客户端的rpx比值
 */
export const rpx = () => {
  const { windowWidth } = uni.getSystemInfoSync();
  // #ifdef H5
  // 与pages.json文件中的 rpxCalcMaxDeviceWidth参数对应, 请勿修改
  const rpxCalcMaxDeviceWidth = 750;
  // 与pages.json文件中的 rpxCalcBaseDeviceWidth参数对应, 请勿修改
  const rpxCalcBaseDeviceWidth = 560;
  const calcWindowWidth =
    windowWidth > rpxCalcMaxDeviceWidth ? rpxCalcBaseDeviceWidth : windowWidth;
  return calcWindowWidth / 750;
  // #endif
};

/**
 * 获取当前客户端的rpx比值
 */
export const rpx2px = (num: number) => {
  return num * rpx();
};

/**
 * 防抖
 * @param func 要执行的函数
 * @param delay 延迟时间
 * @returns {Function} 防抖后的函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setInterval> | null = null;
  return (...args: Parameters<T>) => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => func(...args), delay);
  };
}

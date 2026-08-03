/**
 * 判断是否是外部链接
 * @param {string} path 链接或者路径
 * @returns {boolean} true 是外部链接，false 不是外部链接
 * @example
 * - isExternal('https://www.baidu.com') // true
 * - isExternal('/example/path') // false
 */
export function isExternal(path: string): boolean {
  return /^(https?:|http?:|mailto:|tel:)/.test(path);
}

/**
 * 将短横线连接转变为驼峰命名
 * @param name 字符串
 */
export function toCamelCase(input: string): string {
  const camelCase = input.replace(/-([a-z])/g, (_, char) => char.toUpperCase());
  return camelCase.charAt(0).toUpperCase() + camelCase.slice(1);
}

// /**
//  * @function
//  * @description 设置formData类型的数据
//  * @param {object} data 原始数据
//  * @returns formData数据
//  */
// export const setFormData = (data: Record<string, any>) => {
//   const formData = new FormData();
//   for (const key in data) {
//     if (
//       data[key] &&
//       data[key].constructor === FormData &&
//       data[key] instanceof FormData
//     ) {
//       for (const item of data[key]?.entries()) {
//         formData.append(item[0], item[1]);
//       }
//     } else {
//       formData.append(key, data[key]);
//     }
//   }
//   return formData;
// };

/**
 * 日期格式化函数
 * @param date 日期
 * @param format 格式化字符串
 * @returns 格式化后的日期字符串
 */
export const formatDate = (date: Date, format: CommonDateFormat): string => {
  const pad = (n: number) => n.toString().padStart(2, "0");

  const replacements: Record<string, string> = {
    YYYY: date.getFullYear().toString(),
    MM: pad(date.getMonth() + 1),
    DD: pad(date.getDate()),
    HH: pad(date.getHours()),
    mm: pad(date.getMinutes()),
    ss: pad(date.getSeconds()),
  };

  return format.replace(
    /(YYYY|MM|DD|HH|mm|ss)/g,
    (match) => replacements[match]
  );
};

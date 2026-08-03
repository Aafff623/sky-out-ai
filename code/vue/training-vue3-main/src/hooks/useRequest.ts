/**
 * 封装请求方法，自动管理 loading 状态，最终只返回请求结果
 * @param requestFn 一个接收参数并返回 Promise 的请求函数
 * @param defaultError 默认错误提示信息
 * @param args 请求函数的参数
 * @returns Promise 请求结果
 */
export async function useRequest<T, Args extends any[]>(
  requestFn: (...args: Args) => Promise<T>,
  defaultError = '请求失败',
  ...args: Args
): Promise<T> {
  try {
    // 直接执行传入的请求函数，并返回结果
    return await requestFn(...args);
  } catch (error: any) {
    // 错误提示
    ElMessage.error(error.msg || defaultError);
    throw error;
  }
}
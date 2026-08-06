package com.fortunate.context;

/**
 * @description
 */
public class BaseContext {
  
  public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
  
  /**
   * 设置当前线程的用户id
   * @param userId 用户id
   */
  public static void setUserId(Long userId) {
    threadLocal.set(userId);
  }
  
  /**
   * 获取当前线程的用户id
   * @return
   */
  public static Long getUserId() {
    return threadLocal.get();
  }
  
  /**
   * 移除当前线程的所有用户id
   */
  public static void removeCurrentId() {
    threadLocal.remove();
  }
}

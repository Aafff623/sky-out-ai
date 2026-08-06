package com.fortunate.handler;

import com.fortunate.constant.MessageConstant;
import com.fortunate.exception.BaseException;
import com.fortunate.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @description 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  
  /**
   * 处理业务异常
   *
   * @param e 业务异常
   * @return JSON 格式的错误信息
   */
  @ExceptionHandler(BaseException.class)
  public Result<?> exceptionHandler(BaseException e) {
    log.error("捕获【BaseException】的异常信息==> {}", e.getMessage());
    int code = e.getCode();
    if (code != 0) {
      return Result.error(code, e.getMessage());
    }
    return Result.error(e.getMessage());
  }

  /**
   * 处理其他异常
   * @param e
   * @return
   */
  @ExceptionHandler(Exception.class)
  public Result<?> exceptionHandler(Exception e) {
    String message = e.getMessage();
    if (message.contains("Duplicate entry")){
     return exceptionHandler(message);
    }

    e.printStackTrace();
    log.error("捕获【Exception】的异常信息==> {}", e.getMessage());
    return Result.error(e.getMessage());
  }
  
  /**
   * 处理 SQL 异常
   * @param e SQL 异常
   * @return JSON 格式的错误信息
   */
  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public Result<?> exceptionHandler(SQLIntegrityConstraintViolationException e) {
    // 报错信息示例：Duplicate entry 'zhangsan' for key 'employee.idx_username'
    log.error("捕获【SQLIntegrityConstraintViolationException】的异常信息==> {}", e.getMessage());
    String message = e.getMessage();
    if (message.contains("Duplicate entry")) {
      // 分割字符获取用户名
      String[] split = message.split(" ");
      String username = "【" + split[2].replaceAll("'", "") + "】";
      // 用户名已存在
      String msg = username + MessageConstant.ALREADY_EXISTS;
      return Result.error(msg);
    }
    return Result.error(MessageConstant.UNKNOWN_ERROR);
  }

  /**
   * 处理重复键异常
   * @param e 重复键异常
   * @return JSON 格式的错误信息
   */

  public Result<?> exceptionHandler(String message) {
      // 提取重复的值和字段信息
      int startIndex = message.indexOf("Duplicate entry '") + "Duplicate entry '".length();
      int endIndex = message.indexOf("' for key ");
      String duplicateValue = message.substring(startIndex, endIndex);

      // 提取字段名
      int keyStartIndex = message.indexOf("' for key '") + "' for key '".length();
      int keyEndIndex = message.lastIndexOf("'");
      String key = message.substring(keyStartIndex, keyEndIndex);

      // 根据字段名判断是哪个字段重复
      String fieldName;
      if (key.contains("dish.idx_dish_name")) {
        fieldName = "菜品名称";
      } else if (key.contains("employee.idx_username")) {
        fieldName = "用户名";
      } else if (key.contains("setmeal.idx_setmeal_name")) {
        fieldName = "套餐名称";
      } else {
        // 默认处理
        fieldName = "该字段";
      }
      String errorMsg = fieldName + "【" + duplicateValue + "】" + MessageConstant.ALREADY_EXISTS;
      return Result.error(errorMsg);
  }
}

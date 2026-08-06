package com.fortunate.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 基础异常类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseException extends RuntimeException {
  private int code;
  
  public BaseException(String msg) {
    super(msg);
  }
  
  public BaseException(int code, String msg) {
    super(msg);
    this.code = code;
  }
}

package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description 密码错误异常
 */
@NoArgsConstructor
public class PasswordErrorException extends BaseException {
  
  public PasswordErrorException(String msg) {
    super(msg);
  }
}

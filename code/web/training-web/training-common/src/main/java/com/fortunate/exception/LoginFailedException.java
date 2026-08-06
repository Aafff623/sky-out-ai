package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class LoginFailedException extends BaseException {
  
  public LoginFailedException(String msg) {
    super(msg);
  }
  
  public LoginFailedException(int code, String msg) {
    super(code, msg);
  }
}

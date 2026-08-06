package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class AccountLockedException extends BaseException {
  
  public AccountLockedException(String msg) {
    super(msg);
  }
  
}

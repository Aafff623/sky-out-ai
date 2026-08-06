package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class AccountNotFoundException extends BaseException {
  
  public AccountNotFoundException(String msg) {
    super(msg);
  }
}

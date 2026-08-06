package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class ShoppingCartBusinessException extends BaseException {
  
  public ShoppingCartBusinessException(String msg) {
    super(msg);
  }
}

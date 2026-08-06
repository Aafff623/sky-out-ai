package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class OrderBusinessException extends BaseException {
  
  public OrderBusinessException(String msg) {
    super(msg);
  }
}

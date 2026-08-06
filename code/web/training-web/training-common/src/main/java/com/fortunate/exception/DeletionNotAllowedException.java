package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class DeletionNotAllowedException extends BaseException {
  
  public DeletionNotAllowedException(String msg) {
    super(msg);
  }
}

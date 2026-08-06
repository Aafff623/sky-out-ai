package com.fortunate.exception;

import lombok.NoArgsConstructor;

/**
 * @description
 */
@NoArgsConstructor
public class AddressBookBusinessException extends BaseException {
  
  public AddressBookBusinessException(String msg) {
    super(msg);
  }
}

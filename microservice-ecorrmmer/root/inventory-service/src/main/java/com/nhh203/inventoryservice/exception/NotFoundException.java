
package com.nhh203.inventoryservice.exception;

import com.nhh203.inventoryservice.utils.MessagesUtils;

public class NotFoundException extends RuntimeException {

  private String message;

  public NotFoundException(String errorCode, Object... var2) {
    this.message = MessagesUtils.getMessage(errorCode, var2);
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}


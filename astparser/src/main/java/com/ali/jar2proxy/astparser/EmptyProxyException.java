package com.ali.jar2proxy.astparser;

public class EmptyProxyException extends RuntimeException {

  public EmptyProxyException() {
    super("Input proxy can't be empty.");
  }

}

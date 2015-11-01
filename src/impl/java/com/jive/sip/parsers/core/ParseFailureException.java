package com.jive.sip.parsers.core;

public class ParseFailureException extends RuntimeException
{

  private static final long serialVersionUID = 1L;
  
  public ParseFailureException(String message)
  {
    super(message);
  }
  
  public ParseFailureException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
}

package com.jive.sip.dummer.txn;

import lombok.Value;

@Value
public class SipTransactionErrorInfo
{

  public static enum ErrorCode
  {
    Timeout
  }

  private ErrorCode code;

}

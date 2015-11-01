package com.jive.sip.dummer.txn;

import java.time.Duration;

import lombok.Value;

@Value
public class ClientTransactionOptions
{

  public static final ClientTransactionOptions DEFAULT = new ClientTransactionOptions(
      AbstractDatagramTransaction.T1,
      AbstractDatagramTransaction.T1.multipliedBy(64)
      );

  public Duration timerA;

  public Duration timerB;


}

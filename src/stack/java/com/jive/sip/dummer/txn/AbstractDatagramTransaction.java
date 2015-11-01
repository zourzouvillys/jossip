package com.jive.sip.dummer.txn;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractDatagramTransaction
{

  /**
   * RTT estimate
   */

  public static final Duration T1 = Duration.ofMillis(500);

  /**
   * The maximum retransmit interval for non-INVITE requests and INVITE responses
   */

  public static final Duration T2 = Duration.ofSeconds(4);

  /**
   * Maximum duration a message will remain in the network
   */

  public static final Duration T4 = Duration.ofSeconds(5);


  //
  protected final TransactionRuntime manager;

  public AbstractDatagramTransaction(final TransactionRuntime manager)
  {
    this.manager = manager;
  }

  protected ScheduledFuture<?> schedule(final Runnable run, final Duration duration)
  {
    log.debug("Scheduling {}: {}", run.getClass().getName(), duration);
    return this.manager.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        log.debug("Firing {}", run.getClass().getName());
        try
        {
          run.run();
        }
        catch (final Exception ex)
        {
          log.warn(String.format("Error processing timer %s", run.getClass().getName()), ex);
        }
      }
    }, duration.toNanos(), TimeUnit.NANOSECONDS);
  }


}

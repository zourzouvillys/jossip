package com.jive.sip.transport.udp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.Executor;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Sets;

/**
 * A an event listener which dispatches to other listeners.
 * 
 * @author theo
 * 
 */

public class DispatchingEventListener<L>
{

  @Slf4j
  private static class Handler<L> implements InvocationHandler
  {

    private final Set<Dispatcher<L>> listeners;

    public Handler(final Set<Dispatcher<L>> listeners2)
    {
      this.listeners = listeners2;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {

      if (listeners.isEmpty())
      {
        log.trace("Dispatching {} without listener", method);
        return null;
      }

      for (final Dispatcher<L> listener : this.listeners)
      {

        Runnable run = new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              log.trace("Dispatching {} to {}", method, listener.getHandler());
              method.invoke(listener.getHandler(), args);
            }
            catch (final Exception ex)
            {
              // TODO: how to handle?
              log.warn("Error processing listener", ex);
            }
          }
        };

        listener.executor.execute(run);

      }
      return null;
    }

  }

  @Value
  private static class Dispatcher<L>
  {
    L handler;
    Executor executor;
  }

  private final L wrapper;
  private final Set<Dispatcher<L>> listeners = Sets.newLinkedHashSet();

  @SuppressWarnings("unchecked")
  private DispatchingEventListener(final Class<L> klass)
  {
    this.wrapper = (L) Proxy.newProxyInstance(
        this.getClass().getClassLoader(),
        new Class<?>[]
        { klass },
        new Handler<L>(this.listeners));
  }

  public static <L> DispatchingEventListener<L> create(final Class<L> klass)
  {
    return new DispatchingEventListener<>(klass);
  }

  public void add(final L listener, final Executor executor)
  {
    this.listeners.add(new Dispatcher<L>(listener, executor));
  }

  public L getInvoker()
  {
    return this.wrapper;
  }

}

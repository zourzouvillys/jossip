package io.rtcore.sip.proxy.transport.stream.client;

import com.google.common.util.concurrent.AbstractService;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;

public class MicronautService extends AbstractService {

  private ApplicationContext ctx;
  private Class<?> mainClass;
  private Object[] beans;

  MicronautService(Class<?> mainClass, Object... beans) {
    this.mainClass = mainClass;
    this.beans = beans;
  }

  @Override
  protected void doStart() {
    this.ctx =
      Micronaut.build(new String[] {})
        .mainClass(mainClass)
        .singletons(this.beans)
        .start();
    super.notifyStarted();
  }

  @Override
  protected void doStop() {
    this.ctx.stop();
  }

}

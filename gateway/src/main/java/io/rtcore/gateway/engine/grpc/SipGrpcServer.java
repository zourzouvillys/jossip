package io.rtcore.gateway.engine.grpc;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.AbstractService;
import com.salesforce.rx3grpc.GrpcContextOnScheduleHook;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class SipGrpcServer extends AbstractService {

  private final ServerBuilder<?> builder;

  public SipGrpcServer(final ServerBuilder<?> builder) {
    this.builder = builder;
  }

  public class RxContextPropagator {

    private static AtomicBoolean INSTALLED = new AtomicBoolean();

    public static void ensureInstalled() {
      if (INSTALLED.compareAndSet(false, true)) {
        RxJavaPlugins.setScheduleHandler(new GrpcContextOnScheduleHook());
      }
    }

  }

  private Server server;

  @Override
  protected void doStart() {
    RxContextPropagator.ensureInstalled();
    try {
      this.server = this.builder.build().start();
    }
    catch (final IOException e) {
      this.notifyFailed(e);
    }
  }

  @Override
  protected void doStop() {
    try {
      this.server.shutdown().awaitTermination();
    }
    catch (final InterruptedException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

}

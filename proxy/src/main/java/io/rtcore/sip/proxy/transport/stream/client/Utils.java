package io.rtcore.sip.proxy.transport.stream.client;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.CompletableSubject;
import io.reactivex.rxjava3.subjects.SingleSubject;

final class Utils {

  static <T> Completable toCompletable(Future<T> f) {
    CompletableSubject subject = CompletableSubject.create();
    f.addListener(new GenericFutureListener<Future<T>>() {
      @Override
      public void operationComplete(Future<T> future) throws Exception {
        try {
          future.get();
          subject.onComplete();
        }
        catch (Throwable t) {
          subject.onError(t);
        }
      }
    });
    return subject;
  }

  static <T> Single<T> toSingle(Future<T> f) {
    SingleSubject<T> subject = SingleSubject.create();
    f.addListener(new GenericFutureListener<Future<T>>() {
      @Override
      public void operationComplete(Future<T> future) throws Exception {
        try {
          subject.onSuccess(future.get());
        }
        catch (Throwable t) {
          subject.onError(t);
        }
      }
    });
    return subject;
  }

}

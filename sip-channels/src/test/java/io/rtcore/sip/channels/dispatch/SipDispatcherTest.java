package io.rtcore.sip.channels.dispatch;

import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import io.reactivex.rxjava3.core.Flowable;

class SipDispatcherTest {

  @Test
  void test() {

    //
    SipDispatcher.dispatch(FlowAdapters.toFlowPublisher(Flowable.never()));

  }

}

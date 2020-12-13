package io.rtcore.sip.proxy.transport.stream.client;

import java.nio.charset.StandardCharsets;

import io.netty.channel.Channel;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import io.rtcore.sip.proxy.transport.stream.SipStreamChannelHandler.SipChannelActiveEvent;

public class Flow {

  private static RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();
  private Channel ch;

  public Flow(SipChannelActiveEvent e) {
    this.ch = e.channel();
  }

  public void txmit(RawMessage raw) {
    String value = serializer.writeValueAsString(raw);
    System.err.println(value);
    ch.writeAndFlush(ch.alloc().buffer().writeBytes(value.getBytes(StandardCharsets.UTF_8)));
  }

}

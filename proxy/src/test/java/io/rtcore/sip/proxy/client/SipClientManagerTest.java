package io.rtcore.sip.proxy.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import com.google.common.net.InetAddresses;

import io.rtcore.sip.proxy.MessageWriter;

class SipClientManagerTest {

  @Test
  void test() {

    MessageWriter writer = new InMemoryMessageWriter();
    
    SipClientManager client = new SipClientManager(writer);
    
    ByteBuffer req = ByteBuffer.wrap("OPTIONS sip:localhost SIP/2.0\r\n\r\n".getBytes());
    
    client.send(new InetSocketAddress(InetAddresses.forString("127.0.0.100"), 5060), req);

  }

}

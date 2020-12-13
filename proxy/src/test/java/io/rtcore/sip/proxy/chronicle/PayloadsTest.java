package io.rtcore.sip.proxy.chronicle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.proxy.chronicle.Payloads.Frame;

class PayloadsTest {

  @Test
  void testRoundTrip() throws IOException {

    Frame in = new Payloads.Frame();

    in.time = Instant.now();
    in.proto = "UDP";
    in.local = new InetSocketAddress(3333);
    in.remote = new InetSocketAddress(1111);
    in.payload = "test payload".getBytes();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Payloads.write(baos, in);
    Frame out = Payloads.read(new ByteArrayInputStream(baos.toByteArray()));

    assertEquals(in.time, out.time);
    assertEquals(in.proto, out.proto);
    assertEquals(in.local, out.local);
    assertEquals(in.remote, out.remote);

  }

}

package io.rtcore.sip.channels.internal;

import java.io.Closeable;

import io.rtcore.sip.message.message.SipMessage;

public interface SipWirePacket extends Attributed, Closeable {

  SipMessage payload();

  static SipWirePacket of(final SipMessage payload, final SipAttributes transportAttributes) {

    return new SipWirePacket() {

      @Override
      public SipAttributes attributes() {
        return transportAttributes;
      }

      @Override
      public SipMessage payload() {
        return payload;
      }

      @Override
      public void close() {
        // a no-op.
      }

      @Override
      public String toString() {
        return String.format("%s: %s", payload, transportAttributes);
      }

    };

  }

}

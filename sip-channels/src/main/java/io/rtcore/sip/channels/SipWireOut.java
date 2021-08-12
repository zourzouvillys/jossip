package io.rtcore.sip.channels;

import java.io.InputStream;
import java.net.SocketAddress;
import java.util.concurrent.CompletionStage;

public interface SipWireOut {

  CompletionStage<?> send(final InputStream frame, final SocketAddress target);

}

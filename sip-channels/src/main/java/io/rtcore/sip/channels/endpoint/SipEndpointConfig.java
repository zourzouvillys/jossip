package io.rtcore.sip.channels.endpoint;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

import io.rtcore.sip.channels.handlers.FunctionServerCallHandler;
import io.rtcore.sip.channels.internal.SipChannels;
import io.rtcore.sip.channels.internal.SipServerCallHandler;
import io.rtcore.sip.channels.internal.SipUdpSocket;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.processor.rfc3261.MutableSipResponse;

@Value.Immutable
@Value.Style(
  jdkOnly = true,
  allowedClasspathAnnotations = { Override.class },
  typeImmutable = "Default*",
  build = "config",
  defaultAsDefault = true,
  visibility = ImplementationVisibility.PACKAGE,
  builderVisibility = BuilderVisibility.PACKAGE)
public interface SipEndpointConfig {

  /**
   * the socket to bind this endpoint with.
   */

  default SipUdpSocket socket() {
    return SipChannels.newUdpSocketBuilder().bindNow();
  }

  /**
   * the handler for processing incoming requests.
   */

  default SipServerCallHandler requestHandler() {
    return FunctionServerCallHandler.create(
      req -> MutableSipResponse.createResponse(req, SipResponseStatus.METHOD_NOT_ALLOWED).build(),
      ack -> {
        // just drop ACKs.
      });
  }

  /**
   *
   */

  static abstract class Builder implements SipEndpointBuilder {

    abstract SipEndpointConfig config();

    @Override
    public ManagedSipEndpoint build() {
      return new DefaultSipEndpoint(this.config());
    }

  }

}

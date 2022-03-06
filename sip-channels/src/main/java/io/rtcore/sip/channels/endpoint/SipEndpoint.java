package io.rtcore.sip.channels.endpoint;

import io.rtcore.sip.channels.SipChannelOLD;

public interface SipEndpoint extends SipChannelOLD {

  /**
   * create a SIP endpoint with default settings.
   */

  static SipEndpoint create() {
    return builder().build();
  }

  /**
   * Create a builder that can be used to configure and create a {@link SipEndpointBuilder}.
   */

  static SipEndpointBuilder builder() {
    return DefaultSipEndpointConfig.builder();
  }

}

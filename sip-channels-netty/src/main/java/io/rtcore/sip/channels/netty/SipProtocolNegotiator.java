package io.rtcore.sip.channels.netty;


import io.netty.channel.ChannelHandler;

public interface SipProtocolNegotiator {

  /**
   *
   */

  ChannelHandler newHandler();

  /**
   *
   */

  default void close() {
  }

  /**
   *
   */

  public interface Factory {

    SipProtocolNegotiator newNegotiator();

  }

  /**
   *
   */

  public interface ClientFactory extends Factory {

    /**
     * Creates a new negotiator instance.
     */

    @Override
    SipProtocolNegotiator newNegotiator();

    /**
     * the implicit port to use if no port was specified explicitly by the user.
     */

    int getDefaultPort();

  }

  /**
   *
   */

  interface ServerFactory extends Factory {

    @Override
    SipProtocolNegotiator newNegotiator();

  }

}

package io.rtcore.sip.channels;

public interface SipServiceApi {

  /**
   * start a new invite session.
   */

  void invite();

  /**
   * start a subscription.
   */

  void subscribe();

  /**
   * send a request within an existing established route.
   */

  void route();

  /**
   * register a binding.
   */

  void register();

  /**
   * out of dialog refer.
   */

  void refer();

  /**
   * out of dialog publish
   */

  void publish();

  /**
   * out of dialog options
   */

  void options();

}

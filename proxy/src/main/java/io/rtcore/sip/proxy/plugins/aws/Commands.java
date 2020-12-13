package io.rtcore.sip.proxy.plugins.aws;

/**
 * specific instructions send from platform to a gateway node.
 * 
 * @author theo
 *
 */

public class Commands {

  /**
   * 
   */

  public static final class SendTxn {

    public String target;
    public String request;

  }

}

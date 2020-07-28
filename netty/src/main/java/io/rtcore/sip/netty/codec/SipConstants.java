package io.rtcore.sip.netty.codec;


public class SipConstants {

  public static final byte LF = 10;
  public static final byte CR = 13;
  public static final byte SP = 32;
  public static final byte[] CRLF = new byte[] { CR, LF };
  public static final byte[] SIP_2_0 = new byte[] { 'S', 'I', 'P', '/', '2', '.', '0' };
  public static final byte COLON = 58;

}

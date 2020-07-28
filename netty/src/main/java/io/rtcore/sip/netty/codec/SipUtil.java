package io.rtcore.sip.netty.codec;



public class SipUtil {

  public static long getContentLength(SipMessage message, long defaultValue) {
    CharSequence value = message.headers().get(SipHeaderNames.CONTENT_LENGTH);
    if (value != null) {
      return Long.parseLong(value.toString());
    }
    // Otherwise we don't.
    return defaultValue;
  }

  public static boolean isContentLengthSet(SipMessage message) {
    return message.headers().contains(SipHeaderNames.CONTENT_LENGTH);
  }

  public static CharSequence getContentType(SipMessage msg) {
    
    return msg.headers().get(SipHeaderNames.CONTENT_TYPE);
    
  }

}

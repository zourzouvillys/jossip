package io.rtcore.sip.netty.codec;


import io.netty.util.AsciiString;

public class SipHeaderNames {

  public static final AsciiString CONTENT_LENGTH = AsciiString.cached("content-length");
  public static final AsciiString CONTENT_TYPE = AsciiString.cached("content-type");
  public static final AsciiString TRAILER = AsciiString.cached("trailer");
  public static final AsciiString TRANSFER_ENCODING = AsciiString.cached("transfer-encoding");

  public static final AsciiString TO = AsciiString.cached("to");
  public static final AsciiString FROM = AsciiString.cached("from");
  public static final AsciiString VIA = AsciiString.cached("via");
  public static final AsciiString ROUTE = AsciiString.cached("route");
  public static final AsciiString RECORD_ROUTE = AsciiString.cached("record-route");
  public static final AsciiString CSEQ = AsciiString.cached("cseq");
  public static final AsciiString CONTACT = AsciiString.cached("contact");
  public static final AsciiString CALL_ID = AsciiString.cached("call-id");

}

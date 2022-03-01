package io.rtcore.sip.message.content;

import java.io.InputStream;

import io.rtcore.sip.message.message.api.headers.MIMEType;

public interface SipContent {

  MIMEType type();

  InputStream bufferedReader();

  static SipContent emptyContent() {
    return null;
  }

}

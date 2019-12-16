package com.jive.sip.message.api.headers;

import org.junit.Test;

import com.google.common.net.HostAndPort;
import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.TelUri;

public class HistoryInfoTest {

  @Test
  public void test() {
    HistoryInfo hi = HistoryInfo.fromUnknownRequest(SipUri.fromUserAndHost("theo", HostAndPort.fromString("test.com")));
    hi =
      hi
        .withAppended(new TelUri("+13344545455"))
        .withRecursion(new TelUri("+13344545455"))
        .withNoChange(new TelUri("+13344545455"));
    System.err.println(hi);
  }

}

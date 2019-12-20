package io.rtcore.sip.message.message.api.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.message.api.headers.HistoryInfo;
import io.rtcore.sip.message.message.api.headers.HistoryInfo.ChangeType;
import io.rtcore.sip.message.message.api.headers.HistoryInfo.Entry;
import io.rtcore.sip.message.processor.uri.parsers.TelUriParser;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.TelUri;

public class HistoryInfoTest {

  @Test
  public void test() {

    HistoryInfo hi =
      HistoryInfo.fromUnknownRequest(
        SipUri.fromUserAndHost("theo", HostAndPort.fromString("test.example.com")));
    hi =
      hi
        .withAppended(new TelUri("+13344545455"))
        .withRecursion(new TelUri("+13344545455"))
        .withNoChange(new TelUri("+13344545455"));

    assertFalse(hi.isEmpty());

    Entry e = hi.last().get();

    assertArrayEquals(new int[] { 1 }, e.index());
    assertEquals(TelUriParser.parse("tel:+13344545455"), e.uri());
    assertArrayEquals(new int[] { 1 }, e.prev());
    assertEquals(ChangeType.NP, e.type());

  }

}

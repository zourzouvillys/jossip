package com.jive.sip.message.api.alertinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.uri.HttpUri;
import com.jive.sip.uri.UrnService;
import com.jive.sip.uri.UrnUri;

public class AlertInfoTests {

  @Test
  public void testCallInfo() throws Exception {
    final ParameterizedUri header = new ParameterizedUri(HttpUri.secure("google.com"));
    assertEquals(new HttpUriAlertInfo("https://google.com"), header.uri().apply(AlertInfoUriExtractor.getInstance()));
  }

  @Test
  public void testUrnAlertInfo() throws Exception {
    final ParameterizedUri header = new ParameterizedUri(new UrnUri("alert", new UrnService("service:call-waiting:abc@example")));
    assertEquals(new WellKnownAlertInfo(new UrnUri("alert", new UrnService("service:call-waiting:abc@example"))),
      header.uri().apply(AlertInfoUriExtractor.getInstance()));
  }

}

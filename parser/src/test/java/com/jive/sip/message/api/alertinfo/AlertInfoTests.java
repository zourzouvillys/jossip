package com.jive.sip.message.api.alertinfo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.uri.api.HttpUri;
import com.jive.sip.uri.api.UrnService;
import com.jive.sip.uri.api.UrnUri;

public class AlertInfoTests {

  @Test
  public void testCallInfo() throws Exception {
    final ParameterizedUri header = new ParameterizedUri(HttpUri.secure("google.com"));
    assertEquals(new HttpUriAlertInfo("https://google.com"), header.getUri().apply(AlertInfoUriExtractor.getInstance()));
  }

  @Test
  public void testUrnAlertInfo() throws Exception {
    final ParameterizedUri header = new ParameterizedUri(new UrnUri("alert", new UrnService("service:call-waiting:abc@example")));
    assertEquals(new WellKnownAlertInfo(new UrnUri("alert", new UrnService("service:call-waiting:abc@example"))),
      header.getUri().apply(AlertInfoUriExtractor.getInstance()));
  }

}

package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Assert;
import org.junit.Test;

public class DateTimeSerializerTest {

  @Test
  public void test() {
    final DateTimeSerializer ser = new DateTimeSerializer();
    final ZonedDateTime instance = ZonedDateTime.of(2015, 10, 31, 20, 21, 50, 0, ZoneId.of("GMT"));
    Assert.assertEquals("Sat, 31 Oct 2015 20:21:50 GMT", ser.serialize(instance));
  }

}

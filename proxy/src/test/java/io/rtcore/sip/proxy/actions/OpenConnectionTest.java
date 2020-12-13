package io.rtcore.sip.proxy.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

class OpenConnectionTest {

  @Test
  void test() throws IOException {

    OpenStream in =
      OpenStream.builder()
        .transport("TCP")
        .remote("1.2.3.4:5060")
        .serverName("example.com")
        .putTags("xyz", "test")
        .build();

    ObjectMapper mapper = new JsonMapper().registerModules(new Jdk8Module());
    String x = mapper.writeValueAsString(in);
    OpenStream res = mapper.readValue(x.getBytes(), OpenStream.class);
    assertEquals(in, res);

  }

}

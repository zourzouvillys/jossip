package com.jive.sip.auth.headers;

import static com.jive.sip.auth.headers.DigestCredentials.MD5;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DigestCredentialsTest {

  @Test
  public void test() {

    final DigestCredentials creds =
      DigestCredentials
        .builder()
        .realm("example.com")
        .algorithm(MD5)
        .nonce("xxx")
        .nonceCount(1234)
        .username("theo")
        .build()
        .asCredentials();

    assertEquals(
      MD5,
      creds.algorithm());

    assertEquals(
      "Digest algorithm=MD5,realm=\"example.com\",username=\"theo\",nonce=\"xxx\",stale=false,nc=00001234",
      creds.toString());

  }

}

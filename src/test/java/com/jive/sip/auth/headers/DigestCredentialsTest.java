package com.jive.sip.auth.headers;

import org.junit.Assert;
import org.junit.Test;

public class DigestCredentialsTest
{

  @Test
  public void test()
  {
    final DigestCredentials creds = DigestCredentials.builder()
        .realm("jive.com")
        .algorithm(DigestCredentials.MD5)
        .nonce("xxx")
        .nonceCount(1234)
        .username("theo")
        .build();

    Assert.assertEquals(DigestCredentials.MD5, creds.algorithm());
    Assert.assertEquals("Digest algorithm=MD5,realm=\"jive.com\",username=\"theo\",nonce=\"xxx\",stale=false,nc=00001234", creds.toString());

  }

}

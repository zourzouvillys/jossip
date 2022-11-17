package io.rtcore.sip.channels.auth;

public record DigestClientCredentials(

    String username,
    String ha1,
    long nonceCount,
    String clientNonce

) {

}

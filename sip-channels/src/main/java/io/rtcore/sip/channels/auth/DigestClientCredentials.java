package io.rtcore.sip.channels.auth;

public record DigestClientCredentials(

    //

    String username, // = "1001-theo";
    String ha1, // = "";
    long nonceCount, // = "00000001";
    String clientNonce // = "mynonce";

//

) {

}

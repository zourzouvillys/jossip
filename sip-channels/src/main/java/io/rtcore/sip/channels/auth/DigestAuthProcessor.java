package io.rtcore.sip.channels.auth;

import io.reactivex.rxjava3.core.FlowableTransformer;
import io.rtcore.sip.message.message.SipResponse;

public class DigestAuthProcessor {

  public static FlowableTransformer<SipResponse, SipResponse> forDigestRealm(DigestCredentialsStore store, String string) {
    
    return res -> res;
    
  }

}

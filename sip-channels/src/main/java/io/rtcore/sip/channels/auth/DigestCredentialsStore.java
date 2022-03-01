package io.rtcore.sip.channels.auth;

import io.reactivex.rxjava3.core.Maybe;

public interface DigestCredentialsStore {

  Maybe<Credential> ha1(String username, String realm);

}

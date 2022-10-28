package io.rtcore.sip.channels.auth;

import java.util.concurrent.CompletionStage;

public interface DigestCredentialsStore {

  CompletionStage<CredentialSet> ha1(String username, String realm);

}

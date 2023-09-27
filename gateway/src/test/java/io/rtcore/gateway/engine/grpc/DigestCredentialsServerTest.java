package io.rtcore.gateway.engine.grpc;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.rtcore.gateway.auth.proto.AuthorizationToken;
import io.rtcore.gateway.auth.proto.DigestAuthServiceGrpc;
import io.rtcore.gateway.auth.proto.DigestAuthServiceGrpc.DigestAuthServiceBlockingStub;
import io.rtcore.gateway.auth.proto.GetAuthorizationRequest;

class DigestCredentialsServerTest {

  @Test
  void test() throws IOException {

    final Server server =
      InProcessServerBuilder
        .forName("test")
        .directExecutor()
        .addService(new DigestCredentialsServer())
        .build()
        .start();

    try {

      final ManagedChannel channel =
        InProcessChannelBuilder
          .forName("test")
          .directExecutor()
          // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
          // needing certificates.
          .usePlaintext()
          .build();

      final DigestAuthServiceBlockingStub stub = DigestAuthServiceGrpc.newBlockingStub(channel);

      final AuthorizationToken res =
        stub.getAuthorizationToken(GetAuthorizationRequest.newBuilder()
          .setPrincipal("aaa:bbb")
          .setUri("sip:theo@test.com")
          .setRealm("test.com")
          .addChallenges(
            "Digest realm=\"test.com\",nonce=\"1695800770/654d0d670bd4c1741b9aee74aa196ce8\",opaque=\"17d6be322eec1785\",algorithm=MD5,qop=\"auth\"")
          .build());

      System.err.println(res.getAuthorizationsList().get(0));

    }
    finally {
      server.shutdownNow();
    }

  }

}

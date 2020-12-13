package io.rtcore.sip.proxy.plugins.aws;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.google.common.net.HostAndPort;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

public class RdsSigner {

  public static String getAuthToken(HostAndPort target, String username) {
    return getAuthToken(DefaultCredentialsProvider.create().resolveCredentials(), Region.US_WEST_2, target, username);
  }

  public static String getAuthToken(AwsCredentials credentials, Region region, HostAndPort target, String username) {

    Aws4PresignerParams params =
      Aws4PresignerParams
        .builder()
        .expirationTime(Instant.now().plus(15, ChronoUnit.MINUTES))
        .awsCredentials(credentials)
        .signingName("rds-db")
        .signingRegion(Region.US_WEST_2)
        .build();

    SdkHttpFullRequest request =
      SdkHttpFullRequest
        .builder()
        .encodedPath("/")
        .host(target.getHost())
        .port(target.getPortOrDefault(5432))
        .protocol("http") // Will be stripped off; but we need to satisfy SdkHttpFullRequest
        .method(SdkHttpMethod.GET)
        .appendRawQueryParameter("Action", "connect")
        .appendRawQueryParameter("DBUser", username)
        .build();

    URI uri =
      Aws4Signer.create()
        .presign(request, params)
        .getUri();

    return uri.toString().substring(uri.getScheme().length() + 3);

    // .substring(uri.getRawSchemeSpecificPart().length());

  }
}

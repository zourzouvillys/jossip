package io.zrz.rtcore.useragent;

import java.util.Optional;

import org.immutables.value.Value;

import io.rtcore.sip.channels.connection.ImmutableSipRoute;
import io.rtcore.sip.message.uri.Uri;

@Value.Immutable
public interface InviteRequest extends WithInviteRequest {

  /**
   * The request URI.
   */

  Uri uri();

  /**
   * 
   */

  Uri from();

  /**
   * the SDP offer to apply to this request.
   */

  String offer();

  /**
   * the target to send this request to.
   */

  ImmutableSipRoute target();

  /**
   * optional provider of authentication
   */

  Optional<SipClientAuthProvider> authenticator();

}

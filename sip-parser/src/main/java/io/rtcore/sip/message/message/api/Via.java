/**
 *
 */
package io.rtcore.sip.message.message.api;

import static io.rtcore.sip.message.parameters.tools.ParameterUtils.Branch;
import static io.rtcore.sip.message.parameters.tools.ParameterUtils.RPort;

import java.net.InetAddress;
import java.util.Optional;
import java.util.OptionalInt;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.FlagParameterDefinition;
import io.rtcore.sip.message.parameters.impl.HostParameterDefinition;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 *
 */

// @Value.Immutable
// @Value.Style
public final class Via extends BaseParameterizedObject<Via> {

  public static final String MAGIC = "z9hG4bK";
  public static final Token BRANCH = Token.from("branch");
  public static final FlagParameterDefinition RPORT = new FlagParameterDefinition("rport");
  public static final HostParameterDefinition RECEIVED = new HostParameterDefinition("received");
  public static final String VIA_NAME = "branch";

  // -------
  //
  // -------

  private final ViaProtocol protocol;
  private final HostPort sentBy;

  // -------
  //
  // -------

  public Via(final ViaProtocol protocol, final HostAndPort host) {
    this(protocol, host, DefaultParameters.emptyParameters());
  }

  public Via(final ViaProtocol protocol, final HostAndPort host, final BranchId branch) {
    this(protocol, host, DefaultParameters.of(BRANCH, branch.asToken()));
  }

  public Via(final ViaProtocol protocol, final HostAndPort host, final Parameters parameters) {
    this.protocol = protocol;
    this.sentBy =
        HostPort.fromParts(host.getHost(),
          host.hasPort() ? OptionalInt.of(host.getPort())
                         : OptionalInt.empty());

    this.parameters = Optional.ofNullable(parameters).orElse(DefaultParameters.EMPTY);
  }

  public Via(final ViaProtocol protocol, final HostPort sentBy, final Parameters parameters) {
    this.protocol = protocol;
    this.sentBy = sentBy;
    this.parameters = Optional.ofNullable(parameters).orElse(DefaultParameters.EMPTY);
  }

  // -------
  //
  // -------

  public ViaProtocol protocol() {
    return this.protocol;
  }

  public HostPort sentBy() {
    return this.sentBy;
  }

  // -------
  //
  // -------

  public Via withBranch(final BranchId branch) {
    return this.replaceParameter(Branch, branch.asToken());
  }

  public Via withRPort() {
    return this.withoutParameter(RPort.name()).withParameter(RPort.name());
  }

  public Via withRPort(final int port) {
    Preconditions.checkArgument((port > 0) && (port < 65536));
    return this.replaceParameter(RPort, Token.from(port));
  }

  public Via withReceived(final InetAddress addr) {
    return this.replaceParameter(ParameterUtils.Received, Token.from(addr.getHostAddress()));
  }

  @Override
  public Via withParameters(final Parameters parameters) {
    return new Via(this.protocol, this.sentBy, parameters);
  }

  /**
   * Returns the value of the "branch" parameter, if there is one (includes the magic cookie).
   */
  public Optional<String> getBranch() {
    return this.parameters.getParameter(Branch).map(Token::toString);
  }

  public Optional<BranchId> branch() {
    return this.getBranch().map(BranchId::fromString);
  }

  /**
   * @return true if there is an rport parameter (not if there is any value!)
   */
  public boolean hasRport() {
    return this.parameters.contains("rport");
  }

  // -------
  //
  // -------

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.protocol()).append(' ').append(this.sentBy().toUriString());
    if (this.getParameters().isPresent()) {
      sb.append(this.getParameters().get());
    }
    return sb.toString();
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Via;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $protocol = this.protocol();
    result =
        (result * PRIME)
        + ($protocol == null ? 43
                             : $protocol.hashCode());
    final Object $sentBy = this.sentBy();
    return (result * PRIME)
        + ($sentBy == null ? 43
                           : $sentBy.hashCode());
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Via)) {
      return false;
    }
    final Via other = (Via) o;
    if (!other.canEqual(this) || !super.equals(o)) {
      return false;
    }
    final Object this$protocol = this.protocol();
    final Object other$protocol = other.protocol();
    if (this$protocol == null ? other$protocol != null
        : !this$protocol.equals(other$protocol)) {
      return false;
    }
    final Object this$sentBy = this.sentBy();
    final Object other$sentBy = other.sentBy();
    if (this$sentBy == null ? other$sentBy != null
        : !this$sentBy.equals(other$sentBy)) {
      return false;
    }
    return true;
  }

  /**
   *
   */

  public static Via of(final ViaProtocol protocol, final HostAndPort sentBy, final BranchId branch) {
    return new Via(protocol, sentBy, branch);
  }

  public String encode() {
    return RfcSerializerManager.defaultSerializer().writeValueAsString(this);
  }

  public Optional<String> branchWithoutCookie() {
    return this.branch().flatMap(BranchId::getValueWithoutCookie);
  }

}

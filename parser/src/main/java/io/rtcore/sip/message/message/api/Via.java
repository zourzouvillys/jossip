/**
 * 
 */
package io.rtcore.sip.message.message.api;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.FlagParameterDefinition;
import io.rtcore.sip.message.parameters.impl.HostParameterDefinition;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;

/**
 * 
 */
public final class Via extends BaseParameterizedObject<Via> {
  public static final Token BRANCH = Token.from("branch");
  public static final FlagParameterDefinition RPORT = new FlagParameterDefinition("rport");
  public static final HostParameterDefinition RECEIVED = new HostParameterDefinition("received");
  public static final String VIA_NAME = "branch";
  private final ViaProtocol protocol;
  private final HostAndPort sentBy;

  public Via(ViaProtocol protocol, String hostString) {
    this(protocol, HostAndPort.fromString(hostString), null);
  }

  public Via(ViaProtocol protocol, HostAndPort host) {
    this(protocol, host, null);
  }

  public Via(ViaProtocol protocol, InetSocketAddress host) {
    this(protocol, HostAndPort.fromParts(host.getHostString(), host.getPort()), null);
  }

  public Via(ViaProtocol protocol, String hostString, Parameters parameters) {
    this(protocol, HostAndPort.fromString(hostString), parameters);
  }

  public Via(ViaProtocol protocol, HostAndPort host, Parameters parameters) {
    this.protocol = protocol;
    this.sentBy = host;
    this.parameters = Optional.ofNullable(parameters).orElse(DefaultParameters.EMPTY.withParameter(BRANCH, Token.from(createBranch())));
    if (!this.parameters.contains(BRANCH)) {
      this.parameters = this.parameters.withParameter(BRANCH, Token.from(createBranch()));
    }
  }

  public Via withBranch(BranchId branch) {
    return this.replaceParameter(ParameterUtils.Branch, branch.asToken());
  }

  public Via withRPort() {
    return this.withoutParameter(ParameterUtils.RPort.name()).withParameter(ParameterUtils.RPort.name());
  }

  public Via withRPort(int port) {
    Preconditions.checkArgument(port > 0 && port < 65536);
    return this.replaceParameter(ParameterUtils.RPort, Token.from(port));
  }

  public Via withReceived(InetAddress addr) {
    return this.replaceParameter(ParameterUtils.Received, Token.from(addr.getHostAddress()));
  }

  @Override
  public Via withParameters(Parameters parameters) {
    return new Via(this.protocol, this.sentBy, parameters);
  }

  @Deprecated
  public Via withNewBranch() {
    return this.withParameter(BRANCH, Token.from(createBranch()));
  }

  @Deprecated
  public static String createBranch() {
    return "z9hG4bK" + UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * Returns the value of the "branch" parameter, if there is one.
   */
  public Optional<String> getBranch() {
    return this.parameters.getParameter(ParameterUtils.Branch).map(tok -> tok.toString());
  }

  /**
   * @return true if there is an rport parameter (not if there is any value!)
   */
  public boolean hasRport() {
    return this.parameters.contains("rport");
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(protocol()).append(' ').append(sentBy());
    if (getParameters().isPresent()) {
      sb.append(getParameters().get());
    }
    return sb.toString();
  }

  public ViaProtocol protocol() {
    return this.protocol;
  }

  public HostAndPort sentBy() {
    return this.sentBy;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Via)) return false;
    final Via other = (Via) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$protocol = this.protocol();
    final Object other$protocol = other.protocol();
    if (this$protocol == null ? other$protocol != null : !this$protocol.equals(other$protocol)) return false;
    final Object this$sentBy = this.sentBy();
    final Object other$sentBy = other.sentBy();
    if (this$sentBy == null ? other$sentBy != null : !this$sentBy.equals(other$sentBy)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Via;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $protocol = this.protocol();
    result = result * PRIME + ($protocol == null ? 43 : $protocol.hashCode());
    final Object $sentBy = this.sentBy();
    result = result * PRIME + ($sentBy == null ? 43 : $sentBy.hashCode());
    return result;
  }
}

/**
 * 
 */
package com.jive.sip.message.api;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.FlagParameterDefinition;
import com.jive.sip.parameters.impl.HostParameterDefinition;
import com.jive.sip.parameters.tools.ParameterUtils;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */

@Value
@EqualsAndHashCode(callSuper = true)
public class Via extends BaseParameterizedObject<Via> {

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
    this.parameters =
      Optional.ofNullable(parameters)
        .orElse(DefaultParameters.EMPTY.withParameter(BRANCH, Token.from(createBranch())));
    if (!this.parameters.contains(BRANCH)) {
      this.parameters = this.parameters.withParameter(BRANCH, Token.from(createBranch()));
    }
  }

  public Via withBranch(BranchId branch) {
    return this.replaceParameter(ParameterUtils.Branch, branch.asToken());
  }

  public Via withRPort() {
    return this
      .withoutParameter(ParameterUtils.RPort.getName())
      .withParameter(ParameterUtils.RPort.getName());
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
    sb.append(getProtocol()).append(' ').append(getSentBy());
    if (getParameters().isPresent()) {
      sb.append(getParameters().get());
    }
    return sb.toString();
  }

}

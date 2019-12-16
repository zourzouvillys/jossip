package com.jive.sip.parameters.tools;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.SipParameterDefinition;
import com.jive.sip.parameters.impl.FlagParameterDefinition;
import com.jive.sip.parameters.impl.HostParameterDefinition;
import com.jive.sip.parameters.impl.QuotedStringParameterDefinition;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

public abstract class ParameterUtils {

  public static final SipParameterDefinition<Token> Tag = createTokenParameterDefinition("tag");
  public static final SipParameterDefinition<Token> Expires = createTokenParameterDefinition("expires");
  public static final SipParameterDefinition<String> RegId = createQuotedStringParameterDefinition("reg-id");
  public static final SipParameterDefinition<String> PlusSipDotInstance = createQuotedStringParameterDefinition("+sip.instance");
  public static final SipParameterDefinition<Token> Branch = createTokenParameterDefinition("branch");
  public static final SipParameterDefinition<Token> Received = createTokenParameterDefinition("received");
  public static final SipParameterDefinition<Token> RPort = createTokenParameterDefinition("rport");

  public static SipParameterDefinition<Token> createFlagParameterDefinition(final Token name) {
    return createFlagParameterDefinition(name.toString());
  }

  public static SipParameterDefinition<Token> createFlagParameterDefinition(final CharSequence name) {
    return new FlagParameterDefinition(name);
  }

  public static SipParameterDefinition<Token> createTokenParameterDefinition(final Token name) {
    return new TokenParameterDefinition(name.toString());
  }

  public static SipParameterDefinition<Token> createTokenParameterDefinition(final CharSequence name) {
    return new TokenParameterDefinition(name);
  }

  public static SipParameterDefinition<String> createQuotedStringParameterDefinition(final Token name) {
    return new QuotedStringParameterDefinition(name.toString());
  }

  public static SipParameterDefinition<String> createQuotedStringParameterDefinition(final CharSequence name) {
    return new QuotedStringParameterDefinition(name);
  }

  public static SipParameterDefinition<HostAndPort> createHostAndPortParameterDefinition(final Token name) {
    return new HostParameterDefinition(name.toString());
  }

  public static SipParameterDefinition<HostAndPort> createHostAndPortParameterDefinition(final CharSequence name) {
    return new HostParameterDefinition(name);
  }

}

package io.rtcore.sip.message.message.api;

import java.io.Serializable;
import java.net.URI;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import com.google.common.base.Preconditions;

import io.rtcore.sip.common.NameAddress;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.uri.Uri;

/**
 * Represents a name and address
 *
 *
 */
@SuppressWarnings("serial")
public class NameAddr extends BaseParameterizedObject<NameAddr> implements Serializable, NameAddress {
  /**
   * Static singleton *
   */
  public static final NameAddr STAR = new NameAddr();
  public static final SipParameterDefinition<Token> PTag = ParameterUtils.createTokenParameterDefinition(Token.from("tag"));
  public static final SipParameterDefinition<Token> PExpires = ParameterUtils.createTokenParameterDefinition(Token.from("expires"));

  public static final SipParameterDefinition<String> PSipInstance = ParameterUtils.createQuotedStringParameterDefinition("+sip.instance");
  public static final SipParameterDefinition<Token> PRegId = ParameterUtils.createTokenParameterDefinition("reg-id");

  private static final Function<? super Token, String> TOKEN_TRANSFORMER = input -> input.toString();
  /**
   * The name.
   *
   * TODO: we need to keep track of this being a quoted string or not, as a serializaed version
   * should come out the same way it came in.
   */
  private final String name;
  /**
   * The Uri in this {@link NameAddr}. You may want to use the Uri apply() or adapt() methods to
   * convert it to the type you want.
   */
  private final Uri address;

  public NameAddr(final Uri address) {
    this(null, address, null);
  }

  public NameAddr(final Uri address, final Parameters parameters) {
    this(null, address, parameters);
  }

  public NameAddr(final String name, final Uri address) {
    this(name, address, null);
  }

  public NameAddr(final String name, final Uri address, final Parameters parameters) {
    Preconditions.checkNotNull(address);
    this.address = address;
    this.name = name;
    this.parameters =
      parameters == null ? DefaultParameters.EMPTY
                         : parameters;
  }

  private NameAddr() {
    this.name = null;
    this.address = null;
    this.parameters = null;
  }

  public URI uri() {
    return this.address.uri();
  }

  public Optional<String> getName() {
    return Optional.ofNullable(this.name);
  }

  @Override
  public String toString() {
    final StringBuilder result =
      new StringBuilder().append(this.name != null ? this.name + " "
                      : "");
    result.append("<").append(this.address.toString()).append(">");
    if (this.parameters != null) {
      for (final RawParameter p : this.parameters.getRawParameters()) {
        // TODO: this doesn't keep quoted values ...
        result.append(";")
          .append(p.name())
          .append((p.value() == null) || (p.value() instanceof FlagParameterValue) ? ""
                                                                              : "=" + p.value());
      }
    }
    return result.toString();
  }

  @Override
  public NameAddr withParameters(final Parameters parameters) {
    return new NameAddr(this.name, this.address, parameters);
  }

  public Optional<String> getTag() {
    return this.getParameter(PTag).map(TOKEN_TRANSFORMER);
  }

  public NameAddr withTag(final String tag) {
    return this.withoutParameter(PTag.name()).withParameter(PTag.name(), Token.from(tag));
  }

  public NameAddr withTag(final Optional<String> tag) {
    final NameAddr res = this.withoutParameter(PTag.name());
    if (tag.filter(e -> !e.isBlank()).isPresent()) {
      return res.withParameter(PTag.name(), Token.from(tag.get()));
    }
    return res;
  }

  public NameAddr withoutName() {
    return new NameAddr(null, this.address, this.parameters);
  }

  public NameAddr withName(final String name) {
    return new NameAddr(name, this.address, this.parameters);
  }

  public NameAddr withAddress(final Uri address) {
    return new NameAddr(this.name, address, this.parameters);
  }

  public Optional<Integer> getExpires() {
    return this.getParameter(PExpires).map(val -> Integer.parseInt(val.toString()));
  }

  public OptionalInt expiresSeconds() {
    return this.getParameter(PExpires).map(val -> OptionalInt.of(Integer.parseInt(val.toString()))).orElse(OptionalInt.empty());
  }

  public OptionalInt regId() {
    return this.getParameter(PRegId).map(val -> OptionalInt.of(Integer.parseInt(val.toString()))).orElse(OptionalInt.empty());
  }

  public Optional<URI> instanceId() {
    return this.getParameter(PSipInstance).map(val -> val.replaceAll("^<|>$", "")).map(URI::create);
  }

  public NameAddr withExpires(final int seconds) {
    return this.withoutParameter(PExpires.name()).withParameter(PExpires.name(), Token.from(seconds));
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof NameAddr other)) {
      return false;
    }
    if (!other.canEqual(this) || !super.equals(o)) {
      return false;
    }
    final Object this$name = this.name;
    final Object other$name = other.name;
    if (this$name == null ? other$name != null
                          : !this$name.equals(other$name)) {
      return false;
    }
    final Object this$address = this.address();
    final Object other$address = other.address();
    if (this$address == null ? other$address != null
                             : !this$address.equals(other$address)) {
      return false;
    }
    return true;
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof NameAddr;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $name = this.name;
    result =
      (result * PRIME)
        + ($name == null ? 43
                         : $name.hashCode());
    final Object $address = this.address();
    return (result * PRIME)
      + ($address == null ? 43
                          : $address.hashCode());
  }

  /**
   * The Uri in this {@link NameAddr}. You may want to use the Uri apply() or adapt() methods to
   * convert it to the type you want.
   */
  @Override
  public Uri address() {
    return this.address;
  }

  public String encode() {
    return RfcSerializerManager.defaultSerializer().writeValueAsString(this);
  }

  public static NameAddr of(final Uri address) {
    return new NameAddr(address);
  }

  @Override
  public Optional<String> displayName() {
    return Optional.ofNullable(this.name);
  }


}

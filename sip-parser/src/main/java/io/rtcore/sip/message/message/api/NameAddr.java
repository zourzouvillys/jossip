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
import io.rtcore.sip.message.parameters.api.QuotedString;
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

  private static final Function<? super Token, String> TOKEN_TRANSFORMER = new Function<Token, String>() {
    @Override
    public String apply(Token input) {
      return input.toString();
    }
  };
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
    String result =
      this.name != null ? this.name + " "
                        : "";
    result += "<" + this.address.toString() + ">";
    if (this.parameters != null) {
      for (final RawParameter p : this.parameters.getRawParameters()) {
        // TODO: this doesn't keep quoted values ...
        result +=
          ";"
            + p.name()
            + ((p.value() == null) || (p.value() instanceof FlagParameterValue) ? ""
                                                                                : "=" + p.value());
      }
    }
    return result;
  }

  @Override
  public NameAddr withParameters(final Parameters parameters) {
    return new NameAddr(this.name, this.address, parameters);
  }

  public Optional<String> getTag() {
    return getParameter(PTag).map(TOKEN_TRANSFORMER);
  }

  public NameAddr withTag(String tag) {
    return withoutParameter(PTag.name()).withParameter(PTag.name(), Token.from(tag));
  }

  public NameAddr withoutName() {
    return new NameAddr(null, address, parameters);
  }

  public NameAddr withName(String name) {
    return new NameAddr(name, address, parameters);
  }

  public NameAddr withAddress(Uri address) {
    return new NameAddr(name, address, parameters);
  }

  public Optional<Integer> getExpires() {
    return getParameter(PExpires).map(val -> Integer.parseInt(val.toString()));
  }

  public OptionalInt expiresSeconds() {
    return getParameter(PExpires).map(val -> OptionalInt.of(Integer.parseInt(val.toString()))).orElse(OptionalInt.empty());
  }

  public NameAddr withExpires(int seconds) {
    return withoutParameter(PExpires.name()).withParameter(PExpires.name(), Token.from(seconds));
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof NameAddr))
      return false;
    final NameAddr other = (NameAddr) o;
    if (!other.canEqual((Object) this))
      return false;
    if (!super.equals(o))
      return false;
    final Object this$name = this.name;
    final Object other$name = other.name;
    if (this$name == null ? other$name != null
                          : !this$name.equals(other$name))
      return false;
    final Object this$address = this.address();
    final Object other$address = other.address();
    if (this$address == null ? other$address != null
                             : !this$address.equals(other$address))
      return false;
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
    result =
      (result * PRIME)
        + ($address == null ? 43
                            : $address.hashCode());
    return result;
  }

  /**
   * The Uri in this {@link NameAddr}. You may want to use the Uri apply() or adapt() methods to
   * convert it to the type you want.
   */
  public Uri address() {
    return this.address;
  }

  public String encode() {
    return RfcSerializerManager.defaultSerializer().writeValueAsString(this);
  }

  public static NameAddr of(Uri address) {
    return new NameAddr(address);
  }

  @Override
  public Optional<String> displayName() {
    return Optional.ofNullable(this.name);
  }

}

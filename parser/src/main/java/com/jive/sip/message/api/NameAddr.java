package com.jive.sip.message.api;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.SipParameterDefinition;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.tools.ParameterUtils;
import com.jive.sip.uri.api.Uri;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a name and address
 * 
 * @author theo
 * 
 */
@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class NameAddr extends BaseParameterizedObject<NameAddr> implements Serializable {

  /** Static singleton **/
  public static final NameAddr STAR = new NameAddr();

  public static final SipParameterDefinition<Token> PTag = ParameterUtils.createTokenParameterDefinition(Token.from("tag"));
  public static final SipParameterDefinition<Token> PExpires = ParameterUtils.createTokenParameterDefinition(Token.from("expires"));

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
   * 
   */

  private final String name;

  /**
   * The Uri in this {@link NameAddr}. You may want to use the Uri apply() or adapt() methods to
   * convert it to the type you want.
   */
  @Getter
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
            + p.getName()
            + ((p.getValue() == null) || (p.getValue() instanceof FlagParameterValue) ? ""
                                                                                      : "=" + p.getValue());
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
    return withoutParameter(PTag.getName()).withParameter(PTag.getName(), Token.from(tag));
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

  public NameAddr withExpires(int seconds) {
    return withoutParameter(PExpires.getName()).withParameter(PExpires.getName(), Token.from(seconds));
  }

}

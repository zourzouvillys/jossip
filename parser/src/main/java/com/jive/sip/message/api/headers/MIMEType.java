/**
 * 
 */
package com.jive.sip.message.api.headers;

import com.google.common.base.Preconditions;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.impl.DefaultParameters;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class MIMEType extends BaseParameterizedObject<MIMEType> {
  public static final MIMEType APPLICATION_SDP = new MIMEType("application", "sdp");
  private final String type;
  private final String subType;

  public MIMEType(final String type, final String subType) {
    this(type, subType, DefaultParameters.EMPTY);
  }

  public MIMEType(final String type, final String subType, final Parameters parameters) {
    Preconditions.checkNotNull(type);
    Preconditions.checkNotNull(subType);
    this.type = type;
    this.subType = subType;
    this.parameters = parameters;
  }

  @Override
  public MIMEType withParameters(Parameters parameters) {
    return new MIMEType(this.type, this.subType, parameters);
  }

}

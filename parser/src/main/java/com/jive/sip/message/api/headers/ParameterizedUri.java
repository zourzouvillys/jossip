/**
 * 
 */
package com.jive.sip.message.api.headers;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.uri.Uri;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ParameterizedUri extends BaseParameterizedObject<ParameterizedUri> {

  private final Uri uri;

  public ParameterizedUri(Uri uri) {
    this(uri, DefaultParameters.EMPTY);
  }

  public ParameterizedUri(Uri uri, Parameters parameters) {
    this.uri = uri;
    this.parameters = parameters;
  }

  @Override
  public ParameterizedUri withParameters(Parameters parameters) {
    return new ParameterizedUri(this.uri, parameters);
  }
}

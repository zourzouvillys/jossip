/**
 * 
 */
package com.jive.sip.message.api.headers;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.uri.Uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */
public final class ParameterizedUri extends BaseParameterizedObject<ParameterizedUri> {
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

  public Uri uri() {
    return this.uri;
  }

  @Override
  public String toString() {
    return "ParameterizedUri(uri=" + this.uri() + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof ParameterizedUri)) return false;
    final ParameterizedUri other = (ParameterizedUri) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    final Object this$uri = this.uri();
    final Object other$uri = other.uri();
    if (this$uri == null ? other$uri != null : !this$uri.equals(other$uri)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ParameterizedUri;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $uri = this.uri();
    result = result * PRIME + ($uri == null ? 43 : $uri.hashCode());
    return result;
  }
}

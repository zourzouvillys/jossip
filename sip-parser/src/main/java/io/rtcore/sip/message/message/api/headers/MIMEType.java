/**
 * 
 */
package io.rtcore.sip.message.message.api.headers;

import com.google.common.base.Preconditions;

import io.rtcore.sip.message.parameters.api.BaseParameterizedObject;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

/**
 * 
 */
public final class MIMEType extends BaseParameterizedObject<MIMEType> {
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

  public String type() {
    return this.type;
  }

  public String subType() {
    return this.subType;
  }

  @Override
  public String toString() {
    return String.format("%s/%s", this.type, this.subType());
    // return "MIMEType(type=" + this.type() + ", subType=" + this.subType() + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof MIMEType))
      return false;
    final MIMEType other = (MIMEType) o;
    if (!other.canEqual((Object) this))
      return false;
    if (!super.equals(o))
      return false;
    final Object this$type = this.type();
    final Object other$type = other.type();
    if (this$type == null ? other$type != null
                          : !this$type.equals(other$type))
      return false;
    final Object this$subType = this.subType();
    final Object other$subType = other.subType();
    if (this$subType == null ? other$subType != null
                             : !this$subType.equals(other$subType))
      return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof MIMEType;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $type = this.type();
    result =
      result * PRIME
        + ($type == null ? 43
                         : $type.hashCode());
    final Object $subType = this.subType();
    result =
      result * PRIME
        + ($subType == null ? 43
                            : $subType.hashCode());
    return result;
  }
}

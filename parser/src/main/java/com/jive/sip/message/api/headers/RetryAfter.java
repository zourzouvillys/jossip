/**
 * 
 */
package com.jive.sip.message.api.headers;

import java.util.Optional;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Value()
@Wither
@EqualsAndHashCode(callSuper = true)
public class RetryAfter extends BaseParameterizedObject<RetryAfter>
{

  private final int delta;
  private final String comment;

  public RetryAfter(int delta)
  {
    this(delta, null, null);
  }

  public RetryAfter(int delta, String comment)
  {
    this(delta, comment, null);
  }

  public RetryAfter(int delta, String comment, Parameters parameters)
  {
    this.delta = delta;
    this.comment = comment;
    this.parameters = parameters;
  }

  public Optional<String> getComment()
  {
    return Optional.ofNullable(comment);
  }

  @Override
  public RetryAfter withParameters(Parameters parameters)
  {
    return new RetryAfter(this.delta, this.comment, parameters);
  }

}

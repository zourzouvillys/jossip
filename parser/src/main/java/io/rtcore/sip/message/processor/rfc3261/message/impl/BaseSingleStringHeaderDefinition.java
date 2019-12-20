package io.rtcore.sip.message.processor.rfc3261.message.impl;

import java.util.Collection;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;

public abstract class BaseSingleStringHeaderDefinition<T> extends BaseHeaderDefinition implements SipHeaderDefinition<T> {

  public BaseSingleStringHeaderDefinition(final String name) {
    super(name);
  }

  public BaseSingleStringHeaderDefinition(final String name, final Character ch) {
    super(name, ch);
  }

  @Override
  public T parse(final Collection<RawHeader> headers) {
    for (final RawHeader header : headers) {
      if (matches(header.name())) {
        // TODO: what do we want to do if there are multiple values? -- TPZ
        return this.parse(header.value());
      }
    }
    return null;
  }

  public abstract T parse(final String value);

}

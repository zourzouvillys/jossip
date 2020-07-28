package io.rtcore.sip.netty.codec;


import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DefaultHeaders.NameValidator;
import io.netty.util.internal.StringUtil;

public class DefaultLastSipContent extends DefaultSipContent implements LastSipContent {
  
  private final SipHeaders trailingHeaders;
  private final boolean validateHeaders;

  public DefaultLastSipContent() {
    this(Unpooled.buffer(0));
  }

  public DefaultLastSipContent(ByteBuf content) {
    this(content, true);
  }

  public DefaultLastSipContent(ByteBuf content, boolean validateHeaders) {
    super(content);
    trailingHeaders = new TrailingSipHeaders(validateHeaders);
    this.validateHeaders = validateHeaders;
  }

  @Override
  public LastSipContent copy() {
    return replace(content().copy());
  }

  @Override
  public LastSipContent duplicate() {
    return replace(content().duplicate());
  }

  @Override
  public LastSipContent retainedDuplicate() {
    return replace(content().retainedDuplicate());
  }

  @Override
  public LastSipContent replace(ByteBuf content) {
    final DefaultLastSipContent dup = new DefaultLastSipContent(content, validateHeaders);
    dup.trailingHeaders().set(trailingHeaders());
    return dup;
  }

  @Override
  public LastSipContent retain(int increment) {
    super.retain(increment);
    return this;
  }

  @Override
  public LastSipContent retain() {
    super.retain();
    return this;
  }

  @Override
  public LastSipContent touch() {
    super.touch();
    return this;
  }

  @Override
  public LastSipContent touch(Object hint) {
    super.touch(hint);
    return this;
  }

  @Override
  public SipHeaders trailingHeaders() {
    return trailingHeaders;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(super.toString());
    buf.append(StringUtil.NEWLINE);
    appendHeaders(buf);

    // Remove the last newline.
    buf.setLength(buf.length() - StringUtil.NEWLINE.length());
    return buf.toString();
  }

  private void appendHeaders(StringBuilder buf) {
    for (Entry<CharSequence, CharSequence> e : trailingHeaders()) {
      buf.append(e.getKey());
      buf.append(": ");
      buf.append(e.getValue());
      buf.append(StringUtil.NEWLINE);
    }
  }

  private static final class TrailingSipHeaders extends DefaultSipHeaders {

    private static final NameValidator<CharSequence> TrailerNameValidator = new NameValidator<CharSequence>() {
      @Override
      public void validateName(CharSequence name) {
        DefaultSipHeaders.SipNameValidator.validateName(name);
        if (SipHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(name)
          || SipHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(name)
          || SipHeaderNames.TRAILER.contentEqualsIgnoreCase(name)) {
          throw new IllegalArgumentException("prohibited trailing header: " + name);
        }
      }
    };

    @SuppressWarnings({ "unchecked" })
    TrailingSipHeaders(boolean validate) {
      super(
        validate,
        validate ? TrailerNameValidator
                 : NameValidator.NOT_NULL);
    }

  }

}

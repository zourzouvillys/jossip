package io.rtcore.sip.message.processor.rfc3261.parsing;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import io.rtcore.sip.message.processor.rfc3261.HeaderParseContext;
import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManager;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

public abstract class AbstractStringParseContext implements HeaderParseContext {

  private final RfcSipMessageManager manager;
  private final byte[] value;
  private int pos = 0;

  protected AbstractStringParseContext(final SipMessageManager manager, final byte[] bytes) {
    Preconditions.checkNotNull(bytes);
    if (manager != null) {
      this.manager = manager.adapt(RfcSipMessageManager.class);
    }
    else {
      this.manager = null;
    }
    this.value = bytes;

  }

  @Override
  public List<String> getValue() {
    return Lists.newArrayList(this.getSingleValue());
  }

  @Override
  public String getSingleValue() {
    return new String(this.value, StandardCharsets.UTF_8);
  }

  @Override
  public byte peek() {
    return this.value[this.pos];
  }

  @Override
  public int position() {
    return this.pos;
  }

  @Override
  public void consume(final int bytes) {
    this.pos += bytes;
  }

  @Override
  public int position(final int offset) {
    Preconditions.checkArgument(offset >= 0);
    Preconditions.checkArgument(this.value.length - offset >= 0);
    final int old = this.pos;
    this.pos = offset;
    return old;
  }

  @Override
  public int length() {
    return this.value.length - this.pos;
  }

  @Override
  public char charAt(final int index) {
    return (char) this.value[index + this.pos];
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {

    final byte[] data = new byte[end - start];

    for (int i = start, x = 0; i < end; ++i, x++) {
      data[x] = this.value[this.pos + i];
    }

    return new String(data, StandardCharsets.UTF_8);

  }

  @Override
  public void get(final byte[] target, final int offset, final int len) {

    Preconditions.checkPositionIndexes(offset, len, this.length());

    for (int i = 0; i < len; ++i) {
      target[i] = this.value[i + this.pos + offset];
    }

  }

  @Override
  public void get(final byte[] target) {
    this.get(target, 0, target.length);
  }

  @Override
  public SipMessageManager getManager() {
    return this.manager;
  }

}

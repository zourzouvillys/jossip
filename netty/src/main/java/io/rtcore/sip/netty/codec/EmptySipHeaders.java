package io.rtcore.sip.netty.codec;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EmptyHeaders;
import io.netty.handler.codec.Headers;

public class EmptySipHeaders implements SipHeaders {

  protected static final SipHeaders INSTANCE = new EmptySipHeaders();

  @Override
  public void add(CharSequence name, CharSequence value) {
    throw new UnsupportedOperationException("Unimplemented Method: SipHeaders.add invoked.");
  }

  @Override
  public CharSequence get(CharSequence contentLength) {
    return null;
  }

  @Override
  public Iterator<Entry<CharSequence, CharSequence>> iterator() {
    return Collections.<Entry<CharSequence, CharSequence>>emptyList().iterator();
  }

  @Override
  public void set(SipHeaders headers) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipHeaders.set invoked.");
  }

  @Override
  public List<CharSequence> getAll(CharSequence header) {
    return Collections.emptyList();
  }

  @Override
  public void set(CharSequence name, CharSequence value) {
    throw new UnsupportedOperationException("Unimplemented Method: SipHeaders.set invoked.");
  }

  @Override
  public boolean contains(CharSequence contentLength) {
    return false;
  }

  @Override
  public SipHeaders copy() {
    return this;
  }

  @Override
  public void encode(ByteBuf buf) {
    // none.
  }

  @Override
  public Set<CharSequence> names() {
    return Collections.emptySet();
  }

  @Override
  public Headers<? extends CharSequence, ? extends CharSequence, ?> asHeaders() {
    return new EmptyHeaders<>();
  }

  @Override
  public void set(CharSequence name, List<CharSequence> values) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipHeaders.set invoked.");
  }

  @Override
  public void addAll(CharSequence name, Iterable<? extends CharSequence> values) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipHeaders.addAll invoked.");
  }

}

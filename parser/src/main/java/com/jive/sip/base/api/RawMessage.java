package com.jive.sip.base.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Holder for a header name and value, as encountered in a SIP message.
 * 
 * This class does not know anything about header types or parsing, it simply keeps each header as a
 * {@link RawHeader} for parsing at a later point.
 * 
 * @author theo
 * 
 */

@RequiredArgsConstructor(staticName = "create")
public class RawMessage {
  
  @NonNull
  private final String initialLine;
  private final Multimap<String, RawHeader> headers = LinkedListMultimap.create();
  private byte[] body;

  public void addHeader(final String name, final String value) {
    this.addHeader(new RawHeader(name, value));
  }

  public void addHeader(final RawHeader header) {
    this.headers.put(header.name(), header);
  }

  public Collection<RawHeader> getHeaders() {
    return Collections.unmodifiableCollection(this.headers.values());
  }

  public Set<String> getHeaderNames() {
    return this.headers.keySet();
  }

  public String getInitialLine() {
    return this.initialLine;
  }

  public void setBody(final byte[] body) {
    this.body = body;
  }

  public byte[] getBody() {
    return this.body;
  }

  @Override
  public String toString() {
    return getInitialLine();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof RawMessage) || (obj == null)) {
      return false;
    }
    final RawMessage other = RawMessage.class.cast(obj);
    if (!this.initialLine.equals(other.initialLine)) {
      return false;
    }
    if (!this.headers.equals(other.headers)) {
      return false;
    }
    if (((this.body == null) && (other.body != null)) || ((this.body != null) && (other.body == null))) {
      return false;
    }
    if (this.body != null) {
      if (this.body.length != other.body.length) {
        return false;
      }
      for (int i = 0; i < this.body.length; i++) {
        if (this.body[i] != other.body[i]) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = (result * PRIME) + this.initialLine.hashCode();
    result = (result * PRIME) + this.headers.hashCode();
    if (this.body != null) {
      for (byte b : this.body) {
        result = (result * PRIME) + b;
      }
    }
    return result;
  }

  public Optional<Integer> getContentLength() {
    for (final Entry<String, RawHeader> header : this.headers.entries()) {
      final String name = header.getKey().toLowerCase();
      if (name.equals("content-length") || name.equals("l")) {
        return Optional.of(Integer.parseInt(header.getValue().value()));
      }
    }
    return Optional.empty();
  }

}

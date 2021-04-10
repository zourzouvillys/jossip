package io.rtcore.sip.message.message.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.rtcore.sip.message.uri.Uri;

public class ContactSet implements Iterable<NameAddr> {

  public static ContactSet STAR = new ContactSet();

  //
  private final ImmutableList<NameAddr> contacts;

  private ContactSet() {
    this.contacts = null;
  }

  private ContactSet(final Collection<NameAddr> contacts) {
    this.contacts = ImmutableList.copyOf(contacts);
  }

  public List<NameAddr> values() {
    return this.contacts;
  }

  public boolean isStar() {
    return this.contacts == null;
  }

  public static ContactSet from(final Collection<NameAddr> contacts) {
    return new ContactSet(contacts);
  }

  public int size() {
    return this.contacts.size();
  }

  @Override
  public Iterator<NameAddr> iterator() {
    Preconditions.checkState(!this.isStar());
    return this.contacts.iterator();
  }

  public static ContactSet singleValue(Uri uri) {
    return from(Lists.newArrayList(new NameAddr(uri)));
  }

  public static ContactSet newEmptySet() {
    return from(Collections.emptyList());
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ContactSet))
      return false;
    final ContactSet other = (ContactSet) o;
    if (!other.canEqual((Object) this))
      return false;
    final Object this$contacts = this.contacts;
    final Object other$contacts = other.contacts;
    if (this$contacts == null ? other$contacts != null
                              : !this$contacts.equals(other$contacts))
      return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ContactSet;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $contacts = this.contacts;
    result =
      (result * PRIME)
        + ($contacts == null ? 43
                             : $contacts.hashCode());
    return result;
  }
}

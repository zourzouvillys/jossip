package io.rtcore.sip.common;

import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, visibility = ImplementationVisibility.PACKAGE)
public interface ContactsOrStar {

  /**
   * true if this represents the * contact. if it does, then no addresses will be present.
   */

  @Value.Parameter
  boolean isStar();

  /**
   * each contact.
   */

  @Value.Parameter
  List<NameAddress> addresses();

  // ---

  default boolean isEmpty() {
    return !isStar() && addresses().isEmpty();
  }

  // ---

  static ContactsOrStar star() {
    return STAR;
  }

  static ContactsOrStar of(NameAddress addresses) {
    return ImmutableContactsOrStar.of(false, List.of(addresses));
  }

  static ContactsOrStar of(Iterable<NameAddress> addresses) {
    return ImmutableContactsOrStar.of(false, addresses);
  }

  static final ContactsOrStar STAR = ImmutableContactsOrStar.of(true, List.of());

}

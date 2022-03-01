package io.rtcore.sip.channels.netty.codec;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.google.common.base.Verify;

@Value.Immutable(builder = false)
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class }, visibility = ImplementationVisibility.PACKAGE)
public interface SipKeepalive {

  static final SipKeepalive ONE = ImmutableSipKeepalive.of(1);
  static final SipKeepalive TWO = ImmutableSipKeepalive.of(2);

  @Value.Parameter
  int count();

  public static SipKeepalive crlf(int count) {

    Verify.verify(count > 0);

    switch (count) {
      case 1:
        return ONE;
      case 2:
        return TWO;
    }

    // for all others ...
    return ImmutableSipKeepalive.of(count);

  }

}

package io.rtcore.sip.signaling.call.adapter;

import java.util.List;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.Uri;

/**
 * state needed for each side of the dialog (us and them) for SIP.
 */

public interface DialogState {

  Uri address();

  String tag();

  UnsignedInteger sequenceNumber();

  SipUri contact();

  List<NameAddr> routeSet();

}

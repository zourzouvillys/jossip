package io.rtcore.sip.core.registration;

import com.google.common.primitives.UnsignedLong;

import io.rtcore.sip.message.message.api.DialogId;
import io.rtcore.sip.message.uri.Uri;

public interface RegistrationKey {

  /**
   * the registrar. this is from the R-URI of a SIP REGISTER.
   */

  Uri registrar();

  /**
   * the principal being registered.
   */

  Uri target();

  /**
   * the actor performing the registration.
   */

  Uri actor();

  /**
   * the unique identifier for the registration "session". in SIP, this is the Call-ID, to, and from
   * tag. all devices should (and almost all seem to) use the same to tag as we provided in the
   * previous response, however it is not strictly required.
   * 
   * non SIP signalling needs some other mechansim.
   * 
   */

  DialogId dialogId();

  /**
   * the transaction ID.
   */

  String transactionId();

  /**
   * the CSeq number for this request.
   */

  UnsignedLong sequenceNumber();

  /**
   * 
   */

}

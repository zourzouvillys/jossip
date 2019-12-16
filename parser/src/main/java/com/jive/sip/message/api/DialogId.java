package com.jive.sip.message.api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Iterator;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.jive.sip.message.SipRequest;
import com.jive.sip.message.SipResponse;
import com.jive.sip.message.api.headers.CallId;

import lombok.Value;
import lombok.With;

/**
 * A dialog pair between two endpoints.
 * 
 * @author theo
 */

@Value
@With
public class DialogId {

  private final CallId callId;
  private final String localTag;
  private final String remoteTag;

  public static DialogId fromRemote(final SipRequest req) {
    return new DialogId(req.getCallId(), req.getToTag(), req.getFromTag());
  }

  public static DialogId fromLocal(final SipResponse res) {
    return new DialogId(res.getCallId(), res.getFromTag(), res.getToTag());
  }

  public static DialogId fromLocal(final SipRequest req) {
    return new DialogId(req.getCallId(), req.getFromTag(), req.getToTag());
  }

  /**
   * Create based on a received request and a local tag we provide.
   * 
   * @param req
   *          The request to use from the from tag of.
   * 
   * @param localTag
   *          The local tag.
   * 
   * @throws IllegalStateException
   *           The request had a tag in the 'To' header.
   * 
   * @return The new DialogId
   * 
   */

  public static DialogId fromRemote(final SipRequest req, final String localTag) {
    Preconditions.checkState(req.getToTag() == null, "sipreq had tag in To header");
    return new DialogId(req.getCallId(), localTag, req.getFromTag());
  }

  public static DialogId fromRemote(SipResponse res) {
    return new DialogId(res.getCallId(), res.getFromTag(), res.getToTag());
  }

  /**
   * Fetches a dialog ID from an incoming Replaces header.
   * 
   * @param replaces
   * @return
   */

  public static DialogId fromRemote(Replaces replaces) {
    return new DialogId(replaces.callId(), replaces.getToTag(), replaces.getFromTag());
  }

  /**
   * Converts a dialog ID token to a DialogId.
   * 
   * @param token
   *          A token previously returned by fromToken().
   * 
   * @return
   */

  public static DialogId fromToken(String token) {
    Iterator<String> it = Splitter.on(':').split(token).iterator();
    CallId callId = new CallId(it.next());
    String local = it.next();
    String remote = it.next();
    return new DialogId(callId, local, remote);
  }

  /**
   * Returns a string token which can be used with fromToken(), and will also always be comparable
   * as a string.
   * 
   * @return
   */

  public String getToken() {
    return String.format("%s:%s:%s", callId().getValue(), nullToEmpty(localTag()), nullToEmpty(remoteTag()));
  }

  public DialogId swap() {
    return new DialogId(callId(), remoteTag(), localTag());
  }

  public boolean hasLocalTag() {
    return !isNullOrEmpty(localTag());
  }

}

package io.rtcore.sip.message.message.api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Iterator;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.headers.CallId;

/**
 * A dialog pair between two endpoints.
 * 
 * 
 */
public final class DialogId {
  private final CallId callId;
  private final String localTag;
  private final String remoteTag;

  public static DialogId fromRemote(final SipRequest req) {
    return new DialogId(req.callId(), req.toTag(), req.fromTag());
  }

  public static DialogId fromLocal(final SipResponse res) {
    return new DialogId(res.callId(), res.fromTag(), res.toTag());
  }

  public static DialogId fromLocal(final SipRequest req) {
    return new DialogId(req.callId(), req.fromTag(), req.toTag());
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
   */
  public static DialogId fromRemote(final SipRequest req, final String localTag) {
    Preconditions.checkState(req.toTag() == null, "sipreq had tag in To header");
    return new DialogId(req.callId(), localTag, req.fromTag());
  }

  public static DialogId fromRemote(SipResponse res) {
    return new DialogId(res.callId(), res.fromTag(), res.toTag());
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

  public DialogId(final CallId callId, final String localTag, final String remoteTag) {
    this.callId = callId;
    this.localTag = localTag;
    this.remoteTag = remoteTag;
  }

  public CallId callId() {
    return this.callId;
  }

  public String localTag() {
    return this.localTag;
  }

  public String remoteTag() {
    return this.remoteTag;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof DialogId)) return false;
    final DialogId other = (DialogId) o;
    final Object this$callId = this.callId();
    final Object other$callId = other.callId();
    if (this$callId == null ? other$callId != null : !this$callId.equals(other$callId)) return false;
    final Object this$localTag = this.localTag();
    final Object other$localTag = other.localTag();
    if (this$localTag == null ? other$localTag != null : !this$localTag.equals(other$localTag)) return false;
    final Object this$remoteTag = this.remoteTag();
    final Object other$remoteTag = other.remoteTag();
    if (this$remoteTag == null ? other$remoteTag != null : !this$remoteTag.equals(other$remoteTag)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $callId = this.callId();
    result = result * PRIME + ($callId == null ? 43 : $callId.hashCode());
    final Object $localTag = this.localTag();
    result = result * PRIME + ($localTag == null ? 43 : $localTag.hashCode());
    final Object $remoteTag = this.remoteTag();
    result = result * PRIME + ($remoteTag == null ? 43 : $remoteTag.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "DialogId(callId=" + this.callId() + ", localTag=" + this.localTag() + ", remoteTag=" + this.remoteTag() + ")";
  }

  public DialogId withCallId(final CallId callId) {
    return this.callId == callId ? this : new DialogId(callId, this.localTag, this.remoteTag);
  }

  public DialogId withLocalTag(final String localTag) {
    return this.localTag == localTag ? this : new DialogId(this.callId, localTag, this.remoteTag);
  }

  public DialogId withRemoteTag(final String remoteTag) {
    return this.remoteTag == remoteTag ? this : new DialogId(this.callId, this.localTag, remoteTag);
  }
}

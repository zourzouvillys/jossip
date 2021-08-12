package io.rtcore.sip.message.message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import io.rtcore.sip.message.message.api.Reason;

/**
 * A SIP status code and reason phrase.
 * 
 * 
 */
public final class SipResponseStatus {
  /*
   * Pre-made SIP status codes. Only add response codes defined in the core RFCs here.
   */
  /**
   */
  public static final SipResponseStatus TRYING = new SipResponseStatus(100, "Trying");
  /**
   */
  public static final SipResponseStatus RINGING = new SipResponseStatus(180, "Ringing");
  public static final SipResponseStatus PROGRESS = new SipResponseStatus(183, "Session Progress");
  /**
   */
  public static final SipResponseStatus OK = new SipResponseStatus(200, "OK");
  public static final SipResponseStatus ACCEPTED = new SipResponseStatus(202, "Accepted");
  /**
   */
  public static final SipResponseStatus MULTIPLE_CHOICES = new SipResponseStatus(300, "Multiple Choices");
  public static final SipResponseStatus MOVED_PERMANENTLY = new SipResponseStatus(301, "Moved Permanently");
  public static final SipResponseStatus MOVED_TEMPORARILY = new SipResponseStatus(302, "Moved Temporarily");
  public static final SipResponseStatus USE_PROXY = new SipResponseStatus(305, "Use Proxy");
  public static final SipResponseStatus ALTERNATIVE_SERVICES = new SipResponseStatus(380, "Alternative Services");
  /**
   */
  public static final SipResponseStatus BAD_REQUEST = new SipResponseStatus(400, "Bad Request");
  public static final SipResponseStatus UNAUTHORIZED = new SipResponseStatus(401, "Unauthorized");
  public static final SipResponseStatus FORBIDDEN = new SipResponseStatus(403, "Forbidden");
  public static final SipResponseStatus NOT_FOUND = new SipResponseStatus(404, "Not Found");
  public static final SipResponseStatus METHOD_NOT_ALLOWED = new SipResponseStatus(405, "Method Not Allowed");
  public static final SipResponseStatus PROXY_AUTHENTICATION_REQUIRED = new SipResponseStatus(407, "Proxy Authentication Required");
  public static final SipResponseStatus REQUEST_TIMEOUT = new SipResponseStatus(408, "Request Timeout");
  public static final SipResponseStatus GONE = new SipResponseStatus(410, "Gone");
  public static final SipResponseStatus UNSUPPORTED_MEDIA_TYPE = new SipResponseStatus(415, "Unsupported Media Type");
  public static final SipResponseStatus UNSUPPORTED_URI_SCHEME = new SipResponseStatus(416, "Unsupported URI Scheme");
  public static final SipResponseStatus BAD_EXTENSION = new SipResponseStatus(420, "Bad Extension");
  public static final SipResponseStatus INTERVAL_TOO_BRIEF = new SipResponseStatus(423, "Interval Too Brief");
  public static final SipResponseStatus FIRST_HOP_LACKS_OUTBOUND_SUPPORT = new SipResponseStatus(439, "First Hop Lacks Outbound Support");
  public static final SipResponseStatus TEMPORARILY_UNAVAILABLE = new SipResponseStatus(480, "Temporarily Unavailable");
  public static final SipResponseStatus CALL_DOES_NOT_EXIST = new SipResponseStatus(481, "Call/Transaction Does Not Exist");
  public static final SipResponseStatus LOOP_DETECTED = new SipResponseStatus(482, "Loop Detected");
  public static final SipResponseStatus TOO_MANY_HOPS = new SipResponseStatus(483, "Too Many Hops");
  public static final SipResponseStatus AMBIGUOUS = new SipResponseStatus(485, "Ambiguous");
  public static final SipResponseStatus BUSY_HERE = new SipResponseStatus(486, "Busy Here");
  public static final SipResponseStatus REQUEST_TERMINATED = new SipResponseStatus(487, "Request Terminated");
  public static final SipResponseStatus NOT_ACCEPTABLE_HERE = new SipResponseStatus(488, "Not Acceptable Here");
  public static final SipResponseStatus BAD_EVENT = new SipResponseStatus(489, "Bad Event");
  public static final SipResponseStatus REQUEST_PENDING = new SipResponseStatus(491, "Request Pending");
  public static final SipResponseStatus SERVER_INTERNAL_ERROR = new SipResponseStatus(500, "Server Internal Error");
  public static final SipResponseStatus NOT_IMPLEMENTED = new SipResponseStatus(501, "Not Implemented");
  public static final SipResponseStatus BAD_GATEWAY = new SipResponseStatus(502, "Bad Gateway");
  public static final SipResponseStatus SERVICE_UNAVAILABLE = new SipResponseStatus(503, "Service Unavailable");
  public static final SipResponseStatus SERVER_TIMEOUT = new SipResponseStatus(504, "Server Time-Out");
  public static final SipResponseStatus VERSION_NOT_SUPPORTED = new SipResponseStatus(505, "Version Not Supported");
  public static final SipResponseStatus MESSAGE_TOO_LARGE = new SipResponseStatus(513, "Message Too Large");
  public static final SipResponseStatus BUSY_EVERYWHERE = new SipResponseStatus(600, "Busy Everywhere");
  public static final SipResponseStatus DECLINE = new SipResponseStatus(603, "Decline");
  public static final SipResponseStatus DOES_NOT_EXIST = new SipResponseStatus(604, "Does Not Exist Anywhere");
  public static final SipResponseStatus NOT_ACCEPTABLE = new SipResponseStatus(606, "Not Acceptable");
  private static final Map<Integer, SipResponseStatus> statuses = Maps.newHashMap();

  static {
    for (Field f : SipResponseStatus.class.getDeclaredFields()) {
      if (!Modifier.isStatic(f.getModifiers()) || !SipResponseStatus.class.isAssignableFrom(f.getType())) {
        continue;
      }
      try {
        SipResponseStatus status = (SipResponseStatus) f.get(null);
        statuses.put(status.code(), status);
      }
      catch (Exception e) {
      }
      // swallow.
    }
  }

  /*
   * 
   */
  private final int code;
  private final String reason;

  @Override
  public String toString() {
    return new StringBuilder().append(code()).append(' ').append(reason()).toString();
  }

  public boolean isFinal() {
    return this.code >= 200;
  }

  public boolean isSuccess() {
    return (this.code / 100) == 2;
  }

  public boolean isFailure() {
    return (this.code) >= 300;
  }

  public SipResponseStatus withReason(final String string) {
    return new SipResponseStatus(this.code, string);
  }

  public boolean isRedirect() {
    return (this.code / 100) == 3;
  }

  public static SipResponseStatus fromCode(int code) {
    return statuses.get(code);
  }

  public SipResponseStatus(final int code, final String reason) {
    this.code = code;
    this.reason = reason;
  }

  public int code() {
    return this.code;
  }

  public String reason() {
    return this.reason;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof SipResponseStatus))
      return false;
    final SipResponseStatus other = (SipResponseStatus) o;
    if (this.code() != other.code())
      return false;
    final Object this$reason = this.reason();
    final Object other$reason = other.reason();
    if (this$reason == null ? other$reason != null
                            : !this$reason.equals(other$reason))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = (result * PRIME) + this.code();
    final Object $reason = this.reason();
    result =
      (result * PRIME)
        + ($reason == null ? 43
                           : $reason.hashCode());
    return result;
  }

  public List<Reason> asReasonList() {
    return ImmutableList.of(Reason.fromSipStatus(this));
  }

}

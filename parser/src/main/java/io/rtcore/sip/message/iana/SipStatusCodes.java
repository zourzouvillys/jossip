package io.rtcore.sip.message.iana;

import com.google.common.base.Preconditions;

// @formatter:off
// csvq -q -N -f FIXED -r . 'SELECT "/** " || comment || " */\n" || id || "(" || code || ", \"" || reason || "\"),\n" FROM (SELECT `Response Code` AS code, REPLACE(UPPER(`Description`), " ", "_") AS id, `Description` AS reason, COALESCE(`Reference`, "") AS comment FROM `sip-parameters-7.csv`)'
// @formatter:on

public enum SipStatusCodes {

  /**  */
  TRYING(100, "Trying"),

  /**  */
  RINGING(180, "Ringing"),

  /**  */
  CALL_IS_BEING_FORWARDED(181, "Call Is Being Forwarded"),

  /**  */
  QUEUED(182, "Queued"),

  /**  */
  SESSION_PROGRESS(183, "Session Progress"),

  /** [RFC6228] */
  EARLY_DIALOG_TERMINATED(199, "Early Dialog Terminated"),

  /**  */
  OK(200, "OK"),

  /**
   * [RFC6665]
   * 
   * Due to response handling in forking cases, any 202 response to a SUBSCRIBE request may be
   * absorbed by a proxy, and thus it can never be guaranteed to be received by the UAC.
   * Furthermore, there is no actual processing difference for a 202 as compared to a 200; a NOTIFY
   * request is sent after the subscription is processed, and it conveys the correct state. SIP
   * interoperability tests found that implementations were handling 202 differently from 200,
   * leading to incompatibilities. Therefore, the 202 response is being deprecated to make it clear
   * there is no such difference and 202 should not be handled differently than 200.
   * 
   * Implementations conformant with the current specification MUST treat an incoming 202 response
   * as identical to a 200 response and MUST NOT generate 202 response codes to SUBSCRIBE or NOTIFY
   * requests.
   * 
   * This document also updates [RFC4660], which reiterates the 202-based behavior in several
   * places. Implementations compliant with the present document MUST NOT send a 202 response to a
   * SUBSCRIBE request and will send an alternate success response (such as 200) in its stead.
   * 
   */

  @Deprecated
  ACCEPTED(202, "Accepted"),

  /** [RFC5839] */
  NO_NOTIFICATION(204, "No Notification"),

  /**  */
  MULTIPLE_CHOICES(300, "Multiple Choices"),

  /**  */
  MOVED_PERMANENTLY(301, "Moved Permanently"),

  /**  */
  MOVED_TEMPORARILY(302, "Moved Temporarily"),

  /**  */
  USE_PROXY(305, "Use Proxy"),

  /**  */
  ALTERNATIVE_SERVICE(380, "Alternative Service"),

  /**  */
  BAD_REQUEST(400, "Bad Request"),

  /**  */
  UNAUTHORIZED(401, "Unauthorized"),

  /**  */
  PAYMENT_REQUIRED(402, "Payment Required"),

  /**  */
  FORBIDDEN(403, "Forbidden"),

  /**  */
  NOT_FOUND(404, "Not Found"),

  /**  */
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

  /**  */
  NOT_ACCEPTABLE(406, "Not Acceptable"),

  /**  */
  PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),

  /**  */
  REQUEST_TIMEOUT(408, "Request Timeout"),

  /**  */
  GONE(410, "Gone"),

  /** [RFC3903] */
  CONDITIONAL_REQUEST_FAILED(412, "Conditional Request Failed"),

  /**  */
  REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),

  /**  */
  REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),

  /**  */
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

  /**  */
  UNSUPPORTED_URI_SCHEME(416, "Unsupported URI Scheme"),

  /** [RFC4412] */
  UNKNOWN_RESOURCE_PRIORITY(417, "Unknown Resource-Priority"),

  /**  */
  BAD_EXTENSION(420, "Bad Extension"),

  /**  */
  EXTENSION_REQUIRED(421, "Extension Required"),

  /** [RFC4028] */
  SESSION_INTERVAL_TOO_SMALL(422, "Session Interval Too Small"),

  /**  */
  INTERVAL_TOO_BRIEF(423, "Interval Too Brief"),

  /** [RFC6442] */
  BAD_LOCATION_INFORMATION(424, "Bad Location Information"),

  /** [RFC8224] */
  USE_IDENTITY_HEADER(428, "Use Identity Header"),

  /** [RFC3892] */
  PROVIDE_REFERRER_IDENTITY(429, "Provide Referrer Identity"),

  /** [RFC5626] */
  FLOW_FAILED(430, "Flow Failed"),

  /** [RFC5079] */
  ANONYMITY_DISALLOWED(433, "Anonymity Disallowed"),

  /** [RFC8224] */
  BAD_IDENTITY_INFO(436, "Bad Identity Info"),

  /** [RFC8224] */
  UNSUPPORTED_CREDENTIAL(437, "Unsupported Credential"),

  /** [RFC8224] */
  INVALID_IDENTITY_HEADER(438, "Invalid Identity Header"),

  /** [RFC5626] */
  FIRST_HOP_LACKS_OUTBOUND_SUPPORT(439, "First Hop Lacks Outbound Support"),

  /** [RFC5393] */
  MAX_BREADTH_EXCEEDED(440, "Max-Breadth Exceeded"),

  /** [RFC6086] */
  BAD_INFO_PACKAGE(469, "Bad Info Package"),

  /** [RFC5360] */
  CONSENT_NEEDED(470, "Consent Needed"),

  /**  */
  TEMPORARILY_UNAVAILABLE(480, "Temporarily Unavailable"),

  /**  */
  CALL_TRANSACTION_DOES_NOT_EXIST(481, "Call/Transaction Does Not Exist"),

  /**  */
  LOOP_DETECTED(482, "Loop Detected"),

  /**  */
  TOO_MANY_HOPS(483, "Too Many Hops"),

  /**  */
  ADDRESS_INCOMPLETE(484, "Address Incomplete"),

  /**  */
  AMBIGUOUS(485, "Ambiguous"),

  /**  */
  BUSY_HERE(486, "Busy Here"),

  /**  */
  REQUEST_TERMINATED(487, "Request Terminated"),

  /**  */
  NOT_ACCEPTABLE_HERE(488, "Not Acceptable Here"),

  /** [RFC6665] */
  BAD_EVENT(489, "Bad Event"),

  /**  */
  REQUEST_PENDING(491, "Request Pending"),

  /**  */
  UNDECIPHERABLE(493, "Undecipherable"),

  /** [RFC3329] */
  SECURITY_AGREEMENT_REQUIRED(494, "Security Agreement Required"),

  /**  */
  SERVER_INTERNAL_ERROR(500, "Server Internal Error"),

  /**  */
  NOT_IMPLEMENTED(501, "Not Implemented"),

  /**  */
  BAD_GATEWAY(502, "Bad Gateway"),

  /**  */
  SERVICE_UNAVAILABLE(503, "Service Unavailable"),

  /**  */
  SERVER_TIMEOUT(504, "Server Time-out"),

  /**  */
  VERSION_NOT_SUPPORTED(505, "Version Not Supported"),

  /**  */
  MESSAGE_TOO_LARGE(513, "Message Too Large"),

  /** [RFC8599] */
  PUSH_NOTIFICATION_SERVICE_NOT_SUPPORTED(555, "Push Notification Service Not Supported"),

  /** [RFC3312] */
  PRECONDITION_FAILURE(580, "Precondition Failure"),

  /**  */
  BUSY_EVERYWHERE(600, "Busy Everywhere"),

  /**  */
  DECLINE(603, "Decline"),

  /**  */
  DOES_NOT_EXIST_ANYWHERE(604, "Does Not Exist Anywhere"),

  /**  */
  GLOBAL_NOT_ACCEPTABLE(606, "Not Acceptable"),

  /** [RFC8197] */
  UNWANTED(607, "Unwanted"),

  /** [RFC-ietf-sipcore-rejected-09] */
  REJECTED(608, "Rejected"),

  ;

  private final int statusCode;
  private final String reasonPhrase;
  private final SipStatusCategory category;

  private SipStatusCodes(int statusCode, String reasonPhrase) {
    Preconditions.checkArgument((statusCode >= 100) && (statusCode < 700));
    this.statusCode = statusCode;
    this.reasonPhrase = reasonPhrase;
    this.category = defaultCategory(statusCode);
  }

  private static final SipStatusCodes[] LOOKUPS = new SipStatusCodes[600];
  static {
    for (SipStatusCodes code : values()) {
      LOOKUPS[code.statusCode - 100] = code;
    }
  }

  public static SipStatusCodes forStatusCode(int code) {
    if ((code < 100) || (code >= 700)) {
      return null;
    }
    return LOOKUPS[code - 100];
  }

  public static String defaultReason(int status) {
    return defaultReason(status, forStatusCode(defaultCategory(status).defaultStatusCode()).reasonPhrase);
  }

  private static SipStatusCategory defaultCategory(int status) {
    if (status < 100)
      throw new IllegalArgumentException();
    if (status < 200)
      return SipStatusCategory.PROVISIONAL;
    if (status < 300)
      return SipStatusCategory.SUCCESSFUL;
    if (status < 400)
      return SipStatusCategory.REDIRECTION;
    if (status < 500)
      return SipStatusCategory.REQUEST_FAILURE;
    if (status < 600)
      return SipStatusCategory.SERVER_FAILURE;
    if (status < 700)
      return SipStatusCategory.GLOBAL_FAILURE;
    throw new IllegalArgumentException();
  }

  public static String defaultReason(int status, String defaultValue) {
    SipStatusCodes c = forStatusCode(status);
    if (c != null) {
      return c.reasonPhrase;
    }
    return defaultValue;
  }

  public SipStatusCategory category() {
    return this.category;
  }

  public int statusCode() {
    return this.statusCode;
  }

  public String reasonPhrase() {
    return this.reasonPhrase;
  }

}

package io.rtcore.sip.bind;

public class StandardBinder {

  // -- comma seperated, can have multiple per header value:

  // Accept
  // Resource-Priority
  // Accept-Resource-Priority
  // Via/v
  // Alert-Info
  // Error-Info
  // Path
  // P-Asserted-Identity
  // Record-Route
  // Route
  // History-Info
  
  // -- multi value, single element per header value:

  // Authorization
  // Proxy-Authorization
  // Proxy-Authenticate
  // WWW-Authenticate

  // --- token sets:

  // Allow
  // Privacy
  // Proxy-Require
  // Require
  // Request-Disposition
  // Supported/k
  // Unsupported
  
  // --- weird:
  
  // Contact (because of '*')
  
  // --- single:
  
  // (all others are single value headers, should not have multiple hvalues).

  public void bindTo(String path) {

    // first/top
    // last

    // arrays: via, route, record-route,

    // single
    // raw

    // type intrspector

  }

  // Via
  // CSeq
  // CallId
  // ContactSet
  // Authorization

  // ParameterizedUri
  // NameAddr
  // TokenSet
  // UnsignedInteger

  // RValue
  // MIMEType
  // ContentDisposition
  // ZonedDateTime
  // EventSpec
  // HistoryInfo
  // (MIME)Version
  // MinSE
  // RAck
  // Reason
  // Replaces
  // RetryAfter
  // SessonExpires
  // TargetDialog

}

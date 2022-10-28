package io.rtcore.sip.common.iana;

import static io.rtcore.sip.common.iana.Spec.RFC3261;
import static io.rtcore.sip.common.iana.Spec.RFC3262;
import static io.rtcore.sip.common.iana.Spec.RFC3313;
import static io.rtcore.sip.common.iana.Spec.RFC3323;
import static io.rtcore.sip.common.iana.Spec.RFC3325;
import static io.rtcore.sip.common.iana.Spec.RFC3326;
import static io.rtcore.sip.common.iana.Spec.RFC3327;
import static io.rtcore.sip.common.iana.Spec.RFC3329;
import static io.rtcore.sip.common.iana.Spec.RFC3515;
import static io.rtcore.sip.common.iana.Spec.RFC3608;
import static io.rtcore.sip.common.iana.Spec.RFC3841;
import static io.rtcore.sip.common.iana.Spec.RFC3891;
import static io.rtcore.sip.common.iana.Spec.RFC3892;
import static io.rtcore.sip.common.iana.Spec.RFC3903;
import static io.rtcore.sip.common.iana.Spec.RFC3911;
import static io.rtcore.sip.common.iana.Spec.RFC4028;
import static io.rtcore.sip.common.iana.Spec.RFC4412;
import static io.rtcore.sip.common.iana.Spec.RFC4457;
import static io.rtcore.sip.common.iana.Spec.RFC4488;
import static io.rtcore.sip.common.iana.Spec.RFC4538;
import static io.rtcore.sip.common.iana.Spec.RFC4964;
import static io.rtcore.sip.common.iana.Spec.RFC5002;
import static io.rtcore.sip.common.iana.Spec.RFC5009;
import static io.rtcore.sip.common.iana.Spec.RFC5318;
import static io.rtcore.sip.common.iana.Spec.RFC5360;
import static io.rtcore.sip.common.iana.Spec.RFC5373;
import static io.rtcore.sip.common.iana.Spec.RFC5393;
import static io.rtcore.sip.common.iana.Spec.RFC5503;
import static io.rtcore.sip.common.iana.Spec.RFC5626;
import static io.rtcore.sip.common.iana.Spec.RFC5839;
import static io.rtcore.sip.common.iana.Spec.RFC6050;
import static io.rtcore.sip.common.iana.Spec.RFC6086;
import static io.rtcore.sip.common.iana.Spec.RFC6442;
import static io.rtcore.sip.common.iana.Spec.RFC6446;
import static io.rtcore.sip.common.iana.Spec.RFC6665;
import static io.rtcore.sip.common.iana.Spec.RFC6794;
import static io.rtcore.sip.common.iana.Spec.RFC6809;
import static io.rtcore.sip.common.iana.Spec.RFC7044;
import static io.rtcore.sip.common.iana.Spec.RFC7315;
import static io.rtcore.sip.common.iana.Spec.RFC7316;
import static io.rtcore.sip.common.iana.Spec.RFC7433;
import static io.rtcore.sip.common.iana.Spec.RFC7614;
import static io.rtcore.sip.common.iana.Spec.RFC7989;
import static io.rtcore.sip.common.iana.Spec.RFC8224;
import static io.rtcore.sip.common.iana.Spec.RFC8262;
import static io.rtcore.sip.common.iana.Spec.RFC8496;
import static io.rtcore.sip.common.iana.Spec.RFC8498;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.common.SipHeaderLine;

// @formatter:off
// csvq --format FIXED -N 'select "\n/** " || REPLACE(Reference, "\n", "") || " */\n" || UPPER(REPLACE(`Header Name`, "-", "_")) || "(\"" || `Header Name` || "\"" || COALESCE(", \"" || compact || "\"", "") || ")," from `sip-parameters-2.csv`'
// @formatter:on

public enum StandardSipHeaders implements SipHeaderId {

  /** [RFC3261] */
  ACCEPT("Accept", RFC3261),

  /** [RFC3841] */
  ACCEPT_CONTACT("Accept-Contact", "a", RFC3841),

  /** [RFC3261] */
  ACCEPT_ENCODING("Accept-Encoding", RFC3261),

  /** [RFC3261] */
  ACCEPT_LANGUAGE("Accept-Language", RFC3261),

  /** [RFC4412] */
  ACCEPT_RESOURCE_PRIORITY("Accept-Resource-Priority", RFC4412),

  /** [RFC3261] */
  ALERT_INFO("Alert-Info", RFC3261),

  /** [RFC3261] */
  ALLOW("Allow", RFC3261),

  /** [RFC6665] */
  ALLOW_EVENTS("Allow-Events", "u", RFC6665),

  /** [RFC5373] */
  ANSWER_MODE("Answer-Mode", RFC5373),

  /** [RFC3261] */
  AUTHENTICATION_INFO("Authentication-Info", RFC3261),

  /** [RFC3261] */
  AUTHORIZATION("Authorization", RFC3261),

  /** [RFC3261] */
  CALL_ID("Call-ID", "i", RFC3261),

  /** [RFC3261] */
  CALL_INFO("Call-Info", RFC3261),

  /** [3GPP TS 24.229 v13.9.0] [Frederic_Firmin] */
  CELLULAR_NETWORK_INFO("Cellular-Network-Info", Spec.IMS),

  /** [RFC3261] */
  CONTACT("Contact", "m", RFC3261),

  /** [RFC3261] */
  CONTENT_DISPOSITION("Content-Disposition", RFC3261),

  /** [RFC3261] */
  CONTENT_ENCODING("Content-Encoding", "e", RFC3261),

  /** [RFC8262] */
  CONTENT_ID("Content-ID", RFC8262),

  /** [RFC3261] */
  CONTENT_LANGUAGE("Content-Language", RFC3261),

  /** [RFC3261] */
  CONTENT_LENGTH("Content-Length", "l", RFC3261),

  /** [RFC3261] */
  CONTENT_TYPE("Content-Type", "c", RFC3261),

  /** [RFC3261] */
  CSEQ("CSeq", RFC3261),

  /** [RFC3261] */
  DATE("Date", RFC3261),

  /** [RFC3261] */
  ERROR_INFO("Error-Info", RFC3261),

  /** [RFC6665][RFC6446] */
  EVENT("Event", "o", RFC6446),

  /** [RFC3261] */
  EXPIRES("Expires", RFC3261),

  /** [RFC6809] */
  FEATURE_CAPS("Feature-Caps", RFC6809),

  /** [RFC5626] */
  FLOW_TIMER("Flow-Timer", RFC5626),

  /** [RFC3261] */
  FROM("From", "f", RFC3261),

  /** [RFC6442] */
  GEOLOCATION("Geolocation", RFC6442),

  /** [RFC6442] */
  GEOLOCATION_ERROR("Geolocation-Error", RFC6442),

  /** [RFC6442] */
  GEOLOCATION_ROUTING("Geolocation-Routing", RFC6442),

  /** [RFC3261] */

  /** [RFC7044] */
  HISTORY_INFO("History-Info", RFC7044),

  /** [RFC8224] */
  IDENTITY("Identity", "y", RFC8224),

  /** [RFC6086] */
  INFO_PACKAGE("Info-Package", RFC6086),

  /** [RFC3261] */
  IN_REPLY_TO("In-Reply-To", RFC3261),

  /** [RFC3911] */
  JOIN("Join", RFC3911),

  /** [RFC5393] */
  MAX_BREADTH("Max-Breadth", RFC5393),

  /** [RFC3261] */
  MAX_FORWARDS("Max-Forwards", RFC3261),

  /** [RFC3261] */
  MIME_VERSION("MIME-Version", RFC3261),

  /** [RFC3261] */
  MIN_EXPIRES("Min-Expires", RFC3261),

  /** [RFC4028] */
  MIN_SE("Min-SE", RFC4028),

  /** [RFC3261] */
  ORGANIZATION("Organization", RFC3261),

  /** [RFC7315] */
  P_ACCESS_NETWORK_INFO("P-Access-Network-Info", RFC7315),

  /** [RFC4964] */
  P_ANSWER_STATE("P-Answer-State", RFC4964),

  /** [RFC3325] */
  P_ASSERTED_IDENTITY("P-Asserted-Identity", RFC3325),

  /** [RFC6050] */
  P_ASSERTED_SERVICE("P-Asserted-Service", RFC6050),

  /** [RFC7315] */
  P_ASSOCIATED_URI("P-Associated-URI", RFC7315),

  /** [RFC7315] */
  P_CALLED_PARTY_ID("P-Called-Party-ID", RFC7315),

  /** [RFC8496] */
  P_CHARGE_INFO("P-Charge-Info", RFC8496),

  /** [RFC7315] */
  P_CHARGING_FUNCTION_ADDRESSES("P-Charging-Function-Addresses", RFC7315),

  /** [RFC7315] */
  P_CHARGING_VECTOR("P-Charging-Vector", RFC7315),

  /** [RFC5503] */
  P_DCS_TRACE_PARTY_ID("P-DCS-Trace-Party-ID", RFC5503),

  /** [RFC5503] */
  P_DCS_OSPS("P-DCS-OSPS", RFC5503),

  /** [RFC5503] */
  P_DCS_BILLING_INFO("P-DCS-Billing-Info", RFC5503),

  /** [RFC5503] */
  P_DCS_LAES("P-DCS-LAES", RFC5503),

  /** [RFC5503] */
  P_DCS_REDIRECT("P-DCS-Redirect", RFC5503),

  /** [RFC5009] */
  P_EARLY_MEDIA("P-Early-Media", RFC5009),

  /** [RFC3313] */
  P_MEDIA_AUTHORIZATION("P-Media-Authorization", RFC3313),

  /** [RFC3325] */
  P_PREFERRED_IDENTITY("P-Preferred-Identity", RFC3325),

  /** [RFC6050] */
  P_PREFERRED_SERVICE("P-Preferred-Service", RFC6050),

  /** [RFC7316] */
  P_PRIVATE_NETWORK_INDICATION("P-Private-Network-Indication", RFC7316),

  /** [RFC5002] */
  P_PROFILE_KEY("P-Profile-Key", RFC5002),

  /** [RFC5318] */
  P_REFUSED_URI_LIST("P-Refused-URI-List", RFC5318),

  /** [RFC5502] [RFC8498] */
  P_SERVED_USER("P-Served-User", RFC8498),

  /** [RFC4457] */
  P_USER_DATABASE("P-User-Database", RFC4457),

  /** [RFC7315] */
  P_VISITED_NETWORK_ID("P-Visited-Network-ID", RFC7315),

  /** [RFC3327] */
  PATH("Path", RFC3327),

  /** [RFC5360] */
  PERMISSION_MISSING("Permission-Missing", RFC5360),

  /** [RFC6794] */
  POLICY_CONTACT("Policy-Contact", RFC6794),

  /** [RFC6794] */
  POLICY_ID("Policy-ID", RFC6794),

  /** [RFC3261] */
  PRIORITY("Priority", RFC3261),

  /** [3GPP TS 24.229 v13.16.0] [Frederic_Firmin] */
  PRIORITY_SHARE("Priority-Share", Spec.IMS),

  /** [RFC5373] */
  PRIV_ANSWER_MODE("Priv-Answer-Mode", RFC5373),

  /** [RFC3323] */
  PRIVACY("Privacy", RFC3323),

  /** [RFC3261] */
  PROXY_AUTHENTICATE("Proxy-Authenticate", RFC3261),

  /** [RFC3261] */
  PROXY_AUTHORIZATION("Proxy-Authorization", RFC3261),

  /** [RFC3261] */
  PROXY_REQUIRE("Proxy-Require", RFC3261),

  /** [RFC3262] */
  RACK("RAck", RFC3262),

  /** [RFC3326] */
  REASON("Reason", RFC3326),

  /** Reserved to avoid conflict with [RFC6873]. [Adam_Roach] */
  // REASON_PHRASE("Reason-Phrase"),

  /** [RFC3261] */
  RECORD_ROUTE("Record-Route", RFC3261),

  /** [RFC6086] */
  RECV_INFO("Recv-Info", RFC6086),

  /** [RFC7614] */
  REFER_EVENTS_AT("Refer-Events-At", RFC7614),

  /** [RFC4488] */
  REFER_SUB("Refer-Sub", RFC4488),

  /** [RFC3515] */
  REFER_TO("Refer-To", "r", RFC3515),

  /** [RFC3892] */
  REFERRED_BY("Referred-By", "b", RFC3892),

  /** [RFC3841] */
  REJECT_CONTACT("Reject-Contact", "j", RFC3841),

  /** [3GPP TS 24.229 v12.14.0] [Frederic_Firmin] */
  RELAYED_CHARGE("Relayed-Charge", Spec.IMS),

  /** [RFC3891] */
  REPLACES("Replaces", RFC3891),

  /** [RFC3261] */
  REPLY_TO("Reply-To", RFC3261),

  /** [RFC3841] */
  REQUEST_DISPOSITION("Request-Disposition", "d", RFC3841),

  /** [RFC3261] */
  REQUIRE("Require", RFC3261),

  /** [RFC4412] */
  RESOURCE_PRIORITY("Resource-Priority", RFC4412),

  /** [3GPP TS 24.229 v13.7.0] [Frederic_Firmin] */
  RESOURCE_SHARE("Resource-Share", Spec.IMS),

  /** [3GPP TS 24.229 v12.14.0] [Frederic_Firmin] */
  RESTORATION_INFO("Restoration-Info", Spec.IMS),

  /** [RFC3261] */
  RETRY_AFTER("Retry-After", RFC3261),

  /** [RFC3261] */
  ROUTE("Route", RFC3261),

  /** [RFC3262] */
  RSEQ("RSeq", RFC3262),

  /** [RFC3329] */
  SECURITY_CLIENT("Security-Client", RFC3329),

  /** [RFC3329] */
  SECURITY_SERVER("Security-Server", RFC3329),

  /** [RFC3329] */
  SECURITY_VERIFY("Security-Verify", RFC3329),

  /** [RFC3261] */
  SERVER("Server", RFC3261),

  /** [RFC3608] */
  SERVICE_ROUTE("Service-Route", RFC3608),

  /** [RFC4028] */
  SESSION_EXPIRES("Session-Expires", "x", RFC4028),

  /** [RFC7989] */
  SESSION_ID("Session-ID", RFC7989),

  /** [RFC3903] */
  SIP_ETAG("SIP-ETag", RFC3903),

  /** [RFC3903] */
  SIP_IF_MATCH("SIP-If-Match", RFC3903),

  /** [RFC3261] */
  SUBJECT("Subject", "s", RFC3261),

  /** [RFC6665] */
  SUBSCRIPTION_STATE("Subscription-State", RFC6665),

  /** [RFC3261] */
  SUPPORTED("Supported", "k", RFC3261),

  /** [RFC5839] */
  SUPPRESS_IF_MATCH("Suppress-If-Match", RFC5839),

  /** [RFC4538] */
  TARGET_DIALOG("Target-Dialog", RFC4538),

  /** [RFC3261] */
  TIMESTAMP("Timestamp", RFC3261),

  /** [RFC3261] */
  TO("To", "t", RFC3261),

  /** [RFC5360] */
  TRIGGER_CONSENT("Trigger-Consent", RFC5360),

  /** [RFC3261] */
  UNSUPPORTED("Unsupported", RFC3261),

  /** [RFC3261] */
  USER_AGENT("User-Agent", RFC3261),

  /** [RFC7433] */
  USER_TO_USER("User-to-User", RFC7433),

  /** [RFC3261][RFC7118] */
  VIA("Via", "v", RFC3261),

  /** [RFC3261] */
  WARNING("Warning", RFC3261),

  /** [RFC3261] */
  WWW_AUTHENTICATE("WWW-Authenticate", RFC3261),

  //

  // @Deprecated
  // ENCRYPTION("Encryption", Spec.DEPRECATED),

  // @Deprecated
  // HIDE("Hide"),

  /** deprecated by [RFC8224] */
  // @Deprecated
  // IDENTITY_INFO("Identity-Info"),

  // @Deprecated
  // RESPONSE_KEY("Response-Key"),

  ;

  static {
    final ImmutableMap.Builder<String, StandardSipHeaders> nb = ImmutableMap.builder();
    final ImmutableMap.Builder<Character, StandardSipHeaders> cb = ImmutableMap.builder();
    for (final StandardSipHeaders h : values()) {
      nb.put(h.normalized, h);
      if (h.compact != null) {
        nb.put(h.compact.toString(), h);
        cb.put(h.compact, h);
      }
    }
    normalizedNameLookup = nb.build();
    compactNameLookup = cb.build();
  }

  private static final ImmutableMap<String, StandardSipHeaders> normalizedNameLookup;
  private static final ImmutableMap<Character, StandardSipHeaders> compactNameLookup;

  private final String value;
  private final String normalized;
  private final Character compact;
  private final Spec spec;
  private final ImmutableSet<String> names;
  private final URI id;
  private final Predicate<SipHeaderLine> typeMatcher;

  StandardSipHeaders(final String value, final Character compact, final Spec spec) {
    this.value = value;
    this.normalized = value.toLowerCase();
    this.compact = compact;
    this.spec = spec;
    this.names =
      compact == null ? ImmutableSet.of(value)
                      : ImmutableSet.of(value, compact.toString());

    this.id = URI.create(spec.urn() + ":" + this.normalized);

    this.typeMatcher = line -> line.headerId() == this;

  }

  StandardSipHeaders(final String value, final Spec spec) {
    this(value, (Character) null, spec);
  }

  StandardSipHeaders(final String value, final String compact, final Spec spec) {
    this(value, compact.charAt(0), spec);
    if (compact.length() != 1) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String prettyName() {
    return this.value;
  }

  @Override
  public Set<String> headerNames() {
    return this.names;
  }

  @Override
  public URI headerId() {
    return this.id;
  }

  public static StandardSipHeaders fromString(final CharSequence name) {
    Objects.requireNonNull(name);
    return normalizedNameLookup.get(name.toString().toLowerCase());
  }

  public static String normalizeName(final CharSequence name) {
    if (name.length() == 1) {
      final StandardSipHeaders lookup = compactNameLookup.get(Character.toLowerCase(name.charAt(0)));
      if (lookup != null) {
        return lookup.value;
      }
    }
    return name.toString().toLowerCase();
  }

  public boolean nameMatches(final String name) {
    return normalizeName(name).compareTo(this.normalized) == 0;
  }

  public static void forEach(final Consumer<StandardSipHeaders> consumer) {
    for (final StandardSipHeaders hdr : values()) {
      consumer.accept(hdr);
    }
  }

  @Override
  public String toString() {
    return this.id.toString() + " " + this.headerNames() + " (" + this.spec + ")";
  }

  public SipHeaderLine ofLine(String line) {
    return SipHeaderLine.of(this, line);
  }

  public Predicate<SipHeaderLine> typeMatcher() {
    return this.typeMatcher;
  }

}

package io.rtcore.sip.message.iana;

import static io.rtcore.sip.message.iana.Spec.RFC3262;
import static io.rtcore.sip.message.iana.Spec.RFC3312;
import static io.rtcore.sip.message.iana.Spec.RFC3323;
import static io.rtcore.sip.message.iana.Spec.RFC3327;
import static io.rtcore.sip.message.iana.Spec.RFC3329;
import static io.rtcore.sip.message.iana.Spec.RFC3840;
import static io.rtcore.sip.message.iana.Spec.RFC3891;
import static io.rtcore.sip.message.iana.Spec.RFC3911;
import static io.rtcore.sip.message.iana.Spec.RFC3959;
import static io.rtcore.sip.message.iana.Spec.RFC4028;
import static io.rtcore.sip.message.iana.Spec.RFC4092;
import static io.rtcore.sip.message.iana.Spec.RFC4412;
import static io.rtcore.sip.message.iana.Spec.RFC4488;
import static io.rtcore.sip.message.iana.Spec.RFC4538;
import static io.rtcore.sip.message.iana.Spec.RFC4662;
import static io.rtcore.sip.message.iana.Spec.RFC4916;
import static io.rtcore.sip.message.iana.Spec.RFC5365;
import static io.rtcore.sip.message.iana.Spec.RFC5366;
import static io.rtcore.sip.message.iana.Spec.RFC5367;
import static io.rtcore.sip.message.iana.Spec.RFC5368;
import static io.rtcore.sip.message.iana.Spec.RFC5373;
import static io.rtcore.sip.message.iana.Spec.RFC5626;
import static io.rtcore.sip.message.iana.Spec.RFC5627;
import static io.rtcore.sip.message.iana.Spec.RFC5768;
import static io.rtcore.sip.message.iana.Spec.RFC6140;
import static io.rtcore.sip.message.iana.Spec.RFC6228;
import static io.rtcore.sip.message.iana.Spec.RFC6442;
import static io.rtcore.sip.message.iana.Spec.RFC6794;
import static io.rtcore.sip.message.iana.Spec.RFC7044;
import static io.rtcore.sip.message.iana.Spec.RFC7433;
import static io.rtcore.sip.message.iana.Spec.RFC7614;
import static io.rtcore.sip.message.iana.Spec.RFC7866;
import static io.rtcore.sip.message.iana.Spec.RFC_ietf_mmusic_trickle_ice_sip_18;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;

// @formatter:off
// csvq -q -N -f FIXED -r . 'SELECT "\n\n/** " || REPLACE(Description, "\n", " ") || " */\n" || UPPER(REPLACE(Name, "-", "_")) || "(\"" || Name || "\")," FROM `sip-parameters-4.csv`' 
// @formatter:on

public enum SipOptionTags {

  /**
   * This option tag is for reliability of provisional responses. When present in a Supported
   * header, it indicates that the UA can send or receive reliable provisional responses. When
   * present in a Require header in a request it indicates that the UAS MUST send all provisional
   * responses reliably. When present in a Require header in a reliable provisional response, it
   * indicates that the response is to be sent reliably.
   */

  $100REL("100rel", RFC3262),

  /**
   * This option-tag is for indicating support of the 199 Early Dialog Terminated provisional
   * response code. When present in a Supported header of a request, it indicates that the UAC
   * supports the 199 response code. When present in a Require or Proxy-Require header field of a
   * request, it indicates that the UAS, or proxies, MUST support the 199 response code. It does not
   * require the UAS, or proxies, to actually send 199 responses.
   */

  $199("199", RFC6228),

  /**
   * This option tag is for support of the Answer-Mode and Priv-Answer-Mode extensions used to
   * negotiate automatic or manual answering of a request.
   */

  ANSWERMODE("answermode", RFC5373),

  /**
   * A UA adding the early-session option tag to a message indicates that it understands the
   * early-session content disposition.
   */

  EARLY_SESSION("early-session", RFC3959),

  /**
   * Extension to allow subscriptions to lists of resources
   */

  EVENTLIST("eventlist", RFC4662),

  /**
   * This option tag identifies an extension to REFER to suppress the implicit subscription and
   * provide a URI for an explicit subscription.
   */

  EXPLICITSUB("explicitsub", RFC7614),

  /**
   * This option tag is used to indicate that a UA supports changes to URIs in From and To header
   * fields during a dialog.
   */

  FROM_CHANGE("from-change", RFC4916),

  /**
   * The "geolocation-http" option tag signals support for acquiring location information via an
   * HTTP [RFC2616]. A location recipient who supports this option can request location with an HTTP
   * GET and parse a resulting 200 response containing a PIDF-LO object. The URI schemes supported
   * by this option include "http" and "https".
   */

  GEOLOCATION_HTTP("geolocation-http", RFC6442),

  /**
   * The "geolocation-sip" option tag signals support for acquiring location information via the
   * presence event package of SIP [RFC3856]. A location recipient who supports this option can send
   * a SUBSCRIBE request and parse a resulting NOTIFY containing a PIDF-LO object. The URI schemes
   * supported by this option include "sip", "sips", and "pres".
   */

  GEOLOCATION_SIP("geolocation-sip", RFC6442),

  /**
   * This option tag is used to identify the extension that provides Registration for Multiple Phone
   * Numbers in SIP. When present in a Require or Proxy-Require header field of a REGISTER request,
   * it indicates that support for this extension is required of registrars and proxies,
   * respectively, that are a party to the registration transaction.
   */

  GIN("gin", RFC6140),

  /**
   * This option tag is used to identify the Globally Routable User Agent URI (GRUU) extension. When
   * used in a Supported header, it indicates that a User Agent understands the extension. When used
   * in a Require header field of a REGISTER request, it indicates that the registrar is not
   * expected to process the registration unless it supports the GRUU extension.
   */

  GRUU("gruu", RFC5627),

  /**
   * When used with the Supported header field, this option tag indicates the UAC supports the
   * History Information to be captured for requests and returned in subsequent responses. This tag
   * is not used in a Proxy-Require or Require header field, since support of History-Info is
   * optional.
   */

  HISTINFO("histinfo", RFC7044),

  /**
   * This option tag is used to identify the Interactive Connectivity Establishment (ICE) extension.
   * When present in a Require header field, it indicates that ICE is required by an agent.
   */

  ICE("ice", RFC5768),

  /**
   * Support for the SIP Join Header
   */

  JOIN("join", RFC3911),

  /**
   * This option tag indicates support for REFER requests that contain a resource list document
   * describing multiple REFER targets.
   */

  MULTIPLE_REFER("multiple-refer", RFC5368),

  /**
   * This option tag specifies a User Agent ability of accepting a REFER request without
   * establishing an implicit subscription (compared to the default case defined in [RFC3515].
   */

  NOREFERSUB("norefersub", RFC4488),

  /**
   * This option tag identifies an extension to REFER to suppress the implicit subscription and
   * indicate that no explicit subscription is forthcoming.
   */

  NOSUB("nosub", RFC7614),

  /**
   * This option-tag is used to identify UAs and Registrars which support extensions for Client
   * Initiated Connections. A UA places this option in a Supported header to communicate its support
   * for this extension. A Registrar places this option-tag in a Require header to indicate to the
   * registering User Agent that the Registrar used registrations using the binding rules defined in
   * this extension.
   */

  OUTBOUND("outbound", RFC5626),

  /**
   * A SIP UA that supports the Path extension header field includes this option tag as a header
   * field value in a Supported header field in all requests generated by that UA. Intermediate
   * proxies may use the presence of this option tag in a REGISTER request to determine whether to
   * offer Path service for for that request. If an intermediate proxy requires that the registrar
   * support Path for a request, then it includes this option tag as a header field value in a
   * Requires header field in that request.
   */

  PATH("path", RFC3327),

  /**
   * This option tag is used to indicate that a UA can process policy server URIs for and subscribe
   * to session-specific policies.
   */

  POLICY("policy", RFC6794),

  /**
   * An offerer MUST include this tag in the Require header field if the offer contains one or more
   * "mandatory" strength-tags. If all the strength-tags in the description are "optional" or "none"
   * the offerer MUST include this tag either in a Supported header field or in a Require header
   * field.
   */

  PRECONDITION("precondition", RFC3312),

  /**
   * This option tag is used to ensure that a server understands the callee capabilities parameters
   * used in the request.
   */

  PREF("pref", RFC3840),

  /**
   * This option tag indicates support for the Privacy mechanism. When used in the Proxy-Require
   * header, it indicates that proxy servers do not forward the request unless they can provide the
   * requested privacy service. This tag is not used in the Require or Supported headers. Proxies
   * remove this option tag before forwarding the request if the desired privacy function has been
   * performed.
   */

  PRIVACY("privacy", RFC3323),

  /**
   * The body contains a list of URIs that indicates the recipients of the SIP INVITE request
   */

  RECIPIENT_LIST_INVITE("recipient-list-invite", RFC5366),

  /**
   * The body contains a list of URIs that indicates the recipients of the SIP MESSAGE request
   */

  RECIPIENT_LIST_MESSAGE("recipient-list-message", RFC5365),

  /**
   * This option tag is used to ensure that a server can process the recipient-list body used in a
   * SUBSCRIBE request.
   */

  RECIPIENT_LIST_SUBSCRIBE("recipient-list-subscribe", RFC5367),

  /**
   * This option tag is to indicate the ability of the UA to receive recording indicators in
   * media-level or session-level SDP. When present in a Supported header, it indicates that the UA
   * can receive recording indicators in media-level or session-level SDP.
   */

  RECORD_AWARE("record-aware", RFC7866),

  /**
   * This option tag indicates support for the SIP Replaces header.
   */

  REPLACES("replaces", RFC3891),

  /**
   * Indicates or requests support for the resource priority mechanism.
   */

  RESOURCE_PRIORITY("resource-priority", RFC4412),

  /**
   * The option-tag sdp-anat is defined for use in the Require and Supported SIP [RFC3261] header
   * fields. SIP user agents that place this option-tag in a Supported header field understand the
   * ANAT semantics as defined in [RFC4091].
   */

  SDP_ANAT("sdp-anat", RFC4092),

  /**
   * This option tag indicates support for the Security Agreement mechanism. When used in the
   * Require, or Proxy-Require headers, it indicates that proxy servers are required to use the
   * Security Agreement mechanism. When used in the Supported header, it indicates that the User
   * Agent Client supports the Security Agreement mechanism. When used in the Require header in the
   * 494 (Security Agreement Required) or 421 (Extension Required) responses, it indicates that the
   * User Agent Client must use the Security Agreement Mechanism.
   */

  SEC_AGREE("sec-agree", RFC3329),

  /**
   * This option tag is for identifying that the SIP session is for the purpose of an RS. This is
   * typically not used in a Supported header. When present in a Require header in a request, it
   * indicates that the UA is either an SRC or SRS capable of handling a recording session.
   */

  SIPREC("siprec", RFC7866),

  /**
   * This option tag is used to identify the target dialog header field extension. When used in a
   * Require header field, it implies that the recipient needs to support the Target-Dialog header
   * field. When used in a Supported header field, it implies that the sender of the message
   * supports it.
   */

  TDIALOG("tdialog", RFC4538),

  /**
   * This option tag is for support of the session timer extension. Inclusion in a Supported header
   * field in a request or response indicates that the UA is capable of performing refreshes
   * according to that specification. Inclusion in a Require header in a request means that the UAS
   * must understand the session timer extension to process the request. Inclusion in a Require
   * header field in a response indicates that the UAC must look for the Session-Expires header
   * field in the response, and process accordingly.
   */

  TIMER("timer", RFC4028),

  /**
   * This option tag is used to indicate that a UA supports and understands Trickle-ICE.
   */

  TRICKLE_ICE("trickle-ice", RFC_ietf_mmusic_trickle_ice_sip_18),

  /**
   * This option tag is used to indicate that a UA supports and understands the User-to-User header
   * field.
   */

  UUI("uui", RFC7433),

  ;

  private final String token;
  private final Spec spec;

  SipOptionTags(String token, Spec spec) {
    this.token = token;
    this.spec = spec;
  }

  public String token() {
    return this.token;
  }

  public Spec spec() {
    return this.spec;
  }

  @Override
  public String toString() {
    return this.token;
  }

  // ----

  public static SipOptionTags fromToken(CharSequence token) {
    return tokenToValue.get(token);
  }

  private static final ImmutableMap<String, SipOptionTags> tokenToValue;
  static {
    tokenToValue = Arrays.stream(values()).collect(ImmutableMap.toImmutableMap(e -> e.name(), e -> e));
  }

}

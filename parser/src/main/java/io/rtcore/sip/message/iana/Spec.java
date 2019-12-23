package io.rtcore.sip.message.iana;

public enum Spec {

  /**
   * base standard
   */

  RFC3261,

  /**
   * provisional reliable
   */

  RFC3262,
  /// preconditions
  RFC3312,
  RFC3323,
  RFC3326,
  RFC3327,
  RFC3329,
  RFC3515,
  RFC3608,
  /// callee capabilities
  RFC3840,
  RFC3841,
  RFC3891,
  RFC3892,
  RFC3903,
  RFC3911,
  /// early-session
  RFC3959,

  /* ---- */

  RFC4028,
  
  // sdp-anat
  RFC4092,
  RFC4412,
  RFC4488,
  RFC4538,

  /// eventlist
  RFC4662,

  /// from-change
  RFC4916,

  /* ---- */

  RFC5360,
  
  
  RFC5365,
  RFC5366,
  RFC5367,
  
  // multiple-refer
  RFC5368,

  // answer mode
  RFC5373,
  RFC5393,
  RFC5626,

  /// gruu
  RFC5627,
  /// ice
  RFC5768,
  RFC5839,

  /* ---- */

  RFC6086,

  RFC6140,
  /// 199
  RFC6228,

  RFC6442,
  RFC6446,
  RFC6665,
  RFC6794,
  RFC6809,

  /* ---- */

  RFC7044,
  RFC7433,
  RFC7614,
  // recording indicators
  RFC7866,
  RFC7989,

  /* ---- */

  RFC8224,
  RFC8262,
  
  /* ---- Pending Publication ---- */
  
  RFC_ietf_mmusic_trickle_ice_sip_18,

  /* ---- */

  // P-* IMS specs
  RFC3313,
  RFC3325,
  RFC4457,
  RFC4964,
  RFC5002,
  RFC5009,
  RFC5318,
  RFC5503,
  RFC6050,
  RFC7315,
  RFC7316,
  RFC8496,
  RFC8498,

  // booo
  IMS,

  ;

  String urn() {
    if (name().startsWith("RFC")) {
      return "rfc:" + name().substring(3);
    }
    return name().toLowerCase();
  }

}

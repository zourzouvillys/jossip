package io.rtcore.sip.common.iana;

import java.math.RoundingMode;

import com.google.common.base.Verify;
import com.google.common.math.IntMath;

public enum SipStatusCategory {

  // 100: Trying
  TRYING(100),

  // 1xx: Provisional
  PROVISIONAL(180),

  // 2xx: Successful
  SUCCESSFUL(200),

  // 3xx: Redirection
  REDIRECTION(300),

  // 4xx: Request Failure
  REQUEST_FAILURE(400),

  // 5xx: Server Failure
  SERVER_FAILURE(500),

  // 6xx: Global Failures
  GLOBAL_FAILURE(600)

  ;

  private final int code;

  SipStatusCategory(final int code) {
    this.code = code;
  }

  public int defaultStatusCode() {
    return this.code;
  }

  public static SipStatusCategory forCode(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    if (status == 100) {
      return TRYING;
    }
    return values()[IntMath.divide(status, 100, RoundingMode.DOWN)];
  }

  public static boolean isSuccess(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status >= 200) && (status < 300);
  }

  public static boolean isFinal(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status >= 200);
  }

  public static boolean isTrying(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return status == 100;
  }

  public static boolean isProvisional(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status < 200);
  }

  public static boolean isRedirect(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status >= 300) && (status < 400);
  }

  public static boolean isFailure(final int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return status >= 300;
  }

}

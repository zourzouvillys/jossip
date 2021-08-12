package io.rtcore.sip.iana;

import java.math.RoundingMode;

import com.google.common.base.Verify;
import com.google.common.math.IntMath;

public enum SipStatusCategory {

  // 1xx: Provisional
  PROVISIONAL(100),

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

  SipStatusCategory(int code) {
    this.code = code;
  }

  public int defaultStatusCode() {
    return this.code;
  }

  public static SipStatusCategory forCode(int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return values()[IntMath.divide(status, 100, RoundingMode.DOWN) - 1];
  }

  public static boolean isSuccess(int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status >= 200) && (status < 300);
  }

  public static boolean isFinal(int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status >= 200);
  }

  public static boolean isProvisional(int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return (status < 200);
  }

  public static boolean isFailure(int status) {
    Verify.verify((status >= 100) && (status <= 699));
    return status >= 300;
  }

}

package io.rtcore.gateway.engine.sip;

import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.iana.SipMethodId;

public record ClientBranchId(HostPort sentBy, SipMethodId method, String branchId) {

}

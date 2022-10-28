package io.rtcore.sip.channels.auth;

import io.rtcore.sip.channels.api.SipAttributes;

public record Credential(String username, String realm, String ha1, SipAttributes attributes) {

}

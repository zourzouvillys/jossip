package io.rtcore.sip.channels.auth;

import java.util.Map;

public record Credential(String username, String realm, String ha1, Map<String, ? extends Object> properties) {

}

package io.rtcore.gateway.engine;

import java.util.function.UnaryOperator;

import io.rtcore.gateway.engine.http.HttpCallMapper;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;

public interface BackendProvider {

  HttpCallMapper createMapper(SipRequestFrame req, SipAttributes attributes);

  UnaryOperator<SipResponseFrame> serverResponseInterceptor();

}

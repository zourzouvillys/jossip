package io.rtcore.sip.proxy.http.netty;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.JsonNode;

@Value.Immutable
public interface StreamEvent {

	@Value.Parameter
	String eventId();

	@Value.Parameter
	String eventType();

	@Value.Parameter
	JsonNode data();

}

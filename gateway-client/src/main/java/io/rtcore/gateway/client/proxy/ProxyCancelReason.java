package io.rtcore.gateway.client.proxy;

/**
 * the cancellation reason
 */

public record ProxyCancelReason(int status, String reason) {
}
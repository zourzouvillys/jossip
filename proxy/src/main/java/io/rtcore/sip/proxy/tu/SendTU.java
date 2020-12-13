package io.rtcore.sip.proxy.tu;

/**
 * TU which sends as a transaction to a single destination over another protocol.
 * 
 * a transaction can be retried with modifiers, e.g to add credentials, use a different destination
 * or transport, retry after some backoff time based on a 5xx w/Retry-After, or any other logic
 * which can be applied based on matching a condition and adapting a request to generate a new one.
 * 
 * each attempt will use a new transaction.
 * 
 * note that this does not support internal parallel forking. instead, the controlling client needs
 * for fork as needed, and pass the CANCEL to us.
 * 
 * @author theo
 *
 */

public class SendTU {

}

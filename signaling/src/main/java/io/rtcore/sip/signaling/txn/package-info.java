/**
 * provides a generic SIP state machine API with minimal coupling with any specific SIP parser,
 * transport layer, or even state storage. instead, a lightweight API is provided that consumes and
 * emits events, and accepts commands.
 * 
 * a basic memory runtime model is provided, others should be fairly easy to implement.
 * 
 */

package io.rtcore.sip.signaling.txn;

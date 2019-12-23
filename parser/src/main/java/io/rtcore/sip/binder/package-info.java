/**
 * provides binding of serialization/deserialization of SIP messages, sans Jackson.
 * 
 * the binder can "fill in" messages, for example using a immutables.org value instance, or even
 * just a POJO.
 * 
 * alternatively, an interface can be provided, in which case the binder will generate the
 * implementation. it can pre-calculate or perform "lazy/JIT" materialization on a per field basis.
 * 
 */

package io.rtcore.sip.binder;

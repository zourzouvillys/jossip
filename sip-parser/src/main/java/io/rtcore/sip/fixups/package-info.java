
/**
 * some common shared logic around fixing up incoming SIP messages even though the actual on-wire
 * format violates some specifications.
 * 
 * for example, having two header fields that are only allowed a single value, but where both values
 * are identical could in a relaxed system not fail, and instead use the value that is provided in
 * both headers.
 * 
 * another fixup may use the first, last, max, or minimum value. for example, if there are multiple
 * Max-Forward headers, a reasonable fixup for multiple headers would be to use the smaller of the
 * values.
 * 
 * UTF-8 encoding of display name and in R-URIs is a common bug in UAs.
 * 
 * other more evil - but sometimes needed - fixups can be generating default values for header
 * fields which are missing, such as 'Max-Forwards' within a SIP request (default to X).
 * 
 */

package io.rtcore.sip.fixups;

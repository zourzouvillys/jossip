package com.jive.sip.stack.txn;

import java.util.Comparator;

import com.jive.sip.message.api.SipMessage;

/**
 * Compares if two {@link SipMessage} instances have equal branch values per RFC 3261.
 * 
 * @author theo
 * 
 */

public class BranchComparator implements Comparator<SipMessage>
{

  @Override
  public int compare(final SipMessage o1, final SipMessage o2)
  {
    return o1.getBranchId().compareTo(o2.getBranchId());
  }

}

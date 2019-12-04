/**
 * 
 */
package com.jive.sip.utils.rfc3261;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.ContactSet;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.parameters.tools.ParameterUtils;
import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.SipUriVisitor;
import com.jive.sip.uri.api.Uri;

/**
 * @author Jeff Hutchins {@code <jhutchins@getjive.com>}
 * 
 */
public class RfcHelper
{

  /**
   * Helper method to give the next sequence based on the given CSequence
   * 
   * @param cseq
   * @return <code>CSeq</code>
   */

  public static CSeq nextCSeq(final CSeq cseq)
  {
    return cseq.withSequence(cseq.getSequence().plus(UnsignedInteger.ONE));
  }

  public static UnsignedInteger getContactExpiration(Uri contact, UnsignedInteger defaultExpiry,
      ContactSet contacts)
  {
    SipUri expected = contact.apply(sipExtractor);
    for (NameAddr c : contacts)
    {
      SipUri actual = c.getAddress().apply(sipExtractor);
      if (actual != null && expected.equals(actual))
      {
        if (c.getParameter(ParameterUtils.Expires).isPresent())
        {
          return UnsignedInteger.valueOf(c.getParameter(ParameterUtils.Expires).get().toString());
        }
        else
        {
          return defaultExpiry;
        }
      }
    }
    return null;
  }

  private static SipUriVisitor<SipUri> sipExtractor = new SipUriVisitor<SipUri>()
  {
    @Override
    public SipUri visit(Uri unknown)
    {
      return null;
    }

    @Override
    public SipUri visit(SipUri uri)
    {
      return uri;
    }
  };

}

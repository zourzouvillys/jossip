package com.jive.sip.stack.resolver;

import java.util.Comparator;

public class NaptrPreferenceCompatator implements Comparator<INamingAuthorityPointer>
{

  @Override
  public int compare(final INamingAuthorityPointer o1, final INamingAuthorityPointer o2)
  {
    if (o1.getOrder() > o2.getOrder())
    {
      return 1;
    }
    else if (o1.getOrder() < o2.getOrder())
    {
      return -1;
    }
    return 0;
  }

}

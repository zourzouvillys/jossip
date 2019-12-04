package com.jive.sip.stack.resolver;

import java.util.Comparator;

public class ServiceRecordComparator implements Comparator<IServiceRecord>
{

  @Override
  public int compare(final IServiceRecord arg0, final IServiceRecord arg1)
  {
    if (arg0.getPriority() > arg1.getPriority())
    {
      return -1;
    }
    if (arg1.getPriority() > arg0.getPriority())
    {
      return 1;
    }
    return 0;
  }

}

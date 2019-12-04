package com.jive.sip.stack.resolver;

public interface IServiceRecord
{

  int getPriority();

  int getWeight();

  int getPort();

  String getTarget();

}

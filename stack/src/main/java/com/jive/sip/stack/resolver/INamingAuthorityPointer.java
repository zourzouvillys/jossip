package com.jive.sip.stack.resolver;

public interface INamingAuthorityPointer
{

  int getOrder();

  int getPreferences();

  String getFlags();

  String getService();

  String getRegularExpression();

  String getReplacement();

}

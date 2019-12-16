package com.jive.sip.processor.rfc3261.serializing;

import com.jive.sip.processor.rfc3261.serializing.serializers.AbstractRfcSerializer;
import com.jive.sip.uri.api.UserInfo;

public class UserInfoSerializer extends AbstractRfcSerializer<UserInfo> {

  @Override
  public String serialize(final UserInfo obj) {
    return obj.getUser()
      + (obj.getPassword().isPresent() ? ":" + obj.getPassword().get()
                                       : "");
  }

}

package io.rtcore.sip.message.processor.rfc3261.serializing;

import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.AbstractRfcSerializer;
import io.rtcore.sip.message.uri.UserInfo;

public class UserInfoSerializer extends AbstractRfcSerializer<UserInfo> {

  @Override
  public String serialize(final UserInfo obj) {
    return obj.user()
      + (obj.password().isPresent() ? ":" + obj.password().get()
                                       : "");
  }

}

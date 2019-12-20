package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValueVisitor;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;

public class RawParameterSerializer extends AbstractRfcSerializer<RawParameter> {

  @Override
  public void serialize(final Writer w, final RawParameter obj) throws IOException {
    w.append(obj.name().toString());
    obj.value().apply(new Serializer(w));
  }

  private static class Serializer implements ParameterValueVisitor<String> {

    private final Writer sb;

    public Serializer(final Writer sb) {
      this.sb = sb;
    }

    @Override
    public String visit(final FlagParameterValue parameter) {
      return null;
    }

    @Override
    public String visit(final TokenParameterValue parameter) {
      try {
        this.sb.append('=');
        this.sb.append(parameter.value().toString());
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      return null;
    }

    @Override
    public String visit(final QuotedStringParameterValue parameter) {
      try {
        this.sb.append('=');
        this.sb.append('"');
        this.sb.append(parameter.value().replace("\"", "\\\""));
        this.sb.append('"');
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      return null;
    }

    @Override
    public String visit(final HostAndPortParameterValue parameter) {
      try {
        this.sb.append('=');
        this.sb.append(parameter.value().toString());
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      return null;
    }

  }

}

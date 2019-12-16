package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValueVisitor;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;

import lombok.SneakyThrows;

public class RawParameterSerializer extends AbstractRfcSerializer<RawParameter> {

  @Override
  public void serialize(final Writer w, final RawParameter obj) throws IOException {
    w.append(obj.getName().toString());
    obj.getValue().apply(new Serializer(w));
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
        this.sb.append(parameter.getValue().toString());
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
        this.sb.append(parameter.getValue().replace("\"", "\\\""));
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
        this.sb.append(parameter.getValue().toString());
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      return null;
    }

  }

}

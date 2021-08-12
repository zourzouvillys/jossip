package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.RAck;

public class RAckSerializer extends AbstractRfcSerializer<RAck> {

  private CSeqSerializer cseq = new CSeqSerializer();

  @Override
  public void serialize(final Writer sink, final RAck obj) throws IOException {
    sink.append(obj.reliableSequence().toString());
    sink.append(' ');
    cseq.serialize(sink, obj.sequence());
  }

}

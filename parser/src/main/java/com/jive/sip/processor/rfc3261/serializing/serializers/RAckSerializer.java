package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.RAck;

public class RAckSerializer extends AbstractRfcSerializer<RAck> {

  private CSeqSerializer cseq = new CSeqSerializer();

  @Override
  public void serialize(final Writer sink, final RAck obj) throws IOException {
    sink.append(obj.getReliableSequence().toString());
    sink.append(' ');
    cseq.serialize(sink, obj.getSequence());
  }

}

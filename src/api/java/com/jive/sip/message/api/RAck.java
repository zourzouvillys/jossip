package com.jive.sip.message.api;

import com.google.common.primitives.UnsignedInteger;

import lombok.Value;

@Value
public class RAck
{
  private UnsignedInteger reliableSequence;
  private CSeq sequence;
}

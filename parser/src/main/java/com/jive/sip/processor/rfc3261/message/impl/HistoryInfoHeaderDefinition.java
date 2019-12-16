package com.jive.sip.processor.rfc3261.message.impl;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.message.api.headers.HistoryInfo;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.NameAddrParser;

public final class HistoryInfoHeaderDefinition extends BaseHeaderDefinition implements SipHeaderDefinition<HistoryInfo> {

  public HistoryInfoHeaderDefinition() {
    super("History-Info");
  }

  @Override
  public HistoryInfo parse(final Collection<RawHeader> headers) {

    final List<NameAddr> contacts = Lists.newLinkedList();

    for (final RawHeader header : headers) {

      if (matches(header.getName())) {

        final ParserInput input = ByteParserInput.fromString(header.getValue());

        final ParserContext ctx = new DefaultParserContext(input);

        do {
          NameAddrParser.INSTANCE.find(ctx, new CollectionAppender<NameAddr>(contacts));
        }
        while (ctx.skip(ParserUtils.COMMA));

      }

    }

    return contacts.isEmpty() ? null
                              : HistoryInfo.build(contacts);

  }

}

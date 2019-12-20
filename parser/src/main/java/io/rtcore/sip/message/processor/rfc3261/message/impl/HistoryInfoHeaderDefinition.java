package io.rtcore.sip.message.processor.rfc3261.message.impl;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.headers.HistoryInfo;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;

public final class HistoryInfoHeaderDefinition extends BaseHeaderDefinition implements SipHeaderDefinition<HistoryInfo> {

  public HistoryInfoHeaderDefinition() {
    super("History-Info");
  }

  @Override
  public HistoryInfo parse(final Collection<RawHeader> headers) {

    final List<NameAddr> contacts = Lists.newLinkedList();

    for (final RawHeader header : headers) {

      if (matches(header.name())) {

        final ParserInput input = ByteParserInput.fromString(header.value());

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

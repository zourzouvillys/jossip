package io.rtcore.sip.bind;

import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.parser.GQLParser;

public final class SipMessageBinder {

  GQLParser parser = GQLParser.defaultParser();

  public Query bind(String query) {
    return bind(parser.parseQuery(query));
  }

  Query bind(GQLOperationDefinition q) {
    return new Query(q);
  }

}

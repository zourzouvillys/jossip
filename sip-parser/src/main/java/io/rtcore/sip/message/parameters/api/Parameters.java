package io.rtcore.sip.message.parameters.api;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

public interface Parameters extends io.rtcore.sip.common.Parameters {

  boolean contains(final Token name);

  boolean contains(final String name);

  Collection<RawParameter> getRawParameters();

  <T> Optional<T> getParameter(final SipParameterDefinition<T> parameterDefinition);

  Parameters withParameters(final Collection<RawParameter> parameters);

  Parameters withParameter(final Token name);

  Parameters withParameter(final Token name, final Token value);

  Parameters withParameter(final Token name, final QuotedString value);

  Parameters withParameter(final Token name, final HostAndPort value);

  Parameters withoutParameter(final Token name);

  Optional<String> getParameter(final String string);

  Parameters mergeParameters(final Parameters params);

  Parameters withParameter(final RawParameter param);

  boolean compareCommonParameters(final Parameters params);

  <T> Parameters withParameter(SipParameterDefinition<T> def, String value);

  <T> Parameters withParameter(SipParameterDefinition<T> def, Token value);

  <T> Parameters withParameter(SipParameterDefinition<T> def, long value);

  <T> Parameters withParameter(SipParameterDefinition<T> def, HostAndPort value);

  default boolean isNotEmpty() {
    return !this.isEmpty();
  }

  boolean isEmpty();

  default void encodeTo(final RfcSerializerManager manager, final Writer writer, final String start, final String separator) {
    try {
      writer.append(start);
      manager.serializeCollection(writer, this.getRawParameters(), separator);
    }
    catch (final IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  Iterator<RawParameter> iterator();

  Stream<RawParameter> stream();

  Parameters withParameter(String token);

  Parameters withToken(String token, String value);

  Parameters filter(Predicate<RawParameter> object);

}

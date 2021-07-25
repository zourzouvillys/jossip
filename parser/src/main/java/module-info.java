module io.rtcore.sip.parser {
  exports io.rtcore.sip.message.parameters.tools;
  //exports io.rtcore.sip.message.message.api.header;
  exports io.rtcore.sip.message.processor.rfc3261;
  exports io.rtcore.sip.message.processor.rfc3261.serializing;
  //exports io.rtcore.sip.message.parameters;
  //exports io.rtcore.sip.message.message.impl;
  exports io.rtcore.sip.message.message;
  exports io.rtcore.sip.message.message.api.headers;
  exports io.rtcore.sip.message.processor.uri;
  exports io.rtcore.sip.message.auth.headers;
  exports io.rtcore.sip.message.processor.rfc3261.message.impl;
  exports io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;
  exports io.rtcore.sip.message.processor.uri.parsers;
  exports io.rtcore.sip.message.parsers.core.terminal;
  exports io.rtcore.sip.message.processor.rfc3261.message.api;
  exports io.rtcore.sip.iana;
  exports io.rtcore.sip.verifiers;
  exports io.rtcore.sip.generator;
  exports io.rtcore.sip.message.parameters.impl;
  exports io.rtcore.sip.message.parsers.api;
  exports io.rtcore.sip.message.processor.rfc3261.serializing.serializers;
  exports io.rtcore.sip.message.processor.rfc3261.parsing;
  exports io.rtcore.sip.message.content;
  exports io.rtcore.sip.message.message.api;
  exports io.rtcore.sip.message.parameters.api;
  exports io.rtcore.sip.message.auth;
  exports io.rtcore.sip.message.base.api;
  exports io.rtcore.sip.message.message.api.alertinfo;
  exports io.rtcore.sip.message.processor.rfc3261.parsing.parsers;
  exports io.rtcore.sip.message.parsers.core;
  exports io.rtcore.sip.message.processor.rfc3261.parsing.parsers.uri;
  exports io.rtcore.sip.message.uri;
  exports io.rtcore.sip.fixups;

  //requires com.google.common;
  //requires com.google.errorprone.annotations;
  //requires java.compiler;
  
  // requires static java.annotation;
  
  requires static org.immutables.value.annotations;
  requires com.google.common;
  
}

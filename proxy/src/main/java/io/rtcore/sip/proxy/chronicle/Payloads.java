package io.rtcore.sip.proxy.chronicle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Payloads {

  static AvroMapper objectMapper;
  private static ObjectReader reader;
  private static ObjectWriter writer;

  static {
    try {
      AvroMapper objectMapper = new AvroMapper();
      objectMapper.registerModules(new Jdk8Module(), new JavaTimeModule());
      AvroSchemaGenerator gen = new AvroSchemaGenerator();
      objectMapper.acceptJsonFormatVisitor(Frame.class, gen);
      AvroSchema schema = gen.getGeneratedSchema();
      System.err.println(schema.getAvroSchema().toString(true));
      reader = objectMapper.readerFor(Frame.class).with(schema);
      writer = objectMapper.writerFor(Frame.class).with(schema);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  // static final ObjectMapper objectMapper = new JsonMapper().registerModules(new Jdk8Module(), new
  // JavaTimeModule());

  public static final Frame read(InputStream in) throws IOException {
    return reader.readValue(in);
  }

  public static Frame read(String in) throws JsonMappingException, JsonProcessingException {
    return reader.readValue(in);
  }

  public static final void write(OutputStream out, Frame value) throws IOException {
    writer.writeValue(out, value);
  }

  public static class Frame {

    @JsonProperty
    public Instant time;

    @JsonProperty
    public String proto;

    @JsonProperty
    public InetSocketAddress local;

    @JsonProperty
    public InetSocketAddress remote;

    @JsonProperty
    public byte[] payload;

    public Instant time() {
      return this.time;
    }

    public InetSocketAddress local() {
      return this.local;
    }

    public InetSocketAddress remote() {
      return this.remote;
    }

    public ByteBuffer payload() {
      return ByteBuffer.wrap(payload);
    }

  }

}

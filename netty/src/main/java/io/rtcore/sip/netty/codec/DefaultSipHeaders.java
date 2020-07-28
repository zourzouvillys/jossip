package io.rtcore.sip.netty.codec;

import static io.netty.util.AsciiString.CASE_INSENSITIVE_HASHER;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.DefaultHeaders.NameValidator;
import io.netty.handler.codec.DefaultHeadersImpl;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.PlatformDependent;

public class DefaultSipHeaders implements SipHeaders {

  private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = ~15;

  private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

  public DefaultSipHeaders() {
    this(true);
  }

  public DefaultSipHeaders(boolean validate) {
    this(validate, nameValidator(validate));
  }

  protected DefaultSipHeaders(boolean validate, NameValidator<CharSequence> nameValidator) {
    this(
      new DefaultHeadersImpl<CharSequence, CharSequence>(
        CASE_INSENSITIVE_HASHER,
        valueConverter(validate),
        nameValidator));
  }

  protected DefaultSipHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
    this.headers = headers;
  }

  static ValueConverter<CharSequence> valueConverter(boolean validate) {
    return validate ? HeaderValueConverterAndValidator.INSTANCE
                    : HeaderValueConverter.INSTANCE;
  }

  private static class HeaderValueConverter extends CharSequenceValueConverter {
    static final HeaderValueConverter INSTANCE = new HeaderValueConverter();

    @Override
    public CharSequence convertObject(Object value) {
      if (value instanceof CharSequence) {
        return (CharSequence) value;
      }
      if (value instanceof Date) {
        return DateFormatter.format((Date) value);
      }
      if (value instanceof Calendar) {
        return DateFormatter.format(((Calendar) value).getTime());
      }
      return value.toString();
    }
  }

  private static final class HeaderValueConverterAndValidator extends HeaderValueConverter {

    static final HeaderValueConverterAndValidator INSTANCE = new HeaderValueConverterAndValidator();

    @Override
    public CharSequence convertObject(Object value) {
      CharSequence seq = super.convertObject(value);
      int state = 0;
      // Start looping through each of the character
      for (int index = 0; index < seq.length(); index++) {
        state = validateValueChar(seq, state, seq.charAt(index));
      }

      if (state != 0) {
        throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n':" + seq);
      }
      return seq;
    }

    private static int validateValueChar(CharSequence seq, int state, char character) {
      /*
       * State: 0: Previous character was neither CR nor LF 1: The previous character was CR 2: The
       * previous character was LF
       */
      if ((character & HIGHEST_INVALID_VALUE_CHAR_MASK) == 0) {
        // Check the absolutely prohibited characters.
        switch (character) {
          case 0x0: // NULL
            throw new IllegalArgumentException("a header value contains a prohibited character '\0': " + seq);
          case 0x0b: // Vertical tab
            throw new IllegalArgumentException("a header value contains a prohibited character '\\v': " + seq);
          case '\f':
            throw new IllegalArgumentException("a header value contains a prohibited character '\\f': " + seq);
        }
      }

      // Check the CRLF (HT | SP) pattern
      switch (state) {
        case 0:
          switch (character) {
            case '\r':
              return 1;
            case '\n':
              return 2;
          }
          break;
        case 1:
          switch (character) {
            case '\n':
              return 2;
            default:
              throw new IllegalArgumentException("only '\\n' is allowed after '\\r': " + seq);
          }
        case 2:
          switch (character) {
            case '\t':
            case ' ':
              return 0;
            default:
              throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n': " + seq);
          }
      }
      return state;
    }
  }

  @SuppressWarnings("unchecked")
  static NameValidator<CharSequence> nameValidator(boolean validate) {
    return validate ? SipNameValidator
                    : NameValidator.NOT_NULL;
  }

  private static final ByteProcessor HEADER_NAME_VALIDATOR = new ByteProcessor() {
    @Override
    public boolean process(byte value) throws Exception {
      validateHeaderNameElement(value);
      return true;
    }
  };

  private static void validateHeaderNameElement(byte value) {
    switch (value) {
      case 0x00:
      case '\t':
      case '\n':
      case 0x0b:
      case '\f':
      case '\r':
      case ' ':
      case ',':
      case ':':
      case ';':
      case '=':
        throw new IllegalArgumentException(
          "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: "
            +
            value);
      default:
        // Check to see if the character is not an ASCII character, or invalid
        if (value < 0) {
          throw new IllegalArgumentException(
            "a header name cannot contain non-ASCII character: "
              +
              value);
        }
    }
  }

  private static void validateHeaderNameElement(char value) {
    switch (value) {
      case 0x00:
      case '\t':
      case '\n':
      case 0x0b:
      case '\f':
      case '\r':
      case ' ':
      case ',':
      case ':':
      case ';':
      case '=':
        throw new IllegalArgumentException(
          "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: "
            +
            value);
      default:
        // Check to see if the character is not an ASCII character, or invalid
        if (value > 127) {
          throw new IllegalArgumentException(
            "a header name cannot contain non-ASCII character: "
              +
              value);
        }
    }
  }

  static final NameValidator<CharSequence> SipNameValidator = new NameValidator<CharSequence>() {
    @Override
    public void validateName(CharSequence name) {
      if ((name == null) || (name.length() == 0)) {
        throw new IllegalArgumentException("empty headers are not allowed [" + name + "]");
      }
      if (name instanceof AsciiString) {
        try {
          ((AsciiString) name).forEachByte(HEADER_NAME_VALIDATOR);
        }
        catch (Exception e) {
          PlatformDependent.throwException(e);
        }
      }
      else {
        // Go through each character in the name
        for (int index = 0; index < name.length(); ++index) {
          validateHeaderNameElement(name.charAt(index));
        }
      }
    }
  };

  @Override
  public void add(CharSequence name, CharSequence value) {
    this.headers.add(name, value);
  }

  @Override
  public CharSequence get(CharSequence headerName) {
    return headers.get(headerName);
  }

  @Override
  public Iterator<Entry<CharSequence, CharSequence>> iterator() {
    return headers.iterator();
  }

  @Override
  public void set(SipHeaders headers) {
    this.headers.set(headers.asHeaders());
  }

  @Override
  public Set<CharSequence> names() {
    return headers.names();
  }

  @Override
  public List<CharSequence> getAll(CharSequence header) {
    return headers.getAll(header);
  }

  @Override
  public void set(CharSequence name, CharSequence value) {
    headers.set(name, value);
  }

  @Override
  public boolean contains(CharSequence name) {
    return headers.contains(name);
  }

  @Override
  public SipHeaders copy() {
    return new DefaultSipHeaders(headers.copy());
  }

  public static final byte[] HSEP = new byte[] { ':', ' ' };

  @Override
  public void encode(ByteBuf buf) {
    headers.iterator().forEachRemaining(hdr -> {
      buf.writeCharSequence(hdr.getKey(), UTF_8);
      buf.writeBytes(HSEP);
      buf.writeCharSequence(hdr.getValue(), UTF_8);
      buf.writeBytes(SipConstants.CRLF);
    });
  }

  @Override
  public Headers<? extends CharSequence, ? extends CharSequence, ?> asHeaders() {
    return this.headers;
  }

  @Override
  public void set(CharSequence name, List<CharSequence> values) {
    headers.set(name, values);
  }

  @Override
  public String toString() {
    return this.asHeaders().toString();
  }

  @Override
  public void addAll(CharSequence name, Iterable<? extends CharSequence> values) {
    this.headers.add(name, values);
  }


}

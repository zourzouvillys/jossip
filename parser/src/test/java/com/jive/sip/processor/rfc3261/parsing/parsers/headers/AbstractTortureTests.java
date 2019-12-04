package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

import com.jive.sip.base.api.RawMessage;
import com.jive.sip.processor.rfc3261.RfcSipMessageManagerBuilder;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.parsing.RfcMessageParserBuilder;
import com.jive.sip.processor.rfc3261.parsing.RfcSipMessageParser;

public class AbstractTortureTests
{


  public byte[] read(final File file) throws IOException
  {
    ByteArrayOutputStream ous = null;
    InputStream ios = null;
    try
    {
      final byte[] buffer = new byte[4096];
      ous = new ByteArrayOutputStream();
      ios = new FileInputStream(file);
      int read = 0;
      while ((read = ios.read(buffer)) != -1)
      {
        ous.write(buffer, 0, read);
      }
    }
    finally
    {
      try
      {
        if (ous != null)
        {
          ous.close();
        }
      }
      catch (final IOException e)
      {
      }

      try
      {
        if (ios != null)
        {
          ios.close();
        }
      }
      catch (final IOException e)
      {
      }
    }
    if (ous == null)
    {
      return null;
    }
    return ous.toByteArray();
  }

  public void test(final File path, final byte[] data)
  {
    try
    {
      final RfcSipMessageParser parser = new RfcMessageParserBuilder().build();
      final RawMessage raw = parser.parse(ByteBuffer.wrap(data));
      final SipMessageManager manager = new RfcSipMessageManagerBuilder().build();
      manager.convert(raw, false);
    }
    catch (final Exception e)
    {
      throw new RuntimeException(MessageFormat.format("Error processing message {0}", path), e);
    }
  }

}

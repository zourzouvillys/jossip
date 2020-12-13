package io.rtcore.sip.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteStreams;
import com.google.common.io.MoreFiles;

import io.rtcore.sip.proxy.chronicle.Payloads.Frame;
import net.openhft.chronicle.threads.Pauser;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "dump", description = "print messages from a journal to console or another file")
public class DumpMessagesCommand implements Runnable {

  @Option(names = { "-f" })
  private boolean follow;

  @Option(names = { "-D" })
  private Path queuedir;

  @Option(names = { "-o" }, required = false)
  private Path outdir;

  @Override
  public void run() {

    MessageReader rx = new MessageReader(queuedir, true, null);

    Pauser pauser = Pauser.balanced();

    while (true) {

      Frame r = rx.read();

      if (r == null) {
        if (!follow) {
          break;
        }
        pauser.pause();
        continue;
      }

      ByteBuffer payload = r.payload();

      if (payload.remaining() <= 8) {
        continue;
      }

      if (outdir != null) {
        Path f = outdir.resolve(String.format("%08X", rx.currentIndex()));
        ByteSink sink = MoreFiles.asByteSink(f, StandardOpenOption.CREATE_NEW);
        dispatch(rx.currentIndex(), r, sink);
      }
      else {
        dispatch(rx.currentIndex(), r, System.out);
      }

      pauser.unpause();

    }

  }

  private void dispatch(long index, Frame r, ByteSink sink) {
    try (OutputStream out = sink.openBufferedStream()) {
      dispatch(index, r, out);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  private void dispatch(long index, Frame r, OutputStream out) {

    ByteBuffer payload = r.payload();

    try (InputStream in = new ByteBufferBackedInputStream(payload)) {

      System.err.printf(
        "#%08X @ %s %s -> %s\n",
        index,
        r.time(),
        r.remote(),
        r.local());

      System.err.flush();

      ByteStreams.copy(in, out);

      out.flush();

    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }

  }

}

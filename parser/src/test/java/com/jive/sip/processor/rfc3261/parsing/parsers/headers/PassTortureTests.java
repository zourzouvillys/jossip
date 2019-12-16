package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class PassTortureTests extends AbstractTortureTests {

  private final File file;

  public PassTortureTests(final File file, final String name) {
    this.file = file;
  }

  @Test
  public void testPass() throws Exception {
    test(this.file, read(this.file.getAbsoluteFile()));
  }

  @Parameters(name = "{index}: {1}}")
  public static Collection<Object[]> data() {

    final List<Object[]> files = Lists.newLinkedList();

    final File base = new File(PassTortureTests.class.getResource("/messages").getFile());

    for (final File msg : new File(base.getAbsolutePath() + File.separator + "/pass").listFiles()) {
      files.add(new Object[] { msg, msg.getName() });
    }

    return files;
  }

}

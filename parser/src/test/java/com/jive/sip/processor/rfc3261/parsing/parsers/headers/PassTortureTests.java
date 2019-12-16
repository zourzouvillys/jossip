package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class PassTortureTests extends AbstractTortureTests {

  @ParameterizedTest(name = "{1}")
  @MethodSource("listMessages")
  public void testPasses(Path file, String name) throws Exception {
    test(file.toFile(), read(file.toFile()));
  }

  public static Collection<Arguments> listMessages() {
    final List<Arguments> files = Lists.newLinkedList();
    final File base = new File(FailTortureTests.class.getResource("/messages").getFile());
    for (final File msg : new File(base.getAbsolutePath() + File.separator + "/pass").listFiles()) {
      files.add(Arguments.of(msg.toPath(), msg.getName()));
    }
    return files;
  }

}

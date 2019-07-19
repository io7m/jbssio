/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jbssio.tests;

import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class BSSIntegrationTest
{
  private static final Logger LOG = LoggerFactory.getLogger(BSSIntegrationTest.class);

  @Test
  public void testBasicIOChannel()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var channel = Files.newByteChannel(path, CREATE, WRITE, TRUNCATE_EXISTING)) {
      try (var writer = writers.createWriterFromChannel(pathURI, channel, "root")) {
        try (var sw = writer.createSubWriterBounded("head", 8L)) {
          sw.writeU32BE(0x10203040L);
          sw.writeU32BE(0x50607080L);
        }
        try (var sw = writer.createSubWriterAtBounded("body", 8L, 16L)) {
          sw.writeU32BE(0x90909090L);
          sw.writeU32BE(0x80808080L);
          sw.writeU32BE(0xa0a0a0a0L);
          sw.writeU32BE(0xb0b0b0b0L);
        }
      }
    }

    final var readers = new BSSReaders();
    try (var channel = Files.newByteChannel(path, READ)) {
      try (var reader = readers.createReaderFromChannel(pathURI, channel, "root")) {
        try (var sr = reader.createSubReaderBounded("head", 8L)) {
          final var x0 = sr.readU32BE();
          logUnsigned(x0);
          Assertions.assertEquals(0x10203040L, x0);

          final var x1 = sr.readU32BE();
          logUnsigned(x1);
          Assertions.assertEquals(0x50607080L, x1);
        }
        try (var sr = reader.createSubReaderAtBounded("body", 8L, 16L)) {
          final var x0 = sr.readU32BE();
          logUnsigned(x0);
          Assertions.assertEquals(0x90909090L, x0);

          final var x1 = sr.readU32BE();
          logUnsigned(x1);
          Assertions.assertEquals(0x80808080L, x1);

          final var x2 = sr.readU32BE();
          logUnsigned(x2);
          Assertions.assertEquals(0xa0a0a0a0L, x2);

          final var x3 = sr.readU32BE();
          logUnsigned(x3);
          Assertions.assertEquals(0xb0b0b0b0L, x3);
        }
      }
    }
  }

  @Test
  public void testBasicIOStream()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var stream = Files.newOutputStream(path, CREATE, WRITE, TRUNCATE_EXISTING)) {
      try (var writer = writers.createWriterFromStream(pathURI, stream, "root")) {
        try (var sw = writer.createSubWriterBounded("head", 8L)) {
          sw.writeU32BE(0x10203040L);
          sw.writeU32BE(0x50607080L);
        }
        try (var sw = writer.createSubWriterBounded("body", 16L)) {
          sw.writeU32BE(0x90909090L);
          sw.writeU32BE(0x80808080L);
          sw.writeU32BE(0xa0a0a0a0L);
          sw.writeU32BE(0xb0b0b0b0L);
        }
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newInputStream(path, READ)) {
      try (var reader = readers.createReaderFromStream(pathURI, stream, "root")) {
        try (var sr = reader.createSubReaderBounded("head", 8L)) {
          final var x0 = sr.readU32BE();
          logUnsigned(x0);
          Assertions.assertEquals(0x10203040L, x0);

          final var x1 = sr.readU32BE();
          logUnsigned(x1);
          Assertions.assertEquals(0x50607080L, x1);
        }
        try (var sr = reader.createSubReaderBounded("body", 16L)) {
          final var x0 = sr.readU32BE();
          logUnsigned(x0);
          Assertions.assertEquals(0x90909090L, x0);

          final var x1 = sr.readU32BE();
          logUnsigned(x1);
          Assertions.assertEquals(0x80808080L, x1);

          final var x2 = sr.readU32BE();
          logUnsigned(x2);
          Assertions.assertEquals(0xa0a0a0a0L, x2);

          final var x3 = sr.readU32BE();
          logUnsigned(x3);
          Assertions.assertEquals(0xb0b0b0b0L, x3);
        }
      }
    }
  }

  private static void logUnsigned(final long x)
  {
    LOG.debug("0x{}", Long.toUnsignedString(x, 16));
  }
}

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

import com.io7m.jbssio.api.BSSReaderType;
import com.io7m.jbssio.api.BSSWriterType;
import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;
import org.apache.commons.io.HexDump;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.US_ASCII;
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

  @Test
  public void testBasicIOStrings()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var stream = Files.newOutputStream(path, CREATE, WRITE, TRUNCATE_EXISTING)) {
      try (var writer = writers.createWriterFromStream(pathURI, stream, "root")) {
        writer.writeU32BE(6L);
        writer.writeBytes("Hello.".getBytes(US_ASCII));
        writer.align(4);

        writer.writeU32BE(6L);
        writer.writeBytes("Hello.".getBytes(US_ASCII));
        writer.align(4);
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newInputStream(path, READ)) {
      try (var reader = readers.createReaderFromStream(pathURI, stream, "root")) {
        final var len0 = reader.readU32BE();
        final var bytes0 = new byte[(int) len0];
        reader.readBytes(bytes0);
        reader.align(4);
        LOG.debug("{}", new String(bytes0, US_ASCII));

        final var len1 = reader.readU32BE();
        final var bytes1 = new byte[(int) len1];
        reader.readBytes(bytes1);
        reader.align(4);
        LOG.debug("{}", new String(bytes1, US_ASCII));
      }
    }
  }

  @Test
  public void testBasicIOSpecimenStream()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    try (var input = BSSIntegrationTest.class.getResourceAsStream("specimen.dat")) {
      try (var output = Files.newOutputStream(path, TRUNCATE_EXISTING, WRITE, CREATE)) {
        input.transferTo(output);
        output.flush();
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newInputStream(path, READ)) {
      try (var reader = readers.createReaderFromStream(pathURI, stream, "root")) {
        try (var r = reader.createSubReaderBounded("BE", 128L)) {
          checkSpecimenBE(r);
        }
        try (var r = reader.createSubReaderBounded("LE", 128L)) {
          checkSpecimenLE(r);
        }
      }
    }
  }

  @Test
  public void testBasicIOSpecimenChannel()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    try (var input = BSSIntegrationTest.class.getResourceAsStream("specimen.dat")) {
      try (var output = Files.newOutputStream(path, TRUNCATE_EXISTING, WRITE, CREATE)) {
        input.transferTo(output);
        output.flush();
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newByteChannel(path, READ)) {
      try (var reader = readers.createReaderFromChannel(pathURI, stream, "root")) {
        try (var r = reader.createSubReaderBounded("BE", 128L)) {
          checkSpecimenBE(r);
        }
        try (var r = reader.createSubReaderAtBounded("LE", 128L, 128L)) {
          checkSpecimenLE(r);
        }
      }
    }
  }

  @Test
  public void testBasicIOSpecimenByteBuffer()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    try (var input = BSSIntegrationTest.class.getResourceAsStream("specimen.dat")) {
      try (var output = Files.newOutputStream(path, TRUNCATE_EXISTING, WRITE, CREATE)) {
        input.transferTo(output);
        output.flush();
      }
    }

    final var readers = new BSSReaders();
    try (var stream = FileChannel.open(path, READ)) {
      final var map = stream.map(FileChannel.MapMode.READ_ONLY, 0L, stream.size());
      try (var reader = readers.createReaderFromByteBuffer(pathURI, map, "root")) {
        try (var r = reader.createSubReaderBounded("BE", 128L)) {
          checkSpecimenBE(r);
        }
        try (var r = reader.createSubReaderAtBounded("LE", 128L, 128L)) {
          checkSpecimenLE(r);
        }
      }
    }
  }

  @Test
  public void testBasicIOSpecimenWriteStream()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var stream = Files.newOutputStream(path, WRITE, TRUNCATE_EXISTING, CREATE)) {
      try (var w = writers.createWriterFromStream(pathURI, stream, "root")) {
        writeSpecimen(w);
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newInputStream(path, READ)) {
      try (var reader = readers.createReaderFromStream(pathURI, stream, "root")) {
        try (var r = reader.createSubReaderBounded("BE", 128L)) {
          checkSpecimenBE(r);
        }
        try (var r = reader.createSubReaderBounded("LE", 128L)) {
          checkSpecimenLE(r);
        }
      }
    }
  }

  @Test
  public void testBasicIOSpecimenWriteChannel()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var stream = Files.newByteChannel(path, WRITE, TRUNCATE_EXISTING, CREATE)) {
      stream.truncate(256L);
      try (var w = writers.createWriterFromChannel(pathURI, stream, "root")) {
        writeSpecimen(w);
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newInputStream(path, READ)) {
      try (var reader = readers.createReaderFromStream(pathURI, stream, "root")) {
        try (var r = reader.createSubReaderBounded("BE", 128L)) {
          checkSpecimenBE(r);
        }
        try (var r = reader.createSubReaderBounded("LE", 128L)) {
          checkSpecimenLE(r);
        }
      }
    }
  }

  @Test
  public void testBasicIOSpecimenWriteByteBuffer()
    throws IOException
  {
    final var path = Files.createTempFile("bss-integration-", ".dat");
    LOG.debug("path: {}", path);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var stream = FileChannel.open(path, READ, WRITE, TRUNCATE_EXISTING, CREATE)) {
      stream.truncate(256L);
      final var map = stream.map(FileChannel.MapMode.READ_WRITE, 0L, 256L);
      try (var w = writers.createWriterFromByteBuffer(pathURI, map, "root")) {
        writeSpecimen(w);
      }
    }

    final var readers = new BSSReaders();
    try (var stream = Files.newInputStream(path, READ)) {
      try (var reader = readers.createReaderFromStream(pathURI, stream, "root")) {
        try (var r = reader.createSubReaderBounded("BE", 128L)) {
          checkSpecimenBE(r);
        }
        try (var r = reader.createSubReaderBounded("LE", 128L)) {
          checkSpecimenLE(r);
        }
      }
    }
  }

  private static void writeSpecimen(final BSSWriterType w)
    throws IOException
  {
    // BE
    w.align(16);
    w.writeS8("min", Byte.MIN_VALUE);
    w.writeS8(Byte.MAX_VALUE);
    w.writeU8("min", 0);
    w.writeU8(0xff);

    w.align(16);
    w.writeS16BE("min", Short.MIN_VALUE);
    w.writeS16BE(Short.MAX_VALUE);
    w.writeU16BE("min", 0);
    w.writeU16BE(0xffff);

    w.align(16);
    w.writeS32BE("min", Integer.MIN_VALUE);
    w.writeS32BE(Integer.MAX_VALUE);
    w.writeU32BE("min", 0);
    w.writeU32BE(0xffff_ffff);

    w.align(16);
    w.writeS64BE("min", Long.MIN_VALUE);
    w.writeS64BE(Long.MAX_VALUE);
    w.writeU64BE("min", 0);
    w.writeU64BE(0xffff_ffff_ffff_ffffL);

    w.align(16);
    w.writeF32BE("min", -1.401298464324817E-45);
    w.writeF32BE(3.4028235e+38f);
    w.writeF64BE("min", -4.9406564584124654e-324);
    w.writeF64BE(Double.MAX_VALUE);

    w.align(16);
    w.writeBytes("named", new byte[]{0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x7f});

    // LE
    w.align(16);
    w.writeS8("min", Byte.MIN_VALUE);
    w.writeS8(Byte.MAX_VALUE);
    w.writeU8("min", 0);
    w.writeU8(0xff);

    w.align(16);
    w.writeS16LE("min", Short.MIN_VALUE);
    w.writeS16LE(Short.MAX_VALUE);
    w.writeU16LE("min", 0);
    w.writeU16LE(0xffff);

    w.align(16);
    w.writeS32LE("min", Integer.MIN_VALUE);
    w.writeS32LE(Integer.MAX_VALUE);
    w.writeU32LE("min", 0);
    w.writeU32LE(0xffff_ffff);

    w.align(16);
    w.writeS64LE("min", Long.MIN_VALUE);
    w.writeS64LE(Long.MAX_VALUE);
    w.writeU64LE("min", 0);
    w.writeU64LE(0xffff_ffff_ffff_ffffL);

    w.align(16);
    w.writeF32LE("min", -1.401298464324817E-45);
    w.writeF32LE(3.4028235e+38f);
    w.writeF64LE("min", -4.9406564584124654e-324);
    w.writeF64LE(Double.MAX_VALUE);

    w.align(16);
    w.writeBytes(new byte[]{0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x7f});
  }

  private static void checkSpecimenLE(final BSSReaderType r)
    throws IOException
  {
    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Byte.MIN_VALUE, r.readS8("min"));
    Assertions.assertEquals(Byte.MAX_VALUE, r.readS8());
    Assertions.assertEquals(0, r.readU8("min"));
    Assertions.assertEquals(0xff, r.readU8());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Short.MIN_VALUE, r.readS16LE("min"));
    Assertions.assertEquals(Short.MAX_VALUE, r.readS16LE());
    Assertions.assertEquals(0, r.readU16LE("min"));
    Assertions.assertEquals(0xffff, r.readU16LE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Integer.MIN_VALUE, r.readS32LE("min"));
    Assertions.assertEquals(Integer.MAX_VALUE, r.readS32LE());
    Assertions.assertEquals(0, r.readU32LE("min"));
    Assertions.assertEquals(0xffff_ffffL, r.readU32LE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Long.MIN_VALUE, r.readS64LE("min"));
    Assertions.assertEquals(Long.MAX_VALUE, r.readS64LE());
    Assertions.assertEquals(0L, r.readU64LE("min"));
    Assertions.assertEquals(0xffff_ffff_ffff_ffffL, r.readU64LE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(-1.401298464324817E-45, r.readF32LE("min"));
    Assertions.assertEquals(3.4028235e+38f, r.readF32LE());
    Assertions.assertEquals(-4.9406564584124654e-324, r.readD64LE("min"));
    Assertions.assertEquals(Double.MAX_VALUE, r.readD64LE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    final var received = new byte[8];
    final var expected = new byte[]{0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x7f};
    r.readBytes("named", received);

    HexDump.dump(received, 0L, System.out, 0);
    HexDump.dump(expected, 0L, System.out, 0);
    Assertions.assertArrayEquals(expected, received);
  }

  private static void checkSpecimenBE(final BSSReaderType r)
    throws IOException
  {
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    r.align(16);
    Assertions.assertEquals(Byte.MIN_VALUE, r.readS8("min"));
    Assertions.assertEquals(Byte.MAX_VALUE, r.readS8());
    Assertions.assertEquals(0, r.readU8("min"));
    Assertions.assertEquals(0xff, r.readU8());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Short.MIN_VALUE, r.readS16BE("min"));
    Assertions.assertEquals(Short.MAX_VALUE, r.readS16BE());
    Assertions.assertEquals(0, r.readU16BE("min"));
    Assertions.assertEquals(0xffff, r.readU16BE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Integer.MIN_VALUE, r.readS32BE("min"));
    Assertions.assertEquals(Integer.MAX_VALUE, r.readS32BE());
    Assertions.assertEquals(0, r.readU32BE("min"));
    Assertions.assertEquals(0xffff_ffffL, r.readU32BE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(Long.MIN_VALUE, r.readS64BE("min"));
    Assertions.assertEquals(Long.MAX_VALUE, r.readS64BE());
    Assertions.assertEquals(0L, r.readU64BE("min"));
    Assertions.assertEquals(0xffff_ffff_ffff_ffffL, r.readU64BE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    Assertions.assertEquals(-1.401298464324817E-45, r.readF32BE("min"));
    Assertions.assertEquals(3.4028235e+38f, r.readF32BE());
    Assertions.assertEquals(-4.9406564584124654e-324, r.readD64BE("min"));
    Assertions.assertEquals(Double.MAX_VALUE, r.readD64BE());

    r.align(16);
    LOG.debug("offset: 0x{}", Long.toUnsignedString(r.offsetCurrentAbsolute(), 16));
    final var received = new byte[8];
    final var expected = new byte[]{0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x7f};
    r.readBytes(received);

    HexDump.dump(received, 0L, System.out, 0);
    HexDump.dump(expected, 0L, System.out, 0);
    Assertions.assertArrayEquals(expected, received);
  }

  private static void logUnsigned(final long x)
  {
    LOG.debug("0x{}", Long.toUnsignedString(x, 16));
  }
}

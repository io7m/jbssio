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

import com.io7m.jbssio.vanilla.BSSWriters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.OptionalLong;

public final class BSSWritersSequentialTest
{
  private static final Logger LOG = LoggerFactory.getLogger(BSSWritersSequentialTest.class);

  @Test
  public void testEmptyStream()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, writer.offsetCurrentRelative());
        Assertions.assertEquals(OptionalLong.empty(), writer.bytesRemaining());
      }
    }
  }

  @Test
  public void testClosed()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      final var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a");
      Assertions.assertFalse(writer.isClosed());
      writer.close();
      Assertions.assertTrue(writer.isClosed());
      Assertions.assertThrows(IOException.class, () -> writer.writeS8(0x0));
    }
  }

  @Test
  public void testClosedNested()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      final var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a");
      Assertions.assertFalse(writer.isClosed());

      try (var s = writer.createSubWriter("x")) {
        writer.close();
        Assertions.assertTrue(writer.isClosed());
        Assertions.assertThrows(IOException.class, () -> writer.writeS8(0x0));
        Assertions.assertTrue(s.isClosed());
        Assertions.assertThrows(IOException.class, () -> s.writeS8(0x0));
      }
    }
  }

  @Test
  public void testNames()
    throws Exception
  {
    final var data = new byte[4];
    for (var index = 0; index < 4; ++index) {
      data[index] = (byte) index;
    }

    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        try (var s0 = writer.createSubWriter("x")) {
          try (var s1 = s0.createSubWriter("y")) {
            try (var s2 = s1.createSubWriter("z")) {
              Assertions.assertEquals("a.x.y.z", s2.path());
            }
            Assertions.assertEquals("a.x.y", s1.path());
          }
          Assertions.assertEquals("a.x", s0.path());
        }
        Assertions.assertEquals("a", writer.path());
      }
    }
  }

  @Test
  public void testSkip()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, writer.offsetCurrentRelative());

        writer.skip(10L);
        Assertions.assertEquals(10L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(10L, writer.offsetCurrentRelative());
      }

      Assertions.assertArrayEquals(new byte[10], stream.toByteArray());
    }
  }

  @Test
  public void testSkipBounded()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStreamBounded(URI.create("urn:fake"), stream, "a", 10L)) {
        Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, writer.offsetCurrentRelative());

        writer.skip(10L);
        Assertions.assertEquals(10L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(10L, writer.offsetCurrentRelative());

        final var ex = Assertions.assertThrows(IOException.class, () -> {
          writer.skip(1L);
        });
        LOG.debug("ex: ", ex);
      }

      Assertions.assertArrayEquals(new byte[10], stream.toByteArray());
    }
  }

  @Test
  public void testAlign()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, writer.offsetCurrentRelative());

        writer.writeU8(0x20);
        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(4L, writer.offsetCurrentRelative());

        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(4L, writer.offsetCurrentRelative());

        writer.writeU8(0x21);
        writer.align(4);
        Assertions.assertEquals(8L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(8L, writer.offsetCurrentRelative());

        writer.align(4);
        Assertions.assertEquals(8L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(8L, writer.offsetCurrentRelative());

        writer.writeU8(0x22);
        writer.align(4);
        Assertions.assertEquals(12L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(12L, writer.offsetCurrentRelative());

        writer.align(4);
        Assertions.assertEquals(12L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(12L, writer.offsetCurrentRelative());
      }

      Assertions.assertArrayEquals(new byte[]{
        0x20, 0x0, 0x0, 0x0,
        0x21, 0x0, 0x0, 0x0,
        0x22, 0x0, 0x0, 0x0,
      }, stream.toByteArray());
    }
  }

  @Test
  public void testAlignBounded()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStreamBounded(URI.create("urn:fake"), stream, "a", 5L)) {
        Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, writer.offsetCurrentRelative());

        writer.writeU8(0x20);
        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(4L, writer.offsetCurrentRelative());

        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetCurrentAbsolute());
        Assertions.assertEquals(4L, writer.offsetCurrentRelative());

        writer.writeU8(0x21);
        Assertions.assertThrows(IOException.class, () -> writer.align(4));
      }

      Assertions.assertArrayEquals(new byte[]{
        0x20, 0x0, 0x0, 0x0,
        0x21,
      }, stream.toByteArray());
    }
  }

  @Test
  public void testSubWriterBasic()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStreamBounded(URI.create("urn:fake"), stream, "a", 12L)) {
        Assertions.assertEquals(OptionalLong.of(12L), writer.bytesRemaining());
        LOG.debug("writer: {}", writer);

        try (var s = writer.createSubWriterBounded("x", 4L)) {
          Assertions.assertEquals(OptionalLong.of(4L), s.bytesRemaining());

          Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(0L, writer.offsetCurrentRelative());
          Assertions.assertEquals(0L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(0L, s.offsetCurrentRelative());
          s.writeU8(0x0);
          Assertions.assertEquals(1L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(1L, writer.offsetCurrentRelative());
          Assertions.assertEquals(1L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(1L, s.offsetCurrentRelative());
          s.writeU8("name", 0x1);
          Assertions.assertEquals(2L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(2L, writer.offsetCurrentRelative());
          Assertions.assertEquals(2L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(2L, s.offsetCurrentRelative());
          s.writeS8(0x2);
          Assertions.assertEquals(3L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(3L, writer.offsetCurrentRelative());
          Assertions.assertEquals(3L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(3L, s.offsetCurrentRelative());
          s.writeS8("name", 0x3);
          Assertions.assertEquals(4L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(4L, writer.offsetCurrentRelative());
          Assertions.assertEquals(4L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(4L, s.offsetCurrentRelative());
        }

        try (var s = writer.createSubWriterBounded("y", 4L)) {
          Assertions.assertEquals(OptionalLong.of(4L), s.bytesRemaining());

          Assertions.assertEquals(4L + 0L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(4L + 0L, writer.offsetCurrentRelative());
          Assertions.assertEquals(4L + 0L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(0L, s.offsetCurrentRelative());
          s.writeU8(0x0);
          Assertions.assertEquals(4L + 1L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(4L + 1L, writer.offsetCurrentRelative());
          Assertions.assertEquals(4L + 1L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(1L, s.offsetCurrentRelative());
          s.writeU8(0x1);
          Assertions.assertEquals(4L + 2L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(4L + 2L, writer.offsetCurrentRelative());
          Assertions.assertEquals(4L + 2L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(2L, s.offsetCurrentRelative());
          s.writeU8(0x2);
          Assertions.assertEquals(4L + 3L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(4L + 3L, writer.offsetCurrentRelative());
          Assertions.assertEquals(4L + 3L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(3L, s.offsetCurrentRelative());
          s.writeU8(0x3);
          Assertions.assertEquals(4L + 4L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(4L + 4L, writer.offsetCurrentRelative());
          Assertions.assertEquals(4L + 4L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(4L, s.offsetCurrentRelative());
        }

        try (var s = writer.createSubWriterBounded("z", 4L)) {
          Assertions.assertEquals(OptionalLong.of(4L), s.bytesRemaining());

          Assertions.assertEquals(8L + 0L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(8L + 0L, writer.offsetCurrentRelative());
          Assertions.assertEquals(8L + 0L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(0L, s.offsetCurrentRelative());
          s.writeU8(0x0);
          Assertions.assertEquals(8L + 1L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(8L + 1L, writer.offsetCurrentRelative());
          Assertions.assertEquals(8L + 1L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(1L, s.offsetCurrentRelative());
          s.writeU8(0x1);
          Assertions.assertEquals(8L + 2L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(8L + 2L, writer.offsetCurrentRelative());
          Assertions.assertEquals(8L + 2L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(2L, s.offsetCurrentRelative());
          s.writeU8(0x2);
          Assertions.assertEquals(8L + 3L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(8L + 3L, writer.offsetCurrentRelative());
          Assertions.assertEquals(8L + 3L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(3L, s.offsetCurrentRelative());
          s.writeU8(0x3);
          Assertions.assertEquals(8L + 4L, writer.offsetCurrentAbsolute());
          Assertions.assertEquals(8L + 4L, writer.offsetCurrentRelative());
          Assertions.assertEquals(8L + 4L, s.offsetCurrentAbsolute());
          Assertions.assertEquals(4L, s.offsetCurrentRelative());
        }
      }

      Assertions.assertArrayEquals(new byte[]{
        0x0, 0x1, 0x2, 0x3,
        0x0, 0x1, 0x2, 0x3,
        0x0, 0x1, 0x2, 0x3,
      }, stream.toByteArray());
    }
  }

  @Test
  public void testSubWriterBasicExceeds()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStreamBounded(URI.create("urn:fake"), stream, "a", 12L)) {
        final var ex =
          Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> writer.createSubWriterBounded("x", 13L));
        LOG.debug("ex: ", ex);
      }
    }
  }

  @Test
  public void testSubSubWriterBasicExceeds()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStreamBounded(URI.create("urn:fake"), stream, "a", 12L)) {
        try (var s = writer.createSubWriterBounded("y", 4L)) {
          final var ex =
            Assertions.assertThrows(
              IllegalArgumentException.class,
              () -> s.createSubWriterBounded("z", 5L));
          LOG.debug("ex: ", ex);
        }
      }
    }
  }

  @Test
  public void testWriteS8()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS8("q", 0x1);
        writer.writeS8(0x2);
      }

      Assertions.assertArrayEquals(new byte[]{
        0x1, 0x2
      }, stream.toByteArray());
    }
  }

  @Test
  public void testWriteU8()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU8("q", 0x1);
        writer.writeU8(0x2);
      }

      Assertions.assertArrayEquals(new byte[]{
        0x1, 0x2
      }, stream.toByteArray());
    }
  }

  @Test
  public void testWriteS16LE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS16LE("q", 0x1);
        writer.writeS16LE(0x2);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0x1, buffer.getShort(0));
      Assertions.assertEquals(0x2, buffer.getShort(2));
    }
  }

  @Test
  public void testWriteU16LE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU16LE("q", 0xffff);
        writer.writeU16LE(0x2);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0xffff, buffer.getChar(0));
      Assertions.assertEquals(0x2, buffer.getChar(2));
    }
  }

  @Test
  public void testWriteS16BE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS16BE("q", 0x1);
        writer.writeS16BE(0x2);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0x1, buffer.getShort(0));
      Assertions.assertEquals(0x2, buffer.getShort(2));
    }
  }

  @Test
  public void testWriteU16BE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU16BE("q", 0xffff);
        writer.writeU16BE(0x2);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0xffff, buffer.getChar(0));
      Assertions.assertEquals(0x2, buffer.getChar(2));
    }
  }

  @Test
  public void testWriteS32LE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS32LE("q", Integer.MAX_VALUE);
        writer.writeS32LE(Integer.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(Integer.MAX_VALUE, buffer.getInt(0));
      Assertions.assertEquals(Integer.MIN_VALUE, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteU32LE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU32LE("q", 0xffff_ffffL);
        writer.writeU32LE(0);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0xffff_ffffL, buffer.getInt(0) & 0xffff_ffffL);
      Assertions.assertEquals(0, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteS32BE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS32BE("q", Integer.MAX_VALUE);
        writer.writeS32BE(Integer.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(Integer.MAX_VALUE, buffer.getInt(0));
      Assertions.assertEquals(Integer.MIN_VALUE, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteU32BE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU32BE("q", 0xffff_ffffL);
        writer.writeU32BE(0L);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0xffff_ffffL, buffer.getInt(0) & 0xffff_ffffL);
      Assertions.assertEquals(0, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteS64LE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS64LE("q", Long.MAX_VALUE);
        writer.writeS64LE(Long.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(Long.MAX_VALUE, buffer.getLong(0));
      Assertions.assertEquals(Long.MIN_VALUE, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteU64LE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU64LE("q", 0xffff_ffff_ffff_ffffL);
        writer.writeU64LE(0);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, buffer.getLong(0) & 0xffff_ffff_ffff_ffffL);
      Assertions.assertEquals(0, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteS64BE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeS64BE("q", Long.MAX_VALUE);
        writer.writeS64BE(Long.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(Long.MAX_VALUE, buffer.getLong(0));
      Assertions.assertEquals(Long.MIN_VALUE, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteU64BE()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeU64BE("q", 0xffff_ffff_ffff_ffffL);
        writer.writeU64BE(0L);
      }

      final var buffer = ByteBuffer.wrap(stream.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, buffer.getLong(0) & 0xffff_ffff_ffff_ffffL);
      Assertions.assertEquals(0, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteBytes()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        writer.writeBytes("AAAA".getBytes(StandardCharsets.US_ASCII));
        writer.writeBytes("BBBB".getBytes(StandardCharsets.US_ASCII), 0, 4);
        writer.writeBytes("C", "CCCC".getBytes(StandardCharsets.US_ASCII));
        writer.writeBytes("D", "DDDD".getBytes(StandardCharsets.US_ASCII), 0, 4);
      }

      Assertions.assertArrayEquals(new byte[]{
        'A', 'A', 'A', 'A',
        'B', 'B', 'B', 'B',
        'C', 'C', 'C', 'C',
        'D', 'D', 'D', 'D'
      }, stream.toByteArray());
    }
  }
}

/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.api.BSSWriterRandomAccessType;
import com.io7m.jbssio.vanilla.BSSReaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BSSWritersRandomAccessChannelContract<T extends Channel>
{
  private static final Logger LOG = LoggerFactory.getLogger(
    BSSWritersRandomAccessChannelContract.class);

  private static void checkExceptionMessageContains(
    final Exception e,
    final String text)
  {
    LOG.debug("ex: ", e);
    Assertions.assertTrue(
      e.getMessage().contains(text),
      "Exception message " + e.getMessage() + " contains " + text);
  }

  protected abstract T channelOf(byte[] data)
    throws IOException;

  protected abstract BSSWriterRandomAccessType writerOf(T channel)
    throws IOException;

  protected abstract byte[] writtenDataOf(
    byte[] data)
    throws IOException;

  @Test
  public void testEmptyStream()
    throws Exception
  {
    final var stream = this.channelOf(new byte[0]);

    try (var writer = this.writerOf(stream)) {
      Assertions.assertEquals(0L, writer.offsetCurrentAbsolute());
      Assertions.assertEquals(0L, writer.offsetCurrentRelative());
    }
  }

  @Test
  public void testClosed()
    throws Exception
  {
    final var stream = this.channelOf(new byte[32]);

    final var writer = this.writerOf(stream);
    Assertions.assertFalse(writer.isClosed());
    writer.close();
    Assertions.assertTrue(writer.isClosed());
    Assertions.assertThrows(IOException.class, () -> writer.seekTo(1L));
  }

  @Test
  public void testClosedNested()
    throws Exception
  {
    final var stream = this.channelOf(new byte[32]);

    final var writer = this.writerOf(stream);
    Assertions.assertFalse(writer.isClosed());

    final var s = writer.createSubWriterAt("x", 0L);
    writer.close();
    Assertions.assertTrue(writer.isClosed());
    Assertions.assertThrows(IOException.class, () -> writer.seekTo(1L));
    Assertions.assertTrue(s.isClosed());
    Assertions.assertThrows(IOException.class, () -> s.seekTo(1L));
  }

  @Test
  public void testNames()
    throws Exception
  {
    final var data = new byte[4];
    for (var index = 0; index < 4; ++index) {
      data[index] = (byte) index;
    }

    final var stream = this.channelOf(data);
    try (var writer = this.writerOf(stream)) {
      try (var s0 = writer.createSubWriterAt("x", 0L)) {
        try (var s1 = s0.createSubWriterAt("y", 0L)) {
          try (var s2 = s1.createSubWriterAt("z", 0L)) {
            Assertions.assertEquals("a/x/y/z", s2.path());
          }
          Assertions.assertEquals("a/x/y", s1.path());
        }
        Assertions.assertEquals("a/x", s0.path());
      }
      Assertions.assertEquals("a", writer.path());
    }
  }

  @Test
  public void testSubWriterBasic()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        try (var s = writer.createSubWriterAtBounded("a", 0L, 4L)) {
          LOG.debug("s: {}", s);
          s.writeS8(0x1);
          s.writeS8(0x2);
          s.writeS8(0x3);
          s.writeS8(0x4);
          Assertions.assertThrows(IOException.class, () -> s.writeS8(0x5));
        }
        try (var s = writer.createSubWriterAtBounded("a", 4L, 4L)) {
          LOG.debug("s: {}", s);
          s.writeS8(0x10);
          s.writeS8(0x20);
          s.writeS8(0x30);
          s.writeS8(0x40);
          Assertions.assertThrows(IOException.class, () -> s.writeS8(0x5));
        }
        try (var s = writer.createSubWriterAtBounded("a", 8L, 4L)) {
          LOG.debug("s: {}", s);
          s.writeS8(0x11);
          s.writeS8(0x21);
          s.writeS8(0x31);
          s.writeS8(0x41);
          Assertions.assertThrows(IOException.class, () -> s.writeS8(0x5));
        }
      }
    }

    Assertions.assertArrayEquals(new byte[] {
      0x1, 0x2, 0x3, 0x4,
      0x10, 0x20, 0x30, 0x40,
      0x11, 0x21, 0x31, 0x41,
    }, this.writtenDataOf(data));
  }

  @Test
  public void testSubWriterBasicExceeds()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        final var ex =
          Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> writer.createSubWriterAtBounded("x", 0L, 13L));
        LOG.debug("ex: ", ex);
      }
    }
  }

  @Test
  public void testSubSubWriterBasicExceeds()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        try (var s = writer.createSubWriterAtBounded("y", 0L,4L)) {
          final var ex =
            Assertions.assertThrows(
              IllegalArgumentException.class,
              () -> s.createSubWriterAtBounded("z", 0L, 5L));
          LOG.debug("ex: ", ex);
        }
      }
    }
  }

  @Test
  public void testWriteS8()
    throws Exception
  {
    final var data = new byte[2];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS8("q", 0x1);
        writer.writeS8(0x2);
      }

      final var rdata = this.writtenDataOf(data);
      Assertions.assertArrayEquals(new byte[]{
        0x1, 0x2
      }, rdata);
    }
  }

  @Test
  public void testWriteU8()
    throws Exception
  {
    final var data = new byte[2];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU8("q", 0x1);
        writer.writeU8(0x2);
      }

      final var rdata = this.writtenDataOf(data);
      Assertions.assertArrayEquals(new byte[]{
        0x1, 0x2
      }, rdata);
    }
  }

  @Test
  public void testWriteS16LE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS16LE("q", 0x1);
        writer.writeS16LE(0x2);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0x1, buffer.getShort(0));
      Assertions.assertEquals(0x2, buffer.getShort(2));
    }
  }

  @Test
  public void testWriteU16LE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU16LE("q", 0xffff);
        writer.writeU16LE(0x2);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0xffff, buffer.getChar(0));
      Assertions.assertEquals(0x2, buffer.getChar(2));
    }
  }

  @Test
  public void testWriteS16BE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS16BE("q", 0x1);
        writer.writeS16BE(0x2);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0x1, buffer.getShort(0));
      Assertions.assertEquals(0x2, buffer.getShort(2));
    }
  }

  @Test
  public void testWriteU16BE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU16BE("q", 0xffff);
        writer.writeU16BE(0x2);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0xffff, buffer.getChar(0));
      Assertions.assertEquals(0x2, buffer.getChar(2));
    }
  }

  @Test
  public void testWriteS32LE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS32LE("q", Integer.MAX_VALUE);
        writer.writeS32LE(Integer.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(Integer.MAX_VALUE, buffer.getInt(0));
      Assertions.assertEquals(Integer.MIN_VALUE, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteU32LE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU32LE("q", 0xffff_ffffL);
        writer.writeU32LE(0);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0xffff_ffffL, buffer.getInt(0) & 0xffff_ffffL);
      Assertions.assertEquals(0, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteS32BE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS32BE("q", Integer.MAX_VALUE);
        writer.writeS32BE(Integer.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(Integer.MAX_VALUE, buffer.getInt(0));
      Assertions.assertEquals(Integer.MIN_VALUE, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteU32BE()
    throws Exception
  {
    final var data = new byte[12];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU32BE("q", 0xffff_ffffL);
        writer.writeU32BE(0L);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0xffff_ffffL, buffer.getInt(0) & 0xffff_ffffL);
      Assertions.assertEquals(0, buffer.getInt(4));
    }
  }

  @Test
  public void testWriteS64LE()
    throws Exception
  {
    final var data = new byte[16];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS64LE("q", Long.MAX_VALUE);
        writer.writeS64LE(Long.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(Long.MAX_VALUE, buffer.getLong(0));
      Assertions.assertEquals(Long.MIN_VALUE, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteU64LE()
    throws Exception
  {
    final var data = new byte[16];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU64LE("q", 0xffff_ffff_ffff_ffffL);
        writer.writeU64LE(0);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.LITTLE_ENDIAN);
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, buffer.getLong(0) & 0xffff_ffff_ffff_ffffL);
      Assertions.assertEquals(0, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteS64BE()
    throws Exception
  {
    final var data = new byte[16];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeS64BE("q", Long.MAX_VALUE);
        writer.writeS64BE(Long.MIN_VALUE);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(Long.MAX_VALUE, buffer.getLong(0));
      Assertions.assertEquals(Long.MIN_VALUE, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteU64BE()
    throws Exception
  {
    final var data = new byte[16];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
        writer.writeU64BE("q", 0xffff_ffff_ffff_ffffL);
        writer.writeU64BE(0L);
      }

      final var buffer = ByteBuffer.wrap(this.writtenDataOf(data)).order(ByteOrder.BIG_ENDIAN);
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, buffer.getLong(0) & 0xffff_ffff_ffff_ffffL);
      Assertions.assertEquals(0, buffer.getLong(8));
    }
  }

  @Test
  public void testWriteBytes()
    throws Exception
  {
    final var data = new byte[16];

    try (var channel = this.channelOf(data)) {
      try (var writer = this.writerOf(channel)) {
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
      }, this.writtenDataOf(data));
    }
  }

  @Test
  public final void testException()
    throws IOException
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var stream = new ByteArrayInputStream(data.array())) {
      try (var reader = readers.createReaderFromStreamBounded(URI.create(
        "urn:fake"), stream, "a", 32L)) {

        final var ex = reader.createException(
          "message",
          Map.ofEntries(
            Map.entry("x", "y"),
            Map.entry("z", "0.0")
          ),
          IOException::new
        );

        assertTrue(ex.getMessage().contains("message"));
        assertTrue(ex.getMessage().contains("x"));
        assertTrue(ex.getMessage().contains("y"));
        assertTrue(ex.getMessage().contains("z"));
        assertTrue(ex.getMessage().contains("0.0"));
        assertTrue(ex.getMessage().contains("Offset"));
      }
    }
  }
}

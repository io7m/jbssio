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

import com.io7m.jbssio.api.BSSReaderRandomAccessType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channel;

public abstract class BSSReadersRandomAccessChannelContract<T extends Channel>
{
  private static final Logger LOG = LoggerFactory.getLogger(
    BSSReadersRandomAccessChannelContract.class);

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

  protected abstract BSSReaderRandomAccessType readerOf(T channel)
    throws IOException;

  @Test
  public void testEmptyStream()
    throws Exception
  {

    final var stream = this.channelOf(new byte[0]);

    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(0L, reader.offsetAbsolute());
      Assertions.assertEquals(0L, reader.offsetRelative());
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


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      try (var s0 = reader.createSubReader("x")) {
        try (var s1 = s0.createSubReader("y")) {
          try (var s2 = s1.createSubReader("z")) {
            Assertions.assertEquals("a.x.y.z", s2.path());
          }
          Assertions.assertEquals("a.x.y", s1.path());
        }
        Assertions.assertEquals("a.x", s0.path());
      }
      Assertions.assertEquals("a", reader.path());
    }
  }

  @Test
  public void testSeparateLimits()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(0L, reader.offsetAbsolute());
      Assertions.assertEquals(0L, reader.offsetRelative());
      LOG.debug("reader:    {}", reader);

      try (var subReader = reader.createSubReader("s", 4L)) {
        Assertions.assertEquals(0L, subReader.offsetRelative());
        Assertions.assertEquals(0L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());

        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1L, subReader.offsetRelative());
        Assertions.assertEquals(1L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2L, subReader.offsetRelative());
        Assertions.assertEquals(2L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3L, subReader.offsetRelative());
        Assertions.assertEquals(3L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertEquals(4L, subReader.offsetRelative());
        Assertions.assertEquals(4L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      try (var subReader = reader.createSubReader("s", 4L)) {
        Assertions.assertEquals(0L, subReader.offsetRelative());
        Assertions.assertEquals(0L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());

        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1L, subReader.offsetRelative());
        Assertions.assertEquals(1L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2L, subReader.offsetRelative());
        Assertions.assertEquals(2L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3L, subReader.offsetRelative());
        Assertions.assertEquals(3L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertEquals(4L, subReader.offsetRelative());
        Assertions.assertEquals(4L, subReader.offsetAbsolute());
        Assertions.assertEquals(0L, reader.offsetAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }
    }
  }

  @Test
  public void testSubreaderRanges()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      try (var subReader = reader.createSubReader("s", 4L)) {
        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      reader.seekTo(4L);
      try (var subReader = reader.createSubReader("s", 4L)) {
        Assertions.assertEquals(4, subReader.readS8());
        Assertions.assertEquals(5, subReader.readU8());
        Assertions.assertEquals(6, subReader.readS8());
        Assertions.assertEquals(7, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      reader.seekTo(8L);
      try (var subReader = reader.createSubReader("s", 4L)) {
        Assertions.assertEquals(8, subReader.readS8());
        Assertions.assertEquals(9, subReader.readU8());
        Assertions.assertEquals(10, subReader.readS8());
        Assertions.assertEquals(11, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }
    }
  }

  @Test
  public void testSubreaderRangesReflected()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      try (var subReader = reader.createSubReader("s", 4L)) {
        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);

        reader.seekTo(4L);
        subReader.seekTo(0L);
        Assertions.assertEquals(4, subReader.readS8());
        Assertions.assertEquals(5, subReader.readU8());
        Assertions.assertEquals(6, subReader.readS8());
        Assertions.assertEquals(7, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);

        reader.seekTo(8L);
        subReader.seekTo(0L);
        Assertions.assertEquals(8, subReader.readS8());
        Assertions.assertEquals(9, subReader.readU8());
        Assertions.assertEquals(10, subReader.readS8());
        Assertions.assertEquals(11, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }
    }
  }

  @Test
  public void testSeparateLimitsExceeds()
    throws Exception
  {
    final var data = new byte[4];
    for (var index = 0; index < 4; ++index) {
      data[index] = (byte) index;
    }


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(0L, reader.offsetAbsolute());
      Assertions.assertEquals(0L, reader.offsetRelative());
      LOG.debug("reader:    {}", reader);

      final var ex =
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
          reader.createSubReader("s", 5L);
        });

      LOG.debug("ex: ", ex);
      Assertions.assertTrue(ex.getMessage().contains("Requested bounds: [0x0, 0x5)"));
    }
  }

  @Test
  public void testSkip()
    throws Exception
  {
    final var data = new byte[16];


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      reader.skip(4L);
      reader.skip(4L);
      reader.skip(4L);
      reader.skip(4L);
      Assertions.assertThrows(IOException.class, () -> reader.skip(1L));
    }
  }

  @Test
  public void testSeekTo()
    throws Exception
  {
    final var data = new byte[16];


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      for (var index = 0L; index < 16L; ++index) {
        reader.seekTo(index);
      }
      Assertions.assertThrows(IOException.class, () -> reader.seekTo(16L));
    }
  }

  @Test
  public void testAlign()
    throws Exception
  {
    final var data = new byte[9];


    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      reader.skip(1L);
      Assertions.assertEquals(1L, reader.offsetAbsolute());
      Assertions.assertEquals(1L, reader.offsetRelative());

      reader.align(4);
      Assertions.assertEquals(4L, reader.offsetAbsolute());
      Assertions.assertEquals(4L, reader.offsetRelative());

      reader.align(4);
      Assertions.assertEquals(4L, reader.offsetAbsolute());
      Assertions.assertEquals(4L, reader.offsetRelative());

      reader.skip(1L);
      Assertions.assertEquals(5L, reader.offsetAbsolute());
      Assertions.assertEquals(5L, reader.offsetRelative());

      reader.align(4);
      Assertions.assertEquals(8L, reader.offsetAbsolute());
      Assertions.assertEquals(8L, reader.offsetRelative());

      reader.skip(1L);
      Assertions.assertEquals(9L, reader.offsetAbsolute());
      Assertions.assertEquals(9L, reader.offsetRelative());

      Assertions.assertThrows(IOException.class, () -> reader.align(4));
    }
  }

  @Test
  public void testReadShort()
    throws Exception
  {
    final var data = new byte[0];

    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(0L, reader.bytesRemaining());
      Assertions.assertThrows(IOException.class, reader::readS16BE);
      Assertions.assertThrows(IOException.class, reader::readS16LE);
      Assertions.assertThrows(IOException.class, reader::readS32BE);
      Assertions.assertThrows(IOException.class, reader::readS32LE);
      Assertions.assertThrows(IOException.class, reader::readS64BE);
      Assertions.assertThrows(IOException.class, reader::readS64LE);
      Assertions.assertThrows(IOException.class, reader::readS8);
      Assertions.assertThrows(IOException.class, reader::readU16BE);
      Assertions.assertThrows(IOException.class, reader::readU16LE);
      Assertions.assertThrows(IOException.class, reader::readU32BE);
      Assertions.assertThrows(IOException.class, reader::readU32LE);
      Assertions.assertThrows(IOException.class, reader::readU64BE);
      Assertions.assertThrows(IOException.class, reader::readU64LE);
      Assertions.assertThrows(IOException.class, reader::readU8);
    }
  }

  @Test
  public void testReadShortNamed()
    throws Exception
  {
    final var data = new byte[0];

    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS16BE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS16LE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS32BE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS32LE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS64BE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS64LE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readS8("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU16BE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU16LE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU32BE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU32LE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU64BE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU64LE("q")), "q");
      checkExceptionMessageContains(Assertions.assertThrows(
        IOException.class,
        () -> reader.readU8("q")), "q");
    }
  }

  @Test
  public void testReadS8()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = Byte.MIN_VALUE;
    data[1] = Byte.MAX_VALUE;

    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Byte.MIN_VALUE, reader.readS8());
      Assertions.assertEquals(31L, reader.bytesRemaining());
      Assertions.assertEquals(Byte.MAX_VALUE, reader.readS8());
      Assertions.assertEquals(30L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadS16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MIN_VALUE, reader.readS16LE());
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MAX_VALUE, reader.readS16LE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadS32LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MIN_VALUE, reader.readS32LE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MAX_VALUE, reader.readS32LE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadS64LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MIN_VALUE, reader.readS64LE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MAX_VALUE, reader.readS64LE());
    }
  }

  @Test
  public void testReadS16BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MIN_VALUE, reader.readS16BE());
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MAX_VALUE, reader.readS16BE());
    }
  }

  @Test
  public void testReadS32BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MIN_VALUE, reader.readS32BE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MAX_VALUE, reader.readS32BE());
    }
  }

  @Test
  public void testReadS64BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MIN_VALUE, reader.readS64BE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MAX_VALUE, reader.readS64BE());
    }
  }

  @Test
  public void testReadU8()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = 0;
    data[1] = (byte) 0xff;

    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0, reader.readU8());
      Assertions.assertEquals(31L, reader.bytesRemaining());
      Assertions.assertEquals(0xff, reader.readU8());
    }
  }

  @Test
  public void testReadU16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0, reader.readU16LE());
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff, reader.readU16LE());
    }
  }

  @Test
  public void testReadU32LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU32LE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffffL, reader.readU32LE());
    }
  }

  @Test
  public void testReadU64LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU64LE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64LE());
    }
  }

  @Test
  public void testReadU16BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0, reader.readU16BE());
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff, reader.readU16BE());
    }
  }

  @Test
  public void testReadU32BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU32BE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffffL, reader.readU32BE());
    }
  }

  @Test
  public void testReadU64BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU64BE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64BE());
    }
  }

  @Test
  public void testReadDBE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putDouble(0, 1000.0);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0, reader.readDBE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadDLE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putDouble(0, 1000.0);

    final var channel = this.channelOf(data.array());

    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0, reader.readDLE());
      Assertions.assertEquals(24L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadFBE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0f, reader.readFBE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadFLE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0f, reader.readFLE());
      Assertions.assertEquals(28L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadBytes()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    final var buffer = new byte[16];

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(16, reader.readBytes(buffer, 0, buffer.length));
      Assertions.assertEquals(16L, reader.bytesRemaining());
      Assertions.assertEquals(16, reader.readBytes(buffer));
      Assertions.assertEquals(0L, reader.bytesRemaining());
      Assertions.assertThrows(IOException.class, () -> reader.readBytes(buffer));
    }
  }

  @Test
  public void testReadS8Named()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = Byte.MIN_VALUE;
    data[1] = Byte.MAX_VALUE;

    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Byte.MIN_VALUE, reader.readS8("q"));
      Assertions.assertEquals(31L, reader.bytesRemaining());
      Assertions.assertEquals(Byte.MAX_VALUE, reader.readS8("q"));
      Assertions.assertEquals(30L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadS16LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MIN_VALUE, reader.readS16LE("q"));
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MAX_VALUE, reader.readS16LE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadS32LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MIN_VALUE, reader.readS32LE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MAX_VALUE, reader.readS32LE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadS64LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MIN_VALUE, reader.readS64LE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MAX_VALUE, reader.readS64LE("q"));
    }
  }

  @Test
  public void testReadS16BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MIN_VALUE, reader.readS16BE("q"));
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(Short.MAX_VALUE, reader.readS16BE("q"));
    }
  }

  @Test
  public void testReadS32BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MIN_VALUE, reader.readS32BE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(Integer.MAX_VALUE, reader.readS32BE("q"));
    }
  }

  @Test
  public void testReadS64BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MIN_VALUE, reader.readS64BE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(Long.MAX_VALUE, reader.readS64BE("q"));
    }
  }

  @Test
  public void testReadU8Named()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = 0;
    data[1] = (byte) 0xff;

    final var stream = this.channelOf(data);
    try (var reader = this.readerOf(stream)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0, reader.readU8("q"));
      Assertions.assertEquals(31L, reader.bytesRemaining());
      Assertions.assertEquals(0xff, reader.readU8("q"));
    }
  }

  @Test
  public void testReadU16LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0, reader.readU16LE("q"));
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff, reader.readU16LE("q"));
    }
  }

  @Test
  public void testReadU32LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU32LE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffffL, reader.readU32LE("q"));
    }
  }

  @Test
  public void testReadU64LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU64LE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64LE("q"));
    }
  }

  @Test
  public void testReadU16BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, (char) 0);
    data.putChar(2, (char) 0xffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0, reader.readU16BE("q"));
      Assertions.assertEquals(30L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff, reader.readU16BE("q"));
    }
  }

  @Test
  public void testReadU32BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putInt(0, 0);
    data.putInt(4, 0xffff_ffff);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU32BE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffffL, reader.readU32BE("q"));
    }
  }

  @Test
  public void testReadU64BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putLong(0, 0L);
    data.putLong(8, 0xffff_ffff_ffff_ffffL);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(0L, reader.readU64BE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64BE("q"));
    }
  }

  @Test
  public void testReadDBENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putDouble(0, 1000.0);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0, reader.readDBE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadDLENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putDouble(0, 1000.0);

    final var channel = this.channelOf(data.array());

    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0, reader.readDLE("q"));
      Assertions.assertEquals(24L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadFBENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0f, reader.readFBE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadFLENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(1000.0f, reader.readFLE("q"));
      Assertions.assertEquals(28L, reader.bytesRemaining());
    }
  }

  @Test
  public void testReadBytesNamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    final var buffer = new byte[16];

    final var channel = this.channelOf(data.array());
    try (var reader = this.readerOf(channel)) {
      Assertions.assertEquals(32L, reader.bytesRemaining());
      Assertions.assertEquals(16, reader.readBytes("q", buffer, 0, buffer.length));
      Assertions.assertEquals(16L, reader.bytesRemaining());
      Assertions.assertEquals(16, reader.readBytes("q", buffer));
      Assertions.assertEquals(0L, reader.bytesRemaining());
      Assertions.assertThrows(IOException.class, () -> reader.readBytes("q", buffer));
    }
  }
}

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

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.vanilla.BSSReaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class BSSReadersRandomAccessByteBufferTest
{
  private static final Logger LOG = LoggerFactory.getLogger(BSSReadersRandomAccessByteBufferTest.class);

  @Test
  public void testEmptyStream()
    throws Exception
  {
    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(new byte[0]);

    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(0L, reader.offsetCurrentRelative());
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      try (var s0 = reader.createSubReaderAt("x", 0L)) {
        try (var s1 = s0.createSubReaderAt("y", 0L)) {
          try (var s2 = s1.createSubReaderAt("z", 0L)) {
            Assertions.assertEquals("a/x/y/z", s2.path());
          }
          Assertions.assertEquals("a/x/y", s1.path());
        }
        Assertions.assertEquals("a/x", s0.path());
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(0L, reader.offsetCurrentRelative());
      LOG.debug("reader:    {}", reader);

      try (var subReader = reader.createSubReaderAtBounded("s", 0L, 4L)) {
        Assertions.assertEquals(0L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(0L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());

        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(1L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(2L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(3L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertEquals(4L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(4L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      try (var subReader = reader.createSubReaderAtBounded("s", 0L, 4L)) {
        Assertions.assertEquals(0L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(0L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());

        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(1L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(2L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(3L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertEquals(4L, subReader.offsetCurrentRelative());
        Assertions.assertEquals(4L, subReader.offsetCurrentAbsolute());
        Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }
    }
  }

  @Test
  public void testSubreaderRangesCurrentPosition()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      reader.seekTo(0L);
      LOG.debug("reader: {}", reader);

      try (var subReader = reader.createSubReaderAtBounded("s", 0L, 4L)) {
        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      reader.seekTo(4L);
      LOG.debug("reader: {}", reader);

      try (var subReader = reader.createSubReaderAtBounded("s", 0L, 4L)) {
        Assertions.assertEquals(4, subReader.readS8());
        Assertions.assertEquals(5, subReader.readU8());
        Assertions.assertEquals(6, subReader.readS8());
        Assertions.assertEquals(7, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      reader.seekTo(8L);
      LOG.debug("reader: {}", reader);

      try (var subReader = reader.createSubReaderAtBounded("s", 0L, 4L)) {
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
  public void testSubreaderRanges()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      LOG.debug("reader: {}", reader);

      try (var subReader = reader.createSubReaderAtBounded("s", 0L, 4L)) {
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(0, subReader.readS8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(1, subReader.readU8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(2, subReader.readS8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(3, subReader.readU8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      LOG.debug("reader: {}", reader);
      try (var subReader = reader.createSubReaderAtBounded("s", 4L, 4L)) {
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(4, subReader.readS8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(5, subReader.readU8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(6, subReader.readS8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(7, subReader.readU8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }

      LOG.debug("reader: {}", reader);
      try (var subReader = reader.createSubReaderAtBounded("s", 8L, 4L)) {
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(8, subReader.readS8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(9, subReader.readU8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(10, subReader.readS8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(11, subReader.readU8());
        LOG.debug("subReader: {}", subReader);
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);
      }
    }
  }

  @Test
  public void testSubreaderOffsetAbsoluteSimple()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      try (var subReader = reader.createSubReaderAtBounded("s", 0L,4L)) {
        reader.seekTo(4L);
        subReader.seekTo(0L);
        Assertions.assertEquals(4L, subReader.offsetCurrentAbsolute());
      }
    }
  }

  @Test
  public void testSubreaderOffsetSimple()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      try (var subReader = reader.createSubReaderAtBounded("s", 4L, 4L)) {
        Assertions.assertEquals(4L, subReader.offsetCurrentAbsolute());
      }
    }
  }

  @Test
  public void testSubreaderOffsetShortRead()
    throws Exception
  {
    final var data = new byte[12];
    for (var index = 0; index < 12; ++index) {
      data[index] = (byte) index;
    }

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      try (var subReader = reader.createSubReaderAtBounded("s", 4L, 4L)) {
        subReader.skip(4L);
        Assertions.assertThrows(IOException.class, subReader::readS8);
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      try (var subReader = reader.createSubReaderAtBounded("s",  0L,4L)) {
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(0, subReader.readS8());
        Assertions.assertEquals(1, subReader.readU8());
        Assertions.assertEquals(2, subReader.readS8());
        Assertions.assertEquals(3, subReader.readU8());
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        Assertions.assertThrows(IOException.class, subReader::readS8);
        Assertions.assertThrows(IOException.class, subReader::readU8);

        reader.seekTo(4L);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        subReader.seekTo(0L);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(4, subReader.readS8());
        Assertions.assertEquals(5, subReader.readU8());
        Assertions.assertEquals(6, subReader.readS8());
        Assertions.assertEquals(7, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        Assertions.assertThrows(IOException.class, subReader::readU8);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);

        reader.seekTo(8L);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        subReader.seekTo(0L);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        Assertions.assertEquals(8, subReader.readS8());
        Assertions.assertEquals(9, subReader.readU8());
        Assertions.assertEquals(10, subReader.readS8());
        Assertions.assertEquals(11, subReader.readU8());
        Assertions.assertThrows(IOException.class, subReader::readS8);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
        Assertions.assertThrows(IOException.class, subReader::readU8);
        LOG.debug("reader:    {}", reader);
        LOG.debug("subReader: {}", subReader);
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(0L, reader.offsetCurrentRelative());
      LOG.debug("reader:    {}", reader);

      final var ex =
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
          reader.createSubReaderAtBounded("s", 0L,5L);
        });

      LOG.debug("ex: ", ex);
      Assertions.assertTrue(ex.getMessage().contains("Requested bounds: absolute [0x0, 0x5)"));
    }
  }

  @Test
  public void testSkip()
    throws Exception
  {
    final var data = new byte[16];

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(0L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(0L, reader.offsetCurrentRelative());

      reader.skip(1L);
      Assertions.assertEquals(1L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(1L, reader.offsetCurrentRelative());

      reader.align(4);
      Assertions.assertEquals(4L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(4L, reader.offsetCurrentRelative());

      reader.align(4);
      Assertions.assertEquals(4L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(4L, reader.offsetCurrentRelative());

      reader.skip(1L);
      Assertions.assertEquals(5L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(5L, reader.offsetCurrentRelative());

      reader.align(4);
      Assertions.assertEquals(8L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(8L, reader.offsetCurrentRelative());

      reader.skip(1L);
      Assertions.assertEquals(9L, reader.offsetCurrentAbsolute());
      Assertions.assertEquals(9L, reader.offsetCurrentRelative());

      Assertions.assertThrows(IOException.class, () -> reader.align(4));
    }
  }

  @Test
  public void testReadShort()
    throws Exception
  {
    final var data = new byte[0];

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(0L, reader.bytesRemaining().getAsLong());
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
  public void testReadS8()
    throws Exception
  {
    final var data = new byte[32];
    data[0] = Byte.MIN_VALUE;
    data[1] = Byte.MAX_VALUE;

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Byte.MIN_VALUE, reader.readS8());
      Assertions.assertEquals(31L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Byte.MAX_VALUE, reader.readS8());
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadS16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putShort(0, Short.MIN_VALUE);
    data.putShort(2, Short.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Short.MIN_VALUE, reader.readS16LE());
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Short.MAX_VALUE, reader.readS16LE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadS32LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putInt(0, Integer.MIN_VALUE);
    data.putInt(4, Integer.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Integer.MIN_VALUE, reader.readS32LE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Integer.MAX_VALUE, reader.readS32LE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadS64LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putLong(0, Long.MIN_VALUE);
    data.putLong(8, Long.MAX_VALUE);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Long.MIN_VALUE, reader.readS64LE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Short.MIN_VALUE, reader.readS16BE());
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Integer.MIN_VALUE, reader.readS32BE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(Long.MIN_VALUE, reader.readS64BE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    final var stream = ByteBuffer.wrap(data);
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), stream, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0, reader.readU8());
      Assertions.assertEquals(31L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0, reader.readU16LE());
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0L, reader.readU32LE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0L, reader.readU64LE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0, reader.readU16BE());
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0L, reader.readU32BE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
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

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0L, reader.readU64BE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(0xffff_ffff_ffff_ffffL, reader.readU64BE());
    }
  }

  @Test
  public void testReadDBE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putDouble(0, 1000.0);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1000.0, reader.readD64BE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadDLE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putDouble(0, 1000.0);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1000.0, reader.readD64LE());
      Assertions.assertEquals(24L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadFBE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1000.0f, reader.readF32BE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadFLE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putFloat(0, 1000.0f);

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1000.0f, reader.readF32LE());
      Assertions.assertEquals(28L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadF16BE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1.0, reader.readF16BE(), 0.001);
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadF16LE()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1.0, reader.readF16LE(), 0.001);
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadF16BENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1.0, reader.readF16BE("x"), 0.001);
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadF16LENamed()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.LITTLE_ENDIAN);
    data.putChar(0, Binary16.packDouble(1.0));

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(1.0, reader.readF16LE("x"), 0.001);
      Assertions.assertEquals(30L, reader.bytesRemaining().getAsLong());
    }
  }

  @Test
  public void testReadBytes()
    throws Exception
  {
    final var data = ByteBuffer.wrap(new byte[32]).order(ByteOrder.BIG_ENDIAN);
    final var buffer = new byte[16];

    final var readers = new BSSReaders();
    try (var reader = readers.createReaderFromByteBuffer(URI.create("urn:fake"), data, "a")) {
      Assertions.assertEquals(32L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(16, reader.readBytes(buffer, 0, buffer.length));
      Assertions.assertEquals(16L, reader.bytesRemaining().getAsLong());
      Assertions.assertEquals(16, reader.readBytes(buffer));
      Assertions.assertEquals(0L, reader.bytesRemaining().getAsLong());
      Assertions.assertThrows(IOException.class, () -> reader.readBytes(buffer));
    }
  }
}

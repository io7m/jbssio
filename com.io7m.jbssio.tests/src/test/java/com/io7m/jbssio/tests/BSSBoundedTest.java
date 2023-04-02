/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import com.io7m.jbssio.api.BSSWriterRandomAccessType;
import com.io7m.jbssio.ext.bounded.BSSBounded;
import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Bounded extension.
 */

public final class BSSBoundedTest
{
  private ByteBuffer buffer;
  private BSSReaderRandomAccessType reader;
  private BSSWriterRandomAccessType writer;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.buffer =
      ByteBuffer.allocate(256);

    this.reader =
      new BSSReaders()
        .createReaderFromByteBuffer(
          URI.create("urn:buffer"),
          this.buffer,
          "root"
        );

    this.writer =
      new BSSWriters()
        .createWriterFromByteBuffer(
          URI.create("urn:buffer"),
          this.buffer,
          "root"
        );
  }

  @Test
  public void testBytesReadBE()
    throws IOException
  {
    this.writer.writeU32BE(5L);
    this.writer.writeU8((int) 'A');
    this.writer.writeU8((int) 'B');
    this.writer.writeU8((int) 'C');
    this.writer.writeU8((int) 'D');
    this.writer.writeU8((int) 'E');

    final var b = new BSSBounded(ByteOrder.BIG_ENDIAN);
    assertArrayEquals(
      new byte[]{
        (byte) 'A',
        (byte) 'B',
        (byte) 'C',
        (byte) 'D',
        (byte) 'E'
      },
      b.readBytes(this.reader, 1000, "data")
    );
  }

  @Test
  public void testBytesReadLE()
    throws IOException
  {
    this.writer.writeU32LE(5L);
    this.writer.writeU8((int) 'A');
    this.writer.writeU8((int) 'B');
    this.writer.writeU8((int) 'C');
    this.writer.writeU8((int) 'D');
    this.writer.writeU8((int) 'E');

    final var b = new BSSBounded(ByteOrder.LITTLE_ENDIAN);
    assertArrayEquals(
      new byte[]{
        (byte) 'A',
        (byte) 'B',
        (byte) 'C',
        (byte) 'D',
        (byte) 'E'
      },
      b.readBytes(this.reader, 1000, "data")
    );
  }

  @Test
  public void testBytesWriteBE()
    throws IOException
  {
    final var b = new BSSBounded(ByteOrder.BIG_ENDIAN);
    b.writeBytes(this.writer, "data", new byte[]{
      (byte) 'A',
      (byte) 'B',
      (byte) 'C',
      (byte) 'D',
      (byte) 'E'
    });

    assertEquals(5L, this.reader.readU32BE());
    assertEquals('A', this.reader.readU8());
    assertEquals('B', this.reader.readU8());
    assertEquals('C', this.reader.readU8());
    assertEquals('D', this.reader.readU8());
    assertEquals('E', this.reader.readU8());
  }

  @Test
  public void testBytesWriteLE()
    throws IOException
  {
    final var b = new BSSBounded(ByteOrder.LITTLE_ENDIAN);
    b.writeBytes(this.writer, "data", new byte[]{
      (byte) 'A',
      (byte) 'B',
      (byte) 'C',
      (byte) 'D',
      (byte) 'E'
    });

    assertEquals(5L, this.reader.readU32LE());
    assertEquals('A', this.reader.readU8());
    assertEquals('B', this.reader.readU8());
    assertEquals('C', this.reader.readU8());
    assertEquals('D', this.reader.readU8());
    assertEquals('E', this.reader.readU8());
  }

  @Test
  public void testBytesLimit()
    throws IOException
  {
    this.writer.writeU32BE(5L);
    this.writer.writeU8((int) 'A');
    this.writer.writeU8((int) 'B');
    this.writer.writeU8((int) 'C');
    this.writer.writeU8((int) 'D');
    this.writer.writeU8((int) 'E');

    final var b = new BSSBounded(ByteOrder.BIG_ENDIAN);

    var ex = assertThrows(IOException.class, () -> {
      b.readBytes(this.reader, 4, "data");
    });

    assertTrue(ex.getMessage().contains("Specified length of data 5"));
  }

  @Test
  public void testUTF8()
    throws IOException
  {
    final var b = new BSSBounded(ByteOrder.BIG_ENDIAN);

    b.writeUTF8(this.writer, "Hello!");
    assertEquals("Hello!", b.readUTF8(this.reader, 6, "text"));
  }

  @Test
  public void testUTF16BE()
    throws IOException
  {
    final var b = new BSSBounded(ByteOrder.BIG_ENDIAN);

    b.writeString(this.writer, "Hello!", StandardCharsets.UTF_16BE);
    assertEquals(
      "Hello!",
      b.readString(
        this.reader,
        12,
        "text",
        StandardCharsets.UTF_16BE)
    );
  }
}

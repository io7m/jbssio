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


package com.io7m.jbssio.ext.bounded;

import com.io7m.jbssio.api.BSSReaderIntegerUnsignedType;
import com.io7m.jbssio.api.BSSReaderType;
import com.io7m.jbssio.api.BSSWriterIntegerUnsignedType;
import com.io7m.jbssio.api.BSSWriterType;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.toUnsignedLong;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Convenience functions to read and write bounded values.
 */

public final class BSSBounded
{
  private final ByteOrder order;

  /**
   * Create a new bounded reader/writer.
   *
   * @param inOrder The byte order used for length values
   */

  public BSSBounded(
    final ByteOrder inOrder)
  {
    this.order = Objects.requireNonNull(inOrder, "order");
  }

  /**
   * Read a U32 value.
   *
   * @param reader The reader
   * @param name   The value name
   *
   * @return The value
   *
   * @throws IOException On errors
   */

  public long readU32(
    final BSSReaderIntegerUnsignedType reader,
    final String name)
    throws IOException
  {
    if (this.order.equals(ByteOrder.BIG_ENDIAN)) {
      return reader.readU32BE(name);
    }
    if (this.order.equals(ByteOrder.LITTLE_ENDIAN)) {
      return reader.readU32LE(name);
    }
    throw new IllegalStateException("Unreachable code.");
  }

  /**
   * Write a U32 value.
   *
   * @param writer The writer
   * @param name   The value name
   * @param x      The value
   *
   * @throws IOException On errors
   */

  public void writeU32(
    final BSSWriterIntegerUnsignedType writer,
    final String name,
    final long x)
    throws IOException
  {
    if (this.order.equals(ByteOrder.BIG_ENDIAN)) {
      writer.writeU32BE(name, x);
    } else if (this.order.equals(ByteOrder.LITTLE_ENDIAN)) {
      writer.writeU32LE(name, x);
    }
  }

  /**
   * Write a bytes value.
   *
   * @param writer The writer
   * @param name   The value name
   * @param x      The value
   *
   * @throws IOException On errors
   */

  public void writeBytes(
    final BSSWriterType writer,
    final String name,
    final byte[] x)
    throws IOException
  {
    this.writeU32(writer, name + "Length", toUnsignedLong(x.length));
    writer.writeBytes(name, x);
    writer.align(4);
  }

  /**
   * Write a string value prefixed with a U32 length.
   *
   * @param writer  The writer
   * @param x       The value
   * @param charset The charset
   *
   * @throws IOException On errors
   */

  public void writeString(
    final BSSWriterType writer,
    final String x,
    final Charset charset)
    throws IOException
  {
    this.writeBytes(writer, "bytes", x.getBytes(charset));
  }

  /**
   * Write a UTF-8 string value prefixed with a U32 length.
   *
   * @param writer The writer
   * @param x      The value
   *
   * @throws IOException On errors
   */

  public void writeUTF8(
    final BSSWriterType writer,
    final String x)
    throws IOException
  {
    this.writeString(writer, x, UTF_8);
  }

  /**
   * Read a byte array value, prefixed with a U32 value.
   *
   * @param reader The reader
   * @param limit  The size limit
   * @param name   The value name
   *
   * @return The value
   *
   * @throws IOException On errors
   */

  public byte[] readBytes(
    final BSSReaderType reader,
    final int limit,
    final String name)
    throws IOException
  {
    final var size =
      this.readU32(reader, name + "Length");

    final var longLimit = toUnsignedLong(limit);
    if (Long.compareUnsigned(size, longLimit) > 0) {
      throw reader.createException(
        String.format(
          "Specified length of data %s exceeds limit %s",
          Long.toUnsignedString(size),
          Long.toUnsignedString(longLimit)
        ),
        Map.of(),
        IOException::new
      );
    }

    final var data = new byte[Math.toIntExact(size)];
    reader.readBytes(data);
    return data;
  }

  /**
   * Read a string value, prefixed with a U32 value.
   *
   * @param reader  The reader
   * @param limit   The size limit
   * @param name    The value name
   * @param charset The charset
   *
   * @return The value
   *
   * @throws IOException On errors
   */

  public String readString(
    final BSSReaderType reader,
    final int limit,
    final String name,
    final Charset charset)
    throws IOException
  {
    // CHECKSTYLE:OFF
    return new String(this.readBytes(reader, limit, name), charset);
    // CHECKSTYLE:ON
  }

  /**
   * Read a UTF-8 string value, prefixed with a U32 value.
   *
   * @param reader The reader
   * @param limit  The size limit
   * @param name   The value name
   *
   * @return The value
   *
   * @throws IOException On errors
   */

  public String readUTF8(
    final BSSReaderType reader,
    final int limit,
    final String name)
    throws IOException
  {
    return this.readString(reader, limit, name, UTF_8);
  }
}

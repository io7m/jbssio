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

package com.io7m.jbssio.api;

import java.io.IOException;

/**
 * The type of purely sequential writers.
 */

public interface BSSWriterSequentialType extends BSSWriterType
{
  /**
   * Create a sub writer at the current position.
   *
   * @param name The name of the writer
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  default BSSWriterSequentialType createSubWriter(
    final String name)
    throws IOException
  {
    return this.createSubWriterAt(name, this.offsetCurrentRelative());
  }

  /**
   * Create a sub writer at the current position that may write at most {@code size} bytes.
   *
   * @param name The name of the writer
   * @param size The maximum number of bytes that can be written
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  default BSSWriterSequentialType createSubWriterBounded(
    final String name,
    final long size)
    throws IOException
  {
    return this.createSubWriterAtBounded(name, this.offsetCurrentRelative(), size);
  }

  @Override
  BSSWriterSequentialType createSubWriterAt(
    String name,
    long offset)
    throws IOException;

  @Override
  BSSWriterSequentialType createSubWriterAtBounded(
    String name,
    long offset,
    long size)
    throws IOException;

  /**
   * Pad the given output with {@code value} up to the given {@code offset}.
   * If the position of the current stream is already at or beyond {@code offset},
   * no data is written.
   *
   * @param offset The relative offset
   * @param value  The pad value
   *
   * @return The number of bytes written
   *
   * @throws IOException On I/O errors
   */

  long padTo(
    long offset,
    byte value)
    throws IOException;

  /**
   * Pad the given output with zeroes up to the given {@code offset}.
   * If the position of the current stream is already at or beyond {@code offset},
   * no data is written.
   *
   * @param offset The relative offset
   *
   * @return The number of bytes written
   *
   * @throws IOException On I/O errors
   */

  default long padTo(
    final long offset)
    throws IOException
  {
    return this.padTo(offset, (byte) 0x0);
  }
}

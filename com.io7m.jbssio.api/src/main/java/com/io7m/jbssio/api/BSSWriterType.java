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

package com.io7m.jbssio.api;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.util.OptionalLong;

/**
 * The base type of byte stream structure writers.
 *
 * Writers may optionally create sub-readers that can write a subset of the full stream. Writers are
 * created with names, and the names are appended to the names of the parent writers for useful
 * diagnostic messages.
 */

public interface BSSWriterType extends Closeable, BSSAddressableType
{
  /**
   * @param name The path of the new writer
   *
   * @return A new writer
   */

  BSSWriterType createSubWriter(
    String name);

  /**
   * @param name The path of the new writer
   * @param size A limit on the number of bytes that can be written
   *
   * @return A new writer that can write at most {@code size} bytes
   *
   * @throws IllegalArgumentException If the number of bytes exceeds the limit of the current
   *                                  writer
   */

  BSSWriterType createSubWriter(
    String name,
    long size);

  /**
   * Skip {@code size} bytes of the input.
   *
   * The writer will not be allowed to seek beyond the specified limit.
   *
   * @param size The number of bytes to skip
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void skip(long size)
    throws IOException;

  /**
   * Skip enough bytes to align the writer position to a multiple of {@code size}. If the writer is
   * alwritey aligned, no bytes are skipped.
   *
   * The writer will not be allowed to seek beyond the specified limit.
   *
   * @param size The number of bytes to skip
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void align(int size)
    throws IOException;

  /**
   * @return The size limit defined for this writer, if any
   */

  OptionalLong sizeLimit();

  /**
   * Write an 8-bit signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS8(int b)
    throws IOException;

  /**
   * Write an 8-bit unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU8(int b)
    throws IOException;

  /**
   * Write a named 8-bit signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS8(
    String name,
    int b)
    throws IOException;

  /**
   * Write a named 8-bit unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU8(
    String name,
    int b)
    throws IOException;
}

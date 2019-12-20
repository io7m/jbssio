/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for
 * any purpose with or without fee is hereby granted, provided that the
 * above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 * WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR
 * BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES
 * OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
 * WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,
 * ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
 * SOFTWARE.
 */

package com.io7m.jbssio.api;

import java.io.EOFException;
import java.io.IOException;

/**
 * Functions for writing floating-point values.
 */

public interface BSSWriterFloatType
{
  /**
   * Write a named 16-bit big-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF16BE(
    String name,
    double b)
    throws IOException;

  /**
   * Write a 16-bit big-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF16BE(
    double b)
    throws IOException;

  /**
   * Write a named 16-bit little-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF16LE(
    String name,
    double b)
    throws IOException;

  /**
   * Write a 16-bit little-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF16LE(
    double b)
    throws IOException;

  /**
   * Write a named 32-bit big-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF32BE(
    String name,
    double b)
    throws IOException;

  /**
   * Write a 32-bit big-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF32BE(
    double b)
    throws IOException;

  /**
   * Write a named 32-bit little-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF32LE(
    String name,
    double b)
    throws IOException;

  /**
   * Write a 32-bit little-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF32LE(
    double b)
    throws IOException;

  /**
   * Write a named 64-bit big-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF64BE(
    String name,
    double b)
    throws IOException;

  /**
   * Write a 64-bit big-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF64BE(
    double b)
    throws IOException;

  /**
   * Write a named 64-bit little-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF64LE(
    String name,
    double b)
    throws IOException;

  /**
   * Write a 64-bit little-endian floating-point value.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeF64LE(
    double b)
    throws IOException;
}

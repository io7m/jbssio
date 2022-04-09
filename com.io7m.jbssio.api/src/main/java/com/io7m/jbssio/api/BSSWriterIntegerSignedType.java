/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import java.io.IOException;

/**
 * Functions for writing signed integers.
 */

public interface BSSWriterIntegerSignedType
{
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
   * Write a 16-bit little-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS16LE(int b)
    throws IOException;

  /**
   * Write a 16-bit big-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS16BE(int b)
    throws IOException;

  /**
   * Write a named 16-bit little-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS16LE(
    String name,
    int b)
    throws IOException;

  /**
   * Write a named 16-bit big-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS16BE(
    String name,
    int b)
    throws IOException;

  /**
   * Write a 32-bit little-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS32LE(long b)
    throws IOException;

  /**
   * Write a 32-bit big-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS32BE(long b)
    throws IOException;

  /**
   * Write a named 32-bit little-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS32LE(
    String name,
    long b)
    throws IOException;

  /**
   * Write a named 32-bit big-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS32BE(
    String name,
    long b)
    throws IOException;

  /**
   * Write a 64-bit little-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS64LE(long b)
    throws IOException;

  /**
   * Write a 64-bit big-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS64BE(long b)
    throws IOException;

  /**
   * Write a named 64-bit little-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS64LE(
    String name,
    long b)
    throws IOException;

  /**
   * Write a named 64-bit big-endian signed integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name The name of the value
   * @param b    The integer value
   *
   * @throws IOException On I/O errors, or if an attempt is made to seek or write beyond the
   *                     writer's limit
   */

  void writeS64BE(
    String name,
    long b)
    throws IOException;
}

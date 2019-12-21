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
 * Functions for writing bytes.
 */

public interface BSSWriterBytesType
{
  /**
   * Write bytes.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name   The name of the value
   * @param buffer The byte buffer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeBytes(
    String name,
    byte[] buffer)
    throws IOException;

  /**
   * Write bytes.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param name   The name of the value
   * @param buffer The byte buffer value
   * @param offset The offset in {@code buffer}
   * @param length The number of bytes to write
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeBytes(
    String name,
    byte[] buffer,
    int offset,
    int length)
    throws IOException;

  /**
   * Write bytes.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param buffer The byte buffer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeBytes(
    byte[] buffer)
    throws IOException;

  /**
   * Write bytes.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param buffer The byte buffer value
   * @param offset The offset in {@code buffer}
   * @param length The number of bytes to write
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeBytes(
    byte[] buffer,
    int offset,
    int length)
    throws IOException;
}

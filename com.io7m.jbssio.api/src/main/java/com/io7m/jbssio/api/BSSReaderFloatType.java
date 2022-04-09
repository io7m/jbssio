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

import java.io.EOFException;
import java.io.IOException;

/**
 * Functions for reading floating-point values.
 */

public interface BSSReaderFloatType
{
  /**
   * Read a 16-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF16BE()
    throws IOException, EOFException;

  /**
   * Read a 16-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF16LE()
    throws IOException, EOFException;

  /**
   * Read a 16-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name The name of the value to be used in diagnostic messages
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF16BE(String name)
    throws IOException, EOFException;

  /**
   * Read a 16-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name The name of the value to be used in diagnostic messages
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF16LE(String name)
    throws IOException, EOFException;

  /**
   * Read a 32-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF32BE()
    throws IOException, EOFException;

  /**
   * Read a 32-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF32LE()
    throws IOException, EOFException;


  /**
   * Read a 32-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name The name of the value to be used in diagnostic messages
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF32BE(String name)
    throws IOException, EOFException;

  /**
   * Read a 32-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name The name of the value to be used in diagnostic messages
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  float readF32LE(String name)
    throws IOException, EOFException;

  /**
   * Read a 64-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  double readD64BE()
    throws IOException, EOFException;

  /**
   * Read a 64-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  double readD64LE()
    throws IOException, EOFException;

  /**
   * Read a 64-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name The name of the value to be used in diagnostic messages
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  double readD64BE(String name)
    throws IOException, EOFException;

  /**
   * Read a 64-bit floating point, big-endian value.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name The name of the value to be used in diagnostic messages
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  double readD64LE(String name)
    throws IOException, EOFException;
}

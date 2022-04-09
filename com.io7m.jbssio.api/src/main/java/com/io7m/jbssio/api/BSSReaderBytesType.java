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
 * Functions to read bytes.
 */

public interface BSSReaderBytesType
{
  /**
   * Read bytes.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param buffer The buffer to which to copy bytes
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  default int readBytes(
    final byte[] buffer)
    throws IOException, EOFException
  {
    return this.readBytes(buffer, 0, buffer.length);
  }

  /**
   * Read bytes.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param buffer The buffer to which to copy bytes
   * @param offset The offset in {@code buffer} to which to write
   * @param length The maximum number of bytes to read
   *
   * @return The number of bytes read
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readBytes(
    byte[] buffer,
    int offset,
    int length)
    throws IOException, EOFException;

  /**
   * Read bytes.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name   The name of the value to be used in diagnostic messages
   * @param buffer The buffer to which to copy bytes
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  default int readBytes(
    final String name,
    final byte[] buffer)
    throws IOException, EOFException
  {
    return this.readBytes(name, buffer, 0, buffer.length);
  }

  /**
   * Read bytes.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @param name   The name of the value to be used in diagnostic messages
   * @param buffer The buffer to which to copy bytes
   * @param offset The offset in {@code buffer} to which to write
   * @param length The maximum number of bytes to read
   *
   * @return The number of bytes read
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readBytes(
    String name,
    byte[] buffer,
    int offset,
    int length)
    throws IOException, EOFException;
}

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
 * Functions for reading unsigned integers.
 */

public interface BSSReaderIntegerUnsignedType
{
  /**
   * Read an 8-bit unsigned integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readU8()
    throws IOException, EOFException;

  /**
   * Read an 16-bit signed, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readU16LE()
    throws IOException, EOFException;

  /**
   * Read an 32-bit unsigned, little-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readU32LE()
    throws IOException, EOFException;

  /**
   * Read a 64-bit unsigned, little-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readU64LE()
    throws IOException, EOFException;

  /**
   * Read a 16-bit unsigned, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readU16BE()
    throws IOException, EOFException;

  /**
   * Read a 32-bit unsigned, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readU32BE()
    throws IOException, EOFException;

  /**
   * Read a 64-bit unsigned, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readU64BE()
    throws IOException, EOFException;

  /**
   * Read an 8-bit unsigned integer.
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

  int readU8(String name)
    throws IOException, EOFException;

  /**
   * Read an 16-bit signed, big-endian integer.
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

  int readU16LE(String name)
    throws IOException, EOFException;

  /**
   * Read an 32-bit unsigned, little-endian integer.
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

  long readU32LE(String name)
    throws IOException, EOFException;

  /**
   * Read a 64-bit unsigned, little-endian integer.
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

  long readU64LE(String name)
    throws IOException, EOFException;

  /**
   * Read a 16-bit unsigned, big-endian integer.
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

  int readU16BE(String name)
    throws IOException, EOFException;

  /**
   * Read a 32-bit unsigned, big-endian integer.
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

  long readU32BE(String name)
    throws IOException, EOFException;

  /**
   * Read a 64-bit unsigned, big-endian integer.
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

  long readU64BE(String name)
    throws IOException, EOFException;
}

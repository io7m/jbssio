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

/**
 * The base type of byte stream structure readers.
 *
 * Readers may optionally create sub-readers that can read a subset of the full stream. Readers are
 * created with names, and the names are appended to the names of the parent readers for useful
 * diagnostic messages.
 */

public interface BSSReaderType extends Closeable, BSSAddressableType
{
  /**
   * @param name The path of the new reader
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderType createSubReader(
    String name)
    throws IOException;

  /**
   * @param name The path of the new reader
   * @param size A limit on the number of bytes that can be read
   *
   * @return A new reader that can read at most {@code size} bytes
   *
   * @throws IllegalArgumentException If the number of bytes exceeds the limit of the current
   *                                  reader
   * @throws IOException              On I/O errors
   */

  BSSReaderType createSubReader(
    String name,
    long size)
    throws IOException;

  /**
   * Skip {@code size} bytes of the input.
   *
   * The reader will not be allowed to seek beyond the specified limit.
   *
   * @param size The number of bytes to skip
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  void skip(long size)
    throws IOException, EOFException;

  /**
   * Skip enough bytes to align the reader position to a multiple of {@code size}. If the reader is
   * already aligned, no bytes are skipped.
   *
   * The reader will not be allowed to seek beyond the specified limit.
   *
   * @param size The number of bytes to skip
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  void align(int size)
    throws IOException, EOFException;

  /**
   * Read an 8-bit signed integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readS8()
    throws IOException, EOFException;

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
   * Read an 16-bit signed, little-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readS16LE()
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
   * Read an 32-bit signed, little-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readS32LE()
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
   * Read a 64-bit signed, little-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readS64LE()
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
   * Read a 16-bit signed, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  int readS16BE()
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
   * Read a 32-bit signed, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readS32BE()
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
   * Read a 64-bit signed, big-endian integer.
   *
   * The reader will not be allowed to read beyond the specified limit.
   *
   * @return The resulting integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or read beyond the
   *                      reader's limit
   * @throws EOFException If EOF is reached
   */

  long readS64BE()
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

  float readFBE()
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

  float readFLE()
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

  double readDBE()
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

  double readDLE()
    throws IOException, EOFException;

  /**
   * Retrieve the number of bytes available for reading.
   *
   * @return The number of bytes remaining
   */

  long bytesRemaining();


  /**
   * Read an 8-bit signed integer.
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

  int readS8(String name)
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
   * Read an 16-bit signed, little-endian integer.
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

  int readS16LE(String name)
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
   * Read an 32-bit signed, little-endian integer.
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

  long readS32LE(String name)
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
   * Read a 64-bit signed, little-endian integer.
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

  long readS64LE(String name)
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
   * Read a 16-bit signed, big-endian integer.
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

  int readS16BE(String name)
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
   * Read a 32-bit signed, big-endian integer.
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

  long readS32BE(String name)
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
   * Read a 64-bit signed, big-endian integer.
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

  long readS64BE(String name)
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

  float readFBE(String name)
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

  float readFLE(String name)
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

  double readDBE(String name)
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

  double readDLE(String name)
    throws IOException, EOFException;
}

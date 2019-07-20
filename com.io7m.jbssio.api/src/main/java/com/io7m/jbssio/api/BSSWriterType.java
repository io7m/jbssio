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

import java.io.EOFException;
import java.io.IOException;

/**
 * The base type of byte stream structure writers.
 *
 * Writers may optionally create sub-readers that can write a subset of the full stream. Writers are
 * created with names, and the names are appended to the names of the parent writers for useful
 * diagnostic messages.
 */

public interface BSSWriterType extends BSSCloseableType, BSSAddressableType, BSSSkippableType
{
  /**
   * @param name The path of the new writer
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterType createSubWriter(
    String name)
    throws IOException;

  /**
   * @param name The path of the new writer
   * @param size A limit on the number of bytes that can be written
   *
   * @return A new writer that can write at most {@code size} bytes
   *
   * @throws IllegalArgumentException If the number of bytes exceeds the limit of the current
   *                                  writer
   * @throws IOException              On I/O errors
   */

  BSSWriterType createSubWriterBounded(
    String name,
    long size)
    throws IOException;

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
   * Write a 16-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU16LE(int b)
    throws IOException;

  /**
   * Write a 16-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU16BE(int b)
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
   * Write a named 16-bit little-endian unsigned integer.
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

  void writeU16LE(
    String name,
    int b)
    throws IOException;

  /**
   * Write a named 16-bit big-endian unsigned integer.
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

  void writeU16BE(
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
   * Write a 32-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU32LE(long b)
    throws IOException;

  /**
   * Write a 32-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU32BE(long b)
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
   * Write a named 32-bit little-endian unsigned integer.
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

  void writeU32LE(
    String name,
    long b)
    throws IOException;

  /**
   * Write a named 32-bit big-endian unsigned integer.
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

  void writeU32BE(
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
   * Write a 64-bit little-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU64LE(long b)
    throws IOException;

  /**
   * Write a 64-bit big-endian unsigned integer.
   *
   * The writer will not be allowed to writer beyond the specified limit.
   *
   * @param b The integer value
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek or write beyond the
   *                      writer's limit
   * @throws EOFException If EOF is reached
   */

  void writeU64BE(long b)
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

  /**
   * Write a named 64-bit little-endian unsigned integer.
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

  void writeU64LE(
    String name,
    long b)
    throws IOException;

  /**
   * Write a named 64-bit big-endian unsigned integer.
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

  void writeU64BE(
    String name,
    long b)
    throws IOException;

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
}

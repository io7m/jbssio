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

import java.io.IOException;

/**
 * The base type of byte stream structure writers.
 *
 * Writers may optionally create sub-readers that can write a subset of the full stream. Writers are
 * created with names, and the names are appended to the names of the parent writers for useful
 * diagnostic messages.
 */

public interface BSSWriterType
  extends BSSCloseableType,
  BSSAddressableType,
  BSSSkippableType,
  BSSWriterFloatType,
  BSSWriterIntegerUnsignedType,
  BSSWriterIntegerSignedType, BSSWriterBytesType
{
  /**
   * Create a new sub writer with the given {@code name}, using the given {@code offset} (relative
   * to the current writer). If the current writer is bounded, the new sub writer will also be
   * bounded.
   *
   * @param name   The new name
   * @param offset The relative offset
   *
   * @return A new sub writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterType createSubWriterAt(
    String name,
    long offset)
    throws IOException;

  /**
   * Create a new sub writer with the given {@code name}, using the given {@code offset} (relative
   * to the current writer) and maximum length {@code size}.
   *
   * @param name   The new name
   * @param offset The relative offset
   * @param size   The maximum number of bytes
   *
   * @return A new sub writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterType createSubWriterAtBounded(
    String name,
    long offset,
    long size)
    throws IOException;

}

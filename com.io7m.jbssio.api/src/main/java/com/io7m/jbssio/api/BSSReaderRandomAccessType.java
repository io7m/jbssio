/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import java.util.Optional;

/**
 * A random-access reader.
 */

public interface BSSReaderRandomAccessType extends BSSReaderType,
  BSSSeekableType
{
  @Override
  Optional<BSSReaderRandomAccessType> parentReader();

  /**
   * Create a new sub reader with the given {@code name}, starting at {@code offset} bytes from the
   * start of the bounds of the current reader. If the current reader is bounded, the new sub reader
   * will also be bounded.
   *
   * @param name   The new name
   * @param offset The relative offset
   *
   * @return A new sub reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderRandomAccessType createSubReaderAt(
    String name,
    long offset)
    throws IOException;

  /**
   * Create a new sub reader with the given {@code name}, starting at {@code offset} bytes from the
   * start of the bounds of the current reader, limited to {@code size} bytes.
   *
   * @param name   The new name
   * @param offset The relative offset
   * @param size   The maximum number of bytes
   *
   * @return A new sub reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderRandomAccessType createSubReaderAtBounded(
    String name,
    long offset,
    long size)
    throws IOException;
}

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
 * A random-access reader.
 */

public interface BSSReaderRandomAccessType extends BSSReaderType
{
  @Override
  BSSReaderRandomAccessType createSubReader(
    String name);

  @Override
  default BSSReaderRandomAccessType createSubReader(
    final String name,
    final long size) {
    return this.createSubReader(name, 0L, size);
  }

  /**
   * Create a new sub reader with the given {@code name}, using the given {@code offset} (relative
   * to the current reader) and maximum length {@code size}.
   *
   * @param name   The new name
   * @param offset The relative offset
   * @param size   The maximum number of bytes
   *
   * @return A new sub reader
   */

  BSSReaderRandomAccessType createSubReader(
    String name,
    long offset,
    long size);

  /**
   * @return The number of bytes remaining
   */

  long bytesRemaining();

  /**
   * Seek directly to the given position within the current reader.
   *
   * @param position The position
   *
   * @throws IOException If the seek position is not within the bounds of the current reader
   */

  void seekTo(long position)
    throws IOException;
}

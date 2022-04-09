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

import java.io.EOFException;
import java.io.IOException;
import java.util.OptionalLong;

/**
 * An object that can skip forwards.
 */

public interface BSSSkippableType
{
  /**
   * Skip {@code size} bytes of the input.
   *
   * The observer will not be allowed to seek beyond the specified limit.
   *
   * @param size The number of bytes to skip
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek beyond the observer's
   *                      limit
   * @throws EOFException If EOF is reached
   */

  void skip(long size)
    throws IOException, EOFException;

  /**
   * Skip enough bytes to align the observer position to a multiple of {@code size}. If the observer
   * is already aligned, no bytes are skipped.
   *
   * The observer will not be allowed to seek beyond the specified limit.
   *
   * @param size The number of bytes to skip
   *
   * @throws IOException  On I/O errors, or if an attempt is made to seek beyond the observer's
   *                      limit
   * @throws EOFException If EOF is reached
   */

  void align(int size)
    throws IOException, EOFException;

  /**
   * @return The number of bytes remaining, if a limit is actually known or specified
   *
   * @throws IOException On I/O errors (fetching the bounds of an underlying data source/sink may require I/O)
   */

  OptionalLong bytesRemaining()
    throws IOException;
}

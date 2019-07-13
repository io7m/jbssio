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

/**
 * The base type of byte stream structure writers.
 *
 * Writers may optionally create sub-readers that can write a subset of the full stream. Writers are
 * created with names, and the names are appended to the names of the parent writers for useful
 * diagnostic messages.
 */

public interface BSSWriterType extends Closeable, BSSAddressableType
{
  /**
   * @param name The path of the new writer
   *
   * @return A new writer
   */

  BSSWriterType createSubWriter(
    String name);

  /**
   * @param name The path of the new writer
   * @param size A limit on the number of bytes that can be read
   *
   * @return A new writer that can write at most {@code size} bytes
   *
   * @throws IllegalArgumentException If the number of bytes exceeds the limit of the current
   *                                  writer
   */

  BSSWriterType createSubWriter(
    String name,
    long size);
}

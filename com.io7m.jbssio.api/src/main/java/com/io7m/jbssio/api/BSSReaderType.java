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

import java.util.Optional;

/**
 * The base type of byte stream structure readers.
 *
 * Readers may optionally create sub-readers that can read a subset of the full stream. Readers are
 * created with names, and the names are appended to the names of the parent readers for useful
 * diagnostic messages.
 */

public interface BSSReaderType
  extends BSSCloseableType,
  BSSAddressableType,
  BSSSkippableType,
  BSSReaderIntegerSignedType,
  BSSReaderIntegerUnsignedType, BSSReaderFloatType, BSSReaderBytesType
{
  /**
   * @return The parent of this reader, if one exists
   */

  Optional<? extends BSSReaderType> parentReader();
}

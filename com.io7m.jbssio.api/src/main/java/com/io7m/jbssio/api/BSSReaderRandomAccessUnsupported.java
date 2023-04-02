/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;

/**
 * A reader that throws {@link UnsupportedOperationException} for all read operations.
 *
 * @since 1.1.0
 */

public final class BSSReaderRandomAccessUnsupported
  implements BSSReaderRandomAccessType
{
  /**
   * Construct a reader.
   */

  public BSSReaderRandomAccessUnsupported()
  {

  }

  @Override
  public long offsetCurrentAbsolute()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long offsetCurrentRelative()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public URI uri()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String path()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isClosed()
  {
return false;
  }

  @Override
  public int readBytes(
    final byte[] buffer,
    final int offset,
    final int length)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF16BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF16LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF16BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF16LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF32BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF32LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF32BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public float readF32LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double readD64BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double readD64LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double readD64BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public double readD64LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readS8()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readS16LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS32LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS64LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readS16BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS32BE()
    throws IOException, EOFException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS64BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readS8(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readS16LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS32LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS64LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readS16BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS32BE(final String name)
    throws IOException, EOFException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readS64BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readU8()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readU16LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU32LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU64LE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readU16BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU32BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU64BE()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readU8(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readU16LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU32LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU64LE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int readU16BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU32BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long readU64BE(final String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<BSSReaderRandomAccessType> parentReader()
  {
    return Optional.empty();
  }

  @Override
  public BSSReaderRandomAccessType createSubReaderAt(
    final String name,
    final long offset)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public BSSReaderRandomAccessType createSubReaderAtBounded(
    final String name,
    final long offset,
    final long size)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void skip(final long size)
    throws IOException, EOFException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void align(final int size)
    throws IOException, EOFException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public OptionalLong bytesRemaining()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close()
    throws IOException
  {

  }

  @Override
  public void seekTo(final long position)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public <E extends Exception> E createException(
    final String message,
    final Map<String, String> attributes,
    final Function<String, E> constructor)
  {
    throw new UnsupportedOperationException();
  }
}

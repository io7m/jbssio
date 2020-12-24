/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
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
import java.util.OptionalLong;

/**
 * A writer that throws {@link UnsupportedOperationException} for all write operations.
 *
 * @since 1.1.0
 */

public final class BSSWriterRandomAccessUnsupported
  implements BSSWriterRandomAccessType
{
  /**
   * Construct a writer.
   */

  public BSSWriterRandomAccessUnsupported()
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
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(final byte[] buffer)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16BE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16BE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16LE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF16LE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32BE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32BE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32LE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF32LE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64BE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64BE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64LE(
    final String name,
    final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeF64LE(final double b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS8(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS8(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16LE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16BE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16LE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS16BE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32BE(final long b)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS32BE(
    final String name,
    final long b)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64BE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeS64BE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU8(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU8(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16LE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16BE(final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16LE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU16BE(
    final String name,
    final int b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32BE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU32BE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64LE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64BE(final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64LE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void writeU64BE(
    final String name,
    final long b)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public BSSWriterRandomAccessType createSubWriterAt(
    final String name,
    final long offset)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public BSSWriterRandomAccessType createSubWriterAtBounded(
    final String name,
    final long offset,
    final long size)
    throws IOException
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
}

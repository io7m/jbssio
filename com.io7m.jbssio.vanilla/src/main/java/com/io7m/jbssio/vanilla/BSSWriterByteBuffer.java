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

package com.io7m.jbssio.vanilla;

import com.io7m.jbssio.api.BSSWriterRandomAccessType;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.Callable;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

final class BSSWriterByteBuffer extends BSSRandomAccess implements BSSWriterRandomAccessType
{
  private final ByteBuffer map;

  private BSSWriterByteBuffer(
    final BSSWriterByteBuffer inParent,
    final URI inURI,
    final BSSRangeHalfOpen inParentRangeRelative,
    final String inName,
    final ByteBuffer inMap,
    final Callable<Void> inOnClose)
  {
    super(inParent, inParentRangeRelative, inOnClose, inURI, inName);
    this.map = Objects.requireNonNull(inMap, "map");
  }

  static BSSWriterRandomAccessType createFromByteBuffer(
    final URI uri,
    final ByteBuffer buffer,
    final String name)
  {
    return new BSSWriterByteBuffer(
      null,
      uri,
      new BSSRangeHalfOpen(0L, OptionalLong.of(Integer.toUnsignedLong(buffer.capacity()))),
      name,
      buffer,
      () -> null);
  }

  private static int longPositionTo2GBLimitedByteBufferPosition(final long position)
  {
    return Math.toIntExact(position);
  }

  @Override
  public BSSWriterRandomAccessType createSubWriter(
    final String inName)
    throws IOException
  {
    Objects.requireNonNull(inName, "path");

    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path())
        .append('.')
        .append(inName)
        .toString();

    return new BSSWriterByteBuffer(
      this,
      this.uri,
      this.createSameSubRange(),
      newName,
      this.map,
      () -> null);
  }

  @Override
  public BSSWriterRandomAccessType createSubWriterAt(
    final String inName,
    final long offset)
    throws IOException
  {
    Objects.requireNonNull(inName, "path");

    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path())
        .append('.')
        .append(inName)
        .toString();

    return new BSSWriterByteBuffer(
      this,
      this.uri,
      this.createOffsetSubRange(offset),
      newName,
      this.map,
      () -> null);
  }

  @Override
  public BSSWriterRandomAccessType createSubWriterAtBounded(
    final String inName,
    final long offset,
    final long size)
    throws IOException
  {
    Objects.requireNonNull(inName, "path");

    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path())
        .append('.')
        .append(inName)
        .toString();

    return new BSSWriterByteBuffer(
      this,
      this.uri,
      this.createSubRange(offset, size),
      newName,
      this.map,
      () -> null);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[BSSWriterByteBuffer ")
      .append(this.uri())
      .append(" ")
      .append(this.path())
      .append("]")
      .toString();
  }

  private void writeS8p(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);
    this.map.put(longPositionTo2GBLimitedByteBufferPosition(position), (byte) b);
  }

  private void writeU8p(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);
    this.map.put(longPositionTo2GBLimitedByteBufferPosition(position), (byte) (b & 0xff));
  }

  @Override
  public void writeS8(final int b)
    throws IOException
  {
    this.writeS8p(null, b);
  }

  @Override
  public void writeU8(final int b)
    throws IOException
  {
    this.writeU8p(null, b);
  }

  @Override
  public void writeS8(
    final String name,
    final int b)
    throws IOException
  {
    this.writeS8p(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU8(
    final String name,
    final int b)
    throws IOException
  {
    this.writeU8p(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS16LEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putShort(longPositionTo2GBLimitedByteBufferPosition(position), (short) b);
  }

  private void writeU16LEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putChar(longPositionTo2GBLimitedByteBufferPosition(position), (char) (b & 0xffff));
  }

  private void writeS16BEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(BIG_ENDIAN);
    this.map.putShort(longPositionTo2GBLimitedByteBufferPosition(position), (short) b);
  }

  private void writeU16BEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(BIG_ENDIAN);
    this.map.putChar(longPositionTo2GBLimitedByteBufferPosition(position), (char) (b & 0xffff));
  }

  @Override
  public void writeS16LE(final int b)
    throws IOException
  {
    this.writeS16LEp(null, b);
  }

  @Override
  public void writeS16BE(final int b)
    throws IOException
  {
    this.writeS16BEp(null, b);
  }

  @Override
  public void writeU16LE(final int b)
    throws IOException
  {
    this.writeU16LEp(null, b);
  }

  @Override
  public void writeU16BE(final int b)
    throws IOException
  {
    this.writeU16BEp(null, b);
  }

  @Override
  public void writeS16LE(
    final String name,
    final int b)
    throws IOException
  {
    this.writeS16LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeS16BE(
    final String name,
    final int b)
    throws IOException
  {
    this.writeS16BEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU16LE(
    final String name,
    final int b)
    throws IOException
  {
    this.writeU16LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU16BE(
    final String name,
    final int b)
    throws IOException
  {
    this.writeU16BEp(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS32LEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putInt(longPositionTo2GBLimitedByteBufferPosition(position), (int) b);
  }

  private void writeU32LEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putInt(longPositionTo2GBLimitedByteBufferPosition(position), (int) (b & 0xffff_ffffL));
  }

  private void writeS32BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(BIG_ENDIAN);
    this.map.putInt(longPositionTo2GBLimitedByteBufferPosition(position), (int) b);
  }

  private void writeU32BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(BIG_ENDIAN);
    this.map.putInt(longPositionTo2GBLimitedByteBufferPosition(position), (int) (b & 0xffff_ffffL));
  }

  @Override
  public void writeS32LE(final long b)
    throws IOException
  {
    this.writeS32LEp(null, b);
  }

  @Override
  public void writeS32BE(final long b)
    throws IOException
  {
    this.writeS32BEp(null, b);
  }

  @Override
  public void writeU32LE(final long b)
    throws IOException
  {
    this.writeU32LEp(null, b);
  }

  @Override
  public void writeU32BE(final long b)
    throws IOException
  {
    this.writeU32BEp(null, b);
  }

  @Override
  public void writeS32LE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeS32LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeS32BE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeS32BEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU32LE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeU32LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU32BE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeU32BEp(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS64LEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeU64LEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(LITTLE_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeS64BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(BIG_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeU64BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(BIG_ENDIAN);
    this.map.putLong(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  @Override
  public void writeS64LE(final long b)
    throws IOException
  {
    this.writeS64LEp(null, b);
  }

  @Override
  public void writeS64BE(final long b)
    throws IOException
  {
    this.writeS64BEp(null, b);
  }

  @Override
  public void writeU64LE(final long b)
    throws IOException
  {
    this.writeU64LEp(null, b);
  }

  @Override
  public void writeU64BE(final long b)
    throws IOException
  {
    this.writeU64BEp(null, b);
  }

  @Override
  public void writeS64LE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeS64LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeS64BE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeS64BEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU64LE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeU64LEp(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU64BE(
    final String name,
    final long b)
    throws IOException
  {
    this.writeU64BEp(Objects.requireNonNull(name, "name"), b);
  }

  private void writeBytesP(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException
  {
    Objects.requireNonNull(buffer, "buffer");
    this.checkNotClosed();
    final var llength = Integer.toUnsignedLong(length);
    this.checkHasBytesRemaining(name, llength);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(llength);
    this.map.position(longPositionTo2GBLimitedByteBufferPosition(position));
    this.map.put(buffer, offset, length);
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer)
    throws IOException
  {
    this.writeBytesP(Objects.requireNonNull(name, "name"), buffer, 0, buffer.length);
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException
  {
    this.writeBytesP(name, buffer, offset, length);
  }

  @Override
  public void writeBytes(final byte[] buffer)
    throws IOException
  {
    this.writeBytesP(null, buffer, 0, buffer.length);
  }

  @Override
  public void writeBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException
  {
    this.writeBytesP(null, buffer, offset, length);
  }

  private void writeF64p(
    final String name,
    final double b,
    final ByteOrder order)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(order);
    this.map.putDouble(longPositionTo2GBLimitedByteBufferPosition(position), b);
  }

  private void writeF32p(
    final String name,
    final double b,
    final ByteOrder order)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(order);
    this.map.putFloat(longPositionTo2GBLimitedByteBufferPosition(position), (float) b);
  }

  @Override
  public void writeF64BE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF64p(Objects.requireNonNull(name, "name"), b, BIG_ENDIAN);
  }

  @Override
  public void writeF64BE(final double b)
    throws IOException
  {
    this.writeF64p(null, b, BIG_ENDIAN);
  }

  @Override
  public void writeF32BE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF32p(Objects.requireNonNull(name, "name"), b, BIG_ENDIAN);
  }

  @Override
  public void writeF32BE(final double b)
    throws IOException
  {
    this.writeF32p(null, b, BIG_ENDIAN);
  }

  @Override
  public void writeF64LE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF64p(Objects.requireNonNull(name, "name"), b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF64LE(final double b)
    throws IOException
  {
    this.writeF64p(null, b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF32LE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF32p(Objects.requireNonNull(name, "name"), b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF32LE(final double b)
    throws IOException
  {
    this.writeF32p(null, b, LITTLE_ENDIAN);
  }
}

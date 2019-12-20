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

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.api.BSSReaderRandomAccessType;
import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

final class BSSReaderByteBuffer extends BSSRandomAccess<BSSReaderRandomAccessType>
  implements BSSReaderRandomAccessType
{
  private final ByteBuffer map;
  private final BSSRangeHalfOpen physicalBounds;

  private BSSReaderByteBuffer(
    final BSSReaderByteBuffer inParent,
    final URI inURI,
    final BSSRangeHalfOpen inRangeRelative,
    final String inName,
    final ByteBuffer inMap,
    final Callable<Void> inOnClose,
    final Consumer<? extends BSSReaderRandomAccessType> inOnUserClose)
  {
    super(inParent, inRangeRelative, inOnClose, inURI, inName, inOnUserClose);
    this.map =
      Objects.requireNonNull(inMap, "map");
    this.physicalBounds =
      BSSRangeHalfOpen.create(0L, (long) inMap.capacity());
  }

  static BSSReaderRandomAccessType createFromByteBuffer(
    final URI uri,
    final ByteBuffer buffer,
    final String name,
    final Consumer<? extends BSSReaderRandomAccessType> inOnUserClose)
  {
    return new BSSReaderByteBuffer(
      null,
      uri,
      new BSSRangeHalfOpen(
        0L,
        OptionalLong.of(Integer.toUnsignedLong(buffer.capacity()))),
      name,
      buffer,
      () -> null,
      inOnUserClose);
  }

  private static int longPositionTo2GBLimitedByteBufferPosition(
    final long position)
  {
    return Math.toIntExact(position);
  }

  @Override
  public BSSReaderRandomAccessType createSubReaderAtBounded(
    final String inName,
    final long offset,
    final long size)
    throws IOException
  {
    Objects.requireNonNull(inName, "path");
    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path)
        .append('.')
        .append(inName)
        .toString();

    return new BSSReaderByteBuffer(
      this,
      this.uri,
      this.createSubRange(offset, size),
      newName,
      this.map,
      () -> null,
      this.onUserClose());
  }

  @Override
  public Optional<BSSReaderRandomAccessType> parentReader()
  {
    return Optional.ofNullable((BSSReaderRandomAccessType) super.parent());
  }

  @Override
  public BSSReaderRandomAccessType createSubReaderAt(
    final String inName,
    final long offset)
    throws IOException
  {
    Objects.requireNonNull(inName, "path");
    this.checkNotClosed();

    final var newName =
      new StringBuilder(32)
        .append(this.path)
        .append('.')
        .append(inName)
        .toString();

    return new BSSReaderByteBuffer(
      this,
      this.uri,
      this.createOffsetSubRange(offset),
      newName,
      this.map,
      () -> null,
      this.onUserClose());
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[BSSReaderByteBuffer ")
      .append(this.uri())
      .append(" ")
      .append(this.path())
      .append("]")
      .toString();
  }

  private int readS8p(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);
    this.map.position(0);
    return (int) this.map.get(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private int readU8p(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(1L);
    this.map.position(0);
    return (int) this.map.get(longPositionTo2GBLimitedByteBufferPosition(
      position)) & 0xff;
  }

  private int readS16LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return (int) this.map.getShort(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private int readU16LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return (int) this.map.getChar(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private long readS32LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return (long) this.map.getInt(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private long readU32LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return (long) (this.map.getInt(longPositionTo2GBLimitedByteBufferPosition(
      position))) & 0xffff_ffffL;
  }

  private long readS64LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return this.map.getLong(longPositionTo2GBLimitedByteBufferPosition(position));
  }

  private long readU64LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return this.map.getLong(longPositionTo2GBLimitedByteBufferPosition(position));
  }

  private int readS16BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return (int) this.map.getShort(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private int readU16BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return (int) this.map.getChar(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private long readS32BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return (long) this.map.getInt(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private long readU32BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return (long) (this.map.getInt(longPositionTo2GBLimitedByteBufferPosition(
      position))) & 0xffff_ffffL;
  }

  private long readS64BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return this.map.getLong(longPositionTo2GBLimitedByteBufferPosition(position));
  }

  private long readU64BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return this.map.getLong(longPositionTo2GBLimitedByteBufferPosition(position));
  }

  private float readF32BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return this.map.getFloat(longPositionTo2GBLimitedByteBufferPosition(position));
  }

  private float readF32LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return this.map.getFloat(longPositionTo2GBLimitedByteBufferPosition(position));
  }

  private double readD64BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return this.map.getDouble(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private double readD64LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return this.map.getDouble(longPositionTo2GBLimitedByteBufferPosition(
      position));
  }

  private float readF16BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(ByteOrder.BIG_ENDIAN);
    this.map.position(0);
    return Binary16.unpackFloat(
      this.map.getChar(longPositionTo2GBLimitedByteBufferPosition(position)));
  }

  private float readF16LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    this.map.position(0);
    return Binary16.unpackFloat(
      this.map.getChar(longPositionTo2GBLimitedByteBufferPosition(position)));
  }

  private int readBytesp(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    final var llong = Integer.toUnsignedLong(length);
    this.checkHasBytesRemaining(name, llong);
    final var position = this.offsetCurrentAbsolute();
    this.map.position(longPositionTo2GBLimitedByteBufferPosition(position));
    this.map.get(buffer, offset, length);
    this.increaseOffsetRelative(llong);
    return length;
  }

  @Override
  public int readS8()
    throws IOException
  {
    return this.readS8p(null);
  }

  @Override
  public int readU8()
    throws IOException
  {
    return this.readU8p(null);
  }

  @Override
  public int readS16LE()
    throws IOException, EOFException
  {
    return this.readS16LEp(null);
  }

  @Override
  public int readU16LE()
    throws IOException, EOFException
  {
    return this.readU16LEp(null);
  }

  @Override
  public long readS32LE()
    throws IOException, EOFException
  {
    return this.readS32LEp(null);
  }

  @Override
  public long readU32LE()
    throws IOException, EOFException
  {
    return this.readU32LEp(null);
  }

  @Override
  public long readS64LE()
    throws IOException, EOFException
  {
    return this.readS64LEp(null);
  }

  @Override
  public long readU64LE()
    throws IOException, EOFException
  {
    return this.readU64LEp(null);
  }

  @Override
  public int readS16BE()
    throws IOException, EOFException
  {
    return this.readS16BEp(null);
  }

  @Override
  public int readU16BE()
    throws IOException, EOFException
  {
    return this.readU16BEp(null);
  }

  @Override
  public long readS32BE()
    throws IOException, EOFException
  {
    return this.readS32BEp(null);
  }

  @Override
  public long readU32BE()
    throws IOException, EOFException
  {
    return this.readU32BEp(null);
  }

  @Override
  public long readS64BE()
    throws IOException, EOFException
  {
    return this.readS64BEp(null);
  }

  @Override
  public long readU64BE()
    throws IOException, EOFException
  {
    return this.readU64BEp(null);
  }

  @Override
  public float readF16BE()
    throws IOException, EOFException
  {
    return this.readF16BEp(null);
  }

  @Override
  public float readF16LE()
    throws IOException, EOFException
  {
    return this.readF16LEp(null);
  }

  @Override
  public float readF16BE(final String name)
    throws IOException, EOFException
  {
    return this.readF16BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF16LE(final String name)
    throws IOException, EOFException
  {
    return this.readF16LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF32BE()
    throws IOException, EOFException
  {
    return this.readF32BEp(null);
  }

  @Override
  public float readF32LE()
    throws IOException, EOFException
  {
    return this.readF32LEp(null);
  }

  @Override
  public double readD64BE()
    throws IOException, EOFException
  {
    return this.readD64BEp(null);
  }

  @Override
  public double readD64LE()
    throws IOException, EOFException
  {
    return this.readD64LEp(null);
  }

  @Override
  public int readS8(final String name)
    throws IOException
  {
    return this.readS8p(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readU8(final String name)
    throws IOException
  {
    return this.readU8p(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readS16LE(final String name)
    throws IOException, EOFException
  {
    return this.readS16LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readU16LE(final String name)
    throws IOException, EOFException
  {
    return this.readU16LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS32LE(final String name)
    throws IOException, EOFException
  {
    return this.readS32LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU32LE(final String name)
    throws IOException, EOFException
  {
    return this.readU32LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS64LE(final String name)
    throws IOException, EOFException
  {
    return this.readS64LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU64LE(final String name)
    throws IOException, EOFException
  {
    return this.readU64LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readS16BE(final String name)
    throws IOException, EOFException
  {
    return this.readS16BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readU16BE(final String name)
    throws IOException, EOFException
  {
    return this.readU16BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS32BE(final String name)
    throws IOException, EOFException
  {
    return this.readS32BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU32BE(final String name)
    throws IOException, EOFException
  {
    return this.readU32BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readS64BE(final String name)
    throws IOException, EOFException
  {
    return this.readS64BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public long readU64BE(final String name)
    throws IOException, EOFException
  {
    return this.readU64BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF32BE(final String name)
    throws IOException, EOFException
  {
    return this.readF32BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readF32LE(final String name)
    throws IOException, EOFException
  {
    return this.readF32LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public double readD64BE(final String name)
    throws IOException, EOFException
  {
    return this.readD64BEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public double readD64LE(final String name)
    throws IOException, EOFException
  {
    return this.readD64LEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    return this.readBytesp(
      Objects.requireNonNull(name, "name"),
      buffer,
      offset,
      length);
  }

  @Override
  public int readBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    return this.readBytesp(null, buffer, offset, length);
  }

  @Override
  protected BSSRangeHalfOpen physicalSourceAbsoluteBounds()
  {
    return this.physicalBounds;
  }
}

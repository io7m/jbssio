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

package com.io7m.jbssio.vanilla.internal;

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.api.BSSWriterRandomAccessType;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.Callable;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * A random access writer based on a seekable byte channel.
 */

public final class BSSWriterSeekableChannel
  extends BSSRandomAccess<BSSWriterRandomAccessType> implements
  BSSWriterRandomAccessType
{
  /**
   * Seekable byte channels are assumed to be growable, for writers, and
   * therefore have no specified upper bounds even if the size of the underlying
   * channel is known.
   */

  private static final BSSRangeHalfOpen PHYSICAL_BOUNDS =
    new BSSRangeHalfOpen(0L, OptionalLong.empty());

  private final SeekableByteChannel channel;
  private final ByteBuffer writeBuffer;

  private BSSWriterSeekableChannel(
    final BSSWriterSeekableChannel inParent,
    final URI inURI,
    final BSSRangeHalfOpen inParentRangeRelative,
    final String inName,
    final SeekableByteChannel inChannel,
    final ByteBuffer inBuffer,
    final Callable<Void> inOnClose)
  {
    super(inParent, inParentRangeRelative, inOnClose, inURI, inName);

    this.channel =
      Objects.requireNonNull(inChannel, "channel");
    this.writeBuffer =
      Objects.requireNonNull(inBuffer, "inBuffer");
  }

  /**
   * Create a writer.
   *
   * @param uri     The target URI
   * @param channel The target channel
   * @param name    The name
   * @param size    The size
   *
   * @return A writer
   */

  public static BSSWriterRandomAccessType createFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final OptionalLong size)
  {
    final var buffer = ByteBuffer.allocateDirect(8);
    return new BSSWriterSeekableChannel(
      null,
      uri,
      new BSSRangeHalfOpen(0L, size),
      name,
      channel,
      buffer,
      () -> {
        channel.close();
        return null;
      });
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
        .append(BSSPaths.PATH_SEPARATOR)
        .append(inName)
        .toString();

    return new BSSWriterSeekableChannel(
      this,
      this.uri,
      this.createOffsetSubRange(offset),
      newName,
      this.channel,
      this.writeBuffer,
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
        .append(BSSPaths.PATH_SEPARATOR)
        .append(inName)
        .toString();

    return new BSSWriterSeekableChannel(
      this,
      this.uri,
      this.createSubRange(offset, size),
      newName,
      this.channel,
      this.writeBuffer,
      () -> null);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[BSSWriterSeekableChannel ")
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

    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.limit(1);
    this.writeBuffer.put(0, (byte) b);
    this.writeAll();
  }

  private void writeAll()
    throws IOException
  {
    while (this.writeBuffer.hasRemaining()) {
      this.channel.write(this.writeBuffer);
    }
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

    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.limit(1);
    this.writeBuffer.put(0, (byte) (b & 0xff));
    this.writeAll();
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

  private void writeS16(
    final short b,
    final long position,
    final ByteOrder order)
    throws IOException
  {
    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(2);
    this.writeBuffer.putShort(0, b);
    this.writeAll();
  }

  private void writeU16(
    final int b,
    final long position,
    final ByteOrder order)
    throws IOException
  {
    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(2);
    this.writeBuffer.putChar(0, (char) (b & 0xffff));
    this.writeAll();
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
    this.writeS16((short) b, position, LITTLE_ENDIAN);
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
    this.writeU16(b, position, LITTLE_ENDIAN);
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
    this.writeS16((short) b, position, BIG_ENDIAN);
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
    this.writeU16(b, position, BIG_ENDIAN);
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

  private void writeInt(
    final int b,
    final long position,
    final ByteOrder order)
    throws IOException
  {
    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(4);
    this.writeBuffer.putInt(0, b);
    this.writeAll();
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
    this.writeInt((int) b, position, LITTLE_ENDIAN);
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
    this.writeInt((int) (b & 0xffff_ffffL), position, LITTLE_ENDIAN);
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
    this.writeInt((int) b, position, BIG_ENDIAN);
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
    this.writeInt((int) (b & 0xffff_ffffL), position, BIG_ENDIAN);
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

  private void writeLong(
    final long b,
    final long position,
    final ByteOrder order)
    throws IOException
  {
    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(8);
    this.writeBuffer.putLong(0, b);
    this.writeAll();
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
    this.writeLong(b, position, LITTLE_ENDIAN);
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
    this.writeLong(b, position, LITTLE_ENDIAN);
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
    this.writeLong(b, position, BIG_ENDIAN);
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
    this.writeLong(b, position, BIG_ENDIAN);
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
    Objects.requireNonNull(buffer, "writeBuffer");
    this.checkNotClosed();
    final var llength = Integer.toUnsignedLong(length);
    this.checkHasBytesRemaining(name, llength);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(llength);

    final var wrapper = ByteBuffer.wrap(buffer, offset, length);
    this.channel.position(position);
    while (wrapper.hasRemaining()) {
      this.channel.write(wrapper);
    }
  }

  @Override
  public void writeBytes(
    final String name,
    final byte[] buffer)
    throws IOException
  {
    this.writeBytesP(
      Objects.requireNonNull(name, "name"),
      buffer,
      0,
      buffer.length);
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

  private void writeF64(
    final String name,
    final ByteOrder order,
    final double x)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(8L);
    this.writeDouble(x, position, order);
  }

  private void writeF32(
    final String name,
    final ByteOrder order,
    final double x)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(4L);
    this.writeFloat(x, position, order);
  }

  private void writeF16(
    final String name,
    final ByteOrder order,
    final double x)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetCurrentAbsolute();
    this.increaseOffsetRelative(2L);

    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(2);
    this.writeBuffer.putChar(0, Binary16.packDouble(x));
    this.writeAll();
  }

  private void writeDouble(
    final double x,
    final long position,
    final ByteOrder order)
    throws IOException
  {
    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(8);
    this.writeBuffer.putDouble(0, x);
    this.writeAll();
  }

  private void writeFloat(
    final double x,
    final long position,
    final ByteOrder order)
    throws IOException
  {
    this.channel.position(position);
    this.writeBuffer.position(0);
    this.writeBuffer.order(order);
    this.writeBuffer.limit(4);
    this.writeBuffer.putFloat(0, (float) x);
    this.writeAll();
  }

  @Override
  public void writeF64BE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF64(Objects.requireNonNull(name, "name"), BIG_ENDIAN, b);
  }

  @Override
  public void writeF64BE(final double b)
    throws IOException
  {
    this.writeF64(null, BIG_ENDIAN, b);
  }

  @Override
  public void writeF16BE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF16(Objects.requireNonNull(name, "name"), BIG_ENDIAN, b);
  }

  @Override
  public void writeF16BE(final double b)
    throws IOException
  {
    this.writeF16(null, BIG_ENDIAN, b);
  }

  @Override
  public void writeF16LE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF16(Objects.requireNonNull(name, "name"), LITTLE_ENDIAN, b);
  }

  @Override
  public void writeF16LE(final double b)
    throws IOException
  {
    this.writeF16(null, LITTLE_ENDIAN, b);
  }

  @Override
  public void writeF32BE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF32(Objects.requireNonNull(name, "name"), BIG_ENDIAN, b);
  }

  @Override
  public void writeF32BE(final double b)
    throws IOException
  {
    this.writeF32(null, BIG_ENDIAN, b);
  }

  @Override
  public void writeF64LE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF64(Objects.requireNonNull(name, "name"), LITTLE_ENDIAN, b);
  }

  @Override
  public void writeF64LE(final double b)
    throws IOException
  {
    this.writeF64(null, LITTLE_ENDIAN, b);
  }

  @Override
  public void writeF32LE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF32(Objects.requireNonNull(name, "name"), LITTLE_ENDIAN, b);
  }

  @Override
  public void writeF32LE(final double b)
    throws IOException
  {
    this.writeF32(null, LITTLE_ENDIAN, b);
  }

  @Override
  protected BSSRangeHalfOpen physicalSourceAbsoluteBounds()
  {
    return PHYSICAL_BOUNDS;
  }
}

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

import com.io7m.jbssio.api.BSSReaderRandomAccessType;
import com.io7m.jranges.RangeHalfOpenL;

import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

final class BSSReaderByteBuffer implements BSSReaderRandomAccessType
{
  private final String path;
  private final ByteBuffer map;
  private final AtomicBoolean closed;
  private final Callable<Void> onClose;
  private final BSSReaderByteBuffer parent;
  private final URI uri;
  private final RangeHalfOpenL rangeRelative;
  private long offsetRelative;

  private BSSReaderByteBuffer(
    final BSSReaderByteBuffer inParent,
    final URI inURI,
    final RangeHalfOpenL inRange,
    final String inName,
    final ByteBuffer inMap,
    final AtomicBoolean inClosed,
    final Callable<Void> inOnClose)
  {
    this.parent = inParent;

    this.uri =
      Objects.requireNonNull(inURI, "inURI");
    this.rangeRelative =
      Objects.requireNonNull(inRange, "inRange");
    this.path =
      Objects.requireNonNull(inName, "inName");
    this.map =
      Objects.requireNonNull(inMap, "map");
    this.closed =
      Objects.requireNonNull(inClosed, "closed");
    this.onClose =
      Objects.requireNonNull(inOnClose, "onClose");

    this.offsetRelative = 0L;
  }

  static BSSReaderRandomAccessType createFromByteBuffer(
    final URI uri,
    final ByteBuffer buffer,
    final String name)
  {
    return new BSSReaderByteBuffer(
      null,
      uri,
      RangeHalfOpenL.of(0L, Integer.toUnsignedLong(buffer.capacity())),
      name,
      buffer,
      new AtomicBoolean(false),
      () -> null);
  }

  static BSSReaderRandomAccessType createFromFileChannel(
    final URI uri,
    final FileChannel channel,
    final String name)
    throws IOException
  {
    final var size = channel.size();
    final var map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, size);
    final var closed = new AtomicBoolean(false);
    return new BSSReaderByteBuffer(
      null,
      uri,
      RangeHalfOpenL.of(0L, size),
      name,
      map,
      closed,
      () -> {
        if (closed.compareAndSet(false, true)) {
          channel.close();
        }
        return null;
      });
  }

  private void checkNotClosed()
    throws ClosedChannelException
  {
    if (this.closed.get()) {
      throw new ClosedChannelException();
    }
  }

  @Override
  public BSSReaderRandomAccessType createSubReader(
    final String inName)
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
      this.rangeRelative,
      newName,
      this.map,
      this.closed,
      () -> null);
  }

  @Override
  public BSSReaderRandomAccessType createSubReader(
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

    final var newRange = RangeHalfOpenL.of(offset, size);
    if (!newRange.isIncludedIn(this.rangeRelative)) {
      final var lineSeparator = System.lineSeparator();
      throw new IllegalArgumentException(
        new StringBuilder(128)
          .append("Sub-reader bounds cannot exceed the bounds of this reader.")
          .append(lineSeparator)
          .append("  Reader URI: ")
          .append(this.uri)
          .append(lineSeparator)
          .append("  Reader path: ")
          .append(this.path)
          .append(lineSeparator)
          .append("  Reader bounds: [0x")
          .append(Long.toUnsignedString(this.rangeRelative.lower(), 16))
          .append(", 0x")
          .append(Long.toUnsignedString(this.rangeRelative.upper(), 16))
          .append(")")
          .append(lineSeparator)
          .append("  Requested bounds: [0x")
          .append(Long.toUnsignedString(newRange.lower(), 16))
          .append(", 0x")
          .append(Long.toUnsignedString(newRange.upper(), 16))
          .append(")")
          .append(lineSeparator)
          .toString());
    }

    return new BSSReaderByteBuffer(
      this,
      this.uri,
      newRange,
      newName,
      this.map,
      this.closed,
      () -> null);
  }

  @Override
  public long bytesRemaining()
  {
    return this.rangeRelative.upper() - this.offsetRelative;
  }

  @Override
  public void seekTo(final long position)
    throws IOException
  {
    this.checkNotClosed();

    if (!this.rangeRelative.includesValue(position)) {
      throw this.outOfBounds(position, null, IOException::new);
    }
    this.offsetRelative = position;
  }

  @Override
  public String toString()
  {
    return String.format(
      "[BSSReaderByteBuffer %s %s [absolute %s] [relative %s]]",
      this.uri(),
      this.path(),
      Long.toUnsignedString(this.offsetAbsolute()),
      Long.toUnsignedString(this.offsetRelative()));
  }

  @Override
  public void skip(final long size)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(null, size);
    this.offsetRelative += size;
  }

  @Override
  public void align(final int alignment)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    final var diff = this.offsetAbsolute() % (long) alignment;
    if (diff == 0L) {
      return;
    }

    this.skip((long) alignment - diff);
  }

  private void checkHasBytesRemaining(
    final String name,
    final long want)
    throws IOException
  {
    if (want > this.bytesRemaining()) {
      throw this.outOfBounds(this.offsetRelative + want, name, IOException::new);
    }
  }

  private long absoluteOf(
    final long position)
  {
    final var readerParent = this.parent;
    if (readerParent == null) {
      return position;
    }
    return readerParent.absoluteOf(0L) + position;
  }

  private IOException outOfBounds(
    final long targetPosition,
    final String name,
    final Function<String, IOException> exceptionSupplier)
  {
    final var lineSeparator = System.lineSeparator();
    final var stringBuilder = new StringBuilder(128);

    stringBuilder
      .append("Out of bounds.")
      .append(lineSeparator)
      .append("  Reader URI: ")
      .append(this.uri())
      .append(lineSeparator);

    stringBuilder
      .append("  Reader path: ")
      .append(this.path());

    if (name != null) {
      stringBuilder.append(":")
        .append(name);
    }
    stringBuilder.append(lineSeparator);

    stringBuilder
      .append("  Reader bounds: [absolute 0x")
      .append(Long.toUnsignedString(this.absoluteOf(this.rangeRelative.lower()), 16))
      .append(", 0x")
      .append(Long.toUnsignedString(this.absoluteOf(this.rangeRelative.upper()), 16))
      .append(")")
      .append(lineSeparator);

    stringBuilder
      .append("  Target offset: absolute 0x")
      .append(Long.toUnsignedString(this.absoluteOf(targetPosition), 16))
      .append(lineSeparator);

    stringBuilder
      .append("  Offset: absolute 0x")
      .append(Long.toUnsignedString(this.offsetAbsolute(), 16));

    return exceptionSupplier.apply(stringBuilder.toString());
  }

  private int readS8p(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 1L;
    return (int) this.map.get(Math.toIntExact(position));
  }

  private int readU8p(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 1L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 1L;
    return (int) this.map.get(Math.toIntExact(position)) & 0xff;
  }

  private int readS16LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return (int) this.map.getShort(Math.toIntExact(position));
  }

  private int readU16LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return (int) this.map.getChar(Math.toIntExact(position));
  }

  private long readS32LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return (long) this.map.getInt(Math.toIntExact(position));
  }

  private long readU32LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return (long) (this.map.getInt(Math.toIntExact(position))) & 0xffff_ffffL;
  }

  private long readS64LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return this.map.getLong(Math.toIntExact(position));
  }

  private long readU64LEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return this.map.getLong(Math.toIntExact(position));
  }

  private int readS16BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return (int) this.map.getShort(Math.toIntExact(position));
  }

  private int readU16BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return (int) this.map.getChar(Math.toIntExact(position));
  }

  private long readS32BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return (long) this.map.getInt(Math.toIntExact(position));
  }

  private long readU32BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return (long) (this.map.getInt(Math.toIntExact(position))) & 0xffff_ffffL;
  }

  private long readS64BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return this.map.getLong(Math.toIntExact(position));
  }

  private long readU64BEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return this.map.getLong(Math.toIntExact(position));
  }

  private float readFBEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return this.map.getFloat(Math.toIntExact(position));
  }

  private float readFLEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return this.map.getFloat(Math.toIntExact(position));
  }

  private double readDBEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;
    this.map.order(ByteOrder.BIG_ENDIAN);
    return this.map.getDouble(Math.toIntExact(position));
  }


  private double readDLEp(final String name)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(name, 8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;
    this.map.order(ByteOrder.LITTLE_ENDIAN);
    return this.map.getDouble(Math.toIntExact(position));
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
    this.map.get(buffer, offset, length);
    this.offsetRelative += llong;
    return length;
  }

  @Override
  public long offsetAbsolute()
  {
    final var readerParent = this.parent;
    if (readerParent == null) {
      return this.offsetRelative;
    }
    return readerParent.offsetAbsolute() + this.offsetRelative;
  }

  @Override
  public long offsetRelative()
  {
    return this.offsetRelative;
  }

  @Override
  public URI uri()
  {
    return this.uri;
  }

  @Override
  public String path()
  {
    return this.path;
  }

  @Override
  public void close()
    throws IOException
  {
    try {
      this.onClose.call();
    } catch (final IOException e) {
      throw e;
    } catch (final Exception e) {
      throw new IOException(e);
    }
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
  public float readFBE()
    throws IOException, EOFException
  {
    return this.readFBEp(null);
  }

  @Override
  public float readFLE()
    throws IOException, EOFException
  {
    return this.readFLEp(null);
  }

  @Override
  public double readDBE()
    throws IOException, EOFException
  {
    return this.readDBEp(null);
  }

  @Override
  public double readDLE()
    throws IOException, EOFException
  {
    return this.readDLEp(null);
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
  public float readFBE(final String name)
    throws IOException, EOFException
  {
    return this.readFBEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public float readFLE(final String name)
    throws IOException, EOFException
  {
    return this.readFLEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public double readDBE(final String name)
    throws IOException, EOFException
  {
    return this.readDBEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public double readDLE(final String name)
    throws IOException, EOFException
  {
    return this.readDLEp(Objects.requireNonNull(name, "name"));
  }

  @Override
  public int readBytes(
    final String name,
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    return this.readBytesp(Objects.requireNonNull(name, "name"), buffer, offset, length);
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
}

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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

final class BSSReaderSeekableChannel implements BSSReaderRandomAccessType
{
  private final String path;
  private final SeekableByteChannel channel;
  private final ByteBuffer buffer;
  private final AtomicBoolean closed;
  private final Callable<Void> onClose;
  private final BSSReaderSeekableChannel parent;
  private final URI uri;
  private final RangeHalfOpenL rangeRelative;
  private long offsetRelative;

  private BSSReaderSeekableChannel(
    final BSSReaderSeekableChannel inParent,
    final URI inURI,
    final RangeHalfOpenL inRange,
    final String inName,
    final SeekableByteChannel inChannel,
    final ByteBuffer inBuffer,
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
    this.channel =
      Objects.requireNonNull(inChannel, "channel");
    this.buffer =
      Objects.requireNonNull(inBuffer, "buffer");
    this.closed =
      Objects.requireNonNull(inClosed, "closed");
    this.onClose =
      Objects.requireNonNull(inOnClose, "onClose");

    this.offsetRelative = 0L;
  }

  static BSSReaderRandomAccessType createFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name)
    throws IOException
  {
    final var buffer = ByteBuffer.allocateDirect(8);
    final var size = channel.size();
    final var closed = new AtomicBoolean(false);

    return new BSSReaderSeekableChannel(
      null,
      uri,
      RangeHalfOpenL.of(0L, size),
      name,
      channel,
      buffer,
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

    return new BSSReaderSeekableChannel(
      this,
      this.uri,
      this.rangeRelative,
      newName,
      this.channel,
      this.buffer,
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

    return new BSSReaderSeekableChannel(
      this,
      this.uri,
      newRange,
      newName,
      this.channel,
      this.buffer,
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
      throw this.outOfBounds(position, IOException::new);
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
    this.checkHasBytesRemaining(size);
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

  private void checkHasBytesRemaining(final long want)
    throws IOException
  {
    final var targetPosition = this.offsetRelative + want;
    if (targetPosition > this.rangeRelative.upper()) {
      throw this.outOfBounds(targetPosition, IOException::new);
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
    final Function<String, IOException> exceptionSupplier)
  {
    final var lineSeparator = System.lineSeparator();
    return exceptionSupplier.apply(
      new StringBuilder(128)
        .append("Out of bounds.")
        .append(lineSeparator)
        .append("  Reader URI: ")
        .append(this.uri())
        .append(lineSeparator)
        .append("  Reader path: ")
        .append(this.path())
        .append(lineSeparator)
        .append("  Reader bounds: [absolute 0x")
        .append(Long.toUnsignedString(this.absoluteOf(this.rangeRelative.lower()), 16))
        .append(", 0x")
        .append(Long.toUnsignedString(this.absoluteOf(this.rangeRelative.upper()), 16))
        .append(")")
        .append(lineSeparator)
        .append("  Target offset: absolute 0x")
        .append(Long.toUnsignedString(this.absoluteOf(targetPosition), 16))
        .append(lineSeparator)
        .append("  Offset: absolute 0x")
        .append(Long.toUnsignedString(this.offsetAbsolute(), 16))
        .toString());
  }

  @Override
  public int readS8()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(1L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 1L;

    this.buffer.position(0);
    this.buffer.limit(1);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (int) this.buffer.get(0);
  }

  @Override
  public int readU8()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(1L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 1L;

    this.buffer.position(0);
    this.buffer.limit(1);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (int) this.buffer.get(0) & 0xff;
  }

  @Override
  public int readS16LE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (int) this.buffer.getShort(0);
  }

  @Override
  public int readU16LE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (int) this.buffer.getChar(0);
  }

  @Override
  public long readS32LE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (long) this.buffer.getInt(0);
  }

  @Override
  public long readU32LE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (long) this.buffer.getInt(0) & 0xffff_ffffL;
  }

  @Override
  public long readS64LE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getLong(0);
  }

  @Override
  public long readU64LE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getLong(0);
  }

  @Override
  public int readS16BE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (int) this.buffer.getShort(0);
  }

  @Override
  public int readU16BE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 2L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(2);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (int) this.buffer.getChar(0);
  }

  @Override
  public long readS32BE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (long) this.buffer.getInt(0);
  }

  @Override
  public long readU32BE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return (long) this.buffer.getInt(0) & 0xffff_ffffL;
  }

  @Override
  public long readS64BE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getLong(0);
  }

  @Override
  public long readU64BE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getLong(0);
  }

  @Override
  public int readBytes(
    final byte[] inBuffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    final var llength = Integer.toUnsignedLong(length);
    this.checkHasBytesRemaining(llength);
    final var position = this.offsetAbsolute();
    this.offsetRelative += llength;

    final var wrapper = ByteBuffer.wrap(inBuffer);
    this.channel.position(position);
    return this.channel.read(wrapper);
  }

  @Override
  public float readFBE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getFloat(0);
  }

  @Override
  public float readFLE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 4L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(4);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getFloat(0);
  }

  @Override
  public double readDBE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;

    this.buffer.order(BIG_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getDouble(0);
  }

  @Override
  public double readDLE()
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L);
    final var position = this.offsetAbsolute();
    this.offsetRelative += 8L;

    this.buffer.order(LITTLE_ENDIAN);
    this.buffer.position(0);
    this.buffer.limit(8);
    this.channel.position(position);
    this.channel.read(this.buffer);
    this.buffer.flip();
    return this.buffer.getDouble(0);
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
}

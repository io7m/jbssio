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

import com.io7m.jbssio.api.BSSReaderSequentialType;
import com.io7m.jintegers.Unsigned16;
import com.io7m.jintegers.Unsigned32;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.io.input.CountingInputStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

final class BSSReaderStream implements BSSReaderSequentialType
{
  private final BSSReaderStream parent;
  private final String path;
  private final CountingInputStream stream;
  private final AtomicBoolean closed;
  private final long size;
  private final byte[] buffer8;
  private final byte[] buffer4;
  private final byte[] buffer2;
  private final ByteBuffer buffer8w;
  private final ByteBuffer buffer4w;
  private final ByteBuffer buffer2w;
  private final URI uri;

  static BSSReaderStream create(
    final URI uri,
    final InputStream inStream,
    final String inName,
    final long inSize)
  {
    final var boundedStream =
      new BoundedInputStream(Objects.requireNonNull(inStream, "stream"), inSize);
    final var wrappedStream =
      new CountingInputStream(boundedStream);

    return new BSSReaderStream(null, uri, inName, wrappedStream, inSize);
  }

  private BSSReaderStream(
    final BSSReaderStream inParent,
    final URI inURI,
    final String inName,
    final CountingInputStream inStream,
    final long inSize)
  {
    this.uri = Objects.requireNonNull(inURI, "uri");
    this.parent = inParent;
    this.path = Objects.requireNonNull(inName, "path");
    this.stream = Objects.requireNonNull(inStream, "inStream");
    this.closed = new AtomicBoolean(false);
    this.size = inSize;

    this.buffer8 = new byte[8];
    this.buffer8w = ByteBuffer.wrap(this.buffer8);
    this.buffer4 = new byte[4];
    this.buffer4w = ByteBuffer.wrap(this.buffer4);
    this.buffer2 = new byte[2];
    this.buffer2w = ByteBuffer.wrap(this.buffer2);
  }

  @Override
  public BSSReaderSequentialType createSubReader(
    final String inName)
  {
    Objects.requireNonNull(inName, "inName");

    final var wrappedStream =
      new CountingInputStream(new CloseShieldInputStream(this.stream));

    return new BSSReaderStream(
      this,
      this.uri,
      new StringBuilder(32)
        .append(this.path)
        .append('.')
        .append(inName)
        .toString(),
      wrappedStream,
      this.size);
  }

  @Override
  public BSSReaderSequentialType createSubReader(
    final String inName,
    final long newSize)
  {
    Objects.requireNonNull(inName, "inName");

    if (Long.compareUnsigned(newSize, this.size) > 0) {
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
          .append("  Reader size limit: ")
          .append(Long.toUnsignedString(this.size))
          .append(lineSeparator)
          .append("  Requested size limit: ")
          .append(Long.toUnsignedString(newSize))
          .toString());
    }

    final var boundedStream =
      new BoundedInputStream(new CloseShieldInputStream(this.stream), newSize);
    final var wrappedStream =
      new CountingInputStream(boundedStream);

    return new BSSReaderStream(
      this,
      this.uri,
      new StringBuilder(32)
        .append(this.path)
        .append('.')
        .append(inName)
        .toString(),
      wrappedStream,
      newSize);
  }

  @Override
  public void skip(final long skipSize)
    throws IOException, EOFException
  {
    final var r = this.stream.skip(skipSize);
    this.checkNotShortRead(skipSize, r);
  }

  @Override
  public void align(final int alignment)
    throws IOException, EOFException
  {
    final var diff = this.offsetAbsolute() % (long) alignment;
    if (diff == 0L) {
      return;
    }

    this.skip((long) alignment - diff);
  }

  @Override
  public String toString()
  {
    return String.format(
      "[BSSReaderStream %s %s [absolute %s] [relative %s]]",
      this.uri(),
      this.path(),
      Long.toUnsignedString(this.offsetAbsolute()),
      Long.toUnsignedString(this.offsetRelative()));
  }

  private static void checkEOF(final int r)
    throws EOFException
  {
    if (r == -1) {
      throw new EOFException();
    }
  }

  private void checkNotShortRead(
    final long expected,
    final long received)
    throws IOException
  {
    if (expected != received) {
      final var lineSeparator = System.lineSeparator();
      throw new IOException(
        new StringBuilder(128)
          .append("Short read.")
          .append(lineSeparator)
          .append("  Reader URI: ")
          .append(this.uri())
          .append(lineSeparator)
          .append("  Reader path: ")
          .append(this.path())
          .append(lineSeparator)
          .append("  Offset: 0x")
          .append(Long.toUnsignedString(this.offsetAbsolute(), 16))
          .append(lineSeparator)
          .append("  Expected: ")
          .append(expected)
          .append(" octets")
          .append(lineSeparator)
          .append("  Received: ")
          .append(received)
          .append(" octets")
          .append(lineSeparator)
          .toString());
    }
  }

  @Override
  public int readS8()
    throws IOException
  {
    final var r = this.stream.read();
    checkEOF(r);
    return (int) (byte) r;
  }

  @Override
  public int readU8()
    throws IOException
  {
    final var r = this.stream.read();
    checkEOF(r);
    return r & 0xff;
  }

  @Override
  public int readS16LE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(2L, (long) r);
    this.buffer2w.order(ByteOrder.LITTLE_ENDIAN);
    return (int) this.buffer2w.getShort(0);
  }

  @Override
  public int readU16LE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(2L, (long) r);
    this.buffer2w.order(ByteOrder.LITTLE_ENDIAN);
    return Unsigned16.unpackFromBuffer(this.buffer2w, 0);
  }

  @Override
  public long readS32LE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(4L, (long) r);
    this.buffer4w.order(ByteOrder.LITTLE_ENDIAN);
    return (long) this.buffer4w.getInt(0);
  }

  @Override
  public long readU32LE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(4L, (long) r);
    this.buffer4w.order(ByteOrder.LITTLE_ENDIAN);
    return Unsigned32.unpackFromBuffer(this.buffer4w, 0);
  }

  @Override
  public long readS64LE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(8L, (long) r);
    this.buffer8w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  @Override
  public long readU64LE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(8L, (long) r);
    this.buffer8w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  @Override
  public int readS16BE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(2L, (long) r);
    this.buffer2w.order(ByteOrder.BIG_ENDIAN);
    return (int) this.buffer2w.getShort(0);
  }

  @Override
  public int readU16BE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(2L, (long) r);
    this.buffer2w.order(ByteOrder.BIG_ENDIAN);
    return Unsigned16.unpackFromBuffer(this.buffer2w, 0);
  }

  @Override
  public long readS32BE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(4L, (long) r);
    this.buffer4w.order(ByteOrder.BIG_ENDIAN);
    return (long) this.buffer4w.getInt(0);
  }

  @Override
  public long readU32BE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(4L, (long) r);
    this.buffer4w.order(ByteOrder.BIG_ENDIAN);
    return Unsigned32.unpackFromBuffer(this.buffer4w, 0);
  }

  @Override
  public long readS64BE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(8L, (long) r);
    this.buffer8w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  @Override
  public long readU64BE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(8L, (long) r);
    this.buffer8w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  @Override
  public int readBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    final var r = this.stream.read(buffer, offset, length);
    checkEOF(r);
    return r;
  }

  @Override
  public float readFBE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(4L, (long) r);
    this.buffer4w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer4w.getFloat(0);
  }

  @Override
  public float readFLE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(4L, (long) r);
    this.buffer4w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer4w.getFloat(0);
  }

  @Override
  public double readDBE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(8L, (long) r);
    this.buffer8w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer8w.getDouble(0);
  }

  @Override
  public double readDLE()
    throws IOException, EOFException
  {
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(8L, (long) r);
    this.buffer8w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer8w.getDouble(0);
  }

  @Override
  public long offsetAbsolute()
  {
    final var readerParent = this.parent;
    if (readerParent == null) {
      return this.stream.getByteCount();
    }
    return readerParent.offsetAbsolute();
  }

  @Override
  public long offsetRelative()
  {
    return this.stream.getByteCount();
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
    if (this.closed.compareAndSet(false, true)) {
      this.stream.close();
    }
  }
}

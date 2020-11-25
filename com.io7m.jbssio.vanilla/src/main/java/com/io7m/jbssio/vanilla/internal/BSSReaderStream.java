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

package com.io7m.jbssio.vanilla.internal;

import com.io7m.ieee754b16.Binary16;
import com.io7m.jbssio.api.BSSReaderSequentialType;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.jbssio.vanilla.internal.BSSPaths.PATH_SEPARATOR;

public final class BSSReaderStream implements BSSReaderSequentialType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(BSSReaderStream.class);

  private final BSSReaderStream parent;
  private final String path;
  private final CountingInputStream stream;
  private final AtomicBoolean closed;
  private final OptionalLong size;
  private final byte[] buffer8;
  private final byte[] buffer4;
  private final byte[] buffer2;
  private final ByteBuffer buffer8w;
  private final ByteBuffer buffer4w;
  private final ByteBuffer buffer2w;
  private final URI uri;

  private BSSReaderStream(
    final BSSReaderStream inParent,
    final URI inURI,
    final String inName,
    final CountingInputStream inStream,
    final OptionalLong inSize)
  {
    this.parent = inParent;

    this.uri =
      Objects.requireNonNull(inURI, "uri");
    this.path =
      Objects.requireNonNull(inName, "path");
    this.stream =
      Objects.requireNonNull(inStream, "inStream");
    this.size =
      Objects.requireNonNull(inSize, "inSize");

    this.closed = new AtomicBoolean(false);

    this.buffer8 = new byte[8];
    this.buffer8w = ByteBuffer.wrap(this.buffer8);
    this.buffer4 = new byte[4];
    this.buffer4w = ByteBuffer.wrap(this.buffer4);
    this.buffer2 = new byte[2];
    this.buffer2w = ByteBuffer.wrap(this.buffer2);
  }

  public static BSSReaderStream create(
    final URI uri,
    final InputStream inStream,
    final String inName,
    final OptionalLong inSize)
  {
    Objects.requireNonNull(inStream, "stream");

    final InputStream boundedStream;
    if (inSize.isPresent()) {
      boundedStream = new BoundedInputStream(inStream, inSize.getAsLong());
    } else {
      boundedStream = inStream;
    }

    final var wrappedStream = new CountingInputStream(boundedStream);
    return new BSSReaderStream(null, uri, inName, wrappedStream, inSize);
  }

  private static void checkEOF(final int r)
    throws EOFException
  {
    if (r == -1) {
      throw new EOFException();
    }
  }

  @Override
  public void skip(final long skipSize)
    throws IOException, EOFException
  {
    this.checkLimit(null, skipSize);
    final var r = this.stream.skip(skipSize);
    this.checkNotShortRead(null, skipSize, r);
  }

  @Override
  public void align(final int alignment)
    throws IOException, EOFException
  {
    final var diff = this.offsetCurrentAbsolute() % (long) alignment;
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
      Long.toUnsignedString(this.offsetCurrentAbsolute()),
      Long.toUnsignedString(this.offsetCurrentRelative()));
  }

  private void checkNotShortRead(
    final String name,
    final long expected,
    final long received)
    throws IOException
  {
    if (expected != received) {
      final var attributes = new HashMap<String, String>(4);
      attributes.put("Expected (Octets)", Long.toUnsignedString(expected));
      attributes.put("Received (Octets)", Long.toUnsignedString(received));
      if (name != null) {
        attributes.put("Field", name);
      }
      throw BSSExceptions.createIO(
        this,
        "Short read.",
        attributes);
    }
  }

  private int readS8p(final String name)
    throws IOException
  {
    this.checkLimit(name, 1L);
    final var r = this.stream.read();
    checkEOF(r);
    return (byte) r;
  }

  private void checkLimit(
    final String name,
    final long requested)
    throws EOFException
  {
    if (this.size.isPresent()) {
      final var sizeLimit = this.size.getAsLong();
      if (Long.compareUnsigned(
        this.stream.getByteCount() + requested,
        sizeLimit) > 0) {
        final var attributes = new HashMap<String, String>(4);
        attributes.put("Requested", Long.toUnsignedString(requested));
        if (name != null) {
          attributes.put("Field", name);
        }
        throw BSSExceptions.createEOF(
          this,
          "Attempting to read bytes would exceed the reader size limit.",
          attributes);
      }
    }
  }

  private int readU8p(final String name)
    throws IOException
  {
    this.checkLimit(name, 1L);
    final var r = this.stream.read();
    checkEOF(r);
    return r & 0xff;
  }

  private int readS16LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 2L);
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(name, 2L, r);
    this.buffer2w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer2w.getShort(0);
  }

  private int readU16LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 2L);
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(name, 2L, r);
    this.buffer2w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer2w.getChar(0);
  }

  private long readS32LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 4L);
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(name, 4L, r);
    this.buffer4w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer4w.getInt(0);
  }

  private long readU32LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 4L);
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(name, 4L, r);
    this.buffer4w.order(ByteOrder.LITTLE_ENDIAN);
    return (long) this.buffer4w.getInt(0) & 0xffff_ffffL;
  }

  private long readS64LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 8L);
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(name, 8L, r);
    this.buffer8w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  private long readU64LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 8L);
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(name, 8L, r);
    this.buffer8w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  private int readS16BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 2L);
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(name, 2L, r);
    this.buffer2w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer2w.getShort(0);
  }

  private int readU16BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 2L);
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(name, 2L, r);
    this.buffer2w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer2w.getChar(0);
  }

  private long readS32BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 4L);
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(name, 4L, r);
    this.buffer4w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer4w.getInt(0);
  }

  private long readU32BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 4L);
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(name, 4L, r);
    this.buffer4w.order(ByteOrder.BIG_ENDIAN);
    return (long) this.buffer4w.getInt(0) & 0xffff_ffffL;
  }

  private long readS64BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 8L);
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(name, 8L, r);
    this.buffer8w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  private long readU64BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 8L);
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(name, 8L, r);
    this.buffer8w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer8w.getLong(0);
  }

  private float readF32BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 4L);
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(name, 4L, r);
    this.buffer4w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer4w.getFloat(0);
  }

  private float readF32LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 4L);
    final var r = this.stream.read(this.buffer4, 0, 4);
    checkEOF(r);
    this.checkNotShortRead(name, 4L, r);
    this.buffer4w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer4w.getFloat(0);
  }

  private double readD64BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 8L);
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(name, 8L, r);
    this.buffer8w.order(ByteOrder.BIG_ENDIAN);
    return this.buffer8w.getDouble(0);
  }

  private double readD64LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 8L);
    final var r = this.stream.read(this.buffer8, 0, 8);
    checkEOF(r);
    this.checkNotShortRead(name, 8L, r);
    this.buffer8w.order(ByteOrder.LITTLE_ENDIAN);
    return this.buffer8w.getDouble(0);
  }

  private float readF16BEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 2L);
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(name, 2L, r);
    this.buffer2w.order(ByteOrder.BIG_ENDIAN);
    return Binary16.unpackFloat(this.buffer2w.getChar(0));
  }

  private float readF16LEp(final String name)
    throws IOException
  {
    this.checkLimit(name, 2L);
    final var r = this.stream.read(this.buffer2, 0, 2);
    checkEOF(r);
    this.checkNotShortRead(name, 2L, r);
    this.buffer2w.order(ByteOrder.LITTLE_ENDIAN);
    return Binary16.unpackFloat(this.buffer2w.getChar(0));
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
    this.checkLimit(
      Objects.requireNonNull(name, "name"),
      Integer.toUnsignedLong(length));
    final var r = this.stream.read(buffer, offset, length);
    checkEOF(r);
    return r;
  }

  @Override
  public int readBytes(
    final byte[] buffer,
    final int offset,
    final int length)
    throws IOException, EOFException
  {
    this.checkLimit(null, Integer.toUnsignedLong(length));
    final var r = this.stream.read(buffer, offset, length);
    checkEOF(r);
    return r;
  }

  @Override
  public OptionalLong bytesRemaining()
  {
    return this.size.stream()
      .map(s -> s - this.stream.getByteCount())
      .findFirst();
  }

  @Override
  public long offsetCurrentAbsolute()
  {
    final var readerParent = this.parent;
    if (readerParent == null) {
      return this.stream.getByteCount();
    }
    return readerParent.offsetCurrentAbsolute();
  }

  @Override
  public long offsetCurrentRelative()
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

  @Override
  public boolean isClosed()
  {
    final var parentRef = this.parent;
    if (parentRef != null) {
      return parentRef.isClosed() || this.closed.get();
    }
    return this.closed.get();
  }

  @Override
  public Optional<BSSReaderSequentialType> parentReader()
  {
    return Optional.ofNullable(this.parent);
  }

  @Override
  public BSSReaderSequentialType createSubReader(final String name)
    throws IOException
  {
    Objects.requireNonNull(name, "name");

    final var newStream =
      new CountingInputStream(new CloseShieldInputStream(this.stream));

    final var newName =
      new StringBuilder(this.path.length() + name.length() + 2)
        .append(this.path)
        .append(PATH_SEPARATOR)
        .append(name)
        .toString();

    return new BSSReaderStream(
      this,
      this.uri,
      newName,
      newStream,
      this.size);
  }

  @Override
  public BSSReaderSequentialType createSubReaderBounded(
    final String name,
    final long newSize)
    throws IOException
  {
    Objects.requireNonNull(name, "name");

    if (this.size.isPresent()) {
      final var currentSize = this.size.getAsLong();
      if (Long.compareUnsigned(newSize, currentSize) > 0) {
        final var attributes = new HashMap<String, String>(4);
        attributes.put("Size limit", Long.toUnsignedString(currentSize));
        attributes.put("Requested size limit", Long.toUnsignedString(newSize));
        throw BSSExceptions.createIO(
          this,
          "Sub-reader bounds cannot exceed the bounds of this reader.",
          attributes);
      }
    }

    final var newStream =
      new CountingInputStream(new CloseShieldInputStream(this.stream));

    final var newName =
      new StringBuilder(this.path.length() + name.length() + 2)
        .append(this.path)
        .append(PATH_SEPARATOR)
        .append(name)
        .toString();

    return new BSSReaderStream(
      this,
      this.uri,
      newName,
      newStream,
      OptionalLong.of(newSize));
  }
}

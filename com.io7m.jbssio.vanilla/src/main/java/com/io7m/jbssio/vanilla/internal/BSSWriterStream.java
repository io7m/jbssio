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
import com.io7m.jbssio.api.BSSWriterSequentialType;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CountingOutputStream;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.jbssio.vanilla.internal.BSSPaths.PATH_SEPARATOR;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * A sequential stream writer.
 */

public final class BSSWriterStream implements BSSWriterSequentialType
{
  private final BSSWriterStream parent;
  private final String path;
  private final CountingOutputStream stream;
  private final AtomicBoolean closed;
  private final OptionalLong size;
  private final byte[] buffer8;
  private final byte[] buffer4;
  private final byte[] buffer2;
  private final ByteBuffer buffer8w;
  private final ByteBuffer buffer4w;
  private final ByteBuffer buffer2w;
  private final URI uri;
  private final long start;

  private BSSWriterStream(
    final BSSWriterStream inParent,
    final URI inURI,
    final String inName,
    final CountingOutputStream inStream,
    final long inStart,
    final OptionalLong inSize)
  {
    this.uri = Objects.requireNonNull(inURI, "uri");
    this.parent = inParent;
    this.path = Objects.requireNonNull(inName, "path");
    this.stream = Objects.requireNonNull(inStream, "inStream");
    this.closed = new AtomicBoolean(false);
    this.start = inStart;
    this.size = inSize;

    this.buffer8 = new byte[8];
    this.buffer8w = ByteBuffer.wrap(this.buffer8);
    this.buffer4 = new byte[4];
    this.buffer4w = ByteBuffer.wrap(this.buffer4);
    this.buffer2 = new byte[2];
    this.buffer2w = ByteBuffer.wrap(this.buffer2);
  }

  /**
   * Create a stream writer.
   *
   * @param uri      The target URI
   * @param inStream The output stream
   * @param inName   The name
   * @param inSize   The size
   *
   * @return A stream writer
   */

  public static BSSWriterStream create(
    final URI uri,
    final OutputStream inStream,
    final String inName,
    final OptionalLong inSize)
  {
    final var wrappedStream = new CountingOutputStream(inStream);
    return new BSSWriterStream(null, uri, inName, wrappedStream, 0L, inSize);
  }

  private IOException outOfBounds(
    final String name,
    final long targetPosition)
  {
    final var attributes = new HashMap<String, String>(4);
    if (name != null) {
      attributes.put("Field", name);
    }
    attributes.put(
      "Target Offset (Absolute)",
      "0x" + Long.toUnsignedString(targetPosition, 16));
    if (this.size.isPresent()) {
      final var bounds = new BSSRangeHalfOpen(this.start, this.size);
      attributes.put("Bounds (Absolute)", bounds.toString());
    }
    return BSSExceptions.createIO(this, "Out of bounds.", attributes);
  }

  @Override
  public void skip(final long skipSize)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(skipSize, null);

    try (var output = new BufferedOutputStream(new CloseShieldOutputStream(this.stream))) {
      for (var index = 0L; index < skipSize; ++index) {
        output.write(0x0);
      }
      output.flush();
    }
  }

  private void checkHasBytesRemaining(
    final long count,
    final String name)
    throws IOException
  {
    if (this.size.isPresent()) {
      final var sizeLimit = this.size.getAsLong();
      final var targetPosition = this.stream.getByteCount() + count;
      if (targetPosition > sizeLimit) {
        throw this.outOfBounds(name, targetPosition);
      }
    }
  }

  @Override
  public void align(final int alignment)
    throws IOException, EOFException
  {
    this.checkNotClosed();

    final var diff = this.offsetCurrentAbsolute() % (long) alignment;
    if (diff == 0L) {
      return;
    }

    this.skip((long) alignment - diff);
  }

  @Override
  public OptionalLong bytesRemaining()
  {
    return this.size.stream()
      .map(s -> s - this.stream.getByteCount())
      .findFirst();
  }

  private void writeS8p(
    final String name,
    final int b)
    throws IOException
  {
    this.checkHasBytesRemaining(1L, name);
    this.stream.write(b);
  }

  private void writeU8p(
    final String name,
    final int b)
    throws IOException
  {
    this.checkHasBytesRemaining(1L, name);
    this.stream.write(b);
  }

  @Override
  public void writeS8(final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.writeS8p(null, b);
  }

  @Override
  public void writeU8(final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.writeU8p(null, b);
  }

  @Override
  public void writeS8(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.writeS8p(Objects.requireNonNull(name, "name"), b);
  }

  @Override
  public void writeU8(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.writeU8p(Objects.requireNonNull(name, "name"), b);
  }

  private void writeS16LEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L, name);
    this.buffer2w.order(LITTLE_ENDIAN);
    this.buffer2w.putShort(0, (short) b);
    this.stream.write(this.buffer2);
  }

  private void writeU16LEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L, name);
    this.buffer2w.order(LITTLE_ENDIAN);
    this.buffer2w.putChar(0, (char) b);
    this.stream.write(this.buffer2);
  }

  private void writeS16BEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L, name);
    this.buffer2w.order(BIG_ENDIAN);
    this.buffer2w.putShort(0, (short) b);
    this.stream.write(this.buffer2);
  }

  private void writeU16BEp(
    final String name,
    final int b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L, name);
    this.buffer2w.order(BIG_ENDIAN);
    this.buffer2w.putChar(0, (char) b);
    this.stream.write(this.buffer2);
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
    this.checkHasBytesRemaining(4L, name);
    this.buffer4w.order(LITTLE_ENDIAN);
    this.buffer4w.putInt(0, (int) b);
    this.stream.write(this.buffer4);
  }

  private void writeU32LEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L, name);
    this.buffer4w.order(LITTLE_ENDIAN);
    this.buffer4w.putInt(0, (int) (b & 0xffff_ffff));
    this.stream.write(this.buffer4);
  }

  private void writeS32BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L, name);
    this.buffer4w.order(BIG_ENDIAN);
    this.buffer4w.putInt(0, (int) b);
    this.stream.write(this.buffer4);
  }

  private void writeU32BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L, name);
    this.buffer4w.order(BIG_ENDIAN);
    this.buffer4w.putInt(0, (int) (b & 0xffff_ffff));
    this.stream.write(this.buffer4);
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
    this.checkHasBytesRemaining(8L, name);
    this.buffer8w.order(LITTLE_ENDIAN);
    this.buffer8w.putLong(0, b);
    this.stream.write(this.buffer8);
  }

  private void writeU64LEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L, name);
    this.buffer8w.order(LITTLE_ENDIAN);
    this.buffer8w.putLong(0, b);
    this.stream.write(this.buffer8);
  }

  private void writeS64BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L, name);
    this.buffer8w.order(BIG_ENDIAN);
    this.buffer8w.putLong(0, b);
    this.stream.write(this.buffer8);
  }

  private void writeU64BEp(
    final String name,
    final long b)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L, name);
    this.buffer8w.order(BIG_ENDIAN);
    this.buffer8w.putLong(0, b);
    this.stream.write(this.buffer8);
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
    this.checkHasBytesRemaining(length, name);
    this.stream.write(buffer, offset, length);
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

  private void writeF64p(
    final String name,
    final double b,
    final ByteOrder order)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(8L, name);
    this.buffer8w.order(order);
    this.buffer8w.putDouble(0, b);
    this.stream.write(this.buffer8);
  }

  private void writeF32p(
    final String name,
    final double b,
    final ByteOrder order)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(4L, name);
    this.buffer4w.order(order);
    this.buffer4w.putFloat(0, (float) b);
    this.stream.write(this.buffer4);
  }

  private void writeF16p(
    final String name,
    final double b,
    final ByteOrder order)
    throws IOException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(2L, name);
    this.buffer2w.order(order);
    this.buffer2w.putChar(0, Binary16.packDouble(b));
    this.stream.write(this.buffer2);
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
  public void writeF16BE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF16p(Objects.requireNonNull(name, "name"), b, BIG_ENDIAN);
  }

  @Override
  public void writeF16BE(final double b)
    throws IOException
  {
    this.writeF16p(null, b, BIG_ENDIAN);
  }

  @Override
  public void writeF16LE(
    final String name,
    final double b)
    throws IOException
  {
    this.writeF16p(Objects.requireNonNull(name, "name"), b, LITTLE_ENDIAN);
  }

  @Override
  public void writeF16LE(final double b)
    throws IOException
  {
    this.writeF16p(null, b, LITTLE_ENDIAN);
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

  @Override
  public String toString()
  {
    return String.format(
      "[BSSWriterStream %s %s [absolute %s] [relative %s]]",
      this.uri(),
      this.path(),
      Long.toUnsignedString(this.offsetCurrentAbsolute()),
      Long.toUnsignedString(this.offsetCurrentRelative()));
  }

  @Override
  public long offsetCurrentAbsolute()
  {
    final var parentW = this.parent;
    if (parentW != null) {
      return parentW.offsetCurrentAbsolute();
    }
    return this.offsetCurrentRelative();
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
      if (this.parent == null) {
        this.stream.close();
      }
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
  public BSSWriterSequentialType createSubWriterAt(
    final String name,
    final long targetOffset)
    throws IOException
  {
    Objects.requireNonNull(name, "name");

    final var streamPosition = this.stream.getByteCount();
    final var seek = targetOffset - streamPosition;
    if (seek < 0L) {
      throw this.streamPositionExceeded(targetOffset);
    }

    this.skip(seek);

    final var newStream =
      new CountingOutputStream(new CloseShieldOutputStream(this.stream));

    final var newName =
      new StringBuilder(this.path.length() + name.length() + 2)
        .append(this.path)
        .append(PATH_SEPARATOR)
        .append(name)
        .toString();

    return new BSSWriterStream(
      this,
      this.uri,
      newName,
      newStream,
      this.stream.getByteCount(),
      this.size);
  }

  private EOFException streamPositionExceeded(
    final long targetOffset)
  {
    final var attributes = new HashMap<String, String>(4);
    attributes.put(
      "Target Offset (Relative)",
      "0x" + Long.toUnsignedString(targetOffset, 16));
    return BSSExceptions.createEOF(
      this,
      "Stream position has already exceeded the specified offset.",
      attributes);
  }

  @Override
  public BSSWriterSequentialType createSubWriterAtBounded(
    final String name,
    final long targetOffset,
    final long newSize)
    throws IOException
  {
    Objects.requireNonNull(name, "name");

    final var streamPosition = this.stream.getByteCount();

    if (this.size.isPresent()) {
      final var currentSize = this.size.getAsLong();
      if (Long.compareUnsigned(newSize, currentSize) > 0) {
        final var attributes = new HashMap<String, String>(4);
        attributes.put("Size limit", Long.toUnsignedString(currentSize));
        attributes.put("Requested size limit", Long.toUnsignedString(newSize));
        throw BSSExceptions.createIO(
          this,
          "Sub-writer bounds cannot exceed the bounds of this writer.",
          attributes);
      }
    }

    final var seek = targetOffset - streamPosition;
    if (seek < 0L) {
      throw this.streamPositionExceeded(targetOffset);
    }

    this.skip(seek);

    final var newStream =
      new CountingOutputStream(new CloseShieldOutputStream(this.stream));

    final var newName =
      new StringBuilder(this.path.length() + name.length() + 2)
        .append(this.path)
        .append(PATH_SEPARATOR)
        .append(name)
        .toString();

    return new BSSWriterStream(
      this,
      this.uri,
      newName,
      newStream,
      this.stream.getByteCount(),
      OptionalLong.of(newSize));
  }

  @Override
  public long padTo(
    final long offset,
    final byte value)
    throws IOException
  {
    final var diff = Math.max(0L, offset - this.offsetCurrentRelative());
    if (diff == 0L) {
      return 0L;
    }

    var diffTemp = diff;
    if (diffTemp >= 4096L) {
      final var bigBuffer = new byte[4096];
      if (value != 0) {
        Arrays.fill(bigBuffer, value);
      }
      while (diffTemp >= 4096L) {
        this.writeBytes(bigBuffer);
        diffTemp -= 4096L;
      }
    }

    if (diffTemp > 0L) {
      final byte[] smallBuffer = new byte[(int) diffTemp];
      if (value != 0) {
        Arrays.fill(smallBuffer, value);
      }
      this.writeBytes(smallBuffer);
    }
    return diff;
  }
}

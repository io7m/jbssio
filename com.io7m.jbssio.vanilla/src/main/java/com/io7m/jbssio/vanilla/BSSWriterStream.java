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

import com.io7m.jbssio.api.BSSWriterSequentialType;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.CountingOutputStream;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

final class BSSWriterStream implements BSSWriterSequentialType
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

  static BSSWriterStream create(
    final URI uri,
    final OutputStream inStream,
    final String inName,
    final OptionalLong inSize)
  {
    final var wrappedStream = new CountingOutputStream(inStream);
    return new BSSWriterStream(null, uri, inName, wrappedStream, 0L, inSize);
  }

  private long absoluteOf(
    final long position)
  {
    final var writerParent = this.parent;
    if (writerParent == null) {
      return position;
    }
    return writerParent.absoluteOf(0L) + position;
  }

  @Override
  public BSSWriterSequentialType createSubWriter(
    final String inName)
  {
    Objects.requireNonNull(inName, "inName");

    final var wrappedStream =
      new CountingOutputStream(new CloseShieldOutputStream(this.stream));

    final var newName =
      new StringBuilder(32)
        .append(this.path)
        .append('.')
        .append(inName)
        .toString();

    return new BSSWriterStream(
      this,
      this.uri,
      newName,
      wrappedStream,
      this.stream.getByteCount(),
      this.size);
  }

  @Override
  public BSSWriterSequentialType createSubWriterBounded(
    final String inName,
    final long newSize)
  {
    Objects.requireNonNull(inName, "inName");

    final var currentStart = this.stream.getByteCount();
    this.size.ifPresent(actualSize -> {
      if (newSize > actualSize) {
        final var lineSeparator = System.lineSeparator();
        throw new IllegalArgumentException(
          new StringBuilder(128)
            .append("Sub-writer bounds cannot exceed the bounds of this writer.")
            .append(lineSeparator)
            .append("  Writer URI: ")
            .append(this.uri)
            .append(lineSeparator)
            .append("  Writer path: ")
            .append(this.path)
            .append(lineSeparator)
            .append("  Writer bounds: absolute [0x")
            .append(Long.toUnsignedString(this.absoluteOf(this.start), 16))
            .append(", 0x")
            .append(Long.toUnsignedString(this.absoluteOf(actualSize), 16))
            .append(")")
            .append(lineSeparator)
            .append("  Requested bounds: absolute [0x")
            .append(Long.toUnsignedString(this.absoluteOf(currentStart), 16))
            .append(", 0x")
            .append(Long.toUnsignedString(this.absoluteOf(newSize), 16))
            .append(")")
            .append(lineSeparator)
            .toString());
      }
    });

    final var wrappedStream =
      new CountingOutputStream(new CloseShieldOutputStream(this.stream));

    final var newName =
      new StringBuilder(32)
        .append(this.path)
        .append('.')
        .append(inName)
        .toString();

    return new BSSWriterStream(
      this,
      this.uri,
      newName,
      wrappedStream,
      currentStart,
      OptionalLong.of(newSize));
  }

  private IOException outOfBounds(
    final long targetPosition,
    final String name,
    final Function<String, IOException> exceptionSupplier)
  {
    final var lineSeparator = System.lineSeparator();
    final var builder = new StringBuilder(128);

    builder.append("Out of bounds.")
      .append(lineSeparator)
      .append("  Writer URI: ")
      .append(this.uri())
      .append(lineSeparator);

    builder.append("  Writer path: ").append(this.path());
    if (name != null) {
      builder.append(":");
      builder.append(name);
    }
    builder.append(lineSeparator);

    this.size.ifPresent(
      actualSize ->
        builder.append("  Writer bounds: absolute [0x")
          .append(Long.toUnsignedString(this.start, 16))
          .append(", 0x")
          .append(Long.toUnsignedString(actualSize, 16))
          .append(")")
          .append(lineSeparator)
    );

    builder
      .append("  Offset: absolute 0x")
      .append(Long.toUnsignedString(targetPosition, 16));

    return exceptionSupplier.apply(builder.toString());
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
        throw this.outOfBounds(targetPosition, name, IOException::new);
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
}

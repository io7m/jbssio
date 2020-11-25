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

import com.io7m.jbssio.api.BSSAddressableType;
import com.io7m.jbssio.api.BSSCloseableType;
import com.io7m.jbssio.api.BSSSeekableType;
import com.io7m.jbssio.api.BSSSkippableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class BSSRandomAccess<T>
  implements BSSSeekableType,
  BSSSkippableType,
  BSSAddressableType,
  BSSCloseableType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(BSSRandomAccess.class);

  protected final URI uri;
  protected final String path;
  private final BSSRangeHalfOpen parentRangeRelative;
  private final AtomicBoolean closed;
  private final BSSRandomAccess<T> parent;
  private final Callable<Void> onClose;
  private long offsetRelative;

  BSSRandomAccess(
    final BSSRandomAccess<T> inParent,
    final BSSRangeHalfOpen inParentRangeRelative,
    final Callable<Void> inOnClose,
    final URI inURI,
    final String inPath)
  {
    this.parent = inParent;

    this.parentRangeRelative =
      Objects.requireNonNull(inParentRangeRelative, "parentRangeRelative");
    this.onClose =
      Objects.requireNonNull(inOnClose, "onClose");
    this.uri =
      Objects.requireNonNull(inURI, "uri");
    this.path =
      Objects.requireNonNull(inPath, "path");

    this.closed = new AtomicBoolean(false);

    if (inParentRangeRelative.isUpperUnbounded()) {
      this.checkAncestorsUnbounded();
    }
  }

  protected abstract BSSRangeHalfOpen physicalSourceAbsoluteBounds()
    throws IOException;

  protected final BSSRandomAccess<T> parent()
  {
    return this.parent;
  }

  private void checkAncestorsUnbounded()
  {
    var currentNode = this;
    while (currentNode != null) {
      if (!currentNode.parentRangeRelative.isUpperUnbounded()) {
        throw new IllegalStateException(
          "All ancestors of an unbounded object must also be unbounded");
      }
      currentNode = currentNode.parent;
    }
  }

  private long toAbsolute(
    final long relative)
  {
    return this.absoluteStart() + relative;
  }

  private BSSRangeHalfOpen toAbsoluteRange(
    final BSSRangeHalfOpen relative)
  {
    final var start = this.absoluteStart();
    return new BSSRangeHalfOpen(
      start + relative.lower(),
      relative.upper()
        .stream()
        .map(x -> start + x)
        .findFirst());
  }

  private long absoluteStart()
  {
    var accumulated = this.parentRangeRelative.lower();
    var currentParent = this.parent;
    while (currentParent != null) {
      accumulated += currentParent.parentRangeRelative.lower() + currentParent.offsetRelative;
      currentParent = currentParent.parent;
    }
    return accumulated;
  }

  private OptionalLong absoluteEnd()
    throws IOException
  {
    final var specStart =
      this.absoluteStart();

    final OptionalLong specUpper =
      this.parentRangeRelative.interval()
        .stream()
        .map(interval -> specStart + interval)
        .findFirst();

    final var specBounds =
      new BSSRangeHalfOpen(specStart, specUpper);
    final BSSRangeHalfOpen physBounds =
      this.physicalSourceAbsoluteBounds();

    return BSSRangeHalfOpen.minimumUpperBoundOf(physBounds, specBounds);
  }

  final void checkHasBytesRemaining(
    final String name,
    final long want)
    throws IOException
  {
    final var remainingOpt = this.bytesRemaining();
    if (remainingOpt.isPresent()) {
      if (want > remainingOpt.getAsLong()) {
        throw this.outOfBounds(name, this.offsetRelative + want);
      }
    }
  }

  final BSSRangeHalfOpen createSubRange(
    final long offset,
    final long size)
  {
    final var subRange = new BSSRangeHalfOpen(
      offset,
      OptionalLong.of(offset + size));
    if (!this.parentRangeRelative.isUpperUnbounded()) {
      BSSRanges.checkRangesCompatible(
        this.parentRangeRelative,
        subRange,
        this::toAbsoluteRange,
        (attributes) -> {
          attributes.addExceptionAttribute("Path", this.path);
          attributes.addExceptionAttribute("URI", this.uri.toString());
        });
    }
    return subRange;
  }

  final BSSRangeHalfOpen createOffsetSubRange(
    final long offset)
  {
    return new BSSRangeHalfOpen(offset, this.parentRangeRelative.interval());
  }

  private IOException outOfBounds(
    final String name,
    final long targetPosition)
  {
    BSSRangeHalfOpen bounds;
    try {
      bounds = this.physicalSourceAbsoluteBounds();
    } catch (final IOException e) {
      bounds = this.toAbsoluteRange(this.parentRangeRelative);
    }

    final var attributes = new HashMap<String, String>(4);
    attributes.put(
      "Target Offset (Absolute)",
      "0x" + Long.toUnsignedString(targetPosition, 16));
    attributes.put("Bounds (Relative)", this.parentRangeRelative.toString());
    attributes.put("Bounds (Absolute)", bounds.toString());
    if (name != null) {
      attributes.put("Field", name);
    }
    return BSSExceptions.createIO(this, "Out of bounds.", attributes);
  }

  @Override
  public final void skip(final long size)
    throws IOException, EOFException
  {
    this.checkNotClosed();
    this.checkHasBytesRemaining(null, size);
    this.offsetRelative += size;
  }

  @Override
  public final void align(final int alignment)
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
  public final OptionalLong bytesRemaining()
    throws IOException
  {
    final var absEnd = this.absoluteEnd();
    if (absEnd.isPresent()) {
      final var absOff = this.toAbsolute(this.offsetRelative);
      return OptionalLong.of(absEnd.getAsLong() - absOff);
    }
    return OptionalLong.empty();
  }

  @Override
  public final void seekTo(final long position)
    throws IOException
  {
    this.checkNotClosed();

    if (!this.parentRangeRelative.includesValue(position)) {
      throw this.outOfBounds(null, position);
    }

    this.offsetRelative = position;
  }

  @Override
  public final void close()
    throws IOException
  {
    if (!this.isClosed()) {
      try {
        this.onClose.call();
      } catch (final IOException e) {
        throw e;
      } catch (final Exception e) {
        throw new IOException(e);
      } finally {
        this.closed.set(true);
      }
    }
  }

  @Override
  public final boolean isClosed()
  {
    final var parentRef = this.parent;
    if (parentRef != null) {
      return parentRef.isClosed() || this.closed.get();
    }
    return this.closed.get();
  }

  final void increaseOffsetRelative(final long amount)
  {
    this.offsetRelative += amount;
  }

  @Override
  public final long offsetCurrentAbsolute()
  {
    return this.toAbsolute(this.offsetRelative);
  }

  @Override
  public final long offsetCurrentRelative()
  {
    return this.offsetRelative;
  }

  @Override
  public final URI uri()
  {
    return this.uri;
  }

  @Override
  public final String path()
  {
    return this.path;
  }
}

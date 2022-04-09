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

import java.util.Objects;
import java.util.OptionalLong;

/**
 * A half-open unsigned long range where the upper bound may be unspecified (and therefore
 * effectively unbounded).
 */

public final class BSSRangeHalfOpen
{
  private final long lower;
  private final OptionalLong upper;

  /**
   * Construct a range.
   *
   * @param inLower The lower bound
   * @param inUpper The (optional) upper bound
   */

  public BSSRangeHalfOpen(
    final long inLower,
    final OptionalLong inUpper)
  {
    this.lower = inLower;
    this.upper = Objects.requireNonNull(inUpper, "inUpper");

    this.upper.ifPresent(upperValue -> {
      if (Long.compareUnsigned(this.lower, upperValue) > 0) {
        throw new IllegalArgumentException(
          new StringBuilder(64)
            .append("Lower ")
            .append(Long.toUnsignedString(this.lower))
            .append(" must be <= upper ")
            .append(Long.toUnsignedString(upperValue))
            .toString());
      }
    });
  }

  /**
   * Create a new range with the given bounds.
   *
   * @param lower The lower bound
   * @param upper The upper bound
   *
   * @return A new range
   */

  public static BSSRangeHalfOpen create(
    final long lower,
    final long upper)
  {
    return new BSSRangeHalfOpen(lower, OptionalLong.of(upper));
  }

  /**
   * Determine the smallest upper bound of the given ranges.
   *
   * @param bounds0 Range 0
   * @param bounds1 Range 1
   *
   * @return The upper bound
   */

  public static OptionalLong minimumUpperBoundOf(
    final BSSRangeHalfOpen bounds0,
    final BSSRangeHalfOpen bounds1)
  {
    final var upper0 = bounds0.upper();
    final var upper1 = bounds1.upper();

    if (upper0.isEmpty()) {
      return upper1;
    }
    if (upper1.isEmpty()) {
      return upper0;
    }

    final long upperL0 = upper0.getAsLong();
    final long upperL1 = upper1.getAsLong();

    if (Long.compareUnsigned(upperL0, upperL1) < 0) {
      return OptionalLong.of(upperL0);
    }
    return OptionalLong.of(upperL1);
  }

  @Override
  public String toString()
  {
    final var builder = new StringBuilder(64);
    builder.append("[0x");
    builder.append(Long.toUnsignedString(this.lower, 16));
    builder.append(", ");
    if (this.upper.isPresent()) {
      builder.append("0x");
      builder.append(Long.toUnsignedString(this.upper.getAsLong(), 16));
    } else {
      builder.append("∞");
    }
    builder.append(")");
    return builder.toString();
  }

  /**
   * @return The inclusive lower bound
   */

  public long lower()
  {
    return this.lower;
  }

  /**
   * @return The exclusive upper bound, if one is specified
   */

  public OptionalLong upper()
  {
    return this.upper;
  }

  /**
   * @return {@code true} if an upper bound is present
   */

  public boolean isUpperUnbounded()
  {
    return this.upper.isEmpty();
  }

  /**
   * <p>Retrieve the number of values in the range {@code [lower, upper)}. That
   * is, {@code (upper - lower)}.<p>
   *
   * @return The number of values in the range
   */

  public OptionalLong interval()
  {
    if (this.upper.isPresent()) {
      return OptionalLong.of(this.upper.getAsLong() - this.lower);
    }
    return OptionalLong.empty();
  }

  /**
   * <p> Determine if the given value is included in this range. </p>
   *
   * @param value The given value
   *
   * @return {@code true} iff {@code value &gt;= this.getLower() &amp;&amp; value &lt;
   * this.getUpper()} .
   */

  public boolean includesValue(
    final long value)
  {
    final var lowerIncludes =
      Long.compareUnsigned(value, this.lower) >= 0;

    final boolean upperIncludes;
    if (this.upper.isPresent()) {
      upperIncludes = Long.compareUnsigned(value, this.upper.getAsLong()) < 0;
    } else {
      upperIncludes = true;
    }

    return lowerIncludes && upperIncludes;
  }

  /**
   * <p> Determine if the given range is included in this range. </p>
   *
   * @param other The given range
   *
   * @return {@code true} iff {@code this.getLower() &gt;= other.getLower() &amp;&amp;
   * this.getUpper() &lt;= other.getUpper()} .
   */

  public boolean isIncludedIn(
    final BSSRangeHalfOpen other)
  {
    Objects.requireNonNull(other, "Other range");

    final var lowerIncludes = Long.compareUnsigned(
      this.lower,
      other.lower) >= 0;

    if (other.upper.isEmpty()) {
      return lowerIncludes;
    }

    final var otherUpper = other.upper.getAsLong();
    if (this.upper.isEmpty()) {
      return false;
    }

    final var thisUpper = this.upper.getAsLong();
    final var upperIncludes = Long.compareUnsigned(thisUpper, otherUpper) <= 0;
    return lowerIncludes && upperIncludes;
  }
}

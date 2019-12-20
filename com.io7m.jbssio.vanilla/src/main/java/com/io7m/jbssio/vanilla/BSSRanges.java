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

import java.util.Objects;

final class BSSRanges
{
  private BSSRanges()
  {

  }

  private static IllegalArgumentException makeException(
    final TransformToAbsoluteType transformToAbsolute,
    final BSSRangeHalfOpen existingRange,
    final BSSRangeHalfOpen targetRange,
    final OnIncompatibleListenerType listener)
  {
    final var lineSeparator =
      System.lineSeparator();
    final var existingAbs =
      transformToAbsolute.transformToAbsolute(existingRange);
    final var requestedAbs =
      transformToAbsolute.transformToAbsolute(targetRange);

    final var stringBuilder = new StringBuilder(256);

    stringBuilder
      .append("Bounds cannot exceed the bounds of this object.")
      .append(lineSeparator);

    final ExceptionAttributeReceiverType receiver =
      (name, value) -> {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        stringBuilder
          .append("  ")
          .append(name)
          .append(": ")
          .append(value)
          .append(lineSeparator);
      };

    listener.call(receiver);

    stringBuilder
      .append("  Existing bounds: absolute ")
      .append(existingAbs.toString())
      .append(lineSeparator);

    stringBuilder
      .append("  Requested bounds: absolute ")
      .append(requestedAbs.toString())
      .append(lineSeparator);

    throw new IllegalArgumentException(stringBuilder.toString());
  }

  public static void checkRangesCompatible(
    final BSSRangeHalfOpen existingRange,
    final BSSRangeHalfOpen targetRange,
    final TransformToAbsoluteType transformToAbsolute,
    final OnIncompatibleListenerType listener)
  {
    final var projected =
      new BSSRangeHalfOpen(
        targetRange.lower() - existingRange.lower(),
        targetRange.upper().stream().map(x -> x - existingRange.lower()).findFirst());

    if (!projected.isIncludedIn(existingRange)) {
      throw makeException(
        transformToAbsolute,
        existingRange,
        targetRange,
        listener);
    }
  }

  interface ExceptionAttributeReceiverType
  {
    void addExceptionAttribute(
      String name,
      String value);
  }

  interface OnIncompatibleListenerType
  {
    void call(ExceptionAttributeReceiverType receiver);
  }

  interface TransformToAbsoluteType
  {
    BSSRangeHalfOpen transformToAbsolute(
      BSSRangeHalfOpen range);
  }
}

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

import com.io7m.jbssio.api.BSSAddressableType;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class BSSExceptions
{
  private BSSExceptions()
  {

  }

  public static <E extends Exception> E create(
    final String message,
    final Map<String, String> attributes,
    final Function<String, E> constructor)
  {
    final var separator = System.lineSeparator();
    final var builder = new StringBuilder(128);
    builder.append(message);
    builder.append(separator);

    final var entries = new ArrayList<>(attributes.entrySet());
    entries.sort(Comparator.comparing(Map.Entry::getKey));

    var longest = 0;
    for (final var entry : entries) {
      longest = Math.max(longest, entry.getKey().length());
    }

    final var format = "%-" + longest + "s : %s";
    for (final var entry : entries) {
      builder.append(String.format(format, entry.getKey(), entry.getValue()));
      builder.append(separator);
    }

    return constructor.apply(builder.toString());
  }

  public static <E extends Exception> E create(
    final BSSAddressableType source,
    final String message,
    final Map<String, String> attributes,
    final Function<String, E> constructor)
  {
    final var baseAttributes = new HashMap<String, String>(4 + attributes.size());
    baseAttributes.put("URI", source.uri().toString());
    baseAttributes.put("Path", source.path());
    baseAttributes.put(
      "Offset (Relative)",
      "0x" + Long.toUnsignedString(source.offsetCurrentRelative(), 16));
    baseAttributes.put(
      "Offset (Absolute)",
      "0x" + Long.toUnsignedString(source.offsetCurrentAbsolute(), 16));
    baseAttributes.putAll(attributes);
    return create(message, baseAttributes, constructor);
  }

  public static IOException createIO(
    final BSSAddressableType source,
    final String message,
    final Map<String, String> attributes)
  {
    return create(source, message, attributes, IOException::new);
  }

  public static EOFException createEOF(
    final BSSAddressableType source,
    final String message,
    final Map<String, String> attributes)
  {
    return create(source, message, attributes, EOFException::new);
  }
}

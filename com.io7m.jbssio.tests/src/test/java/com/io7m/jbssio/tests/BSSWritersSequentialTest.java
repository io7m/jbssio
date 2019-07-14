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

package com.io7m.jbssio.tests;

import com.io7m.jbssio.vanilla.BSSWriters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

public final class BSSWritersSequentialTest
{
  private static final Logger LOG = LoggerFactory.getLogger(BSSWritersSequentialTest.class);

  @Test
  public void testEmptyStream()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        Assertions.assertEquals(0L, writer.offsetAbsolute());
        Assertions.assertEquals(0L, writer.offsetRelative());
      }
    }
  }

  @Test
  public void testClosed()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      final var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a");
      Assertions.assertEquals(0L, writer.offsetAbsolute());
      Assertions.assertEquals(0L, writer.offsetRelative());

      writer.close();
      Assertions.assertThrows(IOException.class, () -> writer.writeS8(0x0));
    }
  }

  @Test
  public void testNames()
    throws Exception
  {
    final var data = new byte[4];
    for (var index = 0; index < 4; ++index) {
      data[index] = (byte) index;
    }

    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        try (var s0 = writer.createSubWriter("x")) {
          try (var s1 = s0.createSubWriter("y")) {
            try (var s2 = s1.createSubWriter("z")) {
              Assertions.assertEquals("a.x.y.z", s2.path());
            }
            Assertions.assertEquals("a.x.y", s1.path());
          }
          Assertions.assertEquals("a.x", s0.path());
        }
        Assertions.assertEquals("a", writer.path());
      }
    }
  }

  @Test
  public void testSkip()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        Assertions.assertEquals(0L, writer.offsetAbsolute());
        Assertions.assertEquals(0L, writer.offsetRelative());

        writer.skip(10L);
        Assertions.assertEquals(10L, writer.offsetAbsolute());
        Assertions.assertEquals(10L, writer.offsetRelative());
      }

      Assertions.assertArrayEquals(new byte[10], stream.toByteArray());
    }
  }

  @Test
  public void testSkipBounded()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a", 10L)) {
        Assertions.assertEquals(0L, writer.offsetAbsolute());
        Assertions.assertEquals(0L, writer.offsetRelative());

        writer.skip(10L);
        Assertions.assertEquals(10L, writer.offsetAbsolute());
        Assertions.assertEquals(10L, writer.offsetRelative());

        final var ex = Assertions.assertThrows(IOException.class, () -> {
          writer.skip(1L);
        });
        LOG.debug("ex: ", ex);
      }

      Assertions.assertArrayEquals(new byte[10], stream.toByteArray());
    }
  }

  @Test
  public void testAlign()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a")) {
        Assertions.assertEquals(0L, writer.offsetAbsolute());
        Assertions.assertEquals(0L, writer.offsetRelative());

        writer.writeU8(0x20);
        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetAbsolute());
        Assertions.assertEquals(4L, writer.offsetRelative());

        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetAbsolute());
        Assertions.assertEquals(4L, writer.offsetRelative());

        writer.writeU8(0x21);
        writer.align(4);
        Assertions.assertEquals(8L, writer.offsetAbsolute());
        Assertions.assertEquals(8L, writer.offsetRelative());

        writer.align(4);
        Assertions.assertEquals(8L, writer.offsetAbsolute());
        Assertions.assertEquals(8L, writer.offsetRelative());

        writer.writeU8(0x22);
        writer.align(4);
        Assertions.assertEquals(12L, writer.offsetAbsolute());
        Assertions.assertEquals(12L, writer.offsetRelative());

        writer.align(4);
        Assertions.assertEquals(12L, writer.offsetAbsolute());
        Assertions.assertEquals(12L, writer.offsetRelative());
      }

      Assertions.assertArrayEquals(new byte[]{
        0x20,
        0x0,
        0x0,
        0x0,
        0x21,
        0x0,
        0x0,
        0x0,
        0x22,
        0x0,
        0x0,
        0x0,
      }, stream.toByteArray());
    }
  }

  @Test
  public void testAlignBounded()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a", 5L)) {
        Assertions.assertEquals(0L, writer.offsetAbsolute());
        Assertions.assertEquals(0L, writer.offsetRelative());

        writer.writeU8(0x20);
        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetAbsolute());
        Assertions.assertEquals(4L, writer.offsetRelative());

        writer.align(4);
        Assertions.assertEquals(4L, writer.offsetAbsolute());
        Assertions.assertEquals(4L, writer.offsetRelative());

        writer.writeU8(0x21);
        Assertions.assertThrows(IOException.class, () -> writer.align(4));
      }

      Assertions.assertArrayEquals(new byte[]{
        0x20,
        0x0,
        0x0,
        0x0,
        0x21,
      }, stream.toByteArray());
    }
  }

  @Test
  public void testSubWriterBasic()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a", 12L)) {
        try (var s = writer.createSubWriter("x", 4L)) {
          Assertions.assertEquals(0L, writer.offsetAbsolute());
          Assertions.assertEquals(0L, writer.offsetRelative());
          Assertions.assertEquals(0L, s.offsetAbsolute());
          Assertions.assertEquals(0L, s.offsetRelative());
          s.writeU8(0x0);
          Assertions.assertEquals(1L, writer.offsetAbsolute());
          Assertions.assertEquals(1L, writer.offsetRelative());
          Assertions.assertEquals(1L, s.offsetAbsolute());
          Assertions.assertEquals(1L, s.offsetRelative());
          s.writeU8(0x1);
          Assertions.assertEquals(2L, writer.offsetAbsolute());
          Assertions.assertEquals(2L, writer.offsetRelative());
          Assertions.assertEquals(2L, s.offsetAbsolute());
          Assertions.assertEquals(2L, s.offsetRelative());
          s.writeU8(0x2);
          Assertions.assertEquals(3L, writer.offsetAbsolute());
          Assertions.assertEquals(3L, writer.offsetRelative());
          Assertions.assertEquals(3L, s.offsetAbsolute());
          Assertions.assertEquals(3L, s.offsetRelative());
          s.writeU8(0x3);
          Assertions.assertEquals(4L, writer.offsetAbsolute());
          Assertions.assertEquals(4L, writer.offsetRelative());
          Assertions.assertEquals(4L, s.offsetAbsolute());
          Assertions.assertEquals(4L, s.offsetRelative());
        }

        try (var s = writer.createSubWriter("y", 4L)) {
          Assertions.assertEquals(4L + 0L, writer.offsetAbsolute());
          Assertions.assertEquals(4L + 0L, writer.offsetRelative());
          Assertions.assertEquals(4L + 0L, s.offsetAbsolute());
          Assertions.assertEquals(0L, s.offsetRelative());
          s.writeU8(0x0);
          Assertions.assertEquals(4L + 1L, writer.offsetAbsolute());
          Assertions.assertEquals(4L + 1L, writer.offsetRelative());
          Assertions.assertEquals(4L + 1L, s.offsetAbsolute());
          Assertions.assertEquals(1L, s.offsetRelative());
          s.writeU8(0x1);
          Assertions.assertEquals(4L + 2L, writer.offsetAbsolute());
          Assertions.assertEquals(4L + 2L, writer.offsetRelative());
          Assertions.assertEquals(4L + 2L, s.offsetAbsolute());
          Assertions.assertEquals(2L, s.offsetRelative());
          s.writeU8(0x2);
          Assertions.assertEquals(4L + 3L, writer.offsetAbsolute());
          Assertions.assertEquals(4L + 3L, writer.offsetRelative());
          Assertions.assertEquals(4L + 3L, s.offsetAbsolute());
          Assertions.assertEquals(3L, s.offsetRelative());
          s.writeU8(0x3);
          Assertions.assertEquals(4L + 4L, writer.offsetAbsolute());
          Assertions.assertEquals(4L + 4L, writer.offsetRelative());
          Assertions.assertEquals(4L + 4L, s.offsetAbsolute());
          Assertions.assertEquals(4L, s.offsetRelative());
        }

        try (var s = writer.createSubWriter("z", 4L)) {
          Assertions.assertEquals(8L + 0L, writer.offsetAbsolute());
          Assertions.assertEquals(8L + 0L, writer.offsetRelative());
          Assertions.assertEquals(8L + 0L, s.offsetAbsolute());
          Assertions.assertEquals(0L, s.offsetRelative());
          s.writeU8(0x0);
          Assertions.assertEquals(8L + 1L, writer.offsetAbsolute());
          Assertions.assertEquals(8L + 1L, writer.offsetRelative());
          Assertions.assertEquals(8L + 1L, s.offsetAbsolute());
          Assertions.assertEquals(1L, s.offsetRelative());
          s.writeU8(0x1);
          Assertions.assertEquals(8L + 2L, writer.offsetAbsolute());
          Assertions.assertEquals(8L + 2L, writer.offsetRelative());
          Assertions.assertEquals(8L + 2L, s.offsetAbsolute());
          Assertions.assertEquals(2L, s.offsetRelative());
          s.writeU8(0x2);
          Assertions.assertEquals(8L + 3L, writer.offsetAbsolute());
          Assertions.assertEquals(8L + 3L, writer.offsetRelative());
          Assertions.assertEquals(8L + 3L, s.offsetAbsolute());
          Assertions.assertEquals(3L, s.offsetRelative());
          s.writeU8(0x3);
          Assertions.assertEquals(8L + 4L, writer.offsetAbsolute());
          Assertions.assertEquals(8L + 4L, writer.offsetRelative());
          Assertions.assertEquals(8L + 4L, s.offsetAbsolute());
          Assertions.assertEquals(4L, s.offsetRelative());
        }
      }

      Assertions.assertArrayEquals(new byte[]{
        0x0, 0x1, 0x2, 0x3,
        0x0, 0x1, 0x2, 0x3,
        0x0, 0x1, 0x2, 0x3,
      }, stream.toByteArray());
    }
  }

  @Test
  public void testSubWriterBasicExceeds()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a", 12L)) {
        final var ex =
          Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> writer.createSubWriter("x", 13L));
        LOG.debug("ex: ", ex);
      }
    }
  }

  @Test
  public void testSubSubWriterBasicExceeds()
    throws Exception
  {
    final var writers = new BSSWriters();
    try (var stream = new ByteArrayOutputStream()) {
      try (var writer = writers.createWriterFromStream(URI.create("urn:fake"), stream, "a", 12L)) {
        try (var s = writer.createSubWriter("y", 4L)) {
          final var ex =
            Assertions.assertThrows(
              IllegalArgumentException.class,
              () -> s.createSubWriter("z", 5L));
          LOG.debug("ex: ", ex);
        }
      }
    }
  }
}

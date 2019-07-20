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

import com.io7m.jbssio.vanilla.BSSReaders;
import com.io7m.jbssio.vanilla.BSSWriters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class Demo
{
  private static final Logger LOG = LoggerFactory.getLogger(Demo.class);

  private Demo()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    final var path = Paths.get(args[0]);
    final var pathURI = path.toUri();

    final var writers = new BSSWriters();
    try (var channel = Files.newByteChannel(path, CREATE, WRITE, TRUNCATE_EXISTING)) {
      try (var writer = writers.createWriterFromChannel(pathURI, channel, "root")) {
        try (var sw = writer.createSubWriterBounded("head", 8L)) {
          sw.writeS32BE(0x10203040L);
          sw.writeS32BE(0x50607080L);
        }
        try (var sw = writer.createSubWriterAtBounded("body", 8L, 16L)) {
          sw.writeS32BE(0x90909090L);
          sw.writeS32BE(0x80808080L);
          sw.writeS32BE(0xa0a0a0a0L);
          sw.writeS32BE(0xb0b0b0b0L);
        }
      }
    }

    final var readers = new BSSReaders();
    try (var channel = Files.newByteChannel(path, READ)) {
      try (var reader = readers.createReaderFromChannel(pathURI, channel, "root")) {
        try (var sr = reader.createSubReaderAtBounded("head", 0L,8L)) {
          LOG.debug("{}: 0x{}", sr.path(), Integer.toUnsignedString((int) sr.readS32BE(), 16));
          LOG.debug("{}: 0x{}", sr.path(), Integer.toUnsignedString((int) sr.readS32BE(), 16));
        }
        try (var sr = reader.createSubReaderAtBounded("body", 8L, 16L)) {
          LOG.debug("{}: 0x{}", sr.path(), Integer.toUnsignedString((int) sr.readS32BE(), 16));
          LOG.debug("{}: 0x{}", sr.path(), Integer.toUnsignedString((int) sr.readS32BE(), 16));
          LOG.debug("{}: 0x{}", sr.path(), Integer.toUnsignedString((int) sr.readS32BE(), 16));
          LOG.debug("{}: 0x{}", sr.path(), Integer.toUnsignedString((int) sr.readS32BE(), 16));
        }
      }
    }
  }
}

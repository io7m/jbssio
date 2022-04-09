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

package com.io7m.jbssio.tests;

import com.io7m.jbssio.vanilla.BSSWriters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class DemoSpecimen
{
  private static final Logger LOG = LoggerFactory.getLogger(DemoSpecimen.class);

  private DemoSpecimen()
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
        try (var w = writer.createSubWriterAtBounded("BE",  0L,128L)) {
          w.align(16);
          w.writeS8(Byte.MIN_VALUE);
          w.writeS8(Byte.MAX_VALUE);
          w.writeU8(0);
          w.writeU8(0xff);

          w.align(16);
          w.writeS16BE(Short.MIN_VALUE);
          w.writeS16BE(Short.MAX_VALUE);
          w.writeU16BE(0);
          w.writeU16BE(0xffff);

          w.align(16);
          w.writeS32BE(Integer.MIN_VALUE);
          w.writeS32BE(Integer.MAX_VALUE);
          w.writeS32BE(0L);
          w.writeS32BE(0xffff_ffffL);

          w.align(16);
          w.writeS64BE(Long.MIN_VALUE);
          w.writeS32BE(Long.MAX_VALUE);
          w.writeS32BE(0L);
          w.writeS32BE(0xffff_ffff_ffff_ffffL);

          w.align(16);
          w.writeF32BE(-Float.MIN_VALUE);
          w.writeF32BE(Float.MAX_VALUE);
          w.writeF64BE(-Double.MIN_VALUE);
          w.writeF64BE(Double.MAX_VALUE);

          w.align(16);
          final var bytes = new byte[] { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x7f };
          w.writeBytes(bytes);
        }

        try (var w = writer.createSubWriterAtBounded("LE", 128L, 128L)) {
          w.align(16);
          w.writeS8(Byte.MIN_VALUE);
          w.writeS8(Byte.MAX_VALUE);
          w.writeU8(0);
          w.writeU8(0xff);

          w.align(16);
          w.writeS16LE(Short.MIN_VALUE);
          w.writeS16LE(Short.MAX_VALUE);
          w.writeU16LE(0);
          w.writeU16LE(0xffff);

          w.align(16);
          w.writeS32LE(Integer.MIN_VALUE);
          w.writeS32LE(Integer.MAX_VALUE);
          w.writeS32LE(0L);
          w.writeS32LE(0xffff_ffffL);

          w.align(16);
          w.writeS64LE(Long.MIN_VALUE);
          w.writeS32LE(Long.MAX_VALUE);
          w.writeS32LE(0L);
          w.writeS32LE(0xffff_ffff_ffff_ffffL);

          w.align(16);
          w.writeF32LE(-Float.MIN_VALUE);
          w.writeF32LE(Float.MAX_VALUE);
          w.writeF64LE(-Double.MIN_VALUE);
          w.writeF64LE(Double.MAX_VALUE);

          w.align(16);
          final var bytes = new byte[] { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x7f };
          w.writeBytes(bytes);
        }
      }
    }
  }
}

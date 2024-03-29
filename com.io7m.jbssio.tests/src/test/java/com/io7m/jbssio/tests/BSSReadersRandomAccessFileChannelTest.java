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

import com.io7m.jbssio.api.BSSReaderRandomAccessType;
import com.io7m.jbssio.vanilla.BSSReaders;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class BSSReadersRandomAccessFileChannelTest
  extends BSSReadersRandomAccessChannelContract<FileChannel>
{
  @Override
  protected FileChannel channelOf(final byte[] data)
    throws IOException
  {
    final var path = Files.createTempFile("jbssio-readers-", ".dat");
    Files.write(path, data);
    return FileChannel.open(path, StandardOpenOption.READ);
  }

  @Override
  protected BSSReaderRandomAccessType readerOf(
    final FileChannel channel)
    throws IOException
  {
    return new BSSReaders()
      .createReaderFromChannelBounded(URI.create("urn:fake"), channel, "a", channel.size());
  }

  @Override
  protected BSSReaderRandomAccessType readerUnboundedOf(
    final FileChannel channel)
    throws IOException
  {
    return new BSSReaders()
      .createReaderFromChannel(URI.create("urn:fake"), channel, "a");
  }
}

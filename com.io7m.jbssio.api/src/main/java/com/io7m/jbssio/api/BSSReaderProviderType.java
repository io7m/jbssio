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

package com.io7m.jbssio.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.OptionalLong;

/**
 * A provider of readers.
 */

public interface BSSReaderProviderType
{
  /**
   * Create a new sequential reader from the given stream.
   *
   * @param uri    The URI of the stream
   * @param stream The stream
   * @param name   The name of the initial reader
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderSequentialType createReaderFromStream(
    URI uri,
    InputStream stream,
    String name)
    throws IOException;

  /**
   * Create a new sequential reader from the given stream.
   *
   * @param uri    The URI of the stream
   * @param stream The stream
   * @param name   The name of the initial reader
   * @param size   The maximum number of bytes that can be read
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderSequentialType createReaderFromStreamBounded(
    URI uri,
    InputStream stream,
    String name,
    long size)
    throws IOException;

  /**
   * Create a new sequential reader from the given stream.
   *
   * @param uri    The URI of the stream
   * @param stream The stream
   * @param name   The name of the initial reader
   * @param size   The maximum number of bytes that can be read
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  default BSSReaderSequentialType createReaderFromStream(
    final URI uri,
    final InputStream stream,
    final String name,
    final OptionalLong size)
    throws IOException
  {
    if (size.isPresent()) {
      return this.createReaderFromStreamBounded(
        uri,
        stream,
        name,
        size.getAsLong());
    }
    return this.createReaderFromStream(uri, stream, name);
  }

  /**
   * Create a new random access reader from the given seekable byte channel.
   *
   * @param uri     The URI of the stream
   * @param channel The channel
   * @param name    The name of the initial reader
   * @param size    A limit on the number of bytes that can be read
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  default BSSReaderRandomAccessType createReaderFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final OptionalLong size)
    throws IOException
  {
    if (size.isPresent()) {
      return this.createReaderFromChannelBounded(
        uri,
        channel,
        name,
        size.getAsLong());
    }
    return this.createReaderFromChannel(uri, channel, name);
  }

  /**
   * Create a new random access reader from the given byte buffer.
   *
   * @param uri    The URI of the stream
   * @param buffer The buffer
   * @param name   The name of the initial reader
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderRandomAccessType createReaderFromByteBuffer(
    URI uri,
    ByteBuffer buffer,
    String name)
    throws IOException;

  /**
   * Create a new random access reader from the given seekable byte channel.
   *
   * @param uri     The URI of the stream
   * @param channel The channel
   * @param name    The name of the initial reader
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderRandomAccessType createReaderFromChannel(
    URI uri,
    SeekableByteChannel channel,
    String name)
    throws IOException;

  /**
   * Create a new random access reader from the given seekable byte channel.
   *
   * @param uri     The URI of the stream
   * @param channel The channel
   * @param name    The name of the initial reader
   * @param size    A limit on the number of bytes that can be read
   *
   * @return A new reader
   *
   * @throws IOException On I/O errors
   */

  BSSReaderRandomAccessType createReaderFromChannelBounded(
    URI uri,
    SeekableByteChannel channel,
    String name,
    long size)
    throws IOException;
}

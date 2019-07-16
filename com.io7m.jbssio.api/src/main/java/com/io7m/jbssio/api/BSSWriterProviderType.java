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

package com.io7m.jbssio.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.OptionalLong;

/**
 * A provider of writers.
 */

public interface BSSWriterProviderType
{
  /**
   * Create a new sequential writer from the given stream.
   *
   * @param uri    The URI of the stream
   * @param stream The stream
   * @param name   The name of the initial writer
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterSequentialType createWriterFromStream(
    URI uri,
    OutputStream stream,
    String name)
    throws IOException;

  /**
   * Create a new sequential writer from the given stream.
   *
   * @param uri    The URI of the stream
   * @param stream The stream
   * @param name   The name of the initial writer
   * @param size   The maximum number of bytes that can be written
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterSequentialType createWriterFromStreamBounded(
    URI uri,
    OutputStream stream,
    String name,
    long size)
    throws IOException;

  /**
   * Create a new sequential writer from the given stream.
   *
   * @param uri    The URI of the stream
   * @param stream The stream
   * @param name   The name of the initial writer
   * @param size   The maximum number of bytes that can be written, if any
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  default BSSWriterSequentialType createWriterFromStream(
    final URI uri,
    final OutputStream stream,
    final String name,
    final OptionalLong size)
    throws IOException
  {
    if (size.isPresent()) {
      return this.createWriterFromStreamBounded(uri, stream, name, size.getAsLong());
    }
    return this.createWriterFromStream(uri, stream, name);
  }

  /**
   * Create a new random access writer from the given byte buffer.
   *
   * @param uri    The URI of the stream
   * @param buffer The buffer
   * @param name   The name of the initial writer
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterRandomAccessType createWriterFromByteBuffer(
    URI uri,
    ByteBuffer buffer,
    String name)
    throws IOException;

  /**
   * Create a new random access writer from the given channel.
   *
   * @param uri     The URI of the stream
   * @param channel The channel
   * @param name    The name of the initial writer
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterRandomAccessType createWriterFromChannel(
    URI uri,
    SeekableByteChannel channel,
    String name)
    throws IOException;

  /**
   * Create a new random access writer from the given channel.
   *
   * @param uri     The URI of the stream
   * @param channel The channel
   * @param name    The name of the initial writer
   * @param size    A limit on the number of bytes that can be written
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  BSSWriterRandomAccessType createWriterFromChannelBounded(
    URI uri,
    SeekableByteChannel channel,
    String name,
    long size)
    throws IOException;

  /**
   * Create a new random access writer from the given channel.
   *
   * @param uri     The URI of the stream
   * @param channel The channel
   * @param name    The name of the initial writer
   * @param size    A limit on the number of bytes that can be written
   *
   * @return A new writer
   *
   * @throws IOException On I/O errors
   */

  default BSSWriterRandomAccessType createWriterFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final OptionalLong size)
    throws IOException
  {
    if (size.isPresent()) {
      return this.createWriterFromChannelBounded(uri, channel, name, size.getAsLong());
    }
    return this.createWriterFromChannel(uri, channel, name);
  }
}

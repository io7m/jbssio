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

import com.io7m.jbssio.api.BSSWriterProviderType;
import com.io7m.jbssio.api.BSSWriterRandomAccessType;
import com.io7m.jbssio.api.BSSWriterSequentialType;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.OptionalLong;
import org.osgi.service.component.annotations.Component;

/**
 * A default provider of writers.
 */

@Component(service = BSSWriterProviderType.class)
public final class BSSWriters implements BSSWriterProviderType
{
  /**
   * Construct a provider.
   */

  public BSSWriters()
  {

  }

  @Override
  public BSSWriterSequentialType createWriterFromStream(
    final URI uri,
    final OutputStream stream,
    final String name)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(name, "name");
    return BSSWriterStream.create(uri, stream, name, OptionalLong.empty());
  }

  @Override
  public BSSWriterSequentialType createWriterFromStreamBounded(
    final URI uri,
    final OutputStream stream,
    final String name,
    final long size)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(name, "name");
    return BSSWriterStream.create(uri, stream, name, OptionalLong.of(size));
  }

  @Override
  public BSSWriterRandomAccessType createWriterFromByteBuffer(
    final URI uri,
    final ByteBuffer buffer,
    final String name)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(buffer, "buffer");
    Objects.requireNonNull(name, "name");
    return BSSWriterByteBuffer.createFromByteBuffer(
      uri,
      buffer,
      name,
      writer -> {

      });
  }

  @Override
  public BSSWriterRandomAccessType createWriterFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(channel, "channel");
    Objects.requireNonNull(name, "name");
    return BSSWriterSeekableChannel.createFromChannel(
      uri,
      channel,
      name,
      OptionalLong.empty(),
      writer -> {

      });
  }

  @Override
  public BSSWriterRandomAccessType createWriterFromChannelBounded(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final long size)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(channel, "channel");
    Objects.requireNonNull(name, "name");
    return BSSWriterSeekableChannel.createFromChannel(
      uri,
      channel,
      name,
      OptionalLong.of(size),
      writer -> {

      });
  }

  @Override
  public BSSWriterRandomAccessType createWriterFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final OptionalLong size)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(channel, "channel");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(size, "size");
    return BSSWriterSeekableChannel.createFromChannel(
      uri,
      channel,
      name,
      size,
      writer -> {

      });
  }
}

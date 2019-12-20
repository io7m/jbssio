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

import com.io7m.jbssio.api.BSSReaderProviderType;
import com.io7m.jbssio.api.BSSReaderRandomAccessType;
import com.io7m.jbssio.api.BSSReaderSequentialType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.function.Consumer;
import org.osgi.service.component.annotations.Component;

/**
 * A default provider of readers.
 */

@Component(service = BSSReaderProviderType.class)
public final class BSSReaders implements BSSReaderProviderType
{
  /**
   * Construct a provider.
   */

  public BSSReaders()
  {

  }

  @Override
  public BSSReaderSequentialType createReaderFromStream(
    final URI uri,
    final InputStream stream,
    final String name,
    final Consumer<BSSReaderSequentialType> onClose)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(name, "path");
    Objects.requireNonNull(onClose, "onClose");
    return BSSReaderStream.create(
      uri,
      stream,
      name,
      OptionalLong.empty(),
      onClose);
  }

  @Override
  public BSSReaderSequentialType createReaderFromStreamBounded(
    final URI uri,
    final InputStream stream,
    final String name,
    final long size,
    final Consumer<BSSReaderSequentialType> onClose)
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(name, "path");
    Objects.requireNonNull(onClose, "onClose");
    return BSSReaderStream.create(
      uri,
      stream,
      name,
      OptionalLong.of(size),
      onClose);
  }

  @Override
  public BSSReaderRandomAccessType createReaderFromByteBuffer(
    final URI uri,
    final ByteBuffer buffer,
    final String name,
    final Consumer<BSSReaderRandomAccessType> onClose)
    throws IOException
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(buffer, "buffer");
    Objects.requireNonNull(name, "path");
    Objects.requireNonNull(onClose, "onClose");
    return BSSReaderByteBuffer.createFromByteBuffer(uri, buffer, name, onClose);
  }

  @Override
  public BSSReaderRandomAccessType createReaderFromChannel(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final Consumer<BSSReaderRandomAccessType> onClose)
    throws IOException
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(channel, "channel");
    Objects.requireNonNull(name, "path");
    Objects.requireNonNull(onClose, "onClose");
    return BSSReaderSeekableChannel.createFromChannel(
      uri,
      channel,
      name,
      OptionalLong.empty(),
      onClose);
  }

  @Override
  public BSSReaderRandomAccessType createReaderFromChannelBounded(
    final URI uri,
    final SeekableByteChannel channel,
    final String name,
    final long size,
    final Consumer<BSSReaderRandomAccessType> onClose)
    throws IOException
  {
    Objects.requireNonNull(uri, "uri");
    Objects.requireNonNull(channel, "channel");
    Objects.requireNonNull(name, "path");
    Objects.requireNonNull(onClose, "onClose");
    return BSSReaderSeekableChannel.createFromChannel(
      uri,
      channel,
      name,
      OptionalLong.of(size),
      onClose);
  }
}

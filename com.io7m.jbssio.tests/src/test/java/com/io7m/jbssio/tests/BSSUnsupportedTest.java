/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
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

import com.io7m.jbssio.api.BSSReaderRandomAccessUnsupported;
import com.io7m.jbssio.api.BSSReaderSequentialUnsupported;
import com.io7m.jbssio.api.BSSWriterRandomAccessUnsupported;
import com.io7m.jbssio.api.BSSWriterSequentialUnsupported;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public final class BSSUnsupportedTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(BSSUnsupportedTest.class);

  @Test
  public void testReaderUnsupportedSequentialExtra()
    throws IOException
  {
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderSequentialUnsupported()
        .createSubReaderBounded("any", 23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderSequentialUnsupported()
        .skip(23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderSequentialUnsupported()
        .align(23);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderSequentialUnsupported()
        .readBytes(new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderSequentialUnsupported()
        .readBytes("x", new byte[]{0x0}, 0, 1);
    });

    new BSSReaderSequentialUnsupported().close();
    assertEquals(Optional.empty(), new BSSReaderSequentialUnsupported().parentReader());
    assertFalse(new BSSReaderSequentialUnsupported().isClosed());
  }

  @Test
  public void testReaderUnsupportedRandomExtra()
    throws IOException
  {
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .skip(23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .align(23);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .readBytes(new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .readBytes("x", new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .seekTo(23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .createSubReaderAt("x", 23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSReaderRandomAccessUnsupported()
        .createSubReaderAtBounded("x", 23L, 23L);
    });

    new BSSReaderRandomAccessUnsupported().close();
    assertEquals(Optional.empty(), new BSSReaderRandomAccessUnsupported().parentReader());
    assertFalse(new BSSReaderRandomAccessUnsupported().isClosed());
  }

  @Test
  public void testWriterUnsupportedSequentialExtra()
    throws IOException
  {
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .createSubWriterBounded("any", 23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .skip(23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .align(23);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .writeBytes(new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .writeBytes("x", new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .writeBytes(new byte[]{0x0});
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .writeBytes("x", new byte[]{0x0});
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .createSubWriterAt("x", 0L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .createSubWriterAtBounded("x", 0L, 23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterSequentialUnsupported()
        .padTo(0, (byte) 0);
    });

    new BSSWriterSequentialUnsupported().close();
    assertFalse(new BSSWriterSequentialUnsupported().isClosed());
  }

  @Test
  public void testWriterUnsupportedRandomExtra()
    throws IOException
  {
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .skip(23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .align(23);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .writeBytes(new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .writeBytes("x", new byte[]{0x0}, 0, 1);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .seekTo(23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .createSubWriterAt("x", 23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .createSubWriterAtBounded("x", 23L, 23L);
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .writeBytes(new byte[]{0x0});
    });
    assertThrows(UnsupportedOperationException.class, () -> {
      new BSSWriterRandomAccessUnsupported()
        .writeBytes("x", new byte[]{0x0});
    });

    new BSSWriterRandomAccessUnsupported().close();
    assertFalse(new BSSWriterRandomAccessUnsupported().isClosed());
  }

  @TestFactory
  public Stream<DynamicTest> testReaderUnsupportedSequential()
  {
    final var unsupported = new BSSReaderSequentialUnsupported();
    return Arrays.stream(unsupported.getClass().getMethods())
      .filter(m -> isSuitableMethod(m, BSSReaderSequentialUnsupported.class))
      .map(m -> makeTest(m, unsupported));
  }

  @TestFactory
  public Stream<DynamicTest> testReaderUnsupportedRandom()
  {
    final var unsupported = new BSSReaderRandomAccessUnsupported();
    return Arrays.stream(unsupported.getClass().getMethods())
      .filter(m -> isSuitableMethod(m, BSSReaderRandomAccessUnsupported.class))
      .map(m -> makeTest(m, unsupported));
  }

  @TestFactory
  public Stream<DynamicTest> testWriterUnsupportedRandom()
  {
    final var unsupported = new BSSWriterRandomAccessUnsupported();
    return Arrays.stream(unsupported.getClass().getMethods())
      .filter(m -> isSuitableMethod(m, BSSWriterRandomAccessUnsupported.class))
      .map(m -> makeTest(m, unsupported));
  }

  @TestFactory
  public Stream<DynamicTest> testWriterUnsupportedSequential()
  {
    final var unsupported = new BSSWriterSequentialUnsupported();
    return Arrays.stream(unsupported.getClass().getMethods())
      .filter(m -> isSuitableMethod(m, BSSWriterSequentialUnsupported.class))
      .map(m -> makeTest(m, unsupported));
  }

  private static boolean isSuitableMethod(
    final Method method,
    final Class<?> owner)
  {
    return Objects.equals(method.getDeclaringClass(), owner);
  }

  private static DynamicTest makeTest(
    final Method method,
    final Object unsupported)
  {
    return DynamicTest.dynamicTest(
      String.format("testUnsupportedRandom_%s", method.getName()),
      () -> testMethod(unsupported, method)
    );
  }

  private static void testMethod(
    final Object receiver,
    final Method method)
  {
    if (rejectMethodName(method)) {
      return;
    }

    if (method.getParameterCount() == 2) {
      final var param0 = method.getParameterTypes()[0];
      final var param1 = method.getParameterTypes()[1];

      if (Objects.equals(param0, String.class) && Objects.equals(param1, int.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, "test", Integer.valueOf(23));
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }

      if (Objects.equals(param0, String.class) && Objects.equals(param1, long.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, "test", 23L);
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }

      if (Objects.equals(param0, String.class) && Objects.equals(param1, double.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, "test", 23.0);
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }
    }

    if (method.getParameterCount() == 1) {
      final var param0 = method.getParameterTypes()[0];
      if (Objects.equals(param0, String.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, "test");
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }

      if (Objects.equals(param0, int.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, Integer.valueOf(23));
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }

      if (Objects.equals(param0, long.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, Long.valueOf(23L));
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }

      if (Objects.equals(param0, double.class)) {
        assertThrows(UnsupportedOperationException.class, () -> {
          try {
            LOG.debug("calling {}", method);
            method.invoke(receiver, Double.valueOf(23.0));
          } catch (final UnsupportedOperationException e) {
            throw e;
          } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof UnsupportedOperationException) {
              throw e.getTargetException();
            }
            fail(e);
          } catch (final IllegalAccessException e) {
            fail(e);
          }
        });
        return;
      }
    }

    if (method.getParameterCount() == 0) {
      assertThrows(UnsupportedOperationException.class, () -> {
        try {
          LOG.debug("calling {}", method);
          method.invoke(receiver);
        } catch (final UnsupportedOperationException e) {
          throw e;
        } catch (final InvocationTargetException e) {
          if (e.getTargetException() instanceof UnsupportedOperationException) {
            throw e.getTargetException();
          }
          fail(e);
        } catch (final IllegalAccessException e) {
          fail(e);
        }
      });
    }
  }

  private static boolean rejectMethodName(
    final Method method)
  {
    return List.of("close", "parentReader", "isClosed")
      .stream()
      .anyMatch(name -> method.getName().equals(name));
  }
}

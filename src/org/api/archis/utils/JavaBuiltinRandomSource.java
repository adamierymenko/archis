package org.api.archis.utils;

import java.util.Random;

/**
 * A RandomSource using java.util.Random
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class JavaBuiltinRandomSource implements RandomSource
{
  private Random random;
  private static char[] rclist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

  public JavaBuiltinRandomSource(long seed)
  {
    random = new Random(seed);
  }

  public int randomInteger()
  {
    synchronized(random) {
      return random.nextInt();
    }
  }

  public int randomPositiveInteger()
  {
    synchronized(random) {
      int n = random.nextInt();
      return ((n >= 0) ? n : ((n == Integer.MIN_VALUE) ? Integer.MAX_VALUE : Math.abs(n)));
    }
  }

  public long randomLong()
  {
    synchronized(random) {
      return random.nextLong();
    }
  }

  public long randomPositiveLong()
  {
    synchronized(random) {
      long n = random.nextLong();
      return ((n >= 0L) ? n : ((n == Long.MIN_VALUE) ? Long.MAX_VALUE : Math.abs(n)));
    }
  }

  public char randomChar()
  {
    synchronized(random) {
      return (char)random.nextInt();
    }
  }

  public char randomLetterOrNumber()
  {
    return rclist[randomPositiveInteger() % rclist.length];
  }

  public byte randomByte()
  {
    synchronized(random) {
      return (byte)random.nextInt();
    }
  }

  public short randomShort()
  {
    synchronized(random) {
      return (short)random.nextInt();
    }
  }

  public boolean randomBoolean()
  {
    synchronized(random) {
      return random.nextBoolean();
    }
  }

  public boolean randomEvent(float probability)
  {
    synchronized(random) {
      return (random.nextFloat() <= probability);
    }
  }

  public boolean randomEvent(double probability)
  {
    synchronized(random) {
      return (random.nextDouble() <= probability);
    }
  }

  public double randomDouble()
  {
    synchronized(random) {
      return random.nextDouble();
    }
  }

  public float randomFloat()
  {
    synchronized(random) {
      return random.nextFloat();
    }
  }
}

package org.api.archis.utils;

/**
 * A control random source that is not random; it simply increments the seed
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class NonRandomRandomSource implements RandomSource
{
  private long s;
  private static char[] rclist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

  /**
   * Constructs a new MersenneTwisterRandomSource
   *
   * @param seed Seed value
   */
  public NonRandomRandomSource(long seed)
  {
    s = seed;
  }

  public synchronized int randomInteger()
  {
    return (int)++s;
  }

  public synchronized int randomPositiveInteger()
  {
    return Math.abs((int)++s);
  }

  public synchronized long randomLong()
  {
    return ++s;
  }

  public synchronized long randomPositiveLong()
  {
    return Math.abs(++s);
  }

  public synchronized char randomChar()
  {
    return (char)++s;
  }

  public char randomLetterOrNumber()
  {
    return rclist[randomPositiveInteger() % rclist.length];
  }

  public synchronized byte randomByte()
  {
    return (byte)++s;
  }

  public synchronized short randomShort()
  {
    return (short)++s;
  }

  public synchronized boolean randomBoolean()
  {
    return (((s & 1L) == 0L) ? true : false);
  }

  public boolean randomEvent(float probability)
  {
    return (randomFloat() <= probability);
  }

  public boolean randomEvent(double probability)
  {
    return (randomDouble() <= probability);
  }

  public synchronized double randomDouble()
  {
    return Double.longBitsToDouble(++s);
  }

  public synchronized float randomFloat()
  {
    return Float.intBitsToFloat((int)++s);
  }
}

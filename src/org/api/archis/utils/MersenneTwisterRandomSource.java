package org.api.archis.utils;

import java.util.*;

/**
 * A random source using Mersenne Twister algorithm
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class MersenneTwisterRandomSource implements RandomSource
{
  private MersenneTwister mt;
  private static char[] rclist = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

  /**
   * Constructs a new MersenneTwisterRandomSource
   *
   * @param seed Seed value
   */
  public MersenneTwisterRandomSource(long seed)
  {
    mt = new MersenneTwister(seed);
  }

  public int randomInteger()
  {
    synchronized(mt) {
      return mt.nextInt();
    }
  }

  public int randomPositiveInteger()
  {
    synchronized(mt) {
      int i = mt.nextInt();
      return ((i == -2147483648) ? 2147483647 : Math.abs(i));
    }
  }

  public long randomLong()
  {
    synchronized(mt) {
      return mt.nextLong();
    }
  }

  public long randomPositiveLong()
  {
    synchronized(mt) {
      long i = mt.nextLong();
      return ((i == Long.MIN_VALUE) ? Long.MAX_VALUE : Math.abs(i));
    }
  }

  public char randomChar()
  {
    synchronized(mt) {
      return mt.nextChar();
    }
  }

  public char randomLetterOrNumber()
  {
    return rclist[randomPositiveInteger() % rclist.length];
  }

  public byte randomByte()
  {
    synchronized(mt) {
      return mt.nextByte();
    }
  }

  public short randomShort()
  {
    synchronized(mt) {
      return mt.nextShort();
    }
  }

  public boolean randomBoolean()
  {
    synchronized(mt) {
      return mt.nextBoolean();
    }
  }

  public boolean randomEvent(float probability)
  {
    synchronized(mt) {
      return (mt.nextFloat() <= probability);
    }
  }

  public boolean randomEvent(double probability)
  {
    synchronized(mt) {
      return (mt.nextDouble() <= probability);
    }
  }

  public double randomDouble()
  {
    synchronized(mt) {
      return mt.nextDouble();
    }
  }

  public float randomFloat()
  {
    synchronized(mt) {
      return mt.nextFloat();
    }
  }
}

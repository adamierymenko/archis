package org.api.archis.utils;

/**
 * Source for random events or numbers within Archis
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public interface RandomSource
{
  /**
   * Returns a random positive or negative integer
   *
   * @return Random int
   */
  int randomInteger();

  /**
   * Returns a random positive or zero integer
   *
   * @return Random int &gt;= 0
   */
  int randomPositiveInteger();

  /**
   * Returns a random positive or negative long
   *
   * @return Random long
   */
  long randomLong();

  /**
   * Returns a random positive or zero long
   *
   * @return Random long &gt;= 0
   */
  long randomPositiveLong();

  /**
   * Returns a random char
   *
   * @return Random char
   */
  char randomChar();

  /**
   * Returns a random character out of [abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789]
   *
   * @return Random letter or number
   */
  char randomLetterOrNumber();

  /**
   * Returns a random byte
   *
   * @return Random byte
   */
  byte randomByte();

  /**
   * Returns a random short
   *
   * @return Random short
   */
  short randomShort();

  /**
   * Returns a boolean with a 50/50 chance of being true/false
   *
   * @return Random boolean
   */
  boolean randomBoolean();

  /**
   * Returns a random boolean with a given probability of being true
   *
   * @param probability 0 &lt;= probability &lt= 1
   * @return Random event
   */
  boolean randomEvent(float probability);

  /**
   * Returns a random boolean with a given probability of being true
   *
   * @param probability 0 &lt;= probability &lt= 1
   * @return Random event
   */
  boolean randomEvent(double probability);

  /**
   * Returns a random double from 0.0 to 1.0
   *
   * @return Random double
   */
  double randomDouble();

  /**
   * Returns a random float from 0.0 to 1.0
   *
   * @return Random float
   */
  float randomFloat();
}

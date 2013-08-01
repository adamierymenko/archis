package org.api.archis.utils;

/**
 * Random number source as an IntegerInput
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class RandomIntegerInput implements IntegerInput
{
  private RandomSource source;

  public RandomIntegerInput(RandomSource source)
  {
    this.source = source;
  }

  public int read()
  {
    return source.randomInteger();
  }
}
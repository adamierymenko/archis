package org.api.archis.utils;

/**
 * Returns a single value over and over as an IntegerInput
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class SingleValueIntegerInput implements IntegerInput
{
  private int value;

  /**
   * Construct a new single value IntegerInput
   *
   * @param value Value to output
   */
  public SingleValueIntegerInput(int value)
  {
    this.value = value;
  }

  public int read()
  {
    return value;
  }
}
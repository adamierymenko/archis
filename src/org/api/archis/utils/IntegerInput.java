package org.api.archis.utils;

/**
 * Defines an interface for the capacity to read an integer value from
 * something.
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface IntegerInput
{
  /**
   * Read an integer
   *
   * @return Integer or 0 if there are no more
   */
  int read();
}

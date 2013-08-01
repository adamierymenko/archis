package org.api.archis.utils;

/**
 * An interface for classes capable of accepting real number input
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface RealNumberDataListener
{
  /**
   * Send a data point
   *
   * @param index Index for data point
   * @param dataPoint Data point
   */
  void input(long index,double dataPoint);
}

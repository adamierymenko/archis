package org.api.archis.universe;

import java.util.Map;

import org.api.archis.life.*;

/**
 * An interface for classes capable of processing I/O from lifeforms.
 *
 * @author unascribed
 * @version 1.0
 */

public interface IOHandler
{
  /**
   * Gets a description for a channel this I/O handler is designed to handle or null if none
   *
   * @return Description of channel
   */
  String getChannelDescription(int channel);

  /**
   * Called to evaluate output from a cell with regard to this environmental condition
   *
   * @param l Cell that produced output
   * @param channel Channel of output
   * @param value Value produced
   * @throws DeathException Output caused the death of the cell
   */
  void evaluateOutput(Cell l,int channel,int value)
    throws DeathException;
}
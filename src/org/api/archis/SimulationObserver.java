package org.api.archis;

/**
 * A simulation observer is an object that will be notified when each clock
 * tick cycle completes within the simulation.
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface SimulationObserver
{
  /**
   * Called to notify the simulation observer of a clock tick
   */
  void tick();

  /**
   * Called to notify the simulation observer of a halt
   *
   * @param haltReason Reason for halt
   */
  void halted(String haltReason);
}


package org.api.archis.universe;

import org.api.archis.*;
import org.api.archis.life.Cell;

/**
 * A class enabling cells to be examined every tick
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface Probe
{
  /**
   * Shows Swing or AWT graphical user interface if any
   */
  void showGUI();

  /**
   * Called by universe when probe is added
   *
   * @param universe Universe probe belongs to
   * @param simulation Simulation Simulation probe belongs to
   */
  void init(Universe universe,Simulation simulation);

  /**
   * Called by universe when probe is removed
   */
  void destroy();

  /**
   * Called before each tick
   */
  void preTickNotify();

  /**
   * Called after each tick
   */
  void postTickNotify();

  /**
   * <p>Called each tick for each cell so stats can be detected</p>
   *
   * <p>Note that this can be called by multiple threads concurrently and
   * so anything it does should be properly synchronized.</p>
   *
   * @param cell Cell to scan
   */
  void probeScanCell(Cell cell);

  /**
   * Called when a new cell is created
   *
   * @param parent Parent or null if none
   * @param newCell New cell
   */
  void probeNewCell(Cell parent,Cell newCell);
}

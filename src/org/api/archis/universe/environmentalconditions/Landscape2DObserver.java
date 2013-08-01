package org.api.archis.universe.environmentalconditions;

import org.api.archis.life.Cell;

/**
 * Interface for a class cabable of observing movements on a Landscape2D
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface Landscape2DObserver
{
  /**
   * Called to indicate that the background topography has changed and everything should be redrawn
   */
  void backgroundChanged();

  /**
   * A new cell has been added to the landscape
   *
   * @param x X location of cell
   * @param y Y location of cell
   * @param cell Cell added
   */
  void cellAdded(int x,int y,Cell cell);

  /**
   * A cell has moved from one location to another
   *
   * @param oldX Old X location
   * @param oldY Old Y location
   * @param newX New X location
   * @param newY New Y location
   * @param cell Cell that moved
   */
  void cellMoved(int oldX,int oldY,int newX,int newY,Cell cell);

  /**
   * A cell was removed from the board
   *
   * @param x Previous X location of cell
   * @param y Previous Y location of cell
   */
  void cellRemoved(int x,int y);

  /**
   * Food added to landscape
   *
   * @param x X location of food
   * @param y Y location of food
   */
  void foodAdded(int x,int y);

  /**
   * <p>Food removed from landscape</p>
   *
   * <p>Note that this is *not* called when a cell moves or is added to where
   * food is!</p>
   *
   * @param x Previous X location
   * @param y Previous Y location
   */
  void foodRemoved(int x,int y);

  /**
   * Info block added to landscape
   *
   * @param x New X location
   * @param y New Y location
   */
  void infoAdded(int x,int y);

  /**
   * Info block removed from landscape
   *
   * @param x Previous X location
   * @param y Previous Y location
   */
  void infoRemoved(int x,int y);
}

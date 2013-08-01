package org.api.archis.universe.catastrophes;

import java.util.*;
import java.rmi.RemoteException;

import org.api.archis.*;
import org.api.archis.life.Cell;
import org.api.archis.life.DeathException;
import org.api.archis.universe.*;
import org.api.archis.utils.RandomSource;

/**
 * A universal condition that runs for one clock cycle that kills many cells
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class ExtinctionLevelEvent implements Catastrophe
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Kills many cells in a single tick.";

  private float deathPercentage;
  private boolean destroy;
  private Simulation simulation;

  /**
   * Constructs a new extinction level event with default values of 0.75 and destroy=false
   */
  public ExtinctionLevelEvent()
  {
    deathPercentage = 0.75F;
    destroy = false;
    showGUI();
  }

  /**
   * Constructs a new extinction level event
   *
   * @param deathPercentage Percentage of cells to kill (0.0 &lt; x &lt; 1.0)
   * @param destroy Destroy all energy rather than just kill?
   */
  public ExtinctionLevelEvent(float deathPercentage,boolean destroy)
  {
    this.deathPercentage = deathPercentage;
    this.destroy = destroy;
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.simulation = simulation;
  }

  public void destroy()
  {
  }

  public void showGUI()
  {
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    throw new ConditionExpirationException();
  }

  public void preExecutionNotify(Cell cell)
  {
    if (simulation.randomSource().randomEvent(deathPercentage))
      cell.kill(destroy,"ExtinctionLevelEvent");
  }

  public void deathNotify(Cell deadCell,String reason)
  {
  }

  public boolean newCellNotify(Cell parent,Cell newCell)
  {
    return true;
  }

  public void initCellNotify(Cell cell)
  {
  }
}

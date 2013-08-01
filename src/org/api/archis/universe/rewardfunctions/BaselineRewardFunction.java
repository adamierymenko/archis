package org.api.archis.universe.rewardfunctions;

import java.util.Iterator;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;

/**
 * Maintains a certain level of living cell energy in the universe by
 * rewarding all cells equally.
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class BaselineRewardFunction implements RewardFunction
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Maintains a base level of energy equally distributed to all cells.";

  private long maintainEnergy;
  private Universe universe;
  private Simulation simulation;

  /**
   * Constructs a new baseline reward function
   */
  public BaselineRewardFunction()
  {
    maintainEnergy = 100000000L;
  }

  /**
   * Gets the baseline energy level to maintain
   *
   * @return Baseline to maintain
   */
  public long getMaintainEnergy()
  {
    return maintainEnergy;
  }

  /**
   * Sets the baseline energy level to maintain
   *
   * @param maintainEnergy Baseline to maintain
   */
  public void setMaintainEnergy(long maintainEnergy)
  {
    this.maintainEnergy = maintainEnergy;
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.BaselineRewardFunctionWindow(this,simulation).setVisible(true);
  }

  public String getChannelDescription(int channel)
  {
    return null;
  }

  public boolean newCellNotify(Cell parent, Cell newCell)
  {
    return true;
  }

  public void initCellNotify(Cell cell)
  {
  }

  public void deathNotify(Cell deadCell, String reason)
  {
  }

  public void preExecutionNotify(Cell l)
  {
  }

  public void evaluateOutput(Cell l,int channel,int value)
    throws DeathException
  {
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.universe = universe;
    this.simulation = simulation;
  }

  public void destroy()
  {
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
  }

  public synchronized void postTickNotify()
    throws ConditionExpirationException
  {
    long totalEnergy = universe.totalCellEnergy();
    int p;
    if ((totalEnergy < maintainEnergy)&&((p = universe.population()) > 0)) {
      int energyPerCell = (int)(maintainEnergy - totalEnergy) / p;
      for(Iterator i=universe.populationIterator();i.hasNext();) {
        Cell c = (Cell)i.next();
        if (c.alive())
          c.incEnergy(energyPerCell);
      }
    }
  }
}

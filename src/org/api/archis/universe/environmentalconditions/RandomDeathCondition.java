package org.api.archis.universe.environmentalconditions;

import java.util.Collections;
import java.util.Map;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

/**
 * A condition that randomly kills cells
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class RandomDeathCondition implements EnvironmentalCondition
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Randomly kills cells with a given probability.";

  private Simulation simulation;
  private RandomSource randomSource;
  private float randomDeathLikelihood;
  private boolean destroy;
  private int randomDeathsThisTick;

  /**
   * Constructs a new random death condition
   */
  public RandomDeathCondition()
  {
    randomDeathLikelihood = 0.005F;
    destroy = false;
  }

  /**
   * Gets the current probability of a random death
   *
   * @return 0.0 &lt;= p &lt;= 1.0
   */
  public float getRandomDeathLikelihood()
  {
    return randomDeathLikelihood;
  }

  /**
   * Sets the current probability of a random death
   *
   * @param randomDeathLikelihood 0.0 &lt;= p &lt;= 1.0
   */
  public void setRandomDeathLikelihood(float randomDeathLikelihood)
  {
    this.randomDeathLikelihood = randomDeathLikelihood;
  }

  /**
   * Returns whether or not energy is destroyed on death
   *
   * @return Destroy energy on random death?
   */
  public boolean destroyEnergy()
  {
    return destroy;
  }

  /**
   * Sets whether or not energy is destroyed on death
   *
   * @param destroy Destroy energy on random death?
   */
  public void setDestroyEnergy(boolean destroy)
  {
    this.destroy = destroy;
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.simulation = simulation;
    randomSource = simulation.randomSource();
  }

  public void destroy()
  {
    simulation.removeStatistic("RD1 [RandomDeathCondition] Random Deaths Last Tick");
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.RandomDeathConditionWindow(this,simulation).setVisible(true);
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
    randomDeathsThisTick = 0;
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    simulation.setStatistic("RD1 [RandomDeathCondition] Random Deaths Last Tick",randomDeathsThisTick);
  }

  public void preExecutionNotify(Cell cell)
  {
    if (randomSource.randomEvent(randomDeathLikelihood)) {
      ++randomDeathsThisTick;
      cell.kill(destroy,"RandomDeathCondition");
    }
  }

  public void evaluateOutput(Cell l, int channel, int value)
    throws DeathException
  {
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

  public String getChannelDescription(int channel)
  {
    return null;
  }
}


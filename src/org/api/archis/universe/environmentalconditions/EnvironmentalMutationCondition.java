package org.api.archis.universe.environmentalconditions;

import java.util.Map;
import java.util.Collections;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

/**
 * A condition that causes random mutations to living cells
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class EnvironmentalMutationCondition implements EnvironmentalCondition
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Randomly causes mutations with a given probability.";

  private float mutationLikelihood;
  private int mutationsThisTick;
  private Simulation simulation;
  private RandomSource randomSource;

  /**
   * Constructs a new environmental mutation condition
   */
  public EnvironmentalMutationCondition()
  {
    mutationLikelihood = 0.001F;
  }

  /**
   * Gets the likelihood of a mutation per cell per clock tick
   *
   * @return Likelihood of a mutation between 0.0 and 1.0
   */
  public float getMutationLikelihood()
  {
    return mutationLikelihood;
  }

  /**
   * Sets the likelihood of a mutation per cell per clock tick
   *
   * @param mutationLikelihood Mutation likelihood between 0.0 and 1.0
   */
  public void setMutationLikelihood(float mutationLikelihood)
  {
    this.mutationLikelihood = mutationLikelihood;
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.simulation = simulation;
    this.randomSource = simulation.randomSource();
  }

  public void destroy()
  {
    simulation.removeStatistic("EM1 [EnvironmentalMutationCondition] Mutations Last Tick");
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.EnvironmentalMutationConditionWindow(this,simulation).setVisible(true);
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
    mutationsThisTick = 0;
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    simulation.setStatistic("EM1 [EnvironmentalMutationCondition] Mutations Last Tick",mutationsThisTick);
  }

  public void preExecutionNotify(Cell cell)
  {
    if (randomSource.randomEvent(mutationLikelihood)) {
      ++mutationsThisTick;
      cell.setGenome(cell.genome().pointMutation(randomSource));
    }
  }

  public void evaluateOutput(Cell l, int channel, int value)
    throws DeathException
  {
  }
  public String getChannelDescription(int channel)
  {
    return null;
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

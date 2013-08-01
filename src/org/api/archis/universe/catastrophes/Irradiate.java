package org.api.archis.universe.catastrophes;

import java.util.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.RandomSource;

/**
 * A universal condition that runs for one cycle and causes tons of mutations.
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class Irradiate implements Catastrophe
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Causes many mutations in almost all cells for a single tick.";

  private int maxMutations;
  private float mutationLikelihood;
  private RandomSource randomSource;

  /**
   * Constructs a new irradiate event with a mutation likelihood of 0.75 and maxMutations=5
   */
  public Irradiate()
  {
    mutationLikelihood = 0.75F;
    maxMutations = 5;
    showGUI();
  }

  /**
   * Constructs a new irradiate event
   *
   * @param mutationLikelihood Likelihood of a mutation per organism (0.0 &lt; x &lt;= 1.0)
   * @param maxMutations Maximum number of mutations in each cell
   */
  public Irradiate(float mutationLikelihood,int maxMutations)
  {
    this.mutationLikelihood = mutationLikelihood;
    this.maxMutations = maxMutations;
  }

  public void init(Universe universe,Simulation simulation)
  {
    randomSource = simulation.randomSource();
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
    if (randomSource.randomEvent(mutationLikelihood)) {
      int c = randomSource.randomPositiveInteger() % maxMutations;
      if (c == 0)
        c = 1;
      Genome g = cell.genome();
      for(int i=0;i<c;i++)
        g = g.pointMutation(randomSource);
      cell.setGenome(g);
    }
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

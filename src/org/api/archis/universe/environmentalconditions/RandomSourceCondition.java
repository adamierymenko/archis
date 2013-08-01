package org.api.archis.universe.environmentalconditions;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.api.archis.Simulation;
import org.api.archis.life.Cell;
import org.api.archis.life.DeathException;
import org.api.archis.universe.*;
import org.api.archis.utils.IntegerInput;
import org.api.archis.utils.RandomIntegerInput;
import org.api.archis.utils.RandomSource;

/**
 * An environmental condition that gives all cells a source of random numbers
 * on channel 1.
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class RandomSourceCondition implements EnvironmentalCondition
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Provides cells with a source of random numbers.";

  private Universe universe;
  private RandomIntegerInput rii;

  public void init(Universe universe,Simulation simulation)
  {
    this.universe = universe;
    rii = new RandomIntegerInput(simulation.randomSource());
    universe.assignChannel(1,this);
  }

  public void destroy()
  {
    universe.unassignChannel(1);
  }

  public void showGUI()
  {
    javax.swing.JOptionPane.showMessageDialog(null,"RandomSourceCondition does not have a control panel","No control panel",javax.swing.JOptionPane.INFORMATION_MESSAGE);
  }

  public String getChannelDescription(int channel)
  {
    if (channel == 1)
      return "Random number source for cells";
    return null;
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
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
    l.setInput(1,rii);
  }

  public void evaluateOutput(Cell l, int channel, int value)
    throws DeathException
  {
  }
}

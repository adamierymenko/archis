package org.api.archis.universe.rewardfunctions;

import java.util.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.IntegerHashtable;

/**
 * Distributes a reward on the basis of diversity
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class DiversityRewardFunction implements RewardFunction
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Distributes a finite reward in a manner that encourages diversity.";

  private Universe universe;
  private Simulation simulation;
  private long distributePerTick;
  private IntegerHashtable classificationTable;

  /**
   * Constructs a new diversity reward function
   */
  public DiversityRewardFunction()
  {
    distributePerTick = 1000000L;
    classificationTable = new IntegerHashtable(4096);
  }

  /**
   * Returns the reward to distribute per tick
   *
   * @return Distribute per tick
   */
  public long getDistributePerTick()
  {
    return distributePerTick;
  }

  /**
   * Sets the reward to distribute per tick
   *
   * @param distributePerTick Reward to distribute per tick
   */
  public void setDistributePerTick(long distributePerTick)
  {
    this.distributePerTick = distributePerTick;
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.DiversityRewardFunctionWindow(this,simulation).setVisible(true);
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
    // Classify cell by genome checksum
    int cksum = l.genome().checksum();
    LinkedList list;
    synchronized(classificationTable) {
      list = (LinkedList)classificationTable.get(cksum);
      if (list == null) {
        list = new LinkedList();
        classificationTable.put(cksum,list);
      }
    }
    synchronized(list) {
      list.add(l);
    }
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
    simulation.removeStatistic("DR1 [DiversityRewardFunction] Genome Checksum Classes");
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
  }

  public synchronized void postTickNotify()
    throws ConditionExpirationException
  {
    // Distribute reward to each class; the less cells in a class, the more
    // reward each gets!
    try {
      if (classificationTable.size() > 0) {
        long rewardPerClass = distributePerTick / (long)classificationTable.size();
        if (rewardPerClass > 0L) {
          for (Iterator i = classificationTable.valuesIterator();i.hasNext();) {
            LinkedList l = (LinkedList)i.next();
            if (l != null) {
              if (l.size() > 0) {
                int rewardPerCell = (int)rewardPerClass / l.size();
                if (rewardPerCell > 0) {
                  for (Iterator ci = l.iterator();ci.hasNext();)
                    ((Cell)ci.next()).incEnergy(rewardPerCell);
                }
              }
            }
          }
        }
      }
    } catch (Throwable t) {
      System.out.println("Unexpected exception in DiversityRewardFunction.postTickNotify:");
      t.printStackTrace();
    }

    // Report some stats
    simulation.setStatistic("DR1 [DiversityRewardFunction] Genome Checksum Classes",classificationTable.size());

    // Clear everything to prepare for next round
    classificationTable.clear();
  }
}

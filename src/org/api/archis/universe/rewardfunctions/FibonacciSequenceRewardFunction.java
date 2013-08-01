package org.api.archis.universe.rewardfunctions;

import java.util.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

public class FibonacciSequenceRewardFunction implements RewardFunction
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Rewards cells for outputting fibonacci numbers.";

  // Cell info field for it's current output sequence length
  private static final String CELL_INFO_FSSEQLEN = "fib_sl";

  // Universe and simulation data
  private Universe universe;
  private Simulation simulation;

  // Fibonacci sequence
  private int[] fibonacci;

  // Reward to give out per turn
  private long rewardPerTick;

  // Number of total right answers that have been given
  private long totalRightAnswers;

  // Number of cells that have ever answered
  private long totalScorers;

  // Counter to maintain what tick a score is for
  private int counter;

  // Highest score this run
  private int highScore;

  /**
   * Constructs a new baseline reward function
   */
  public FibonacciSequenceRewardFunction()
  {
    setMaxSequenceLength(64);
    rewardPerTick = 100000000L;
    totalRightAnswers = 0L;
    counter = 0;
    highScore = 0;
  }

  /**
   * Sets the maximum length of the sequence to reward
   *
   * @param maxSequenceLength Maximum sequence length rewarded
   */
  public void setMaxSequenceLength(int maxSequenceLength)
  {
    if (maxSequenceLength < 2)
      maxSequenceLength = 2;
    int[] nf = new int[maxSequenceLength];
    nf[0] = 0;
    nf[1] = 1;
    for(int i=2;i<maxSequenceLength;i++)
      nf[i] = nf[i-1] + nf[i-2];
    fibonacci = nf;
  }

  /**
   * Gets the maximum sequence length rewarded
   *
   * @return Maximum sequence length rewarded
   */
  public int getMaxSequenceLength()
  {
    return fibonacci.length;
  }

  /**
   * Sets reward to distribute to winners per turn
   *
   * @param rewardPerTick Reward per turn
   */
  public void setRewardPerTick(long rewardPerTick)
  {
    this.rewardPerTick = rewardPerTick;
  }

  /**
   * Gets reward to distribute to winners per turn
   *
   * @return Reward per turn
   */
  public long getRewardPerTick()
  {
    return rewardPerTick;
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.FibonacciSequenceRewardFunctionWindow(this,simulation).setVisible(true);
  }

  public String getChannelDescription(int channel)
  {
    if (channel == 7)
      return "Cell fibonacci sequence output";
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
    if (channel == 7) {
      // Get score from cell and add score field if not present
      int[] sl = (int[])l.getMetaInfo(CELL_INFO_FSSEQLEN);
      if (sl == null) {
        sl = new int[2];
        l.setMetaInfo(CELL_INFO_FSSEQLEN,sl);
      }

      // Zero score for each new counter tick
      if (sl[0] != counter)
        sl[1] = 0;
      sl[0] = counter;

      // Increment score and update stats if this is another right answer
      if ((sl[1] < fibonacci.length)&&(sl[1] >= 0)) {
        if (fibonacci[sl[1]] == value) {
          ++sl[1];
          if (sl[1] > highScore)
            highScore = sl[1];
          ++totalRightAnswers;
        } else sl[1] = -1;
      }
    }
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.universe = universe;
    this.simulation = simulation;
    universe.assignChannel(7,this);
  }

  public void destroy()
  {
    universe.unassignChannel(7);
    simulation.removeStatistic("FS1 [FibonacciSequenceRewardFunction] High Score");
    simulation.removeStatistic("FS2 [FibonacciSequenceRewardFunction] Number of High Scorers");
    simulation.removeStatistic("FS3 [FibonacciSequenceRewardFunction] Number of Cells that Answered");
    simulation.removeStatistic("FS4 [FibonacciSequenceRewardFunction] Number of Cells that Answered (total)");
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
    ++counter;
    totalRightAnswers = 0L;
    highScore = 0;
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    if (totalRightAnswers > 0L) {
      int rpc = (int)(rewardPerTick / totalRightAnswers);
      if (rpc < 1)
        rpc = 1;
      int highScorers = 0;
      int scorers = 0;

      for (Iterator i = universe.populationIterator();i.hasNext();) {
        Cell c = (Cell)i.next();
        int[] sl = (int[])c.getMetaInfo(CELL_INFO_FSSEQLEN);
        if (sl != null) {
          if ((sl[0] == counter) && (sl[1] > 0)) {
            c.incEnergy(rpc * sl[1]);
            ++scorers;
            if (sl[1] == highScore)
              ++highScorers;
          }
        }
      }
      totalScorers += (long)scorers;

      simulation.setStatistic("FS1 [FibonacciSequenceRewardFunction] High Score",((scorers == 0) ? 0 : highScore));
      simulation.setStatistic("FS2 [FibonacciSequenceRewardFunction] Number of High Scorers",highScorers);
      simulation.setStatistic("FS3 [FibonacciSequenceRewardFunction] Number of Cells that Answered",scorers);
    } else {
      simulation.setStatistic("FS1 [FibonacciSequenceRewardFunction] High Score",0);
      simulation.setStatistic("FS2 [FibonacciSequenceRewardFunction] Number of High Scorers",0);
      simulation.setStatistic("FS3 [FibonacciSequenceRewardFunction] Number of Cells that Answered",0);
    }
    simulation.setStatistic("FS4 [FibonacciSequenceRewardFunction] Number of Cells that Answered (total)",totalScorers);
  }
}

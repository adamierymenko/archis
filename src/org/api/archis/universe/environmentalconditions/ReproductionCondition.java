package org.api.archis.universe.environmentalconditions;

import java.util.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

/**
 * <p>An environmental condition that permits cells to reproduce using I/O channels</p>
 *
 * <p>Channels:</p>
 * <p>
 * 0 - Output genome to this channel to reproduce, input is current genome size
 * </p>
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class ReproductionCondition implements EnvironmentalCondition
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Provides cells with the ability to reproduce by outputting the genetic information of the child.";

  // Children to be created at postTickNotify() (key is parent, value is
  // EfficientByteBuffer containing genome output so far)
  private HashMap children;

  // Universe we exist within and our simulation
  private Universe universe;
  private Simulation simulation;

  // Min and max genome sizes
  private int minGenomeSize;
  private int maxGenomeSize;

  // Value to divide parent energy by when spawning children (>=2)
  private int parentEnergyDividend;

  /**
   * An IntegerInput that returns a cell's energy and genome size as the
   * first two values.
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class EnergyAndGenomeSizeIntegerInput implements IntegerInput
  {
    private int i;
    private Cell c;

    public EnergyAndGenomeSizeIntegerInput(Cell c)
    {
      this.c = c;
      i = 0;
    }

    public int read()
    {
      switch(i++) {
        case 0:
          return c.energy();
        case 1:
          return c.genome().size();
      }
      return 0;
    }
  }

  /**
   * Class for child buffer structure
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class ChildBuffer
  {
    public int ptr;
    public byte[] buf;
  }

  /**
   * Constructs a new reproduction condition with the given parameters
   */
  public ReproductionCondition()
  {
    children = new HashMap(16384,0.75F);
    minGenomeSize = 8;
    maxGenomeSize = 131072;
    parentEnergyDividend = 2;
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.ReproductionConditionWindow(this,simulation).setVisible(true);
  }

  /**
   * Gets the minimum size of a child genome that will reproduce successfully
   *
   * @return Minimum child size
   */
  public int getMinGenomeSize()
  {
    return minGenomeSize;
  }

  /**
   * Gets the maximum allowable size of a child genome
   *
   * @return Maximum child size
   */
  public int getMaxGenomeSize()
  {
    return maxGenomeSize;
  }

  /**
   * Sets the minimum child genome size
   *
   * @param minGenomeSize New min child genome size
   */
  public void setMinGenomeSize(int minGenomeSize)
  {
    this.minGenomeSize = minGenomeSize;
  }

  /**
   * Sets the maximum child genome size
   *
   * @param maxGenomeSize New max child genome size
   */
  public void setMaxGenomeSize(int maxGenomeSize)
  {
    this.maxGenomeSize = maxGenomeSize;
  }

  /**
   * Sets the division factor for the energy of children (parent energy / nn)
   *
   * @param parentEnergyDividend Division factor &gt;= 2
   * @throws IllegalArgumentException Invalid dividend value
   */
  public void setParentEnergyDividend(int parentEnergyDividend)
    throws IllegalArgumentException
  {
    if (parentEnergyDividend < 2)
      throw new IllegalArgumentException("Parent energy dividend must be >= 2");
    this.parentEnergyDividend = parentEnergyDividend;
  }

  /**
   * Gets the division factor for the energy of children
   *
   * @return Energy dividend
   */
  public int getParentEnergyDividend()
  {
    return parentEnergyDividend;
  }

  public String getChannelDescription(int channel)
  {
    if (channel == 0)
      return "Reproduction output, input provides energy and genome size";
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
    if (l.alive())
      l.setInput(0,new EnergyAndGenomeSizeIntegerInput(l));
  }

  public void evaluateOutput(Cell l,int channel,int value)
    throws DeathException
  {
    if (channel == 0) {
      ChildBuffer cb;
      synchronized(children) {
        cb = (ChildBuffer)children.get(l);
      }
      if (cb == null) {
        cb = new ChildBuffer();
        cb.ptr = 0;
        cb.buf = new byte[l.genome().size() * 2];
        synchronized(children) {
          children.put(l,cb);
        }
      }
      if (cb.ptr < maxGenomeSize) {
        cb.buf[cb.ptr++] = (byte)value;
        if (cb.ptr >= cb.buf.length) {
          byte[] nb = new byte[cb.buf.length*2];
          for(int i=0;i<cb.ptr;i++)
            nb[i] = cb.buf[i];
          cb.buf = nb;
        }
      }
    }
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.universe = universe;
    this.simulation = simulation;
    universe.assignChannel(0,this);
  }

  public void destroy()
  {
    universe.unassignChannel(0);
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    synchronized(children) {
      for(Iterator it=children.entrySet().iterator();it.hasNext();) {
        Map.Entry ent = (Map.Entry)it.next();
        ChildBuffer cb = (ChildBuffer)ent.getValue();

        if (cb.ptr >= minGenomeSize) {
          Cell parent = (Cell)ent.getKey();
          int parentEnergy = parent.energy();
          if (parentEnergy > parentEnergyDividend) {
            int childEnergy = parentEnergy / parentEnergyDividend;
            try {
              parent.decEnergy(childEnergy);
            } catch (DeathException e) {
              // should never happen as childEnergy is always < parent energy
            }
            universe.addCell(parent,new Cell(simulation,universe,parent,childEnergy,parent.genome().createNew(cb.buf,0,cb.ptr).canonicalize()));
          }
        }
      }
      children.clear();
    }
  }
}

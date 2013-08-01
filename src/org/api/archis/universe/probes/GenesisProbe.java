package org.api.archis.universe.probes;

import java.util.*;

import org.api.archis.Simulation;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.LongHashtable;

/**
 * Probe for detecting genesis and backtracing to the original first cell
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class GenesisProbe implements Probe
{
  public static final String PROBE_DESCRIPTION = "Detects and provides backtrace information about random genesis events.";
  private Universe universe;
  private Simulation simulation;
  private long genesisThreshold;
  private boolean genesisDetected;
  private LongHashtable cellsById;

  /**
   * Constructs a new genesis probe with default settings (genesisThreshold==16)
   */
  public GenesisProbe()
  {
    genesisThreshold = 16L;
    genesisDetected = false;
    cellsById = new LongHashtable(131072);
  }

  /**
   * Gets genesis threshold
   *
   * @return Genesis threshold
   */
  public long getGenesisThreshold()
  {
    return genesisThreshold;
  }

  /**
   * Sets generation threshold to count as genesis and halt simulation
   *
   * @param genesisThreshold Genesis threshold
   */
  public void setGenesisThreshold(long genesisThreshold)
  {
    this.genesisThreshold = genesisThreshold;
  }

  /**
   * Returns whether or not genesis has been detected
   *
   * @return Genesis detected?
   */
  public boolean genesisDetected()
  {
    return genesisDetected;
  }

  /**
   * <p>Gets the first cell from which all else came</p>
   *
   * <p>This backtraces the population of cells after genesis to the first
   * cell that begat all others.  If genesis has not yet occurred, null is
   * returned.</p>
   *
   * @return First cell or null if genesis hasn't happened yet
   */
  public Cell getFirstCell()
  {
    if (genesisDetected) {
      // Find a cell with the highest generation count
      Cell c = null;
      long maxgen = 0L;
      for(Iterator i=cellsById.valuesIterator();i.hasNext();) {
        Cell _c = (Cell)i.next();
        if (_c.generation() > maxgen) {
          maxgen = _c.generation();
          c = _c;
        }
      }

      // Backtrace to the original cell
      while (c.generation() > 0L) {
        if (c.parentId() > 0L) {
          Cell parent = (Cell)cellsById.get(c.parentId());
          if (parent == null)
            break;
          else c = parent;
        } else break;
      }

      return c;
    } else return null;
  }

  public void showGUI()
  {
    new org.api.archis.gui.probes.GenesisProbeWindow(this,simulation).setVisible(true);
  }

  public void init(Universe universe, Simulation simulation)
  {
    this.universe = universe;
    this.simulation = simulation;
  }

  public void destroy()
  {
  }

  public void preTickNotify()
  {
  }

  public void postTickNotify()
  {
    if ((universe.maxGenerationThisRun() >= genesisThreshold)&&(!genesisDetected)) {
      genesisDetected = true;
      simulation.halt("Genesis detected: max generation currently alive is "+universe.maxGenerationThisRun());
    }
  }

  public void probeScanCell(Cell cell)
  {
  }

  public void probeNewCell(Cell parent,Cell newCell)
  {
    // Record naturally born cells and their parents; ignore newly created
    // cells when they are first created.
    if ((!genesisDetected)&&(parent != null)) {
      synchronized(cellsById) {
        cellsById.put(parent.id(),parent);
        cellsById.put(newCell.id(),newCell);
      }
    }
  }
}

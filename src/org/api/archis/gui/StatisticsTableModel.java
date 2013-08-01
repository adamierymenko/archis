package org.api.archis.gui;

import java.util.*;
import java.awt.Color;

import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.TableModelListener;

import org.api.archis.*;
import org.api.archis.universe.*;

/**
 * Table model for simulation statistics
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class StatisticsTableModel implements TableModel,SimulationObserver
{
  private static Class stringClass = "string".getClass();
  private Simulation simulation;
  private HashSet tableModelListeners;
  private volatile TreeMap lastStats;
  private volatile Object[][] lastStatsProc;
  private volatile boolean newStats;
  private TableModelEvent tme;
  private int longestName;

  public StatisticsTableModel(Simulation simulation)
  {
    this.simulation = simulation;
    longestName = 16;
    simulation.addObserver(this);
    tableModelListeners = new HashSet(32,0.75F);
    lastStats = new TreeMap();
    lastStatsProc = null;
    newStats = true;
    tme = new TableModelEvent(this);
  }

  public void tick()
  {
    int lastn = lastStats.size();
    synchronized(lastStats) {
      lastStats.clear();
      simulation.getStatistics(lastStats);
    }
    newStats = true;
    if (lastStats.size() != lastn) {
      for (Iterator i = tableModelListeners.iterator(); i.hasNext(); )
        ( (TableModelListener) i.next()).tableChanged(tme);
    } else {
      TableModelEvent e = new TableModelEvent(this,0,lastn,1);
      for(Iterator i=tableModelListeners.iterator();i.hasNext();)
        ((TableModelListener)i.next()).tableChanged(e);
    }
  }

  public void halted(String haltReason)
  {
  }

  public int getRowCount()
  {
    synchronized(lastStats) {
      return lastStats.size();
    }
  }

  public int getColumnCount()
  {
    return 2;
  }

  public String getColumnName(int columnIndex)
  {
    return null;
  }

  public Class getColumnClass(int columnIndex)
  {
    return stringClass;
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public synchronized Object getValueAt(int rowIndex, int columnIndex)
  {
    synchronized(lastStats) {
      if (newStats) {
        int l = lastStats.size();
        if ( (lastStatsProc == null) || (lastStatsProc.length != l))
          lastStatsProc = new Object[l][2];
        int n = 0;
        for (Iterator i = lastStats.entrySet().iterator(); i.hasNext(); ) {
          Map.Entry ent = (Map.Entry) i.next();
          Object k = ent.getKey();
          Object v = ent.getValue();
          String tmp = "";
          lastStatsProc[n][0] = ( (k == null) ? "null" : (tmp = k.toString().substring(4)));
          if (tmp.length() > longestName)
            longestName = tmp.length();
          lastStatsProc[n][1] = ( (v == null) ? "--" : v.toString());
          ++n;
        }
      }
      newStats = false;
    }
    if (rowIndex < lastStatsProc.length)
      return lastStatsProc[rowIndex][columnIndex];
    return "";
  }

  public int getLongestName()
  {
    return longestName;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
  }

  public void addTableModelListener(TableModelListener l)
  {
    tableModelListeners.add(l);
  }

  public void removeTableModelListener(TableModelListener l)
  {
    tableModelListeners.remove(l);
  }
}

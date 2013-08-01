package org.api.archis.gui;

import java.util.*;
import java.awt.Color;
import java.lang.reflect.*;

import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;

import org.api.archis.*;
import org.api.archis.universe.*;

public class ProbeListTableModel implements TableModel
{
  private HashSet tableModelListeners;
  private TreeMap probes;
  private HashMap descriptions;
  private Object[] probesArray;
  private Universe universe;
  private TableModelEvent allChangedEvent;

  public ProbeListTableModel(Universe universe)
  {
    this.universe = universe;
    tableModelListeners = new HashSet(32,0.75F);
    probes = new TreeMap();
    descriptions = new HashMap(64,0.75F);
    probesArray = new Object[0];
    allChangedEvent = new TableModelEvent(this);
  }

  public void addProbe(Class probe)
  {
    String desc = "No description available";
    try {
      Field df = probe.getField("PROBE_DESCRIPTION");
      if (Modifier.isStatic(df.getModifiers())&&Modifier.isPublic(df.getModifiers())) {
        Object tmp = df.get(null);
        desc = tmp.toString();
      }
    } catch (NoSuchFieldException e) {
      desc = "No description available";
    } catch (IllegalAccessException e) {
      desc = "No description available";
    }
    synchronized(probes) {
      String cn = probe.getName();
      int tmp = cn.lastIndexOf('.');
      if (tmp > 0)
        cn = cn.substring(tmp+1);
      probes.put(cn,probe);
      descriptions.put(cn,desc);
      probesArray = probes.keySet().toArray();
      for(Iterator i=tableModelListeners.iterator();i.hasNext();) {
        TableModelListener l = (TableModelListener)i.next();
        l.tableChanged(allChangedEvent);
      }
    }
  }

  public void activateProbe(String name)
    throws IllegalAccessException,InstantiationException
  {
    Class cond = (Class)probes.get(name);
    if (cond != null) {
      Probe nc = (Probe)cond.newInstance();
      universe.addProbe(nc);
      nc.showGUI();
    }
    for(Iterator i=tableModelListeners.iterator();i.hasNext();) {
      TableModelListener l = (TableModelListener)i.next();
      l.tableChanged(allChangedEvent);
    }
  }

  public boolean hasProbe(String name)
  {
    synchronized(probes) {
      return probes.containsKey(name);
    }
  }

  public void inactivateProbe(String name)
  {
    Class probe = (Class)probes.get(name);
    if (probe != null) {
      Set uc = universe.getProbes();
      Object c = null;
      for(Iterator i=uc.iterator();i.hasNext();) {
        Object tmp = i.next();
        if (tmp.getClass().getName().equals(probe.getName())) {
          c = tmp;
          break;
        }
      }
      if (c != null)
        universe.removeProbe((Probe)c);
      for(Iterator i=tableModelListeners.iterator();i.hasNext();) {
        TableModelListener l = (TableModelListener)i.next();
        l.tableChanged(allChangedEvent);
      }
    }
  }

  public int getRowCount()
  {
    return probesArray.length;
  }

  public int getColumnCount()
  {
    return 3;
  }

  public String getColumnName(int columnIndex)
  {
    switch(columnIndex) {
      case 0:
        return "Active";
      case 1:
        return "Name";
      case 2:
        return "Description";
    }
    return "error";
  }

  public Class getColumnClass(int columnIndex)
  {
    if (columnIndex == 0)
      return Boolean.TRUE.getClass();
    return "".getClass();
  }

  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return false;
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    switch(columnIndex) {
      case 0:
        Set uc = universe.getProbes();
        for(Iterator i=uc.iterator();i.hasNext();) {
          Object c = i.next();
          if (c.getClass().getName().endsWith((String)probesArray[rowIndex]))
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
      case 1:
        return probesArray[rowIndex];
      case 2:
        return descriptions.get(probesArray[rowIndex]);
    }
    return "";
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

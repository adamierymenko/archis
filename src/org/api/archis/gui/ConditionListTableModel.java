package org.api.archis.gui;

import java.util.*;
import java.awt.Color;
import java.lang.reflect.*;

import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;

import org.api.archis.*;
import org.api.archis.universe.*;

/**
 * A table model for the tables of conditions in the main simulation dialog
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class ConditionListTableModel implements TableModel
{
  private HashSet tableModelListeners;
  private TreeMap conditions;
  private HashMap descriptions;
  private Object[] conditionsArray;
  private Universe universe;
  private TableModelEvent allChangedEvent;

  public ConditionListTableModel(Universe universe)
  {
    this.universe = universe;
    tableModelListeners = new HashSet(32,0.75F);
    conditions = new TreeMap();
    descriptions = new HashMap(64,0.75F);
    conditionsArray = new Object[0];
    allChangedEvent = new TableModelEvent(this);
  }

  public void addCondition(Class condition)
  {
    String desc = "No description available";
    try {
      Field df = condition.getField("CONDITION_DESCRIPTION");
      if (Modifier.isStatic(df.getModifiers())&&Modifier.isPublic(df.getModifiers())) {
        Object tmp = df.get(null);
        desc = tmp.toString();
      }
    } catch (NoSuchFieldException e) {
      desc = "No description available";
    } catch (IllegalAccessException e) {
      desc = "No description available";
    }
    synchronized(conditions) {
      String cn = condition.getName();
      int tmp = cn.lastIndexOf('.');
      if (tmp > 0)
        cn = cn.substring(tmp+1);
      conditions.put(cn,condition);
      descriptions.put(cn,desc);
      conditionsArray = conditions.keySet().toArray();
      for(Iterator i=tableModelListeners.iterator();i.hasNext();) {
        TableModelListener l = (TableModelListener)i.next();
        l.tableChanged(allChangedEvent);
      }
    }
  }

  public void activateCondition(String name)
    throws IllegalAccessException,InstantiationException
  {
    Class cond = (Class)conditions.get(name);
    if (cond != null) {
      Condition nc = (Condition)cond.newInstance();
      universe.addCondition(nc);
      nc.showGUI();
    }
    for(Iterator i=tableModelListeners.iterator();i.hasNext();) {
      TableModelListener l = (TableModelListener)i.next();
      l.tableChanged(allChangedEvent);
    }
  }

  public boolean hasCondition(String name)
  {
    synchronized(conditions) {
      return conditions.containsKey(name);
    }
  }

  public void inactivateCondition(String name)
  {
    Class condition = (Class)conditions.get(name);
    if (condition != null) {
      Set uc = universe.getConditions();
      Object c = null;
      for(Iterator i=uc.iterator();i.hasNext();) {
        Object tmp = i.next();
        if (tmp.getClass().getName().equals(condition.getName())) {
          c = tmp;
          break;
        }
      }
      if (c != null)
        universe.removeCondition((Condition)c);
      for(Iterator i=tableModelListeners.iterator();i.hasNext();) {
        TableModelListener l = (TableModelListener)i.next();
        l.tableChanged(allChangedEvent);
      }
    }
  }

  public int getRowCount()
  {
    return conditionsArray.length;
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
        Set uc = universe.getConditions();
        for(Iterator i=uc.iterator();i.hasNext();) {
          Object c = i.next();
          if (c.getClass().getName().endsWith((String)conditionsArray[rowIndex]))
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
      case 1:
        return conditionsArray[rowIndex];
      case 2:
        return descriptions.get(conditionsArray[rowIndex]);
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

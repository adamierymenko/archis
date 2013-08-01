package org.api.archis.gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import org.api.archis.*;
import org.api.archis.universe.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChannelAssignmentsWindow extends JFrame
{
  private Simulation simulation;
  private IOHandler[] channels;
  JPanel contentPane;
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel jPanel1 = new JPanel();
  JTable channelsTable = new JTable();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel descriptionLabel = new JLabel();
  JButton jButton1 = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  private static class ChannelsTableModel implements TableModel
  {
    private static final Class stringClass = "".getClass();
    private HashSet tableModelListeners;
    private Universe universe;
    private IOHandler[] channels;

    public ChannelsTableModel(Universe universe)
    {
      this.universe = universe;
      channels = universe.getChannelAssignments();
      tableModelListeners = new HashSet(16,0.75F);
    }

    public int getRowCount()
    {
      return channels.length;
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
      switch(columnIndex) {
        case 0:
          return Integer.toString(rowIndex);
        case 1:
          if (channels[rowIndex] != null) {
            String cn = channels[rowIndex].getClass().getName();
            return cn.substring(cn.lastIndexOf('.') + 1);
          } else return "";
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

  public ChannelAssignmentsWindow(Simulation simulation,Universe universe)
  {
    simulation.newFrameNotify(this);
    this.simulation = simulation;
    channels = universe.getChannelAssignments();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setIconImage(Archis.ICON);
    this.setSize(600,275);
    this.setLocation(180,175);
    this.setTitle("["+simulation.getName()+"] I/O Channel Assignments");
    channelsTable.setModel(new ChannelsTableModel(universe));
    channelsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void jbInit() throws Exception
  {
    contentPane = (JPanel)this.getContentPane();
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    contentPane.setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    descriptionLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    descriptionLabel.setBorder(BorderFactory.createEtchedBorder());
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    descriptionLabel.setText("Select Channel for Description");
    jButton1.setText("Close");
    jButton1.addActionListener(new ChannelAssignmentsWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    channelsTable.setFont(new java.awt.Font("Dialog", 1, 14));
    channelsTable.setAutoCreateColumnsFromModel(true);
    channelsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    channelsTable.setShowVerticalLines(false);
    channelsTable.setCellSelectionEnabled(true);
    channelsTable.addMouseListener(new ChannelAssignmentsWindow_channelsTable_mouseAdapter(this));
    channelsTable.addMouseMotionListener(new ChannelAssignmentsWindow_channelsTable_mouseMotionAdapter(this));
    channelsTable.addKeyListener(new ChannelAssignmentsWindow_channelsTable_keyAdapter(this));
    contentPane.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(channelsTable, null);
    contentPane.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(descriptionLabel,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jButton1,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 2, 5), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void channelsTable_keyReleased(KeyEvent e) {
    if (channelsTable.getSelectedRow() >= 0) {
      if (channels[channelsTable.getSelectedRow()] != null)
        descriptionLabel.setText(channels[channelsTable.getSelectedRow()].getChannelDescription(channelsTable.getSelectedRow()));
      else descriptionLabel.setText("[Unassigned]");
    } else descriptionLabel.setText("Select Channel for Description");
  }

  void channelsTable_mouseDragged(MouseEvent e) {
    if (channelsTable.getSelectedRow() >= 0) {
      if (channels[channelsTable.getSelectedRow()] != null)
        descriptionLabel.setText(channels[channelsTable.getSelectedRow()].getChannelDescription(channelsTable.getSelectedRow()));
      else descriptionLabel.setText("[Unassigned]");
    } else descriptionLabel.setText("Select Channel for Description");
  }

  void channelsTable_mouseReleased(MouseEvent e) {
    if (channelsTable.getSelectedRow() >= 0) {
      if (channels[channelsTable.getSelectedRow()] != null)
        descriptionLabel.setText(channels[channelsTable.getSelectedRow()].getChannelDescription(channelsTable.getSelectedRow()));
      else descriptionLabel.setText("[Unassigned]");
    } else descriptionLabel.setText("Select Channel for Description");
  }
}

class ChannelAssignmentsWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  ChannelAssignmentsWindow adaptee;

  ChannelAssignmentsWindow_jButton1_actionAdapter(ChannelAssignmentsWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class ChannelAssignmentsWindow_channelsTable_keyAdapter extends java.awt.event.KeyAdapter {
  ChannelAssignmentsWindow adaptee;

  ChannelAssignmentsWindow_channelsTable_keyAdapter(ChannelAssignmentsWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.channelsTable_keyReleased(e);
  }
}

class ChannelAssignmentsWindow_channelsTable_mouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
  ChannelAssignmentsWindow adaptee;

  ChannelAssignmentsWindow_channelsTable_mouseMotionAdapter(ChannelAssignmentsWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseDragged(MouseEvent e) {
    adaptee.channelsTable_mouseDragged(e);
  }
}

class ChannelAssignmentsWindow_channelsTable_mouseAdapter extends java.awt.event.MouseAdapter {
  ChannelAssignmentsWindow adaptee;

  ChannelAssignmentsWindow_channelsTable_mouseAdapter(ChannelAssignmentsWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.channelsTable_mouseReleased(e);
  }
}

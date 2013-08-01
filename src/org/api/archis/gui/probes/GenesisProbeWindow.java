package org.api.archis.gui.probes;

import java.awt.*;

import javax.swing.*;

import org.api.archis.*;
import org.api.archis.gui.ExamineGenomeWindow;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.universe.probes.GenesisProbe;

import java.awt.event.*;

public class GenesisProbeWindow extends JFrame implements SimulationObserver
{
  private JPanel contentPane;
  private Simulation simulation;
  private GenesisProbe probe;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel genesisDetectedLabel = new JLabel();
  JButton viewFirstCellButton = new JButton();
  JButton closeButton = new JButton();
  JLabel genesisThresholdLabel = new JLabel();
  JButton changeThresholdButton = new JButton();

  public GenesisProbeWindow(GenesisProbe probe,Simulation simulation)
  {
    this.simulation = simulation;
    this.probe = probe;
    simulation.addObserver(this);
    simulation.newFrameNotify(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(375,175);
    this.setTitle("["+simulation.getName()+"] GenesisProbe");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2)+100,(ss.height/2)-(this.getHeight()/2)+100);
    this.setIconImage(Archis.ICON);
    updateIndicators();
    genesisThresholdLabel.setText(Long.toString(probe.getGenesisThreshold()));
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setForeground(Color.black);
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Genesis Threshold:");
    contentPane.setLayout(gridBagLayout1);
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Genesis Detected:");
    genesisDetectedLabel.setBorder(BorderFactory.createLineBorder(Color.black));
    genesisDetectedLabel.setText("NO");
    viewFirstCellButton.setEnabled(false);
    viewFirstCellButton.setText("View First Cell Genome");
    viewFirstCellButton.addActionListener(new GenesisProbeWindow_viewFirstCellButton_actionAdapter(this));
    closeButton.setText("Close");
    closeButton.addActionListener(new GenesisProbeWindow_closeButton_actionAdapter(this));
    genesisThresholdLabel.setBorder(BorderFactory.createLineBorder(Color.black));
    genesisThresholdLabel.setText("0");
    changeThresholdButton.setFont(new java.awt.Font("Dialog", 0, 12));
    changeThresholdButton.setText("Change...");
    changeThresholdButton.addActionListener(new GenesisProbeWindow_changeThresholdButton_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    contentPane.add(jLabel2,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    contentPane.add(genesisDetectedLabel,   new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    contentPane.add(viewFirstCellButton,     new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(closeButton,     new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
    contentPane.add(genesisThresholdLabel,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(changeThresholdButton,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
  }

  void closeButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void viewFirstCellButton_actionPerformed(ActionEvent e) {
    if (probe.genesisDetected()) {
      Cell c = probe.getFirstCell();
      if (c == null)
        JOptionPane.showMessageDialog(this,"Get First Cell Failed","Error",JOptionPane.ERROR_MESSAGE);
      else new ExamineGenomeWindow(simulation,c,"["+simulation.getName()+"] First Cell in Genesis Event (Cell #"+c.id()+")").setVisible(true);
    }
  }

  void changeThresholdButton_actionPerformed(ActionEvent e) {
    String newThreshold = JOptionPane.showInputDialog(this,"Enter New Genesis Threshold (in generations):","Change Genesis Threshold",JOptionPane.QUESTION_MESSAGE);
    if (newThreshold != null) {
      newThreshold = newThreshold.trim();
      if (newThreshold.length() > 0) {
        try {
          long tmp = Long.parseLong(newThreshold);
          if (tmp <= 0L)
            JOptionPane.showMessageDialog(this,"Value must be a positive integer","Invalid Genesis Threshold",JOptionPane.ERROR_MESSAGE);
          else probe.setGenesisThreshold(Long.parseLong(newThreshold));
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(this,"Value must be a positive integer","Invalid Genesis Threshold",JOptionPane.ERROR_MESSAGE);
        }
        genesisThresholdLabel.setText(Long.toString(probe.getGenesisThreshold()));
      }
    }
  }

  private void updateIndicators()
  {
    if (probe.genesisDetected()) {
      genesisDetectedLabel.setText("YES");
      genesisDetectedLabel.setForeground(Color.green.brighter());
      viewFirstCellButton.setEnabled(true);
    } else {
      genesisDetectedLabel.setText("NO");
      genesisDetectedLabel.setForeground(Color.red.brighter());
      viewFirstCellButton.setEnabled(false);
    }
  }

  public void halted(String reason)
  {
    updateIndicators();
  }

  public void tick()
  {
  }
}

class GenesisProbeWindow_closeButton_actionAdapter implements java.awt.event.ActionListener {
  GenesisProbeWindow adaptee;

  GenesisProbeWindow_closeButton_actionAdapter(GenesisProbeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.closeButton_actionPerformed(e);
  }
}

class GenesisProbeWindow_viewFirstCellButton_actionAdapter implements java.awt.event.ActionListener {
  GenesisProbeWindow adaptee;

  GenesisProbeWindow_viewFirstCellButton_actionAdapter(GenesisProbeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.viewFirstCellButton_actionPerformed(e);
  }
}

class GenesisProbeWindow_changeThresholdButton_actionAdapter implements java.awt.event.ActionListener {
  GenesisProbeWindow adaptee;

  GenesisProbeWindow_changeThresholdButton_actionAdapter(GenesisProbeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.changeThresholdButton_actionPerformed(e);
  }
}

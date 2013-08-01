package org.api.archis.gui.conditions;

import java.awt.*;

import javax.swing.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.universe.rewardfunctions.DiversityRewardFunction;

import java.awt.event.*;

public class DiversityRewardFunctionWindow extends JFrame
{
  private JPanel contentPane;
  private Simulation simulation;
  private DiversityRewardFunction condition;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField rewardTextField = new JTextField();
  JButton jButton1 = new JButton();

  public DiversityRewardFunctionWindow(DiversityRewardFunction condition,Simulation simulation)
  {
    this.simulation = simulation;
    this.condition = condition;
    simulation.newFrameNotify(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(400,125);
    this.setTitle("["+simulation.getName()+"] DiversityRewardFunction");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2)-50,(ss.height/2)-(this.getHeight()/2)-50);
    this.setIconImage(Archis.ICON);
    rewardTextField.setText(Long.toString(condition.getDistributePerTick()));
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setText("Reward to Distribute Per Tick (total):");
    contentPane.setLayout(gridBagLayout1);
    rewardTextField.setText("0");
    rewardTextField.addKeyListener(new DiversityRewardFunctionWindow_rewardTextField_keyAdapter(this));
    jButton1.setToolTipText("");
    jButton1.setText("Close");
    jButton1.addActionListener(new DiversityRewardFunctionWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(rewardTextField,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    contentPane.add(jButton1,   new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  void rewardTextField_keyReleased(KeyEvent e) {
    String v = rewardTextField.getText().trim();
    if (v.length() > 0) {
      try {
        long tmp = Long.parseLong(v);
        if (tmp <= 0L)
          JOptionPane.showMessageDialog(this,"Reward must be a positive integer","Invalid Value",JOptionPane.ERROR_MESSAGE);
        else condition.setDistributePerTick(tmp);
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Reward must be a positive integer","Invalid Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }
}

class DiversityRewardFunctionWindow_rewardTextField_keyAdapter extends java.awt.event.KeyAdapter {
  DiversityRewardFunctionWindow adaptee;

  DiversityRewardFunctionWindow_rewardTextField_keyAdapter(DiversityRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.rewardTextField_keyReleased(e);
  }
}

class DiversityRewardFunctionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  DiversityRewardFunctionWindow adaptee;

  DiversityRewardFunctionWindow_jButton1_actionAdapter(DiversityRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

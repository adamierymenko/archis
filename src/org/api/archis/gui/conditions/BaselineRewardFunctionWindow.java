package org.api.archis.gui.conditions;

import javax.swing.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.universe.rewardfunctions.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Window for managing BaselineRewardFunction
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class BaselineRewardFunctionWindow extends JFrame
{
  private JPanel contentPane;
  private Simulation simulation;
  private BaselineRewardFunction condition;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField baselineEnergyTextField = new JTextField();
  JButton jButton1 = new JButton();

  public BaselineRewardFunctionWindow(BaselineRewardFunction condition,Simulation simulation)
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
    this.setSize(320,150);
    this.setTitle("["+simulation.getName()+"] BaselineRewardFunction");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    baselineEnergyTextField.setText(Long.toString(condition.getMaintainEnergy()));
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Maintain Baseline Energy:");
    contentPane.setLayout(gridBagLayout1);
    baselineEnergyTextField.setText("");
    baselineEnergyTextField.addKeyListener(new BaselineRewardFunctionWindow_baselineEnergyTextField_keyAdapter(this));
    jButton1.setText("Close");
    jButton1.addActionListener(new BaselineRewardFunctionWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(baselineEnergyTextField,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    contentPane.add(jButton1,  new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void baselineEnergyTextField_keyReleased(KeyEvent e) {
    String tmp = baselineEnergyTextField.getText().trim();
    if (tmp.length() > 0) {
      try {
        condition.setMaintainEnergy(Long.parseLong(tmp));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Input must be a positive integer",
                                      "Invalid energy value",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}

class BaselineRewardFunctionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  BaselineRewardFunctionWindow adaptee;

  BaselineRewardFunctionWindow_jButton1_actionAdapter(BaselineRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class BaselineRewardFunctionWindow_baselineEnergyTextField_keyAdapter extends java.awt.event.KeyAdapter {
  BaselineRewardFunctionWindow adaptee;

  BaselineRewardFunctionWindow_baselineEnergyTextField_keyAdapter(BaselineRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.baselineEnergyTextField_keyReleased(e);
  }
}
package org.api.archis.gui.conditions;

import javax.swing.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.universe.rewardfunctions.*;

import java.awt.*;
import java.awt.event.*;

public class FibonacciSequenceRewardFunctionWindow extends JFrame
{
  private JPanel contentPane;
  private Simulation simulation;
  private FibonacciSequenceRewardFunction condition;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField maxSequenceLengthTextField = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField rewardPerTickTextField = new JTextField();
  JButton jButton1 = new JButton();

  public FibonacciSequenceRewardFunctionWindow(FibonacciSequenceRewardFunction condition,Simulation simulation)
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
    this.setSize(400,170);
    this.setTitle("["+simulation.getName()+"] FibonacciSequenceRewardFunction");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    rewardPerTickTextField.setText(Long.toString(condition.getRewardPerTick()));
    maxSequenceLengthTextField.setText(Integer.toString(condition.getMaxSequenceLength()));
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Maximum Sequence Length to Reward:");
    contentPane.setLayout(gridBagLayout1);
    maxSequenceLengthTextField.setText("0");
    maxSequenceLengthTextField.addKeyListener(new FibonacciSequenceRewardFunctionWindow_maxSequenceLengthTextField_keyAdapter(this));
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Reward for Winners per Tick:");
    rewardPerTickTextField.setText("0");
    rewardPerTickTextField.addKeyListener(new FibonacciSequenceRewardFunctionWindow_rewardPerTickTextField_keyAdapter(this));
    jButton1.setText("Close");
    jButton1.addActionListener(new FibonacciSequenceRewardFunctionWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(maxSequenceLengthTextField,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));
    contentPane.add(jLabel2,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 5, 5), 0, 0));
    contentPane.add(rewardPerTickTextField,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 5, 5), 0, 0));
    contentPane.add(jButton1,  new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  void maxSequenceLengthTextField_keyReleased(KeyEvent e) {
    String v = maxSequenceLengthTextField.getText().trim();
    if (v.length() > 0) {
      try {
        condition.setMaxSequenceLength(Integer.parseInt(v));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Invalid integer value","Value must be a positive integer",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void rewardPerTickTextField_keyReleased(KeyEvent e) {
    String v = rewardPerTickTextField.getText().trim();
    if (v.length() > 0) {
      try {
        condition.setRewardPerTick(Long.parseLong(v));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Invalid integer value","Value must be a positive integer",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }
}

class FibonacciSequenceRewardFunctionWindow_maxSequenceLengthTextField_keyAdapter extends java.awt.event.KeyAdapter {
  FibonacciSequenceRewardFunctionWindow adaptee;

  FibonacciSequenceRewardFunctionWindow_maxSequenceLengthTextField_keyAdapter(FibonacciSequenceRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.maxSequenceLengthTextField_keyReleased(e);
  }
}

class FibonacciSequenceRewardFunctionWindow_rewardPerTickTextField_keyAdapter extends java.awt.event.KeyAdapter {
  FibonacciSequenceRewardFunctionWindow adaptee;

  FibonacciSequenceRewardFunctionWindow_rewardPerTickTextField_keyAdapter(FibonacciSequenceRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.rewardPerTickTextField_keyReleased(e);
  }
}

class FibonacciSequenceRewardFunctionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  FibonacciSequenceRewardFunctionWindow adaptee;

  FibonacciSequenceRewardFunctionWindow_jButton1_actionAdapter(FibonacciSequenceRewardFunctionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

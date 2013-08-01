package org.api.archis.gui;

import java.awt.*;

import javax.swing.*;

import java.util.*;

import javax.swing.border.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.*;
import org.api.archis.utils.*;

import java.awt.event.*;

/**
 * Dialog box for new simulations
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class NewSimulationDialog extends JFrame
{
  private JPanel contentPane;
  private java.util.List simulations;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField nameTextField = new JTextField();
  JTextField threadsTextField = new JTextField();
  TitledBorder titledBorder1;
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JLabel jLabel3 = new JLabel();
  JComboBox prngComboBox = new JComboBox();
  JLabel jLabel4 = new JLabel();
  JTextField randomSeedTextField = new JTextField();
  JLabel jLabel5 = new JLabel();

  public NewSimulationDialog(java.util.List simulations)
  {
    this.simulations = simulations;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(450,210);
    this.setIconImage(Archis.ICON);
    this.setLocation(170,170);
    prngComboBox.addItem("Mersenne Twister");
    prngComboBox.addItem("Java Built-in PRNG");
    prngComboBox.addItem("Non-Random Fake Generator");
    prngComboBox.setSelectedItem("Mersenne Twister");
    randomSeedTextField.setText(Long.toString(System.currentTimeMillis()));
  }
  private void jbInit() throws Exception
  {
    contentPane = (JPanel)this.getContentPane();
    titledBorder1 = new TitledBorder("");
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Number of Execution Threads:");
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setTitle("New Simulation");
    contentPane.setLayout(gridBagLayout1);
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Simulation Name:");
    nameTextField.setText("NewSimulation");
    threadsTextField.setText("1");
    jButton1.setText("Cancel");
    jButton1.addActionListener(new NewSimulationDialog_jButton1_actionAdapter(this));
    jButton2.setText("OK");
    jButton2.addActionListener(new NewSimulationDialog_jButton2_actionAdapter(this));
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel3.setText("Pseudo-Random Number Generator:");
    jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel4.setText("Random Seed:");
    randomSeedTextField.setText("0");
    jLabel5.setFont(new java.awt.Font("Dialog", 0, 11));
    jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel5.setText("(Suggested Value Derived from Clock)");
    contentPane.add(jLabel1,        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(jLabel2,          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(nameTextField,          new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));
    contentPane.add(threadsTextField,          new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));
    contentPane.add(jPanel1,        new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jButton2, null);
    jPanel1.add(jButton1, null);
    contentPane.add(jLabel3,          new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(prngComboBox,         new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));
    contentPane.add(jLabel4,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(randomSeedTextField,    new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));
    contentPane.add(jLabel5,   new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
  }

  void jButton2_actionPerformed(ActionEvent e) {
    // OK
    try {
      int threads = Integer.parseInt(threadsTextField.getText().trim());
      long seed = Long.parseLong(randomSeedTextField.getText().trim());
      RandomSource prng = null;
      Object prngSelected = prngComboBox.getSelectedItem();
      if ("Mersenne Twister".equals(prngSelected)) {
        prng = new MersenneTwisterRandomSource(seed);
      } else if ("Java Built-in PRNG".equals(prngSelected)) {
        prng = new JavaBuiltinRandomSource(seed);
      } else if ("Non-Random Fake Generator".equals(prngSelected)) {
        prng = new NonRandomRandomSource(seed);
      }
      if (prng == null)
        throw new RuntimeException("Internal error: bad selected PRNG!");
      Simulation simulation = new Simulation(threads,nameTextField.getText().trim(),prng);
      simulation.universe().addCondition(new ReproductionCondition());
      simulation.universe().addCondition(new RandomSourceCondition());
      simulation.universe().addCondition(new EnvironmentalMutationCondition());
      SimulationWindow sw = new SimulationWindow(simulation);
      sw.setVisible(true);
      simulations.add(sw);
      this.setVisible(false);
      this.dispose();
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Integer fields must be integer values","Bad value",JOptionPane.ERROR_MESSAGE);
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {
    // Cancel
    this.setVisible(false);
    this.dispose();
  }
}

class NewSimulationDialog_jButton2_actionAdapter implements java.awt.event.ActionListener {
  NewSimulationDialog adaptee;

  NewSimulationDialog_jButton2_actionAdapter(NewSimulationDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class NewSimulationDialog_jButton1_actionAdapter implements java.awt.event.ActionListener {
  NewSimulationDialog adaptee;

  NewSimulationDialog_jButton1_actionAdapter(NewSimulationDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

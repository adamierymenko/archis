package org.api.archis.gui.conditions;

/**
 * Control panel for EnvironmentalMutationCondition
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.EnvironmentalMutationCondition;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.awt.event.*;

public class EnvironmentalMutationConditionWindow extends JFrame
{
  private JPanel contentPane;
  private EnvironmentalMutationCondition condition;
  private NumberFormat vFormat;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JSlider mutationLikelihoodSlider = new JSlider();
  JLabel mutationLikelihoodLabel = new JLabel();
  JButton jButton1 = new JButton();

  public EnvironmentalMutationConditionWindow(EnvironmentalMutationCondition condition,Simulation simulation)
  {
    vFormat = DecimalFormat.getNumberInstance();
    vFormat.setMaximumFractionDigits(4);
    vFormat.setMinimumFractionDigits(4);
    simulation.newFrameNotify(this);
    this.condition = condition;
    try {
      jbInit();
    } catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(480,120);
    this.setTitle("["+simulation.getName()+"] EnvironmentalMutationCondition");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    updateDisplays(true);
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setText("Mutation Probability:");
    contentPane.setLayout(gridBagLayout1);
    mutationLikelihoodSlider.setMaximum(100);
    mutationLikelihoodSlider.addChangeListener(new EnvironmentalMutationConditionWindow_mutationLikelihoodSlider_changeAdapter(this));
    mutationLikelihoodLabel.setText("0.0000");
    jButton1.setText("Close");
    jButton1.addActionListener(new EnvironmentalMutationConditionWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
    contentPane.add(mutationLikelihoodSlider,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(mutationLikelihoodLabel,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    contentPane.add(jButton1,   new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  private void updateDisplays(boolean updateSliders)
  {
    if (updateSliders)
      mutationLikelihoodSlider.setValue(Math.round(condition.getMutationLikelihood() * 10000.0F));
    mutationLikelihoodLabel.setText(vFormat.format((double)condition.getMutationLikelihood()));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void mutationLikelihoodSlider_stateChanged(ChangeEvent e) {
    condition.setMutationLikelihood((float)mutationLikelihoodSlider.getValue() / 10000.0F);
    updateDisplays(false);
  }
}

class EnvironmentalMutationConditionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  EnvironmentalMutationConditionWindow adaptee;

  EnvironmentalMutationConditionWindow_jButton1_actionAdapter(EnvironmentalMutationConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class EnvironmentalMutationConditionWindow_mutationLikelihoodSlider_changeAdapter implements javax.swing.event.ChangeListener {
  EnvironmentalMutationConditionWindow adaptee;

  EnvironmentalMutationConditionWindow_mutationLikelihoodSlider_changeAdapter(EnvironmentalMutationConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void stateChanged(ChangeEvent e) {
    adaptee.mutationLikelihoodSlider_stateChanged(e);
  }
}

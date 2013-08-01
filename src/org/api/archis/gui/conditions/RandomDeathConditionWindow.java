package org.api.archis.gui.conditions;

/**
 * Control panel for RandomDeathCondition
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
import org.api.archis.universe.environmentalconditions.RandomDeathCondition;

import java.awt.event.*;

public class RandomDeathConditionWindow extends JFrame
{
  private JPanel contentPane;
  private RandomDeathCondition condition;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JSlider randomDeathLikelihoodSlider = new JSlider();
  JLabel randomDeathLikelihoodLabel = new JLabel();
  JButton jButton1 = new JButton();

  public RandomDeathConditionWindow(RandomDeathCondition condition,Simulation simulation)
  {
    simulation.newFrameNotify(this);
    this.condition = condition;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(480,120);
    this.setTitle("["+simulation.getName()+"] RandomDeathCondition");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    updateDisplays(true);
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setText("Random Death Likelihood:");
    contentPane.setLayout(gridBagLayout1);
    randomDeathLikelihoodSlider.setMaximum(1000);
    randomDeathLikelihoodSlider.addChangeListener(new RandomDeathConditionWindow_randomDeathLikelihoodSlider_changeAdapter(this));
    randomDeathLikelihoodLabel.setText("0.000");
    jButton1.setText("Close");
    jButton1.addActionListener(new RandomDeathConditionWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
    contentPane.add(randomDeathLikelihoodSlider,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(randomDeathLikelihoodLabel,    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    contentPane.add(jButton1,     new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
  }

  private void updateDisplays(boolean updateSliders)
  {
    if (updateSliders)
      randomDeathLikelihoodSlider.setValue(Math.round(condition.getRandomDeathLikelihood() * 1000.0F));
    String tmp = Float.toString(condition.getRandomDeathLikelihood());
    if (tmp.length() == 3)
      tmp = tmp.concat("00");
    else if (tmp.length() == 4)
      tmp = tmp.concat("0");
    else if (tmp.length() > 5)
      tmp = tmp.substring(0,5);
    randomDeathLikelihoodLabel.setText(tmp);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void randomDeathLikelihoodSlider_stateChanged(ChangeEvent e) {
    condition.setRandomDeathLikelihood((float)randomDeathLikelihoodSlider.getValue() / 1000.0F);
    updateDisplays(false);
  }
}

class RandomDeathConditionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  RandomDeathConditionWindow adaptee;

  RandomDeathConditionWindow_jButton1_actionAdapter(RandomDeathConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class RandomDeathConditionWindow_randomDeathLikelihoodSlider_changeAdapter implements javax.swing.event.ChangeListener {
  RandomDeathConditionWindow adaptee;

  RandomDeathConditionWindow_randomDeathLikelihoodSlider_changeAdapter(RandomDeathConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void stateChanged(ChangeEvent e) {
    adaptee.randomDeathLikelihoodSlider_stateChanged(e);
  }
}

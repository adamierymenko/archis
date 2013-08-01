package org.api.archis.gui.conditions;

/**
 * Control panel for reproduction condition
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.*;

import java.awt.event.*;
import java.text.*;

public class ReproductionConditionWindow extends JFrame
{
  private JPanel contentPane;
  private ReproductionCondition condition;
  private NumberFormat decimalFormat;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JTextField minGenomeSizeTextField = new JTextField();
  JTextField maxGenomeSizeTextField = new JTextField();
  JPanel jPanel3 = new JPanel();
  JButton jButton1 = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();
  JPanel jPanel4 = new JPanel();
  JLabel jLabel7 = new JLabel();
  JTextField energyDividendTextField = new JTextField();
  BorderLayout borderLayout1 = new BorderLayout();

  public ReproductionConditionWindow(ReproductionCondition condition,Simulation simulation)
  {
    simulation.newFrameNotify(this);
    this.condition = condition;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(480,200);
    this.setTitle("["+simulation.getName()+"] ReproductionCondition");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    decimalFormat = DecimalFormat.getPercentInstance();
    decimalFormat.setMinimumFractionDigits(4);
    decimalFormat.setMaximumFractionDigits(4);
    updateDisplays(true);
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(gridBagLayout1);
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel2.setLayout(gridBagLayout3);
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Minimum Genome Size:");
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Maximum Genome Size:");
    minGenomeSizeTextField.setText("0");
    minGenomeSizeTextField.addFocusListener(new ReproductionConditionWindow_minGenomeSizeTextField_focusAdapter(this));
    minGenomeSizeTextField.addKeyListener(new ReproductionConditionWindow_minGenomeSizeTextField_keyAdapter(this));
    maxGenomeSizeTextField.setText("0");
    maxGenomeSizeTextField.addFocusListener(new ReproductionConditionWindow_maxGenomeSizeTextField_focusAdapter(this));
    maxGenomeSizeTextField.addKeyListener(new ReproductionConditionWindow_maxGenomeSizeTextField_keyAdapter(this));
    jPanel3.setLayout(flowLayout1);
    jButton1.setText("Close");
    jButton1.addActionListener(new ReproductionConditionWindow_jButton1_actionAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    jLabel7.setText("Child Energy is Parent Energy / ");
    energyDividendTextField.setText("2");
    energyDividendTextField.addKeyListener(new ReproductionConditionWindow_energyDividendTextField_keyAdapter(this));
    jPanel4.setLayout(borderLayout1);
    contentPane.add(jPanel2,       new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    jPanel2.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(jLabel2,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    jPanel2.add(minGenomeSizeTextField,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(maxGenomeSizeTextField,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    jPanel2.add(jPanel4,  new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    jPanel4.add(jLabel7,  BorderLayout.WEST);
    jPanel4.add(energyDividendTextField,  BorderLayout.CENTER);
    contentPane.add(jPanel3,    new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    jPanel3.add(jButton1, null);
  }

  private synchronized void updateDisplays(boolean updateSliders)
  {
    maxGenomeSizeTextField.setText(Integer.toString(condition.getMaxGenomeSize()));
    minGenomeSizeTextField.setText(Integer.toString(condition.getMinGenomeSize()));
    energyDividendTextField.setText(Integer.toString(condition.getParentEnergyDividend()));
  }

  void minGenomeSizeTextField_keyReleased(KeyEvent e) {
    boolean nonempty = true;
    try {
      int mgs = (nonempty = (minGenomeSizeTextField.getText().trim().length() > 0)) ? Integer.parseInt(minGenomeSizeTextField.getText().trim()) : 0;
      if (mgs < 0)
        JOptionPane.showMessageDialog(this,"Minimum genome size must be > 0","Invalid Minimum Genome Size",JOptionPane.ERROR_MESSAGE);
      else if (nonempty)
        condition.setMinGenomeSize(mgs);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Numeric fields must contain only numbers and be within integer range","Invalid Numeric Entry",JOptionPane.ERROR_MESSAGE);
    }
    if (nonempty)
      updateDisplays(false);
  }

  void minGenomeSizeTextField_focusLost(FocusEvent e) {
    updateDisplays(false);
  }

  void maxGenomeSizeTextField_keyReleased(KeyEvent e) {
    boolean nonempty = true;
    try {
      int mgs = (nonempty = (maxGenomeSizeTextField.getText().trim().length() > 0)) ? Integer.parseInt(maxGenomeSizeTextField.getText().trim()) : 0;
      if (mgs < 1)
        JOptionPane.showMessageDialog(this,"Maximum genome size must be > 1","Invalid Maximum Genome Size",JOptionPane.ERROR_MESSAGE);
      else if (nonempty)
        condition.setMaxGenomeSize(mgs);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Numeric fields must contain only numbers and be within integer range","Invalid Numeric Entry",JOptionPane.ERROR_MESSAGE);
    }
    if (nonempty)
      updateDisplays(false);
  }

  void maxGenomeSizeTextField_focusLost(FocusEvent e) {
    updateDisplays(false);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void energyDividendTextField_keyReleased(KeyEvent e) {
    boolean nonempty = true;
    try {
      int mgs = (nonempty = (energyDividendTextField.getText().trim().length() > 0)) ? Integer.parseInt(energyDividendTextField.getText().trim()) : 0;
      if (mgs < 2)
        JOptionPane.showMessageDialog(this,"Energy dividend must be >= 2","Invalid Energy Dividend",JOptionPane.ERROR_MESSAGE);
      else if (nonempty)
        condition.setParentEnergyDividend(mgs);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Numeric fields must contain only numbers and be within integer range","Invalid Numeric Entry",JOptionPane.ERROR_MESSAGE);
    }
    if (nonempty)
      updateDisplays(false);
  }
}

class ReproductionConditionWindow_minGenomeSizeTextField_keyAdapter extends java.awt.event.KeyAdapter {
  ReproductionConditionWindow adaptee;

  ReproductionConditionWindow_minGenomeSizeTextField_keyAdapter(ReproductionConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.minGenomeSizeTextField_keyReleased(e);
  }
}

class ReproductionConditionWindow_minGenomeSizeTextField_focusAdapter extends java.awt.event.FocusAdapter {
  ReproductionConditionWindow adaptee;

  ReproductionConditionWindow_minGenomeSizeTextField_focusAdapter(ReproductionConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.minGenomeSizeTextField_focusLost(e);
  }
}

class ReproductionConditionWindow_maxGenomeSizeTextField_keyAdapter extends java.awt.event.KeyAdapter {
  ReproductionConditionWindow adaptee;

  ReproductionConditionWindow_maxGenomeSizeTextField_keyAdapter(ReproductionConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.maxGenomeSizeTextField_keyReleased(e);
  }
}

class ReproductionConditionWindow_maxGenomeSizeTextField_focusAdapter extends java.awt.event.FocusAdapter {
  ReproductionConditionWindow adaptee;

  ReproductionConditionWindow_maxGenomeSizeTextField_focusAdapter(ReproductionConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void focusLost(FocusEvent e) {
    adaptee.maxGenomeSizeTextField_focusLost(e);
  }
}

class ReproductionConditionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  ReproductionConditionWindow adaptee;

  ReproductionConditionWindow_jButton1_actionAdapter(ReproductionConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class ReproductionConditionWindow_energyDividendTextField_keyAdapter extends java.awt.event.KeyAdapter {
  ReproductionConditionWindow adaptee;

  ReproductionConditionWindow_energyDividendTextField_keyAdapter(ReproductionConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.energyDividendTextField_keyReleased(e);
  }
}

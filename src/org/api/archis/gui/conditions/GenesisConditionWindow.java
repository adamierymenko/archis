package org.api.archis.gui.conditions;

/**
 * Control panel for GenesisCondition
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.GenesisCondition;

import java.awt.event.*;

public class GenesisConditionWindow extends JFrame
{
  private JPanel contentPane;
  private GenesisCondition condition;
  private Map genomeTypes;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JButton jButton1 = new JButton();
  JTextField createPerTickTextField = new JTextField();
  JTextField newCellEnergyTextField = new JTextField();
  JTextField meanGenomeSizeTextField = new JTextField();
  JTextField genomeSizeDeviationTextField = new JTextField();
  JLabel jLabel5 = new JLabel();
  JComboBox genomeTypeComboBox = new JComboBox();

  public GenesisConditionWindow(GenesisCondition condition,Simulation simulation)
  {
    simulation.newFrameNotify(this);
    genomeTypes = GenomeFactory.getGenomeTypes();
    for(Iterator i=genomeTypes.keySet().iterator();i.hasNext();)
      genomeTypeComboBox.addItem(i.next());
    int selType = condition.getGenomeType();
    for(Iterator i=genomeTypes.entrySet().iterator();i.hasNext();) {
      Map.Entry ent = (Map.Entry)i.next();
      if (((Integer)ent.getValue()).intValue() == selType)
        genomeTypeComboBox.setSelectedItem(ent.getKey());
    }
    this.condition = condition;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(480,200);
    this.setTitle("["+simulation.getName()+"] GenesisCondition");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    newCellEnergyTextField.setText(Integer.toString(condition.getNewCellEnergy()));
    meanGenomeSizeTextField.setText(Integer.toString(condition.getGenomeMeanSize()));
    genomeSizeDeviationTextField.setText(Integer.toString(condition.getGenomeSizeDeviation()));
    createPerTickTextField.setText(Integer.toString(condition.getCreateCellsPerTick()));
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Cells to Create Per Tick:");
    contentPane.setLayout(gridBagLayout1);
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Energy for New Cells:");
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel3.setText("Mean Cell Genome Size:");
    jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel4.setText("Genome Size Deviation (+/- mean):");
    jButton1.setText("Close");
    jButton1.addActionListener(new GenesisConditionWindow_jButton1_actionAdapter(this));
    createPerTickTextField.setText("0");
    createPerTickTextField.addKeyListener(new GenesisConditionWindow_createPerTickTextField_keyAdapter(this));
    createPerTickTextField.addKeyListener(new GenesisConditionWindow_createPerTickTextField_keyAdapter(this));
    newCellEnergyTextField.setText("0");
    newCellEnergyTextField.addKeyListener(new GenesisConditionWindow_newCellEnergyTextField_keyAdapter(this));
    meanGenomeSizeTextField.setText("0");
    meanGenomeSizeTextField.addKeyListener(new GenesisConditionWindow_meanGenomeSizeTextField_keyAdapter(this));
    genomeSizeDeviationTextField.setText("0");
    genomeSizeDeviationTextField.addKeyListener(new GenesisConditionWindow_genomeSizeDeviationTextField_keyAdapter(this));
    jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel5.setText("Genome Type:");
    genomeTypeComboBox.addItemListener(new GenesisConditionWindow_genomeTypeComboBox_itemAdapter(this));
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    contentPane.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
    contentPane.add(jLabel2,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
    contentPane.add(jLabel3,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
    contentPane.add(jLabel4,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
    contentPane.add(jButton1,     new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(createPerTickTextField,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(newCellEnergyTextField,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(meanGenomeSizeTextField,     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(genomeSizeDeviationTextField,     new GridBagConstraints(1, 3, 1, 2, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    contentPane.add(jLabel5,     new GridBagConstraints(0, 4, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));
    contentPane.add(genomeTypeComboBox,     new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
  }

  void newCellEnergyTextField_keyReleased(KeyEvent e) {
    String tmp = newCellEnergyTextField.getText().trim();
    if (tmp.length() > 0) {
      try {
        int tmp2 = Integer.parseInt(tmp);
        if (tmp2 < 0)
          JOptionPane.showMessageDialog(this,"Must be a positive integer","Bad Value",JOptionPane.ERROR_MESSAGE);
        else {
          condition.setNewCellEnergy(Integer.parseInt(tmp));
          createPerTickTextField.setText(Integer.toString(condition.getNewCellEnergy()));
        }
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Must be a positive integer","Bad Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void meanGenomeSizeTextField_keyReleased(KeyEvent e) {
    String tmp = meanGenomeSizeTextField.getText().trim();
    if (tmp.length() > 0) {
      try {
        int tmp2 = Integer.parseInt(tmp);
        if (tmp2 < 0)
          JOptionPane.showMessageDialog(this,"Must be a positive integer","Bad Value",JOptionPane.ERROR_MESSAGE);
        else {
          condition.setGenomeMeanSize(Integer.parseInt(tmp));
          createPerTickTextField.setText(Integer.toString(condition.getGenomeMeanSize()));
        }
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Must be a positive integer","Bad Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void genomeSizeDeviationTextField_keyReleased(KeyEvent e) {
    String tmp = genomeSizeDeviationTextField.getText().trim();
    if (tmp.length() > 0) {
      try {
        int tmp2 = Integer.parseInt(tmp);
        if (tmp2 < 0)
          JOptionPane.showMessageDialog(this,"Must be a positive integer","Bad Value",JOptionPane.ERROR_MESSAGE);
        else {
          condition.setGenomeSizeDeviation(Integer.parseInt(tmp));
          genomeSizeDeviationTextField.setText(Integer.toString(condition.getGenomeSizeDeviation()));
        }
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Must be a positive integer","Bad Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void genomeTypeComboBox_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == e.SELECTED) {
      Object t = genomeTypes.get(e.getItem());
      if (t != null)
        condition.setGenomeType(((Integer)t).intValue());
      int selType = condition.getGenomeType();
      for (Iterator i = genomeTypes.entrySet().iterator();i.hasNext();) {
        Map.Entry ent = (Map.Entry)i.next();
        if (((Integer)ent.getValue()).intValue() == selType)
          genomeTypeComboBox.setSelectedItem(ent.getKey());
      }
    }
  }

  void createPerTickTextField_keyReleased(KeyEvent e) {
    String tmp = createPerTickTextField.getText().trim();
    if (tmp.length() > 0) {
      try {
        condition.setCreateCellsPerTick(Integer.parseInt(tmp));
        createPerTickTextField.setText(Integer.toString(condition.getCreateCellsPerTick()));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Must be an integer","Bad Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}

class GenesisConditionWindow_newCellEnergyTextField_keyAdapter extends java.awt.event.KeyAdapter {
  GenesisConditionWindow adaptee;

  GenesisConditionWindow_newCellEnergyTextField_keyAdapter(GenesisConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.newCellEnergyTextField_keyReleased(e);
  }
}

class GenesisConditionWindow_meanGenomeSizeTextField_keyAdapter extends java.awt.event.KeyAdapter {
  GenesisConditionWindow adaptee;

  GenesisConditionWindow_meanGenomeSizeTextField_keyAdapter(GenesisConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.meanGenomeSizeTextField_keyReleased(e);
  }
}

class GenesisConditionWindow_genomeSizeDeviationTextField_keyAdapter extends java.awt.event.KeyAdapter {
  GenesisConditionWindow adaptee;

  GenesisConditionWindow_genomeSizeDeviationTextField_keyAdapter(GenesisConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.genomeSizeDeviationTextField_keyReleased(e);
  }
}

class GenesisConditionWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  GenesisConditionWindow adaptee;

  GenesisConditionWindow_jButton1_actionAdapter(GenesisConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class GenesisConditionWindow_genomeTypeComboBox_itemAdapter implements java.awt.event.ItemListener {
  GenesisConditionWindow adaptee;

  GenesisConditionWindow_genomeTypeComboBox_itemAdapter(GenesisConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void itemStateChanged(ItemEvent e) {
    adaptee.genomeTypeComboBox_itemStateChanged(e);
  }
}

class GenesisConditionWindow_createPerTickTextField_keyAdapter extends java.awt.event.KeyAdapter {
  GenesisConditionWindow adaptee;

  GenesisConditionWindow_createPerTickTextField_keyAdapter(GenesisConditionWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.createPerTickTextField_keyReleased(e);
  }
}

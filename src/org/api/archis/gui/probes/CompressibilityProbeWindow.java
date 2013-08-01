package org.api.archis.gui.probes;

import javax.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

import org.api.archis.*;
import org.api.archis.gui.*;
import org.api.archis.universe.*;
import org.api.archis.universe.probes.*;

public class CompressibilityProbeWindow extends JFrame
{
  private JPanel contentPane;
  private Simulation simulation;
  private CompressibilityProbe probe;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  HorizontalLineGraph icGraph = new HorizontalLineGraph(16384,16384,Color.green,Color.black);
  JSlider icGraphSlider = new JSlider();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField dataFileTextField = new JTextField();
  JButton browseButton = new JButton();

  public CompressibilityProbeWindow(CompressibilityProbe probe,Simulation simulation)
  {
    this.simulation = simulation;
    this.probe = probe;
    simulation.newFrameNotify(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(400,320);
    this.setTitle("["+simulation.getName()+"] CompressibilityProbe");
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    probe.addDataListener(icGraph);
    icGraph.setAutoScale(false);
    icGraph.setFloor(0.0);
    icGraph.setCeiling(1.0);
    icGraphSlider.setPaintLabels(false);
    icGraphSlider.setPaintTicks(false);
    icGraphSlider.setPaintTrack(true);
    icGraph.setScrollBar(icGraphSlider);
    File outf = probe.getOutputFile();
    if (outf != null)
      dataFileTextField.setText(outf.getAbsolutePath());
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    titledBorder1 = new TitledBorder("");
    titledBorder2 = new TitledBorder("");
    contentPane.setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    jButton1.setText("Close");
    jButton1.addActionListener(new CompressibilityProbeWindow_jButton1_actionAdapter(this));
    jPanel1.setBorder(titledBorder1);
    jPanel1.setLayout(gridBagLayout2);
    titledBorder1.setTitle("Entropy Measured by Compressibility");
    jPanel2.setBorder(titledBorder2);
    jPanel2.setLayout(gridBagLayout3);
    titledBorder2.setTitle("Output Data to File");
    jLabel1.setText("Output To:");
    dataFileTextField.setEditable(false);
    dataFileTextField.setText("");
    browseButton.setFont(new java.awt.Font("Dialog", 0, 12));
    browseButton.setText("Browse");
    browseButton.addActionListener(new CompressibilityProbeWindow_browseButton_actionAdapter(this));
    icGraphSlider.setPaintLabels(false);
    icGraphSlider.setPaintTicks(false);
    contentPane.add(jPanel1,    new GridBagConstraints(0, 0, 1, 1, 1.0, 2.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    jPanel1.add(icGraph,    new GridBagConstraints(0, 0, 1, 1, 1.0, 2.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(icGraphSlider,  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
    contentPane.add(jPanel2,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    jPanel2.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(dataFileTextField,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
    jPanel2.add(browseButton,    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(jButton1,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 5, 5, 5), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  void browseButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle("Select Output File");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File out = chooser.getSelectedFile();
      if (out == null)
        JOptionPane.showMessageDialog(this,"No output file selected","No file selected",JOptionPane.ERROR_MESSAGE);
      boolean ok = true;
      if (out.exists())
        ok = (JOptionPane.showConfirmDialog(this,"File "+out.getName()+" already exists. Overwrite?","Overwrite existing file",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION);
      try {
        probe.setOutput(out);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this,"Error opening output file: "+ex.getMessage(),"Error opening file",JOptionPane.ERROR_MESSAGE);
      }
    }
    dataFileTextField.setText(probe.getOutputFile().getAbsolutePath());
  }
}

class CompressibilityProbeWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  CompressibilityProbeWindow adaptee;

  CompressibilityProbeWindow_jButton1_actionAdapter(CompressibilityProbeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class CompressibilityProbeWindow_browseButton_actionAdapter implements java.awt.event.ActionListener {
  CompressibilityProbeWindow adaptee;

  CompressibilityProbeWindow_browseButton_actionAdapter(CompressibilityProbeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.browseButton_actionPerformed(e);
  }
}

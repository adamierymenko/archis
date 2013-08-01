package org.api.archis.gui;

import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.io.*;
import java.awt.event.*;

import javax.swing.border.*;

import org.api.archis.*;
import org.api.archis.life.*;

public class ExamineGenomeWindow extends JFrame
{
  private Genome genome;
  private Cell cell;

  JPanel contentPane;
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton jButton1 = new JButton();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JButton jButton2 = new JButton();
  JPanel jPanel2 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JLabel cellIdLabel = new JLabel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel ageLabel = new JLabel();
  JLabel energyLabel = new JLabel();
  JLabel parentCellIdLabel = new JLabel();
  JLabel generationLabel = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel simulationClockLabel = new JLabel();
  TitledBorder titledBorder1;
  JPanel jPanel4 = new JPanel();
  TitledBorder titledBorder3;
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea1 = new JTextArea();

  public ExamineGenomeWindow(Simulation simulation,Cell cell,String title) {
    simulation.newFrameNotify(this);
    this.genome = cell.genome();
    this.cell = cell;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(600,600);
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setTitle(title);
    this.setIconImage(Archis.ICON);

    simulationClockLabel.setText(Long.toString(simulation.universe().clock()));
    cellIdLabel.setText(Long.toString(cell.id()));
    ageLabel.setText(Long.toString(cell.age()));
    energyLabel.setText(Integer.toString(cell.energy()));
    parentCellIdLabel.setText(((cell.parentId() <= 0L) ? "NONE" : Long.toString(cell.parentId())));
    generationLabel.setText(Long.toString(cell.generation()));
    SwingUtilities.invokeLater(new DoGenomePrintTask());
  }

  private class DoGenomePrintTask implements Runnable
  {
    public void run()
    {
      FontMetrics fm = jTextArea1.getFontMetrics(jTextArea1.getFont());
      int w = (jTextArea1.getWidth()/fm.charWidth('#')) - 3;
      if (w <= 0)
        w = 4;
      int wc = 0;
      String g = genome.toString();
      StringBuffer r = new StringBuffer(g.length()+128);
      int depth = 0;
      for(int i=0,j=g.length();i<j;i++) {
        char c = g.charAt(i);
        if (c == '{') {
          r.append('\n');
          for(int d=0;d<depth;d++)
            r.append(' ');
          r.append('{');
          r.append('\n');
          ++depth;
          for(int d=0;d<depth;d++)
            r.append(' ');
          wc = depth;
        } else if (c == '}') {
          r.append('\n');
          if (--depth < 0)
            depth = 0;
          for(int d=0;d<depth;d++)
            r.append(' ');
          r.append('}');
          r.append('\n');
          for(int d=0;d<depth;d++)
            r.append(' ');
          wc = depth;
        } else {
          r.append(c);
          if (wc++ > w) {
            wc = depth;
            r.append('\n');
            for(int d=0;d<depth;d++)
              r.append(' ');
          }
        }
      }
      jTextArea1.setText(r.toString());
    }
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    titledBorder1 = new TitledBorder("");
    titledBorder3 = new TitledBorder("");
    jPanel1.setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    jButton1.setText("Save Genome");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    contentPane.setLayout(gridBagLayout2);
    jButton2.setText("Close");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridBagLayout3);
    jLabel2.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setText("Cell ID: ");
    cellIdLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    cellIdLabel.setText("--");
    jLabel4.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel4.setText("Age: ");
    jLabel5.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel5.setText("Energy: ");
    jLabel6.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel6.setText("Parent Cell ID: ");
    jLabel7.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel7.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel7.setText("Generation: ");
    ageLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    ageLabel.setText("--");
    energyLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    energyLabel.setText("--");
    parentCellIdLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    parentCellIdLabel.setText("--");
    generationLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    generationLabel.setText("--");
    jPanel1.setVerifyInputWhenFocusTarget(true);
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel3.setText("Snapshot Time: ");
    simulationClockLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    simulationClockLabel.setText("--");
    titledBorder1.setTitle("Selected Cell Stats");
    jPanel2.setBorder(titledBorder1);
    titledBorder3.setTitle("Selected Cell Genome");
    jPanel4.setBorder(titledBorder3);
    jPanel4.setOpaque(false);
    jPanel4.setLayout(gridBagLayout4);
    jTextArea1.setEditable(false);
    jTextArea1.setSelectedTextColor(Color.white);
    jTextArea1.setWrapStyleWord(false);
    jTextArea1.setOpaque(true);
    contentPane.add(jPanel1,  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 219, 217));
    jPanel1.add(jButton1,            new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    jPanel1.add(jButton2,           new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jPanel2,                 new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
    jPanel2.add(jLabel2,         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 5), 0, 0));
    jPanel2.add(cellIdLabel,       new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    jPanel2.add(jLabel4,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 0, 5), 0, 0));
    jPanel2.add(jLabel5,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 0, 5), 0, 0));
    jPanel2.add(jLabel6,     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 0, 5), 0, 0));
    jPanel2.add(jLabel7,     new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 5, 5), 0, 0));
    jPanel1.add(jPanel4,       new GridBagConstraints(0, 2, 2, 1, 1.0, 3.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    jPanel4.add(jScrollPane1,  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(jTextArea1, null);
    jPanel2.add(ageLabel,     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 5), 0, 0));
    jPanel2.add(energyLabel,      new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 5), 0, 0));
    jPanel2.add(parentCellIdLabel,      new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 5), 0, 0));
    jPanel2.add(generationLabel,     new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 5, 5), 0, 0));
    jPanel2.add(jLabel3,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 10, 5), 0, 0));
    jPanel2.add(simulationClockLabel,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 5), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(false);
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      try {
        FileOutputStream out = new FileOutputStream(f);
        out.write(jTextArea1.getText().getBytes());
        out.close();
      } catch (IOException ex ) {
        JOptionPane.showMessageDialog(this,"Error writing file: "+ex.getMessage(),"Could not save genome",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void jButton2_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }
}


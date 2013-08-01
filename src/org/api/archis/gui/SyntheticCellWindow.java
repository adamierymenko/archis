package org.api.archis.gui;

import java.util.*;
import java.rmi.RemoteException;
import java.awt.*;

import javax.swing.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;

import java.awt.event.*;
import java.io.*;

public class SyntheticCellWindow extends JFrame
{
  private Universe universe;
  private Simulation simulation;
  private Map genomeTypes;

  private JPanel contentPane;
  private JPanel jPanel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JPanel jPanel2 = new JPanel();
  private JPanel jPanel3 = new JPanel();
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JEditorPane genomeEditorPane = new JEditorPane();
  private JLabel jLabel5 = new JLabel();
  private JLabel jLabel6 = new JLabel();
  private JTextField energyTextField = new JTextField();
  private JPanel jPanel4 = new JPanel();
  private JButton jButton3 = new JButton();
  private JButton jButton4 = new JButton();
  JLabel jLabel1 = new JLabel();
  JComboBox genomeTypeComboBox = new JComboBox();

  public SyntheticCellWindow(Universe universe,Simulation simulation) {
    simulation.newFrameNotify(this);
    genomeTypes = GenomeFactory.getGenomeTypes();
    this.universe = universe;
    this.simulation = simulation;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(400,600);
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    this.setTitle("["+simulation.getName()+"] Introduce Synthetic Lifeform");
    for(Iterator i=genomeTypes.keySet().iterator();i.hasNext();)
      genomeTypeComboBox.addItem(i.next());
    genomeTypeComboBox.setSelectedItem("Register Machine");
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });
    contentPane.setLayout(borderLayout1);
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(gridBagLayout1);
    jButton1.setText("OK");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jButton2.setText("Cancel");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel2.setLayout(gridBagLayout2);
    jPanel3.setBorder(BorderFactory.createEtchedBorder());
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setBorder(BorderFactory.createLineBorder(Color.black));
    genomeEditorPane.setFont(new java.awt.Font("Monospaced", 0, 12));
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    jLabel5.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel5.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel5.setText("Cell Genome");
    jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel6.setText("Energy to Give New Cell:");
    energyTextField.setBorder(BorderFactory.createLoweredBevelBorder());
    energyTextField.setText("16384");
    jButton3.setText("Save");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton3_actionPerformed(e);
      }
    });
    jButton4.setText("Load");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton4_actionPerformed(e);
      }
    });
    jLabel1.setText("Genome Type:");
    contentPane.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jLabel6,         new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 2), 0, 0));
    jPanel1.add(energyTextField,          new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 15), 0, 0));
    contentPane.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jScrollPane1,             new GridBagConstraints(0, 2, 2, 1, 2.0, 2.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
    jPanel2.add(jLabel5,          new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    jPanel2.add(jPanel4,         new GridBagConstraints(1, 3, 1, 2, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 2, 5), 0, 0));
    jPanel4.add(jButton4, null);
    jPanel4.add(jButton3, null);
    jPanel2.add(jLabel1,     new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
    jPanel2.add(genomeTypeComboBox,    new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 5, 5), 0, 0));
    jScrollPane1.getViewport().add(genomeEditorPane, null);
    contentPane.add(jPanel3,  BorderLayout.SOUTH);
    jPanel3.add(jButton1, null);
    jPanel3.add(jButton2, null);
  }

  void jButton2_actionPerformed(ActionEvent e) {
    this.hide();
    this.dispose();
  }

  void jButton1_actionPerformed(ActionEvent e) {
    int genomeType = 1;
    Object _genomeType = genomeTypes.get(genomeTypeComboBox.getSelectedItem());
    if (_genomeType != null)
      genomeType = ((Integer)_genomeType).intValue();
    if (genomeEditorPane.getText() == null) {
      JOptionPane.showMessageDialog(this,"The genome is empty","Cannot create cell",JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (genomeEditorPane.getText().trim().length() <= 0) {
      JOptionPane.showMessageDialog(this,"The genome is empty","Cannot create cell",JOptionPane.ERROR_MESSAGE);
      return;
    }
    int v;
    try {
      v = Integer.parseInt(energyTextField.getText().trim());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Numeric fields must be positive integers","Cannot create cell",JOptionPane.ERROR_MESSAGE);
      return;
    }
    universe.addCell(null,new Cell(simulation,universe,null,v,GenomeFactory.create(genomeType,simulation.randomSource(),genomeEditorPane.getText()).canonicalize()));
    this.hide();
    this.dispose();
  }

  void jButton4_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(false);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f.exists()&&f.canRead()) {
        try {
          byte[] buf = new byte[(int)f.length()];
          FileInputStream in = new FileInputStream(f);
          in.read(buf);
          in.close();
          genomeEditorPane.setCaretPosition(0);
          genomeEditorPane.setText(new String(buf));
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this,"Error reading file: "+ex.getMessage(),"Could not load genome",JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(this,"Error reading file: file not found or not readable.","Could not load genome",JOptionPane.ERROR_MESSAGE);
    }
  }

  void jButton3_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(false);
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      try {
        FileOutputStream out = new FileOutputStream(f);
        out.write(genomeEditorPane.getText().getBytes());
        out.close();
      } catch (IOException ex ) {
        JOptionPane.showMessageDialog(this,"Error writing file: "+ex.getMessage(),"Could not save genome",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void this_windowClosing(WindowEvent e) {
    this.hide();
    this.dispose();
  }
}

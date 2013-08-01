package org.api.archis.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

import org.api.archis.*;

public class AboutBox extends JFrame
{
  private JPanel contentPane;
  private TitledBorder titledBorder1;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel archisVersionLabel = new JLabel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JButton jButton1 = new JButton();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();

  public AboutBox() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setSize(520,280);
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    archisVersionLabel.setText(Archis.ARCHIS_DESCRIPTION);
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    archisVersionLabel.setText("Archis Version xx (c)2001-2003 Adam Ierymenko, All Rights Reserved");
    titledBorder1 = new TitledBorder("");
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setResizable(false);
    this.setTitle("About Archis");
    contentPane.setBorder(BorderFactory.createLineBorder(Color.black));
    contentPane.setLayout(gridBagLayout1);
    jLabel1.setText("Archis is a generalized plugin-based platform for artificial life and");
    jLabel2.setText("genetic programming simulation.");
    jLabel3.setText("Author(s): Adam Ierymenko");
    jButton1.setText("Close");
    jButton1.addActionListener(new AboutBox_jButton1_actionAdapter(this));
    contentPane.add(archisVersionLabel,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 15, 5), 0, 0));
    contentPane.add(jLabel1,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    contentPane.add(jLabel2,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    contentPane.add(jLabel3,         new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 10, 5), 0, 0));
    contentPane.add(jButton1,       new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    contentPane.add(jLabel4,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    contentPane.add(jLabel5,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    contentPane.add(jLabel6,   new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    contentPane.add(jLabel7,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }
}

class AboutBox_jButton1_actionAdapter implements java.awt.event.ActionListener {
  AboutBox adaptee;

  AboutBox_jButton1_actionAdapter(AboutBox adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}


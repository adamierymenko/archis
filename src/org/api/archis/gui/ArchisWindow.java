package org.api.archis.gui;

import javax.swing.*;

import org.api.archis.*;
import org.api.archis.utils.RandomSource;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Main window for the app
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class ArchisWindow extends JFrame
{
  // List of SimulationWindow objects
  private java.util.List simulations;

  // Updater thread
  private UpdaterThread updaterThread;

  //
  // Internal thread to periodically update
  //
  private class UpdaterThread extends Thread
  {
    public boolean die;
    private Runtime runtime;

    public UpdaterThread()
    {
      super("Archis Window Stats Updater");
      runtime = Runtime.getRuntime();
      die = false;
      super.setDaemon(true);
      super.start();
    }

    public void run()
    {
      for(;;) {
        if (die)
          break;

        int t = 0;
        try {
          synchronized (simulations) {
            for (Iterator i = simulations.iterator(); i.hasNext(); ) {
              Simulation s = ( (SimulationWindow) i.next()).simulation;
              if (s.isKilled())
                i.remove();
              else t += s.nThreads();
            }
          }
        } catch (Throwable th) {}
        threadsLabel.setText(Integer.toString(t));
        simulationsLabel.setText(Integer.toString(simulations.size()));
        memLabel.setText(Long.toString((runtime.totalMemory() - runtime.freeMemory()) / 1024L)+"k");

        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {}
      }
    }
  }

  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem jMenuItem3 = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JMenuItem jMenuItem4 = new JMenuItem();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel simulationsLabel = new JLabel();
  JLabel threadsLabel = new JLabel();
  JLabel memLabel = new JLabel();
  JButton jButton1 = new JButton();

  public ArchisWindow()
  {
    simulations = Collections.synchronizedList(new LinkedList());
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setIconImage(Archis.ICON);
    this.setSize(215,165);
    this.setLocation(100,100);
    updaterThread = new UpdaterThread();
  }
  private void jbInit() throws Exception
  {
    contentPane = (JPanel)this.getContentPane();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.setJMenuBar(jMenuBar1);
    this.setTitle("Archis");
    this.addMouseListener(new ArchisWindow_this_mouseAdapter(this));
    this.addWindowListener(new ArchisWindow_this_windowAdapter(this));
    jMenu1.setText("File");
    jMenuItem1.setText("New Simulation");
    jMenuItem1.addActionListener(new ArchisWindow_jMenuItem1_actionAdapter(this));
    jMenuItem3.setText("Exit");
    jMenuItem3.addActionListener(new ArchisWindow_jMenuItem3_actionAdapter(this));
    jMenu2.setText("Help");
    jMenuItem4.setText("About Archis");
    jMenuItem4.addActionListener(new ArchisWindow_jMenuItem4_actionAdapter(this));
    contentPane.setLayout(gridBagLayout1);
    jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel1.setText("Simulations:");
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Threads:");
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel3.setText("Memory Usage:");
    simulationsLabel.setText("0");
    threadsLabel.setText("0");
    memLabel.setText("0k");
    jButton1.setText("Force GC");
    jButton1.addActionListener(new ArchisWindow_jButton1_actionAdapter(this));
    jButton1.setToolTipText("Force JVM garbage collection to run now");
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenu1.add(jMenuItem1);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem3);
    jMenu2.add(jMenuItem4);
    contentPane.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 2, 5), 0, 0));
    contentPane.add(jLabel2,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 5, 2, 5), 0, 0));
    contentPane.add(jLabel3,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    contentPane.add(simulationsLabel,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 2, 5), 0, 0));
    contentPane.add(threadsLabel,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 5), 0, 0));
    contentPane.add(memLabel,    new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
    contentPane.add(jButton1,   new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }

  private void onCloseAttempt()
  {
    // Confirm closes if simulations are running
    if (simulations.size() <= 0) {
      this.setVisible(false);
      updaterThread.die = true;
      updaterThread.interrupt();
      this.dispose();
      Runtime.getRuntime().exit(1);
    } else {
      if (JOptionPane.showConfirmDialog(this,"Are you sure you wish to exit and terminate ALL simulations?","Exit and terminate?",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
        this.setVisible(false);
        for(Iterator i=simulations.iterator();i.hasNext();) {
          SimulationWindow sw = (SimulationWindow)i.next();
          sw.setVisible(false);
          sw.simulation.kill();
          updaterThread.die = true;
          updaterThread.interrupt();
          sw.dispose();
        }
        simulations.clear();
        this.dispose();
        Runtime.getRuntime().exit(1);
      }
    }
  }

  void jMenuItem4_actionPerformed(ActionEvent e) {
    // About Archis
    new AboutBox().setVisible(true);
  }

  void this_windowClosing(WindowEvent e) {
    // Close button on window decoration clicked
    onCloseAttempt();
  }

  void this_mouseEntered(MouseEvent e) {
  }

  void jMenuItem3_actionPerformed(ActionEvent e) {
    // Exit
    onCloseAttempt();
  }

  void jMenuItem1_actionPerformed(ActionEvent e) {
    // New simulation
    new NewSimulationDialog(simulations).setVisible(true);
  }


  void jButton1_actionPerformed(ActionEvent e) {
    System.gc();
  }
}

class ArchisWindow_jMenuItem4_actionAdapter implements java.awt.event.ActionListener {
  ArchisWindow adaptee;

  ArchisWindow_jMenuItem4_actionAdapter(ArchisWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem4_actionPerformed(e);
  }
}

class ArchisWindow_this_windowAdapter extends java.awt.event.WindowAdapter {
  ArchisWindow adaptee;

  ArchisWindow_this_windowAdapter(ArchisWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void windowClosing(WindowEvent e) {
    adaptee.this_windowClosing(e);
  }
}

class ArchisWindow_this_mouseAdapter extends java.awt.event.MouseAdapter {
  ArchisWindow adaptee;

  ArchisWindow_this_mouseAdapter(ArchisWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseEntered(MouseEvent e) {
    adaptee.this_mouseEntered(e);
  }
}

class ArchisWindow_jMenuItem3_actionAdapter implements java.awt.event.ActionListener {
  ArchisWindow adaptee;

  ArchisWindow_jMenuItem3_actionAdapter(ArchisWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem3_actionPerformed(e);
  }
}

class ArchisWindow_jMenuItem1_actionAdapter implements java.awt.event.ActionListener {
  ArchisWindow adaptee;

  ArchisWindow_jMenuItem1_actionAdapter(ArchisWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem1_actionPerformed(e);
  }
}

class ArchisWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  ArchisWindow adaptee;

  ArchisWindow_jButton1_actionAdapter(ArchisWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

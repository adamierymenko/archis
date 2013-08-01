package org.api.archis.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.universe.catastrophes.*;
import org.api.archis.universe.environmentalconditions.*;
import org.api.archis.universe.rewardfunctions.*;

import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Main window for a simulation
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class SimulationWindow extends JFrame implements SimulationObserver
{
  Simulation simulation;
  Universe universe;
  ConditionListTableModel environmentalConditions;
  ConditionListTableModel rewardFunctions;
  ConditionListTableModel catastrophes;
  ProbeListTableModel probes;
  StatisticsTableModel statisticsTableModel;
  NumberFormat cellTimeNumberFormat = DecimalFormat.getNumberInstance();
  NumberFormat ioStaticProbabilityNumberFormat = DecimalFormat.getNumberInstance();
  double ttcTotal,ttcTotalTicks;

  JPanel contentPane;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel startStopPanel = new JPanel();
  JToggleButton stopButton = new JToggleButton();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JToggleButton runButton = new JToggleButton();
  JLabel jLabel1 = new JLabel();
  JLabel simulationClockLabel = new JLabel();
  JScrollPane statisticsScrollPane = new JScrollPane();
  JTabbedPane conditionTabbedPane = new JTabbedPane();
  JPanel environmentalConditionsPanel = new JPanel();
  JPanel catastrophesPanel = new JPanel();
  JPanel rewardFunctionsPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable environmentalConditionsTable = new JTable();
  JButton addExternalEnvironmentalConditionButton = new JButton();
  JButton terminateEnvironmentalConditionButton = new JButton();
  JButton activateEnvironmentalConditionButton = new JButton();
  JTable statisticsTable = new JTable();
  JScrollPane jScrollPane2 = new JScrollPane();
  JButton addExternalCatastropheButton = new JButton();
  JButton activateCatastropheButton = new JButton();
  JButton terminateCatastropheButton = new JButton();
  JTable catastrophesTable = new JTable();
  JScrollPane jScrollPane3 = new JScrollPane();
  JTable rewardFunctionTable = new JTable();
  JButton addExternalRewardFunctionButton = new JButton();
  JButton activateRewardFunctionButton = new JButton();
  JButton terminateRewardFunctionButton = new JButton();
  JButton openEnvironmentalConditionButton = new JButton();
  JButton openCatastropheButton = new JButton();
  JButton openRewardFunctionButton = new JButton();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenu1 = new JMenu();
  JMenu jMenu2 = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem jMenuItem2 = new JMenuItem();
  JMenuItem jMenuItem3 = new JMenuItem();
  JMenuItem jMenuItem4 = new JMenuItem();
  JButton singleStepButton = new JButton();
  JPanel jPanel1 = new JPanel();
  TitledBorder titledBorder1;
  JLabel jLabel2 = new JLabel();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JLabel jLabel3 = new JLabel();
  JLabel lastTickMSLabel = new JLabel();
  JLabel timeCellMSLabel = new JLabel();
  JPanel probesPanel = new JPanel();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  JPanel settingsPanel = new JPanel();
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  JScrollPane jScrollPane4 = new JScrollPane();
  JTable probesTable = new JTable();
  JButton addExternalProbeButton = new JButton();
  JButton activateProbeButton = new JButton();
  JButton openProbeButton = new JButton();
  JButton terminateProbeButton = new JButton();
  JLabel jLabel4 = new JLabel();
  JTextField ioStaticTextField = new JTextField();
  JLabel jLabel5 = new JLabel();
  JLabel stopPointLabel = new JLabel();
  JButton setStopPointButton = new JButton();

  public SimulationWindow(Simulation simulation)
  {
    simulation.newFrameNotify(this);
    this.simulation = simulation;
    String simulationName = simulation.getName();
    cellTimeNumberFormat.setMinimumIntegerDigits(1);
    cellTimeNumberFormat.setMaximumFractionDigits(3);
    cellTimeNumberFormat.setMinimumFractionDigits(3);
    cellTimeNumberFormat.setMaximumIntegerDigits(4);
    statisticsTableModel = new StatisticsTableModel(simulation);
    statisticsTable = new JTable(statisticsTableModel);
    statisticsTable.setTableHeader(null);
    simulation.addObserver(this);
    universe = simulation.universe();
    environmentalConditions = new ConditionListTableModel(universe);
    catastrophes = new ConditionListTableModel(universe);
    rewardFunctions = new ConditionListTableModel(universe);
    probes = new ProbeListTableModel(universe);
    environmentalConditionsTable.setModel(environmentalConditions);
    rewardFunctionTable.setModel(rewardFunctions);
    catastrophesTable.setModel(catastrophes);
    probesTable.setModel(probes);
    ttcTotal = 0.0;
    ttcTotalTicks = 0.0;
    try {
      jbInit();
    } catch(Exception e) {
      e.printStackTrace();
    }
    this.setIconImage(Archis.ICON);
    this.setSize(640,600);
    this.setTitle("["+simulationName+"] Control Panel");
    this.setLocation(150,150);

    statisticsTable.getColumnModel().getColumn(1).setResizable(false);
    statisticsTable.getColumnModel().getColumn(1).setMaxWidth(128);
    statisticsTable.getColumnModel().getColumn(1).setMinWidth(128);
    statisticsTable.getColumnModel().getColumn(1).setPreferredWidth(128);

    try {
      environmentalConditions.addCondition(Class.forName("org.api.archis.universe.environmentalconditions.Landscape2D"));
      environmentalConditions.addCondition(Class.forName("org.api.archis.universe.environmentalconditions.RandomDeathCondition"));
      environmentalConditions.addCondition(Class.forName("org.api.archis.universe.environmentalconditions.EnvironmentalMutationCondition"));
      environmentalConditions.addCondition(Class.forName("org.api.archis.universe.environmentalconditions.RandomSourceCondition"));
      environmentalConditions.addCondition(Class.forName("org.api.archis.universe.environmentalconditions.GenesisCondition"));
      environmentalConditions.addCondition(Class.forName("org.api.archis.universe.environmentalconditions.ReproductionCondition"));
      catastrophes.addCondition(Class.forName("org.api.archis.universe.catastrophes.ExtinctionLevelEvent"));
      catastrophes.addCondition(Class.forName("org.api.archis.universe.catastrophes.Irradiate"));
      rewardFunctions.addCondition(Class.forName("org.api.archis.universe.rewardfunctions.BaselineRewardFunction"));
      rewardFunctions.addCondition(Class.forName("org.api.archis.universe.rewardfunctions.FibonacciSequenceRewardFunction"));
      rewardFunctions.addCondition(Class.forName("org.api.archis.universe.rewardfunctions.DiversityRewardFunction"));
      probes.addProbe(Class.forName("org.api.archis.universe.probes.CompressibilityProbe"));
      probes.addProbe(Class.forName("org.api.archis.universe.probes.GenesisProbe"));
    } catch (ClassNotFoundException e) {
      System.out.println("Part of this program seems to be missing!");
      e.printStackTrace();
      throw new Error("Missing core class");
    }

    TableColumnModel tmp = environmentalConditionsTable.getColumnModel();
    tmp.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tmp.getColumn(0).setWidth(60);
    tmp.getColumn(0).setMinWidth(10);
    tmp.getColumn(0).setResizable(false);
    tmp.getColumn(0).setMaxWidth(60);
    tmp.getColumn(2).setCellRenderer(new TableMultilineCellRenderer());

    tmp = catastrophesTable.getColumnModel();
    tmp.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tmp.getColumn(0).setWidth(60);
    tmp.getColumn(0).setMinWidth(10);
    tmp.getColumn(0).setResizable(false);
    tmp.getColumn(0).setMaxWidth(60);
    tmp.getColumn(2).setCellRenderer(new TableMultilineCellRenderer());

    tmp = rewardFunctionTable.getColumnModel();
    tmp.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tmp.getColumn(0).setWidth(60);
    tmp.getColumn(0).setMinWidth(10);
    tmp.getColumn(0).setResizable(false);
    tmp.getColumn(0).setMaxWidth(60);
    tmp.getColumn(2).setCellRenderer(new TableMultilineCellRenderer());

    tmp = probesTable.getColumnModel();
    tmp.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tmp.getColumn(0).setWidth(60);
    tmp.getColumn(0).setMinWidth(10);
    tmp.getColumn(0).setResizable(false);
    tmp.getColumn(0).setMaxWidth(60);
    tmp.getColumn(2).setCellRenderer(new TableMultilineCellRenderer());

    updateEnvironmentalConditionsTabButtons();
    updateRewardFunctionsTabButtons();
    updateProbesTabButtons();

    ioStaticProbabilityNumberFormat.setMinimumIntegerDigits(1);
    ioStaticProbabilityNumberFormat.setMaximumFractionDigits(6);
    ioStaticProbabilityNumberFormat.setMinimumFractionDigits(1);
    ioStaticProbabilityNumberFormat.setMaximumIntegerDigits(16384);
    ioStaticProbabilityNumberFormat.setGroupingUsed(false);
    ioStaticTextField.setText(ioStaticProbabilityNumberFormat.format(universe.getStaticProbability()));

    // Fixes wierd GTK theme problem under Linux
    titledBorder1.setTitleColor(jLabel1.getForeground());
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    titledBorder1 = new TitledBorder("");
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.setJMenuBar(jMenuBar1);
    this.addWindowListener(new SimulationWindow_this_windowAdapter(this));
    environmentalConditionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    catastrophesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    rewardFunctionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    contentPane.setLayout(gridBagLayout1);
    startStopPanel.setLayout(gridBagLayout2);
    stopButton.setToolTipText("Pause the simulation");
    stopButton.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Pause24.gif")));
    runButton.setToolTipText("Run the simulation");
    runButton.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Play24.gif")));
    singleStepButton.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Forward24.gif")));
    singleStepButton.addActionListener(new SimulationWindow_singleStepButton_actionAdapter(this));
    runButton.addActionListener(new SimulationWindow_runButton_actionAdapter(this));
    jLabel1.setFont(new java.awt.Font("Dialog", 0, 16));
    jLabel1.setText("Simulation Clock:");
    simulationClockLabel.setFont(new java.awt.Font("Dialog", 1, 16));
    simulationClockLabel.setText("0");
    stopButton.setSelected(true);
    startStopPanel.setBorder(BorderFactory.createEtchedBorder());
    statisticsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    statisticsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    statisticsScrollPane.getViewport().setBackground(Color.black);
    statisticsScrollPane.setForeground(Color.yellow);
    statisticsScrollPane.setBorder(BorderFactory.createEtchedBorder());
    conditionTabbedPane.setBorder(BorderFactory.createEtchedBorder());
    environmentalConditionsPanel.setLayout(gridBagLayout3);
    catastrophesPanel.setLayout(gridBagLayout4);
    rewardFunctionsPanel.setLayout(gridBagLayout5);
    openRewardFunctionButton.setEnabled(false);
    terminateRewardFunctionButton.setEnabled(false);
    addExternalEnvironmentalConditionButton.setToolTipText("Add an environmental condition to the list by specifying the class name");
    addExternalEnvironmentalConditionButton.setText("Add External...");
    addExternalEnvironmentalConditionButton.addActionListener(new SimulationWindow_addExternalEnvironmentalConditionButton_actionAdapter(this));
    terminateEnvironmentalConditionButton.setToolTipText("Remove an environmental condition from the universe");
    terminateEnvironmentalConditionButton.setText("Terminate");
    terminateEnvironmentalConditionButton.addActionListener(new SimulationWindow_terminateEnvironmentalConditionButton_actionAdapter(this));
    activateEnvironmentalConditionButton.setToolTipText("Add an environmental condition to the universe");
    activateEnvironmentalConditionButton.setText("Activate");
    activateEnvironmentalConditionButton.addActionListener(new SimulationWindow_activateEnvironmentalConditionButton_actionAdapter(this));
    addExternalCatastropheButton.setToolTipText("Add a catastrophe to the list by specifying the class name");
    addExternalCatastropheButton.setText("Add External...");
    addExternalCatastropheButton.addActionListener(new SimulationWindow_addExternalCatastropheButton_actionAdapter(this));
    activateCatastropheButton.setToolTipText("Cause a catastrophe in the universe");
    activateCatastropheButton.setText("Activate");
    activateCatastropheButton.addActionListener(new SimulationWindow_activateCatastropheButton_actionAdapter(this));
    terminateCatastropheButton.setEnabled(false);
    terminateCatastropheButton.setToolTipText("(Catastrophes sunset automatically after a given time)");
    terminateCatastropheButton.setText("Terminate");
    addExternalRewardFunctionButton.setToolTipText("Add a reward function to the list by specifying the class name");
    addExternalRewardFunctionButton.setText("Add External...");
    addExternalRewardFunctionButton.addActionListener(new SimulationWindow_addExternalRewardFunctionButton_actionAdapter(this));
    activateRewardFunctionButton.setToolTipText("Add a reward function to the universe");
    activateRewardFunctionButton.setText("Activate");
    activateRewardFunctionButton.addActionListener(new SimulationWindow_activateRewardFunctionButton_actionAdapter(this));
    terminateRewardFunctionButton.setToolTipText("Remove a reward function from the universe");
    terminateRewardFunctionButton.setText("Terminate");
    terminateRewardFunctionButton.addActionListener(new SimulationWindow_terminateRewardFunctionButton_actionAdapter(this));
    openEnvironmentalConditionButton.setEnabled(false);
    terminateEnvironmentalConditionButton.setEnabled(false);
    environmentalConditionsTable.setEnabled(true);
    environmentalConditionsTable.setAutoCreateColumnsFromModel(true);
    environmentalConditionsTable.addKeyListener(new SimulationWindow_environmentalConditionsTable_keyAdapter(this));
    environmentalConditionsTable.addMouseListener(new SimulationWindow_environmentalConditionsTable_mouseAdapter(this));
    statisticsTable.setBackground(Color.black);
    statisticsTable.setForeground(Color.yellow);
    statisticsTable.setAutoscrolls(false);
    statisticsTable.setOpaque(true);
    statisticsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    statisticsTable.setGridColor(Color.black);
    statisticsTable.setRowSelectionAllowed(false);
    statisticsTable.setSelectionBackground(Color.black);
    statisticsTable.setSelectionForeground(Color.black);
    statisticsTable.setShowHorizontalLines(false);
    statisticsTable.setShowVerticalLines(false);
    openEnvironmentalConditionButton.setToolTipText("Open environmental condition control panel");
    openEnvironmentalConditionButton.setText("Open Controls");
    openEnvironmentalConditionButton.addActionListener(new SimulationWindow_openEnvironmentalConditionButton_actionAdapter(this));
    openCatastropheButton.setEnabled(false);
    openCatastropheButton.setToolTipText("Catastrophes do not have control panels");
    openCatastropheButton.setText("Open Controls");
    catastrophesPanel.setToolTipText("");
    openRewardFunctionButton.setText("Open Controls");
    openRewardFunctionButton.addActionListener(new SimulationWindow_openRewardFunctionButton_actionAdapter(this));
    openRewardFunctionButton.setToolTipText("Open reward function control panel");
    rewardFunctionTable.addMouseListener(new SimulationWindow_rewardFunctionTable_mouseAdapter(this));
    rewardFunctionTable.addKeyListener(new SimulationWindow_rewardFunctionTable_keyAdapter(this));
    jMenu1.setText("File");
    jMenu2.setText("Universe");
    jMenuItem1.setEnabled(false);
    jMenuItem1.setText("Save Simulation");
    jMenuItem2.setActionCommand("Close");
    jMenuItem2.setText("Close");
    jMenuItem2.addActionListener(new SimulationWindow_jMenuItem2_actionAdapter(this));
    jMenuItem3.setText("Introduce Synthetic Lifeform");
    jMenuItem3.addActionListener(new SimulationWindow_jMenuItem3_actionAdapter(this));
    jMenuItem4.setText("View I/O Channel Assignments");
    jMenuItem4.addActionListener(new SimulationWindow_jMenuItem4_actionAdapter(this));
    singleStepButton.setToolTipText("Step forward one");
    jPanel1.setBorder(titledBorder1);
    jPanel1.setLayout(gridBagLayout6);
    titledBorder1.setTitle("Performance");
    titledBorder1.setBorder(BorderFactory.createEtchedBorder());
    titledBorder1.setTitleFont(new java.awt.Font("Dialog", 0, 10));
    jLabel2.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Last Tick (ms):");
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 10));
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel3.setText("Time/Cell (ms):");
    lastTickMSLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    lastTickMSLabel.setText("0ms");
    timeCellMSLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    timeCellMSLabel.setText("0ms");
    probesPanel.setLayout(gridBagLayout7);
    settingsPanel.setLayout(gridBagLayout8);
    addExternalProbeButton.setText("Add External...");
    addExternalProbeButton.addActionListener(new SimulationWindow_addExternalProbeButton_actionAdapter(this));
    activateProbeButton.setText("Activate");
    activateProbeButton.addActionListener(new SimulationWindow_activateProbeButton_actionAdapter(this));
    openProbeButton.setText("Open Controls");
    openProbeButton.addActionListener(new SimulationWindow_openProbeButton_actionAdapter(this));
    terminateProbeButton.setText("Terminate");
    terminateProbeButton.addActionListener(new SimulationWindow_terminateProbeButton_actionAdapter(this));
    jLabel4.setText("I/O Static Probability:");
    ioStaticTextField.addKeyListener(new SimulationWindow_ioStaticTextField_keyAdapter(this));
    probesTable.addMouseListener(new SimulationWindow_probesTable_mouseAdapter(this));
    probesTable.addKeyListener(new SimulationWindow_probesTable_keyAdapter(this));
    jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel5.setText("Stop at Tick:");
    jLabel5.setVerticalAlignment(SwingConstants.TOP);
    jLabel5.setVerticalTextPosition(SwingConstants.TOP);
    stopPointLabel.setBorder(null);
    stopPointLabel.setText("[none]");
    stopPointLabel.setVerticalAlignment(SwingConstants.TOP);
    stopPointLabel.setVerticalTextPosition(SwingConstants.TOP);
    setStopPointButton.setFont(new java.awt.Font("Dialog", 0, 12));
    setStopPointButton.setText("Set Stop Point...");
    setStopPointButton.addActionListener(new SimulationWindow_setStopPointButton_actionAdapter(this));
    contentPane.add(startStopPanel,    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));
    startStopPanel.add(stopButton,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    startStopPanel.add(runButton,        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    startStopPanel.add(jLabel1,        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
    startStopPanel.add(simulationClockLabel,          new GridBagConstraints(4, 0, 1, 1, 2.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    startStopPanel.add(singleStepButton,     new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
    startStopPanel.add(jPanel1,       new GridBagConstraints(5, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 1, 5), 0, 0));
    jPanel1.add(jLabel2,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));
    jPanel1.add(jLabel3,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
    jPanel1.add(lastTickMSLabel,  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
    jPanel1.add(timeCellMSLabel,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 2), 0, 0));
    contentPane.add(statisticsScrollPane,      new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    contentPane.add(conditionTabbedPane,     new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 2, 2), 0, 0));
    conditionTabbedPane.add(environmentalConditionsPanel,   "Environmental Conditions");
    environmentalConditionsPanel.add(jScrollPane1,       new GridBagConstraints(1, 1, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    environmentalConditionsPanel.add(addExternalEnvironmentalConditionButton,      new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    jScrollPane1.getViewport().add(environmentalConditionsTable, null);
    conditionTabbedPane.add(catastrophesPanel,  "Catastrophes");
    catastrophesPanel.add(jScrollPane2,     new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    jScrollPane2.getViewport().add(catastrophesTable, null);
    catastrophesPanel.add(addExternalCatastropheButton,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    catastrophesPanel.add(activateCatastropheButton,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    catastrophesPanel.add(terminateCatastropheButton,     new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    conditionTabbedPane.add(rewardFunctionsPanel,  "Reward Functions");
    rewardFunctionsPanel.add(jScrollPane3,        new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    rewardFunctionsPanel.add(addExternalRewardFunctionButton,     new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    rewardFunctionsPanel.add(activateRewardFunctionButton,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    rewardFunctionsPanel.add(terminateRewardFunctionButton,    new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    jScrollPane3.getViewport().add(rewardFunctionTable, null);
    environmentalConditionsPanel.add(terminateEnvironmentalConditionButton,      new GridBagConstraints(4, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    environmentalConditionsPanel.add(activateEnvironmentalConditionButton,     new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    environmentalConditionsPanel.add(openEnvironmentalConditionButton,  new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    statisticsScrollPane.getViewport().add(statisticsTable, null);
    catastrophesPanel.add(openCatastropheButton,  new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    rewardFunctionsPanel.add(openRewardFunctionButton,  new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    conditionTabbedPane.add(probesPanel,  "Probes");
    conditionTabbedPane.add(settingsPanel,  "Settings");
    settingsPanel.add(jLabel4,             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    settingsPanel.add(ioStaticTextField,                new GridBagConstraints(1, 0, 2, 2, 1.0, 1.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    settingsPanel.add(jLabel5,          new GridBagConstraints(0, 1, 1, 4, 0.0, 1.0
            ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0));
    settingsPanel.add(stopPointLabel,       new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 0, 0, 5), 0, 0));
    settingsPanel.add(setStopPointButton,        new GridBagConstraints(2, 1, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenu1.add(jMenuItem1);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem2);
    jMenu2.add(jMenuItem3);
    jMenu2.add(jMenuItem4);
    probesPanel.add(jScrollPane4,       new GridBagConstraints(0, 0, 4, 1, 1.0, 2.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    probesPanel.add(addExternalProbeButton,      new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    probesPanel.add(activateProbeButton,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    probesPanel.add(openProbeButton,    new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    probesPanel.add(terminateProbeButton,   new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    jScrollPane4.getViewport().add(probesTable, null);
    stopButton.addActionListener(new SimulationWindow_stopButton_actionAdapter(this));
  }

  void stopButton_actionPerformed(ActionEvent e) {
    if (stopButton.isSelected()) {
      if (simulation.isRunning())
        simulation.stop(true);
      runButton.setSelected(false);
      singleStepButton.setEnabled(true);
    } else stopButton.setSelected(!simulation.isRunning());
  }

  void runButton_actionPerformed(ActionEvent e) {
    if (runButton.isSelected()) {
      if (!simulation.isRunning())
        simulation.start();
      stopButton.setSelected(false);
      singleStepButton.setEnabled(false);
    } else runButton.setSelected(simulation.isRunning());
  }

  void singleStepButton_actionPerformed(ActionEvent e) {
    try {
      simulation.step();
    } catch (IllegalStateException ex) {}
  }

  public void tick()
  {
    long clock = universe.clock();
    simulationClockLabel.setText(Long.toString(clock));
    long tlt = simulation.getLastTickTime();
    lastTickMSLabel.setText(Long.toString(tlt));
    if (universe.population() > 0) {
      double ttc;
      timeCellMSLabel.setText(cellTimeNumberFormat.format((ttc = (double)tlt / (double)universe.population())));
      ttcTotal += ttc;
      ttcTotalTicks += 1.0;
    } else timeCellMSLabel.setText("0");
  }

  public void halted(String haltReason)
  {
    runButton.setSelected(false);
    singleStepButton.setEnabled(true);
    stopButton.setSelected(true);
    JOptionPane.showMessageDialog(this,"Simulation Halted: "+haltReason,"Simulation Halted",JOptionPane.INFORMATION_MESSAGE);
  }

  void this_windowClosing(WindowEvent e) {
    if (JOptionPane.showConfirmDialog(this,"Are you sure you want to close and discard this simulation? (Save first if you do not!)","Close Confirmation",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
      this.setVisible(false);
      simulation.kill();
      this.dispose();
    }
  }

  void addExternalEnvironmentalConditionButton_actionPerformed(ActionEvent e) {
    // Add external by class name
  }

  void activateEnvironmentalConditionButton_actionPerformed(ActionEvent e) {
    // Activate environmental condition
    int seln = environmentalConditionsTable.getSelectedRow();
    if ((seln >= 0)&&(seln < environmentalConditions.getRowCount())) {
      String ecn = (String)environmentalConditions.getValueAt(seln,1);
      if ((ecn != null)&&(!universe.hasConditionByName(ecn))) {
        try {
          environmentalConditions.activateCondition(ecn);
        } catch (Throwable t) {
          t.printStackTrace();
          JOptionPane.showMessageDialog(this,"Error creating condition: "+t.toString(),"Error creating condition",JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  void terminateEnvironmentalConditionButton_actionPerformed(ActionEvent e) {
    // Terminate environmental condition
    int seln = environmentalConditionsTable.getSelectedRow();
    if ((seln >= 0)&&(seln < environmentalConditions.getRowCount())) {
      String ecn = (String)environmentalConditions.getValueAt(seln,1);
      environmentalConditions.inactivateCondition(ecn);
    }
    statisticsTable.repaint();
  }

  void activateCatastropheButton_actionPerformed(ActionEvent e) {
    // Activate catastrophe
    int seln = catastrophesTable.getSelectedRow();
    if ((seln >= 0)&&(seln < catastrophes.getRowCount())) {
      String ecn = (String)catastrophes.getValueAt(seln,1);
      if ((ecn != null)&&(!universe.hasConditionByName(ecn))) {
        try {
          catastrophes.activateCondition(ecn);
        } catch (Throwable t) {
          t.printStackTrace();
          JOptionPane.showMessageDialog(this,"Error creating condition: "+t.toString(),"Error creating condition",JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  void activateRewardFunctionButton_actionPerformed(ActionEvent e) {
    // Activate reward function
    int seln = rewardFunctionTable.getSelectedRow();
    if ((seln >= 0)&&(seln < rewardFunctions.getRowCount())) {
      String ecn = (String)rewardFunctions.getValueAt(seln,1);
      if ((ecn != null)&&(!universe.hasConditionByName(ecn))) {
        try {
          rewardFunctions.activateCondition(ecn);
        } catch (Throwable t) {
          t.printStackTrace();
          JOptionPane.showMessageDialog(this,"Error creating condition: "+t.toString(),"Error creating condition",JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  void addExternalCatastropheButton_actionPerformed(ActionEvent e) {
    // Add catastrophe based on external class name
  }

  void addExternalRewardFunctionButton_actionPerformed(ActionEvent e) {
    // Add reward function based on external class name
  }

  void terminateRewardFunctionButton_actionPerformed(ActionEvent e) {
    // Terminate reward function
    int seln = rewardFunctionTable.getSelectedRow();
    if ((seln >= 0)&&(seln < rewardFunctions.getRowCount())) {
      String ecn = (String)rewardFunctions.getValueAt(seln,1);
      rewardFunctions.inactivateCondition(ecn);
    }
    statisticsTable.repaint();
  }

  void openEnvironmentalConditionButton_actionPerformed(ActionEvent e) {
    // Open environmental condition controls
    int seln = environmentalConditionsTable.getSelectedRow();
    if ((seln >= 0)&&(seln < environmentalConditions.getRowCount())) {
      String ecn = (String)environmentalConditions.getValueAt(seln,1);
      Set uc = universe.getConditions();
      for(Iterator i=uc.iterator();i.hasNext();) {
        Condition c = (Condition)i.next();
        if (c.getClass().getName().endsWith(ecn))
          c.showGUI();
      }
    }
  }

  void openRewardFunctionButton_actionPerformed(ActionEvent e) {
    // Open reward function controls
    int seln = rewardFunctionTable.getSelectedRow();
    if ((seln >= 0)&&(seln < rewardFunctions.getRowCount())) {
      String ecn = (String)rewardFunctions.getValueAt(seln,1);
      Set uc = universe.getConditions();
      for(Iterator i=uc.iterator();i.hasNext();) {
        Condition c = (Condition)i.next();
        if (c.getClass().getName().endsWith(ecn))
          c.showGUI();
      }
    }
  }

  private void updateEnvironmentalConditionsTabButtons()
  {
    int seln = environmentalConditionsTable.getSelectedRow();
    if ((seln >= 0)&&(seln < environmentalConditions.getRowCount())) {
      if (universe.hasConditionByName((String)environmentalConditions.getValueAt(seln,1))) {
        openEnvironmentalConditionButton.setEnabled(true);
        terminateEnvironmentalConditionButton.setEnabled(true);
        activateEnvironmentalConditionButton.setEnabled(false);
      } else {
        openEnvironmentalConditionButton.setEnabled(false);
        terminateEnvironmentalConditionButton.setEnabled(false);
        activateEnvironmentalConditionButton.setEnabled(true);
      }
    } else {
      openEnvironmentalConditionButton.setEnabled(false);
      terminateEnvironmentalConditionButton.setEnabled(false);
      activateEnvironmentalConditionButton.setEnabled(false);
    }
  }
  private void updateRewardFunctionsTabButtons()
  {
    int seln = rewardFunctionTable.getSelectedRow();
    if ((seln >= 0)&&(seln < rewardFunctions.getRowCount())) {
      if (universe.hasConditionByName((String)rewardFunctions.getValueAt(seln,1))) {
        openRewardFunctionButton.setEnabled(true);
        terminateRewardFunctionButton.setEnabled(true);
        activateRewardFunctionButton.setEnabled(false);
      } else {
        openRewardFunctionButton.setEnabled(false);
        terminateRewardFunctionButton.setEnabled(false);
        activateRewardFunctionButton.setEnabled(true);
      }
    } else {
      openRewardFunctionButton.setEnabled(false);
      terminateRewardFunctionButton.setEnabled(false);
      activateRewardFunctionButton.setEnabled(false);
    }
  }
  private void updateProbesTabButtons()
  {
    int seln = probesTable.getSelectedRow();
    if ((seln >= 0)&&(seln < probes.getRowCount())) {
      if (universe.hasProbeByName((String)probes.getValueAt(seln,1))) {
        openProbeButton.setEnabled(true);
        terminateProbeButton.setEnabled(true);
        activateProbeButton.setEnabled(false);
      } else {
        openProbeButton.setEnabled(false);
        terminateProbeButton.setEnabled(false);
        activateProbeButton.setEnabled(true);
      }
    } else {
      openProbeButton.setEnabled(false);
      terminateProbeButton.setEnabled(false);
      activateProbeButton.setEnabled(false);
    }
  }

  void environmentalConditionsTable_mouseReleased(MouseEvent e) {
    updateEnvironmentalConditionsTabButtons();
  }
  void environmentalConditionsTable_keyReleased(KeyEvent e) {
    updateEnvironmentalConditionsTabButtons();
  }

  void rewardFunctionTable_mouseReleased(MouseEvent e) {
    updateRewardFunctionsTabButtons();
  }
  void rewardFunctionTable_keyReleased(KeyEvent e) {
    updateRewardFunctionsTabButtons();
  }

  void jMenuItem2_actionPerformed(ActionEvent e) {
    // close
    if (JOptionPane.showConfirmDialog(this,"Are you sure you want to close and discard this simulation? (Save first if you do not!)","Close Confirmation",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
      this.setVisible(false);
      simulation.kill();
      this.dispose();
    }
  }

  void jMenuItem3_actionPerformed(ActionEvent e) {
    // introduce synthetic lifeform
    new SyntheticCellWindow(universe,simulation).setVisible(true);
  }

  void jMenuItem4_actionPerformed(ActionEvent e) {
    // View I/O channel assignments
    new ChannelAssignmentsWindow(simulation,universe).setVisible(true);
  }

  void addExternalProbeButton_actionPerformed(ActionEvent e) {
    // Add external probe by class name
  }

  void activateProbeButton_actionPerformed(ActionEvent e) {
    // Activate probe
    int seln = probesTable.getSelectedRow();
    if ((seln >= 0)&&(seln < probes.getRowCount())) {
      String ecn = (String)probes.getValueAt(seln,1);
      if ((ecn != null)&&(!universe.hasProbeByName(ecn))) {
        try {
          probes.activateProbe(ecn);
        } catch (Throwable t) {
          t.printStackTrace();
          JOptionPane.showMessageDialog(this,"Error creating probe: "+t.toString(),"Error creating probe",JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  void openProbeButton_actionPerformed(ActionEvent e) {
    // Open probe controls
    int seln = probesTable.getSelectedRow();
    if ((seln >= 0)&&(seln < probes.getRowCount())) {
      String ecn = (String)probes.getValueAt(seln,1);
      Set uc = universe.getProbes();
      for(Iterator i=uc.iterator();i.hasNext();) {
        Probe c = (Probe)i.next();
        if (c.getClass().getName().endsWith(ecn))
          c.showGUI();
      }
    }
  }

  void terminateProbeButton_actionPerformed(ActionEvent e) {
    // Terminate probe
    int seln = probesTable.getSelectedRow();
    if ((seln >= 0)&&(seln < probes.getRowCount())) {
      String ecn = (String)probes.getValueAt(seln,1);
      probes.inactivateProbe(ecn);
    }
    statisticsTable.repaint();
  }

  void ioStaticTextField_keyReleased(KeyEvent e) {
    String s = ioStaticTextField.getText().trim();
    if (s.length() > 0) {
      try {
        universe.setStaticProbability(Float.parseFloat(s));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"I/O static level must be a floating point number","Invalid value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void probesTable_mouseReleased(MouseEvent e) {
    updateProbesTabButtons();
  }

  void probesTable_keyReleased(KeyEvent e) {
    updateProbesTabButtons();
  }

  void setStopPointButton_actionPerformed(ActionEvent e) {
    String newStopPoint = JOptionPane.showInputDialog(this,"Enter new stop point or zero for none:","Set Stop Point",JOptionPane.QUESTION_MESSAGE);
    if (newStopPoint != null) {
      newStopPoint = newStopPoint.trim();
      if (newStopPoint.length() > 0) {
        try {
          long sp = Long.parseLong(newStopPoint);
          if (sp < 0L)
            JOptionPane.showMessageDialog(this,"Input must be an integer >= 0","Invalid stop point",JOptionPane.ERROR_MESSAGE);
          stopPointLabel.setText(Long.toString(sp));
          universe.setStopPoint(sp);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(this,"Input must be an integer >= 0","Invalid stop point",JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
}

class SimulationWindow_stopButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_stopButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.stopButton_actionPerformed(e);
  }
}

class SimulationWindow_runButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_runButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.runButton_actionPerformed(e);
  }
}

class SimulationWindow_this_windowAdapter extends java.awt.event.WindowAdapter {
  SimulationWindow adaptee;

  SimulationWindow_this_windowAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void windowClosing(WindowEvent e) {
    adaptee.this_windowClosing(e);
  }
}

class SimulationWindow_addExternalEnvironmentalConditionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_addExternalEnvironmentalConditionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.addExternalEnvironmentalConditionButton_actionPerformed(e);
  }
}

class SimulationWindow_activateEnvironmentalConditionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_activateEnvironmentalConditionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.activateEnvironmentalConditionButton_actionPerformed(e);
  }
}

class SimulationWindow_terminateEnvironmentalConditionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_terminateEnvironmentalConditionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.terminateEnvironmentalConditionButton_actionPerformed(e);
  }
}

class SimulationWindow_activateCatastropheButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_activateCatastropheButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.activateCatastropheButton_actionPerformed(e);
  }
}

class SimulationWindow_activateRewardFunctionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_activateRewardFunctionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.activateRewardFunctionButton_actionPerformed(e);
  }
}

class SimulationWindow_addExternalCatastropheButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_addExternalCatastropheButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.addExternalCatastropheButton_actionPerformed(e);
  }
}

class SimulationWindow_addExternalRewardFunctionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_addExternalRewardFunctionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.addExternalRewardFunctionButton_actionPerformed(e);
  }
}

class SimulationWindow_terminateRewardFunctionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_terminateRewardFunctionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.terminateRewardFunctionButton_actionPerformed(e);
  }
}

class SimulationWindow_openEnvironmentalConditionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_openEnvironmentalConditionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.openEnvironmentalConditionButton_actionPerformed(e);
  }
}

class SimulationWindow_openRewardFunctionButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_openRewardFunctionButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.openRewardFunctionButton_actionPerformed(e);
  }
}

class SimulationWindow_environmentalConditionsTable_mouseAdapter extends java.awt.event.MouseAdapter {
  SimulationWindow adaptee;

  SimulationWindow_environmentalConditionsTable_mouseAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.environmentalConditionsTable_mouseReleased(e);
  }
}

class SimulationWindow_environmentalConditionsTable_keyAdapter extends java.awt.event.KeyAdapter {
  SimulationWindow adaptee;

  SimulationWindow_environmentalConditionsTable_keyAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.environmentalConditionsTable_keyReleased(e);
  }
}

class SimulationWindow_rewardFunctionTable_mouseAdapter extends java.awt.event.MouseAdapter {
  SimulationWindow adaptee;

  SimulationWindow_rewardFunctionTable_mouseAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.rewardFunctionTable_mouseReleased(e);
  }
}

class SimulationWindow_rewardFunctionTable_keyAdapter extends java.awt.event.KeyAdapter {
  SimulationWindow adaptee;

  SimulationWindow_rewardFunctionTable_keyAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.rewardFunctionTable_keyReleased(e);
  }
}

class SimulationWindow_jMenuItem2_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_jMenuItem2_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem2_actionPerformed(e);
  }
}

class SimulationWindow_jMenuItem3_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_jMenuItem3_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem3_actionPerformed(e);
  }
}

class SimulationWindow_jMenuItem4_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_jMenuItem4_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem4_actionPerformed(e);
  }
}

class SimulationWindow_singleStepButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_singleStepButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.singleStepButton_actionPerformed(e);
  }
}

class SimulationWindow_addExternalProbeButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_addExternalProbeButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.addExternalProbeButton_actionPerformed(e);
  }
}

class SimulationWindow_activateProbeButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_activateProbeButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.activateProbeButton_actionPerformed(e);
  }
}

class SimulationWindow_openProbeButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_openProbeButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.openProbeButton_actionPerformed(e);
  }
}

class SimulationWindow_terminateProbeButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_terminateProbeButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.terminateProbeButton_actionPerformed(e);
  }
}

class SimulationWindow_ioStaticTextField_keyAdapter extends java.awt.event.KeyAdapter {
  SimulationWindow adaptee;

  SimulationWindow_ioStaticTextField_keyAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.ioStaticTextField_keyReleased(e);
  }
}

class SimulationWindow_probesTable_mouseAdapter extends java.awt.event.MouseAdapter {
  SimulationWindow adaptee;

  SimulationWindow_probesTable_mouseAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.probesTable_mouseReleased(e);
  }
}

class SimulationWindow_probesTable_keyAdapter extends java.awt.event.KeyAdapter {
  SimulationWindow adaptee;

  SimulationWindow_probesTable_keyAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.probesTable_keyReleased(e);
  }
}

class SimulationWindow_setStopPointButton_actionAdapter implements java.awt.event.ActionListener {
  SimulationWindow adaptee;

  SimulationWindow_setStopPointButton_actionAdapter(SimulationWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.setStopPointButton_actionPerformed(e);
  }
}

package org.api.archis.gui.conditions;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

import org.api.archis.*;
import org.api.archis.gui.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.*;

import java.util.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.beans.*;

public class Landscape2DWindow extends JFrame
{
  public Landscape2D landscape;
  public Object browser;
  private boolean mouseInViewPanel,mousePressedInViewPanel;
  private Cell selectedCell;
  private String simulationName;
  private Simulation simulation;
  private long lastRepaintTime,repaintFrequency;
  private ScrollerThread scrollerThread;
  private boolean northPressed,southPressed,eastPressed,westPressed;
  private NumberFormat probabilityFormat;
  Icon UP_ICON = new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Up24.gif"));
  Icon DOWN_ICON = new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Down24.gif"));
  Icon LEFT_ICON = new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Back24.gif"));
  Icon RIGHT_ICON = new ImageIcon(this.getClass().getClassLoader().getResource("org/api/archis/resources/Forward24.gif"));
  JPanel contentPane;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel viewPanel = new JPanel();
  JPanel navigatePanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton westButton = new JButton();
  JButton southButton = new JButton();
  JButton eastButton = new JButton();
  JButton northButton = new JButton();
  JPanel jPanel4 = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JSlider zoomSlider = new JSlider();
  JLabel jLabel1 = new JLabel();
  JLabel locationLabel = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel pointerLocationLabel = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel selectedCellLabel = new JLabel();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JButton examineGenomeButton = new JButton();
  JPanel settingsPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JPanel gardenOfEdenPanel = new JPanel();
  TitledBorder titledBorder2;
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  JTextField gardenCenterXTextField = new JTextField();
  JTextField gardenCenterYTextField = new JTextField();
  JLabel jLabel8 = new JLabel();
  JLabel jLabel9 = new JLabel();
  JCheckBox gardenCircularCheckBox = new JCheckBox();
  JTextField gardenRadiusTextField = new JTextField();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel7 = new JLabel();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JTextField maintainEnergyTextField = new JTextField();
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  JCheckBox gardenCheckBox = new JCheckBox();
  JButton jButton3 = new JButton();
  ButtonGroup repaintButtonGroup = new ButtonGroup();
  JLabel jLabel10 = new JLabel();
  JTextField attackSuccessProbabilityTextField = new JTextField();
  JLabel jLabel11 = new JLabel();
  JTextField energyDensityTextField = new JTextField();

  private class ScrollerThread extends Thread
  {
    public boolean die;

    public ScrollerThread()
    {
      super("Landscape2DWindow Scroller Thread");
      super.setPriority(Thread.MIN_PRIORITY);
      super.setDaemon(true);
      die = false;
      super.start();
    }

    public void run()
    {
      for(;;) {
        try {
          sleep(100L);
        } catch (Throwable t) {}

        if (die)
          return;

        if (northPressed)
          ((Landscape2DBrowserJComponent)browser).scrollY(-10);

        if (southPressed)
          ((Landscape2DBrowserJComponent)browser).scrollY(10);

        if (eastPressed)
          ((Landscape2DBrowserJComponent)browser).scrollX(10);

        if (westPressed)
          ((Landscape2DBrowserJComponent)browser).scrollX(-10);

        if (northPressed||southPressed||eastPressed||westPressed)
          locationLabel.setText(Integer.toString(((Landscape2DBrowserJComponent)browser).getTopX())+","+Integer.toString(((Landscape2DBrowserJComponent)browser).getTopY()));
      }
    }
  }

  public Landscape2DWindow(Simulation simulation,Landscape2D landscape)
  {
    probabilityFormat = DecimalFormat.getNumberInstance();
    probabilityFormat.setMinimumFractionDigits(1);
    probabilityFormat.setMaximumFractionDigits(6);
    probabilityFormat.setMinimumIntegerDigits(1);
    probabilityFormat.setMaximumIntegerDigits(16384);
    probabilityFormat.setGroupingUsed(false);

    simulation.newFrameNotify(this);
    this.landscape = landscape;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    this.setSize(640,640);
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width/2)-(this.getWidth()/2),(ss.height/2)-(this.getHeight()/2));
    this.setIconImage(Archis.ICON);
    northPressed = southPressed = eastPressed = westPressed = false;
    scrollerThread = new ScrollerThread();

    simulationName = simulation.getName();
    this.simulation = simulation;
    this.setTitle("["+simulationName+"] Landscape2D");
    browser = new Landscape2DBrowserJComponent(simulation,landscape,1);
    viewPanel.add((Component)browser,BorderLayout.CENTER);
    zoomSlider.setValue(1);
    viewPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    mouseInViewPanel = false;
    mousePressedInViewPanel = false;
    selectedCell = null;
    ((JComponent)browser).addMouseMotionListener(new browser_viewPanel_mouseMotionAdapter(this));
    ((JComponent)browser).addMouseListener(new browser_viewPanel_mouseAdapter(this));
    maintainEnergyTextField.setText(Long.toString(landscape.getMaintainEnergy()));
    attackSuccessProbabilityTextField.setText(probabilityFormat.format((double)landscape.getAttackSuccessProbability()));
    java.util.Map tmp = landscape.getGardenOfEden();
    lastRepaintTime = 0L;
    energyDensityTextField.setText(Long.toString(landscape.getEnergyDensity()));
    if (tmp == null) {
      gardenCheckBox.setSelected(false);
      gardenCenterXTextField.setEnabled(false);
      gardenCenterYTextField.setEnabled(false);
      gardenRadiusTextField.setEnabled(false);
      gardenCircularCheckBox.setEnabled(false);
    } else {
      gardenCheckBox.setSelected(true);
      gardenCenterXTextField.setEnabled(true);
      gardenCenterYTextField.setEnabled(true);
      gardenRadiusTextField.setEnabled(true);
      gardenCircularCheckBox.setEnabled(true);
      gardenCenterXTextField.setText(tmp.get("centerx").toString());
      gardenCenterYTextField.setText(tmp.get("centery").toString());
      gardenRadiusTextField.setText(tmp.get("radius").toString());
      gardenCircularCheckBox.setEnabled(((Boolean)tmp.get("circular")).booleanValue());
    }
  }

  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    titledBorder2 = new TitledBorder("");
    contentPane.setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new Landscape2DWindow_this_windowAdapter(this));
    viewPanel.setBorder(BorderFactory.createEtchedBorder());
    viewPanel.setRequestFocusEnabled(true);
    viewPanel.setLayout(borderLayout1);
    navigatePanel.setBorder(BorderFactory.createEtchedBorder());
    navigatePanel.setLayout(gridBagLayout2);
    jPanel3.setLayout(borderLayout2);
    northButton.setIcon(UP_ICON);
    northButton.addMouseListener(new Landscape2DWindow_northButton_mouseAdapter(this));
    southButton.setIcon(DOWN_ICON);
    southButton.addMouseListener(new Landscape2DWindow_southButton_mouseAdapter(this));
    eastButton.setIcon(RIGHT_ICON);
    eastButton.addMouseListener(new Landscape2DWindow_eastButton_mouseAdapter(this));
    westButton.setIcon(LEFT_ICON);
    westButton.addMouseListener(new Landscape2DWindow_westButton_mouseAdapter(this));
    jPanel4.setLayout(gridBagLayout3);
    zoomSlider.setOrientation(JSlider.HORIZONTAL);
    zoomSlider.setMajorTickSpacing(1);
    zoomSlider.setMaximum(4);
    zoomSlider.setMinimum(1);
    zoomSlider.setMinorTickSpacing(1);
    zoomSlider.setPaintLabels(false);
    zoomSlider.setPaintTicks(true);
    zoomSlider.setPaintTrack(true);
    zoomSlider.addKeyListener(new Landscape2DWindow_zoomSlider_keyAdapter(this));
    zoomSlider.addMouseListener(new Landscape2DWindow_zoomSlider_mouseAdapter(this));
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel1.setText("Zoom");
    locationLabel.setFont(new java.awt.Font("Dialog", 1, 14));
    locationLabel.setText("0,0");
    jLabel3.setFont(new java.awt.Font("Dialog", 0, 14));
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel3.setText("Window:");
    jLabel2.setFont(new java.awt.Font("Dialog", 0, 14));
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel2.setText("Cursor:");
    pointerLocationLabel.setFont(new java.awt.Font("Dialog", 1, 14));
    pointerLocationLabel.setText("-,-");
    jLabel4.setFont(new java.awt.Font("Dialog", 0, 14));
    jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel4.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel4.setText("Selected Cell:");
    selectedCellLabel.setFont(new java.awt.Font("Dialog", 1, 14));
    selectedCellLabel.setRequestFocusEnabled(true);
    selectedCellLabel.setHorizontalAlignment(SwingConstants.LEADING);
    selectedCellLabel.setText("none");
    examineGenomeButton.setFont(new java.awt.Font("Dialog", 0, 10));
    examineGenomeButton.setText("Examine Cell");
    examineGenomeButton.addActionListener(new Landscape2DWindow_examineGenomeButton_actionAdapter(this));
    examineGenomeButton.setEnabled(false);
    settingsPanel.setLayout(gridBagLayout4);
    gardenOfEdenPanel.setBorder(titledBorder2);
    gardenOfEdenPanel.setLayout(gridBagLayout5);
    titledBorder2.setTitle("Garden of Eden");
    jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel5.setText("Center:");
    jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel6.setText("Radius:");
    gardenCenterXTextField.setEnabled(false);
    gardenCenterXTextField.setText("100");
    gardenCenterXTextField.addKeyListener(new Landscape2DWindow_gardenCenterXTextField_keyAdapter(this));
    gardenCenterYTextField.setEnabled(false);
    gardenCenterYTextField.setText("100");
    gardenCenterYTextField.addKeyListener(new Landscape2DWindow_gardenCenterYTextField_keyAdapter(this));
    jLabel8.setText("X");
    jLabel9.setText("Y");
    gardenCircularCheckBox.setEnabled(false);
    gardenCircularCheckBox.setSelected(true);
    gardenCircularCheckBox.setText("Circular Garden");
    gardenCircularCheckBox.addActionListener(new Landscape2DWindow_gardenCircularCheckBox_actionAdapter(this));
    gardenRadiusTextField.setEnabled(false);
    gardenRadiusTextField.setText("80");
    gardenRadiusTextField.addKeyListener(new Landscape2DWindow_gardenRadiusTextField_keyAdapter(this));
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(gridBagLayout6);
    jLabel7.setText("Maintain Energy in Universe:");
    maintainEnergyTextField.setText("0");
    maintainEnergyTextField.addKeyListener(new Landscape2DWindow_maintainEnergyTextField_keyAdapter(this));
    jButton1.setFont(new java.awt.Font("Dialog", 0, 10));
    jButton1.setText("Init Landscape");
    jButton1.addActionListener(new Landscape2DWindow_jButton1_actionAdapter(this));
    jButton2.setFont(new java.awt.Font("Dialog", 0, 10));
    jButton2.setText("Randomize Cell Locations");
    jButton2.addActionListener(new Landscape2DWindow_jButton2_actionAdapter(this));
    jPanel2.setLayout(gridBagLayout7);
    gardenCheckBox.setFont(new java.awt.Font("Dialog", 0, 10));
    gardenCheckBox.setText("Garden of Eden");
    gardenCheckBox.addActionListener(new Landscape2DWindow_gardenCheckBox_actionAdapter(this));
    jButton3.setFont(new java.awt.Font("Dialog", 0, 10));
    jButton3.setText("Repaint Now");
    jButton3.addActionListener(new Landscape2DWindow_jButton3_actionAdapter(this));
    jLabel10.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel10.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel10.setText("Attack Success Probability:");
    attackSuccessProbabilityTextField.setText("0.0");
    attackSuccessProbabilityTextField.addKeyListener(new Landscape2DWindow_attackSuccessProbabilityTextField_keyAdapter(this));
    jLabel11.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel11.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabel11.setText("Energy Density:");
    energyDensityTextField.setText("0");
    energyDensityTextField.addKeyListener(new Landscape2DWindow_energyDensityTextField_keyAdapter(this));
    contentPane.add(viewPanel,        new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0));
    jTabbedPane1.add(navigatePanel,  "Navigate");
    navigatePanel.add(jPanel3,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
    jPanel3.add(eastButton,  BorderLayout.EAST);
    jPanel3.add(southButton, BorderLayout.SOUTH);
    jPanel3.add(westButton, BorderLayout.WEST);
    jPanel3.add(northButton, BorderLayout.NORTH);
    navigatePanel.add(jPanel4,        new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
    jPanel4.add(zoomSlider,                               new GridBagConstraints(2, 1, 1, 2, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
    jPanel4.add(locationLabel,                        new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 0));
    jPanel4.add(jLabel3,                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));
    jPanel4.add(jLabel2,                     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));
    jPanel4.add(pointerLocationLabel,                              new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
    jPanel4.add(selectedCellLabel,                     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
    jPanel4.add(jLabel4,            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
    jPanel4.add(jLabel1,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(4, 3, 0, 1), 0, 0));
    navigatePanel.add(examineGenomeButton,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
    jTabbedPane1.add(settingsPanel,  "Settings");
    contentPane.add(jTabbedPane1,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    settingsPanel.add(gardenOfEdenPanel,   new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    gardenOfEdenPanel.add(jLabel5,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));
    gardenOfEdenPanel.add(jLabel6,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 2), 0, 0));
    gardenOfEdenPanel.add(gardenCenterXTextField,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    gardenOfEdenPanel.add(gardenCenterYTextField,     new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));
    gardenOfEdenPanel.add(jLabel8,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    gardenOfEdenPanel.add(jLabel9,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    gardenOfEdenPanel.add(gardenCircularCheckBox,    new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
    gardenOfEdenPanel.add(gardenRadiusTextField,    new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));
    settingsPanel.add(jPanel1,  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    jPanel1.add(jLabel7,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
    jPanel1.add(maintainEnergyTextField,           new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0));
    jPanel1.add(jPanel2,          new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(jButton1,          new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
    jPanel2.add(jButton2,          new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
    jPanel2.add(gardenCheckBox,        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(jButton3,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(jLabel10,         new GridBagConstraints(0, 2, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    jPanel1.add(attackSuccessProbabilityTextField,         new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
    jPanel1.add(jLabel11,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
    jPanel1.add(energyDensityTextField,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 5), 0, 0));
  }

  protected void finalize()
    throws Throwable
  {
    try {
      scrollerThread.die = true;
    } catch (Throwable t) {}
  }

  void zoomSlider_mouseReleased(MouseEvent e) {
    ((Landscape2DBrowserJComponent)browser).setMagnification(zoomSlider.getValue());
  }

  void zoomSlider_keyReleased(KeyEvent e) {
    ((Landscape2DBrowserJComponent)browser).setMagnification(zoomSlider.getValue());
  }

  void examineGenomeButton_actionPerformed(ActionEvent e) {
    if (selectedCell != null)
      new ExamineGenomeWindow(simulation,selectedCell,"["+simulationName+"] Cell #"+selectedCell.id()).setVisible(true);
  }

  void browser_mouseEntered(MouseEvent e) {
    mouseInViewPanel = true;
    pointerLocationLabel.setText(Integer.toString(((Landscape2DBrowserJComponent)browser).getMouseX())+","+Integer.toString(((Landscape2DBrowserJComponent)browser).getMouseY()));
  }

  void browser_mouseExited(MouseEvent e) {
    mouseInViewPanel = false;
    pointerLocationLabel.setText("-,-");
  }

  void browser_mouseMoved(MouseEvent e) {
    if (mouseInViewPanel)
      pointerLocationLabel.setText(Integer.toString(((Landscape2DBrowserJComponent)browser).getMouseX())+","+Integer.toString(((Landscape2DBrowserJComponent)browser).getMouseY()));
    if (mousePressedInViewPanel) {
      selectedCell = landscape.getCellByLocation(((Landscape2DBrowserJComponent)browser).getMouseX(),((Landscape2DBrowserJComponent)browser).getMouseY());
      if (selectedCell == null) {
        selectedCellLabel.setText("none");
        examineGenomeButton.setEnabled(false);
      } else {
        selectedCellLabel.setText("#" + Long.toString(selectedCell.id()));
        examineGenomeButton.setEnabled(true);
      }
    }
  }

  void browser_mousePressed(MouseEvent e) {
    mousePressedInViewPanel = true;
    selectedCell = landscape.getCellByLocation(((Landscape2DBrowserJComponent)browser).getMouseX(),((Landscape2DBrowserJComponent)browser).getMouseY());
    if (selectedCell == null) {
      selectedCellLabel.setText("none");
      examineGenomeButton.setEnabled(false);
    } else {
      selectedCellLabel.setText("#" + Long.toString(selectedCell.id()));
      examineGenomeButton.setEnabled(true);
    }
  }

  void browser_mouseReleased(MouseEvent e) {
    mousePressedInViewPanel = false;
  }

  public void showInitializeWindow()
  {
    new InitializeLandscapeWindow(simulation,landscape,(JComponent)browser).setVisible(true);
  }

  void closeButton_actionPerformed(ActionEvent e) {
    scrollerThread.die = true;
    this.setVisible(false);
    this.dispose();
  }

  void gardenCheckBox_actionPerformed(ActionEvent e) {
    if (gardenCheckBox.isSelected()) {
      try {
        landscape.setGardenOfEden(Integer.parseInt(gardenCenterXTextField.getText().trim()),Integer.parseInt(gardenCenterYTextField.getText().trim()),Integer.parseInt(gardenRadiusTextField.getText().trim()),gardenCircularCheckBox.isSelected());
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Garden of Eden Locations and Sizes Must be Integers!","Invalid Garden of Eden Parameters",JOptionPane.ERROR_MESSAGE);
      }
    } else {
      try {
        landscape.setGardenOfEden(Integer.parseInt(gardenCenterXTextField.getText().trim()),Integer.parseInt(gardenCenterYTextField.getText().trim()),0,gardenCircularCheckBox.isSelected());
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Garden of Eden Locations and Sizes Must be Integers!","Invalid Garden of Eden Parameters",JOptionPane.ERROR_MESSAGE);
      }
    }
    Map tmp = landscape.getGardenOfEden();
    gardenCheckBox.setSelected((tmp != null));
    gardenCenterXTextField.setEnabled((tmp != null));
    gardenCenterYTextField.setEnabled((tmp != null));
    gardenRadiusTextField.setEnabled((tmp != null));
    gardenCircularCheckBox.setEnabled((tmp != null));
  }

  void jButton2_actionPerformed(ActionEvent e) {
    landscape.randomizeCellLocations();
  }

  void jButton1_actionPerformed(ActionEvent e) {
    new InitializeLandscapeWindow(simulation,landscape,(JComponent)browser).setVisible(true);
  }

  void maintainEnergyTextField_keyReleased(KeyEvent e) {
    try {
      landscape.setMaintainEnergy(Long.parseLong(maintainEnergyTextField.getText().trim()));
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Energy to Maintain Must be an Integer","Invalid Parameter",JOptionPane.ERROR_MESSAGE);
    }
  }

  void gardenCenterXTextField_keyReleased(KeyEvent e) {
    if (!gardenCenterXTextField.isEnabled())
      return;
    try {
      landscape.setGardenOfEden(Integer.parseInt(gardenCenterXTextField.getText().trim()),Integer.parseInt(gardenCenterYTextField.getText().trim()),Integer.parseInt(gardenRadiusTextField.getText().trim()),gardenCircularCheckBox.isSelected());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Garden of Eden Locations and Sizes Must be Integers!","Invalid Garden of Eden Parameters",JOptionPane.ERROR_MESSAGE);
    }
  }

  void gardenCircularCheckBox_actionPerformed(ActionEvent e) {
    if (!gardenCircularCheckBox.isEnabled())
      return;
    try {
      landscape.setGardenOfEden(Integer.parseInt(gardenCenterXTextField.getText().trim()),Integer.parseInt(gardenCenterYTextField.getText().trim()),Integer.parseInt(gardenRadiusTextField.getText().trim()),gardenCircularCheckBox.isSelected());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Garden of Eden Locations and Sizes Must be Integers!","Invalid Garden of Eden Parameters",JOptionPane.ERROR_MESSAGE);
    }
  }

  void gardenCenterYTextField_keyReleased(KeyEvent e) {
    if (!gardenCenterYTextField.isEnabled())
      return;
    try {
      landscape.setGardenOfEden(Integer.parseInt(gardenCenterXTextField.getText().trim()),Integer.parseInt(gardenCenterYTextField.getText().trim()),Integer.parseInt(gardenRadiusTextField.getText().trim()),gardenCircularCheckBox.isSelected());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Garden of Eden Locations and Sizes Must be Integers!","Invalid Garden of Eden Parameters",JOptionPane.ERROR_MESSAGE);
    }
  }

  void gardenRadiusTextField_keyReleased(KeyEvent e) {
    if (!gardenRadiusTextField.isEnabled())
      return;
    try {
      landscape.setGardenOfEden(Integer.parseInt(gardenCenterXTextField.getText().trim()),Integer.parseInt(gardenCenterYTextField.getText().trim()),Integer.parseInt(gardenRadiusTextField.getText().trim()),gardenCircularCheckBox.isSelected());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this,"Garden of Eden Locations and Sizes Must be Integers!","Invalid Garden of Eden Parameters",JOptionPane.ERROR_MESSAGE);
    }
  }

  void jButton3_actionPerformed(ActionEvent e) {
    // repaint now
    ((JComponent)browser).repaint();
  }

  void this_windowClosing(WindowEvent e) {
    scrollerThread.die = true;
    this.setVisible(false);
    this.dispose();
  }

  void northButton_mousePressed(MouseEvent e) {
    northPressed = true;
    scrollerThread.interrupt();
  }

  void southButton_mousePressed(MouseEvent e) {
    southPressed = true;
    scrollerThread.interrupt();
  }

  void eastButton_mousePressed(MouseEvent e) {
    eastPressed = true;
    scrollerThread.interrupt();
  }

  void westButton_mousePressed(MouseEvent e) {
    westPressed = true;
    scrollerThread.interrupt();
  }

  void northButton_mouseReleased(MouseEvent e) {
    northPressed = false;
  }

  void southButton_mouseReleased(MouseEvent e) {
    southPressed = false;
  }

  void eastButton_mouseReleased(MouseEvent e) {
    eastPressed = false;
  }

  void westButton_mouseReleased(MouseEvent e) {
    westPressed = false;
  }

  void northButton_mouseExited(MouseEvent e) {
    northPressed = false;
  }

  void southButton_mouseExited(MouseEvent e) {
    southPressed = false;
  }

  void westButton_mouseExited(MouseEvent e) {
    westPressed = false;
  }

  void eastButton_mouseExited(MouseEvent e) {
    eastPressed = false;
  }

  void attackSuccessProbabilityTextField_keyReleased(KeyEvent e) {
    String v = attackSuccessProbabilityTextField.getText().trim();
    if (v.length() > 0) {
      try {
        landscape.setAttackSuccessProbability(Float.parseFloat(v));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Value must be a real number","Invalid Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  void energyDensityTextField_keyReleased(KeyEvent e) {
    String v = energyDensityTextField.getText().trim();
    if (v.length() > 0) {
      try {
        landscape.setEnergyDensity(Long.parseLong(v));
        energyDensityTextField.setText(Long.toString(landscape.getEnergyDensity()));
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"Value must be a real number","Invalid Value",JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}

class Landscape2DWindow_zoomSlider_mouseAdapter extends java.awt.event.MouseAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_zoomSlider_mouseAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.zoomSlider_mouseReleased(e);
  }
}

class Landscape2DWindow_zoomSlider_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_zoomSlider_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.zoomSlider_keyReleased(e);
  }
}

class browser_viewPanel_mouseAdapter extends java.awt.event.MouseAdapter {
  Landscape2DWindow adaptee;

  browser_viewPanel_mouseAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseEntered(MouseEvent e) {
    adaptee.browser_mouseEntered(e);
  }
  public void mouseExited(MouseEvent e) {
    adaptee.browser_mouseExited(e);
  }
  public void mousePressed(MouseEvent e) {
    adaptee.browser_mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.browser_mouseReleased(e);
  }
}

class browser_viewPanel_mouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
  Landscape2DWindow adaptee;

  browser_viewPanel_mouseMotionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseMoved(MouseEvent e) {
    adaptee.browser_mouseMoved(e);
  }
  public void mouseDragged(MouseEvent e) {
    adaptee.browser_mouseMoved(e);
  }
}

class Landscape2DWindow_jButton2_actionAdapter implements java.awt.event.ActionListener {
  Landscape2DWindow adaptee;

  Landscape2DWindow_jButton2_actionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class Landscape2DWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  Landscape2DWindow adaptee;

  Landscape2DWindow_jButton1_actionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class Landscape2DWindow_maintainEnergyTextField_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_maintainEnergyTextField_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.maintainEnergyTextField_keyReleased(e);
  }
}

class Landscape2DWindow_gardenCenterXTextField_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_gardenCenterXTextField_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.gardenCenterXTextField_keyReleased(e);
  }
}

class Landscape2DWindow_gardenCircularCheckBox_actionAdapter implements java.awt.event.ActionListener {
  Landscape2DWindow adaptee;

  Landscape2DWindow_gardenCircularCheckBox_actionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.gardenCircularCheckBox_actionPerformed(e);
  }
}

class Landscape2DWindow_gardenCenterYTextField_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_gardenCenterYTextField_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.gardenCenterYTextField_keyReleased(e);
  }
}

class Landscape2DWindow_gardenRadiusTextField_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_gardenRadiusTextField_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.gardenRadiusTextField_keyReleased(e);
  }
}

class Landscape2DWindow_jButton3_actionAdapter implements java.awt.event.ActionListener {
  Landscape2DWindow adaptee;

  Landscape2DWindow_jButton3_actionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton3_actionPerformed(e);
  }
}

class Landscape2DWindow_gardenCheckBox_actionAdapter implements java.awt.event.ActionListener {
  Landscape2DWindow adaptee;

  Landscape2DWindow_gardenCheckBox_actionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.gardenCheckBox_actionPerformed(e);
  }
}

class Landscape2DWindow_examineGenomeButton_actionAdapter implements java.awt.event.ActionListener {
  Landscape2DWindow adaptee;

  Landscape2DWindow_examineGenomeButton_actionAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.examineGenomeButton_actionPerformed(e);
  }
}

class Landscape2DWindow_this_windowAdapter extends java.awt.event.WindowAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_this_windowAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void windowClosing(WindowEvent e) {
    adaptee.this_windowClosing(e);
  }
}

class Landscape2DWindow_northButton_mouseAdapter extends java.awt.event.MouseAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_northButton_mouseAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.northButton_mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.northButton_mouseReleased(e);
  }
  public void mouseExited(MouseEvent e) {
    adaptee.northButton_mouseExited(e);
  }
}

class Landscape2DWindow_southButton_mouseAdapter extends java.awt.event.MouseAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_southButton_mouseAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.southButton_mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.southButton_mouseReleased(e);
  }
  public void mouseExited(MouseEvent e) {
    adaptee.southButton_mouseExited(e);
  }
}

class Landscape2DWindow_eastButton_mouseAdapter extends java.awt.event.MouseAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_eastButton_mouseAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.eastButton_mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.eastButton_mouseReleased(e);
  }
  public void mouseExited(MouseEvent e) {
    adaptee.eastButton_mouseExited(e);
  }
}

class Landscape2DWindow_westButton_mouseAdapter extends java.awt.event.MouseAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_westButton_mouseAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void mousePressed(MouseEvent e) {
    adaptee.westButton_mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    adaptee.westButton_mouseReleased(e);
  }
  public void mouseExited(MouseEvent e) {
    adaptee.westButton_mouseExited(e);
  }
}

class Landscape2DWindow_attackSuccessProbabilityTextField_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_attackSuccessProbabilityTextField_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.attackSuccessProbabilityTextField_keyReleased(e);
  }
}

class Landscape2DWindow_energyDensityTextField_keyAdapter extends java.awt.event.KeyAdapter {
  Landscape2DWindow adaptee;

  Landscape2DWindow_energyDensityTextField_keyAdapter(Landscape2DWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.energyDensityTextField_keyReleased(e);
  }
}


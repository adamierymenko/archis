package org.api.archis.gui.conditions;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

import javax.swing.border.*;

import java.beans.*;

import javax.swing.event.*;

import org.api.archis.*;
import org.api.archis.gui.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.*;

public class InitializeLandscapeWindow extends JFrame
{
  private Landscape2D landscape;
  private String lastImageFile;
  private Map imageSizeCache;
  private JComponent l2dwindow;
  private Image currentImage;
  JPanel contentPane;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JPanel jPanel3 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JLabel estimatedLandscapeSizeLabel = new JLabel();
  JTabbedPane initTypeTabbedPane = new JTabbedPane();
  JTextField ySizeTextField = new JTextField();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel3 = new JLabel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JTextField xSizeTextField = new JTextField();
  JLabel jLabel4 = new JLabel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JTextField imageFileTextField = new JTextField();
  JPanel jPanel2 = new JPanel();
  JButton jButton3 = new JButton();

  public InitializeLandscapeWindow(Simulation simulation,Landscape2D landscape,JComponent l2dwindow)
  {
    simulation.newFrameNotify(this);
    this.landscape = landscape;
    this.l2dwindow = l2dwindow;
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    this.setSize(600,175);
    Dimension ss = this.getToolkit().getScreenSize();
    this.setLocation((ss.width / 2) - (this.getWidth() / 2),(ss.height / 2) - (this.getHeight() / 2));
    this.setIconImage(Archis.ICON);
    this.setTitle("[" + simulation.getName() + "] Initialize Landscape2D");
    lastImageFile = "asdfqwerty";
    imageSizeCache = new HashMap(16,0.75F);
    updateEstimatedSize();
  }
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(gridBagLayout1);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    jButton1.setText("Cancel");
    jButton1.addActionListener(new InitializeLandscapeWindow_jButton1_actionAdapter(this));
    jButton2.setText("OK");
    jButton2.addActionListener(new InitializeLandscapeWindow_jButton2_actionAdapter(this));
    estimatedLandscapeSizeLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    estimatedLandscapeSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    estimatedLandscapeSizeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
    estimatedLandscapeSizeLabel.setText("(Estimated Size of New Landscape: 0k)");
    ySizeTextField.setText("768");
    ySizeTextField.addKeyListener(new InitializeLandscapeWindow_ySizeTextField_keyAdapter(this));
    jPanel1.setBorder(BorderFactory.createEtchedBorder());
    jPanel1.setLayout(gridBagLayout2);
    jLabel2.setForeground(Color.black);
    jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel2.setText("X");
    jLabel1.setText("Landscape Size:");
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel3.setText("Y");
    xSizeTextField.setText("1024");
    xSizeTextField.addKeyListener(new InitializeLandscapeWindow_xSizeTextField_keyAdapter(this));
    jLabel4.setText("Landscape Topography File:");
    imageFileTextField.setToolTipText("");
    imageFileTextField.setText("");
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel2.setLayout(gridBagLayout3);
    jButton3.setText("Browse");
    jButton3.addActionListener(new InitializeLandscapeWindow_jButton3_actionAdapter(this));
    initTypeTabbedPane.addChangeListener(new InitializeLandscapeWindow_initTypeTabbedPane_changeAdapter(this));
    contentPane.add(jPanel3,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    jPanel3.add(jButton2, null);
    jPanel3.add(jButton1, null);
    contentPane.add(estimatedLandscapeSizeLabel,     new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    contentPane.add(initTypeTabbedPane,   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    initTypeTabbedPane.add(jPanel1,   "Blank Landscape");
    jPanel1.add(jLabel1,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 5, 2, 2), 0, 0));
    jPanel1.add(xSizeTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
    jPanel1.add(ySizeTextField,  new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 5), 0, 0));
    jPanel1.add(jLabel2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0));
    jPanel1.add(jLabel3,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 5), 0, 0));
    initTypeTabbedPane.add(jPanel2,   "Landscape From Image");
    jPanel2.add(jLabel4,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 5, 2, 2), 0, 0));
    jPanel2.add(imageFileTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
    jPanel2.add(jButton3,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 5), 0, 0));
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.setVisible(false);
    this.dispose();
  }

  private void updateEstimatedSize()
  {
    String selectedTab = initTypeTabbedPane.getTitleAt(initTypeTabbedPane.getSelectedIndex());
    if ("Blank Landscape".equals(selectedTab)) {
      try {
        int xs = Integer.parseInt(xSizeTextField.getText().trim());
        int ys = Integer.parseInt(ySizeTextField.getText().trim());
        estimatedLandscapeSizeLabel.setText("(Estimated Size of New Landscape: "+(Landscape2D.estimateMemoryUsage(xs,ys) / 1024L)+"k)");
      } catch (Throwable t) {
        estimatedLandscapeSizeLabel.setText("(Estimated Size of New Landscape: 0k)");
      }
      lastImageFile = "asdfqwerty";
    } else if ("Landscape From Image".equals(selectedTab)) {
      try {
        if (!imageFileTextField.getText().trim().equalsIgnoreCase(lastImageFile)) {
          File imageFile = new File((lastImageFile = imageFileTextField.getText().trim()));
          if (imageFile.exists() && imageFile.canRead()) {
            String cacheId = Long.toHexString(imageFile.length())+imageFile.getAbsolutePath();
            Object ce = imageSizeCache.get(cacheId);
            if (ce != null)
              estimatedLandscapeSizeLabel.setText(ce.toString());
            else {
              Image im = this.getToolkit().getImage(imageFile.getAbsolutePath());
              SizeEstimateImageObserver imgobs = new SizeEstimateImageObserver(cacheId);
              im.getWidth(imgobs);
              im.getHeight(imgobs);
              currentImage = im;
            }
          } else estimatedLandscapeSizeLabel.setText("(Estimated Size of New Landscape: 0k)");
        }
      } catch (Throwable t) {
        estimatedLandscapeSizeLabel.setText("(Estimated Size of New Landscape: 0k)");
      }
    }
  }

  void initTypeTabbedPane_stateChanged(ChangeEvent e) {
    updateEstimatedSize();
  }

  // ImageObserver for getting image sizes and updating estimatedLandscapeSizeLabel
  private class SizeEstimateImageObserver implements ImageObserver
  {
    private int xsize = -1,ysize = -1;
    private String cacheId;

    public SizeEstimateImageObserver(String cacheId)
    {
      this.cacheId = cacheId;
    }

    public boolean imageUpdate(Image img,int infoflags,int x,int y,int width,int height)
    {
      if ((infoflags & ImageObserver.WIDTH) != 0)
        xsize = width;
      if ((infoflags & ImageObserver.HEIGHT) != 0)
        ysize = height;
      if ((xsize > 0)&&(ysize > 0)) {
        String sizeStr;
        estimatedLandscapeSizeLabel.setText((sizeStr = "(Estimated Size of New Landscape: " + (Landscape2D.estimateMemoryUsage(xsize,ysize) / 1024L) + "k)"));
        imageSizeCache.put(cacheId,sizeStr);
        return true;
      }
      return true;
    }
  }

  void jButton3_actionPerformed(ActionEvent e) {
    // browse for images
    JFileChooser fc = new JFileChooser();
    fc.setMultiSelectionEnabled(false);
    fc.setFileFilter(new BrowseFileFilter());
    fc.setVisible(true);
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      if (f != null) {
        imageFileTextField.setText(f.getAbsolutePath());
        updateEstimatedSize();
      }
    }
  }

  private static class BrowseFileFilter extends javax.swing.filechooser.FileFilter
  {
    public boolean accept(File f)
    {
      if (f.isDirectory())
        return true;
      String n = f.getName().toLowerCase();
      if (n.endsWith(".jpg"))
        return true;
      else if (n.endsWith(".gif"))
        return true;
      else if (n.endsWith(".png"))
        return true;
      else if (n.endsWith(".bmp"))
        return true;
      return false;
    }

    public String getDescription()
    {
      return "Image files [jpg/gif/png/bmp]";
    }
  }

  void jButton2_actionPerformed(ActionEvent e) {
    // ok -- init landscape
    String selectedTab = initTypeTabbedPane.getTitleAt(initTypeTabbedPane.getSelectedIndex());
    if ("Blank Landscape".equals(selectedTab)) {
      try {
        int xs = Integer.parseInt(xSizeTextField.getText().trim());
        int ys = Integer.parseInt(ySizeTextField.getText().trim());
        if ((xs <= 0)||(ys <= 0))
          JOptionPane.showMessageDialog(this,"X and Y size must be positive integers","Invalid Size",JOptionPane.ERROR_MESSAGE);
        landscape.initFlatLandscape(xs,ys);
        l2dwindow.repaint();
        this.setVisible(false);
        this.dispose();
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this,"X and Y size must be positive integers","Invalid Size",JOptionPane.ERROR_MESSAGE);
      }
    } else if ("Landscape From Image".equals(selectedTab)) {
      if (currentImage != null) {
        BufferedImage bi = new BufferedImage(currentImage.getWidth(null),currentImage.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.drawImage(currentImage,null,null);
        landscape.initImageLandscape(bi);
        l2dwindow.repaint();
        this.setVisible(false);
        this.dispose();
      }
    }
  }

  void xSizeTextField_keyReleased(KeyEvent e) {
    updateEstimatedSize();
  }

  void ySizeTextField_keyReleased(KeyEvent e) {
    updateEstimatedSize();
  }
}

class InitializeLandscapeWindow_jButton1_actionAdapter implements java.awt.event.ActionListener {
  InitializeLandscapeWindow adaptee;

  InitializeLandscapeWindow_jButton1_actionAdapter(InitializeLandscapeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton1_actionPerformed(e);
  }
}

class InitializeLandscapeWindow_initTypeTabbedPane_changeAdapter implements javax.swing.event.ChangeListener {
  InitializeLandscapeWindow adaptee;

  InitializeLandscapeWindow_initTypeTabbedPane_changeAdapter(InitializeLandscapeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void stateChanged(ChangeEvent e) {
    adaptee.initTypeTabbedPane_stateChanged(e);
  }
}

class InitializeLandscapeWindow_jButton3_actionAdapter implements java.awt.event.ActionListener {
  InitializeLandscapeWindow adaptee;

  InitializeLandscapeWindow_jButton3_actionAdapter(InitializeLandscapeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton3_actionPerformed(e);
  }
}

class InitializeLandscapeWindow_jButton2_actionAdapter implements java.awt.event.ActionListener {
  InitializeLandscapeWindow adaptee;

  InitializeLandscapeWindow_jButton2_actionAdapter(InitializeLandscapeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButton2_actionPerformed(e);
  }
}

class InitializeLandscapeWindow_xSizeTextField_keyAdapter extends java.awt.event.KeyAdapter {
  InitializeLandscapeWindow adaptee;

  InitializeLandscapeWindow_xSizeTextField_keyAdapter(InitializeLandscapeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.xSizeTextField_keyReleased(e);
  }
}

class InitializeLandscapeWindow_ySizeTextField_keyAdapter extends java.awt.event.KeyAdapter {
  InitializeLandscapeWindow adaptee;

  InitializeLandscapeWindow_ySizeTextField_keyAdapter(InitializeLandscapeWindow adaptee) {
    this.adaptee = adaptee;
  }
  public void keyReleased(KeyEvent e) {
    adaptee.ySizeTextField_keyReleased(e);
  }
}

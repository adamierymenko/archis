package org.api.archis;

import java.util.*;
import java.io.*;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.UIDefaults;

import org.api.archis.gui.*;
import org.api.archis.utils.*;

/**
 * <p>Main class to be invoked to start the application</p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class Archis
{
  /**
   * Number of I/O channels in total (don't change unless you're prepared to
   * overhaul some shiat!)
   */
  public static final int CHANNEL_COUNT = 8;

  /**
   * Program version
   */
  public static final String ARCHIS_VERSION = "2.2.0";
  /**
   * Program description
   */
  public static final String ARCHIS_DESCRIPTION = "Archis Version 2.2.0 \u00A92001-2003 Adam Ierymenko, All Rights Reserved";

  /**
   * Size of cell state memory in ints
   */
  public static final int CELL_STATE_MEMORY_SIZE = 128;

  /**
   * Icon for GUI
   */
  public static Image ICON;

  /**
   * Main method
   *
   * @param argv Command line arguments
   */
  public static void main(String[] argv)
  {
    // Try to set a nicer look and feel...
    boolean lafset = false;
    try {
      // Windoze look and feel
      Class.forName("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      lafset = true;
    } catch (Throwable t) {}
    if (!lafset) {
      // MacOSX look and feel on OSX
      try {
        Class.forName("com.apple.mrj.swing.MacLookAndFeel");
        UIManager.setLookAndFeel("com.apple.mrj.swing.MacLookAndFeel");
        lafset = true;
      } catch (Throwable t) {}
    }
    if (!lafset) {
      // GTK look and feel on Unix boxen
      try {
        Class.forName("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        lafset = true;
      } catch (Throwable t) {}
    }

    // OS-specific setup stuff
    String osName = System.getProperty("os.name","").toLowerCase();
    if ((osName.indexOf("xp") > 0)&&(osName.indexOf("win") >= 0)) {
      // Turn off off-screen direct draw acceleration or the colors look like
      // a bad acid trip...
      System.setProperty("sun.java2d.ddoffscreen","false");
    }

    // Get icon image
    ICON = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("org/api/archis/resources/spiral.gif")).getImage();

    // Create GUI window
    new ArchisWindow().setVisible(true);
  }
}

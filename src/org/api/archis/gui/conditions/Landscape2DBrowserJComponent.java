package org.api.archis.gui.conditions;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

import org.api.archis.*;
import org.api.archis.gui.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.universe.environmentalconditions.*;
import org.api.archis.utils.*;

import java.awt.event.MouseEvent;

/**
 * A Swing JComponent that allows a Landscape2D landscape to be viewed
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class Landscape2DBrowserJComponent extends JComponent implements Landscape2DObserver
{
  // Colors
  private static final Color OUTOFBOUNDS_COLOR = Color.lightGray;
  private static final Color FOOD_COLOR = Color.green.brighter();
  private static final Color INFO_COLOR = Color.blue.brighter();
  private static final Color SOLID_COLOR = Color.gray;
  private static final Color CELL_COLOR_DEFAULT = Color.white;

  private Landscape2D landscape;
  private int magnification;
  private int topX,topY;
  private int mouseX,mouseY;
  private Object paintSync;
  private CellColorCoder colorCoder;
  private BufferedImage tempImage;

  public static class GenerationColorCoder implements CellColorCoder
  {
    /**
     * Color range for generations (oldest to newest)
     */
    private static Color[] colorRange;
    private static double colorRangeSize;
    static
    {
      colorRange = new Color[128];
      colorRangeSize = 128.0;
      int c = 0;
      for(int i=0;i<128;i++) {
        colorRange[i] = new Color(255,c,c);
        ++c; ++c;
      }
    }

    // Universe for getting min genome size as point of reference
    private Universe universe;

    public GenerationColorCoder(Universe universe)
    {
      this.universe = universe;
    }

    public Color getColor(Cell c)
    {
      if (c == null)
        return colorRange[0];
      long max;
      if ((max = universe.maxGenerationThisRun()) < 4L)
        return colorRange[colorRange.length-1];
      long min = universe.minGenerationThisRun();
      int n = (int)Math.round(((double)(c.generation() - min) / (double)(max - min)) * colorRangeSize);
      if (n >= colorRange.length)
        n = colorRange.length-1;
      else if (n < 0)
        n = 0;
      return colorRange[n];
    }
  }

  /**
   * Constructs a new landscape browser with the given landscape and
   * magnification.
   *
   * @param simulation Simulation we're running inside
   * @param landscape Landscape to browse
   * @param magnification Magnification (>= 1)
   */
  public Landscape2DBrowserJComponent(Simulation simulation,Landscape2D landscape,int magnification)
  {
    super();
    colorCoder = new GenerationColorCoder(simulation.universe());
    super.setDoubleBuffered(true);
    super.setOpaque(true);
    super.setBorder(null);
    paintSync = new Object();
    if (magnification < 1)
      throw new IllegalArgumentException("magnification must be >= 1");
    this.landscape = landscape;
    this.magnification = magnification;
    topX = topY = 0;
    this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    tempImage = null;
    landscape.addObserver(this);
  }

  public void processMouseMotionEvent(MouseEvent e)
  {
    if ((e.getX() >= 0)&&(e.getY() >= 0)) {
      mouseX = e.getX() / magnification;
      mouseY = e.getY() / magnification;
    }
    super.processMouseMotionEvent(e);
  }

  /**
   * Gets the X location of the mouse within the landscape
   *
   * @return X location of mouse
   */
  public int getMouseX()
  {
    return mouseX;
  }

  /**
   * Gets the Y location of the mouse within the landscape
   *
   * @return Y location of mouse
   */
  public int getMouseY()
  {
    return mouseY;
  }

  /**
   * Sets the magnification
   *
   * @param magnification Value must be >= 1 or errors will occur
   */
  public synchronized void setMagnification(int magnification)
  {
    if (magnification < 1)
      throw new IllegalArgumentException("magnification must be >= 1");
    if (magnification != this.magnification) {
      this.magnification = magnification;
      repaint();
    }
  }

  /**
   * Gets the magnification
   *
   * @return Magnification
   */
  public int getMagnification()
  {
    return magnification;
  }

  /**
   * Returns the top left X coordinate
   *
   * @return Top left X coordinate
   */
  public int getTopX()
  {
    return topX;
  }

  /**
   * Returns the top left Y coordinate
   *
   * @return Top left Y coordinate
   */
  public int getTopY()
  {
    return topY;
  }

  /**
   * Scrolls left or right along X dimension
   *
   * @param delta Change in X (negative for left, positive for right)
   */
  public void scrollX(int delta)
  {
    int ltx = topX;
    topX += delta;
    if (topX < 0) {
      topX = 0;
      if (topX != ltx)
        repaint();
      return;
    } else if (topX >= landscape.getSizeX()) {
      topX = landscape.getSizeX() - 1;
      if (topX != ltx)
        repaint();
      return;
    }
    Graphics graphics = this.getGraphics();
//    synchronized(paintSync) {
      graphics.copyArea((delta > 0) ? delta*magnification : 0,0,this.getWidth()-(Math.abs(delta)*magnification),this.getHeight(),delta*magnification*-1,0);
      int width = getWidth() / magnification;
      int height = getHeight() / magnification;
      int lwidth = landscape.getSizeX();
      int lheight = landscape.getSizeY();
      int lx,ly;
      int lim = (delta > 0) ? width : Math.abs(delta);

      for (int x=((delta > 0) ? (width - delta) : 0);x<lim;x++) {
        for (int y=0;y<height;y++) {
          lx = x + topX;
          ly = y + topY;

          if ((lx < lwidth)&&(ly < lheight)) {
            if (landscape.isOccupied(lx,ly)) {
              graphics.setColor((colorCoder == null) ? CELL_COLOR_DEFAULT : colorCoder.getColor(landscape.getCellByLocation(lx,ly)));
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            } else if (landscape.hasEnergy(lx,ly)) {
              graphics.setColor(FOOD_COLOR);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            } else if (landscape.isSolid(lx,ly)) {
              graphics.setColor(SOLID_COLOR);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            } else {
              graphics.setColor(Color.black);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            }
          } else {
            graphics.setColor(OUTOFBOUNDS_COLOR);
            graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  /**
   * Scrolls up or down along Y dimension
   *
   * @param delta Change in Y (negative for up, positive for down)
   */
  public void scrollY(int delta)
  {
    int lty = topY;
    topY += delta;
    if (topY < 0) {
      topY = 0;
      if (topY != lty)
        repaint();
      return;
    } else if (topY >= landscape.getSizeY()) {
      topY = landscape.getSizeY() - 1;
      if (topY != lty)
        repaint();
      return;
    }
    Graphics graphics = this.getGraphics();
//    synchronized(paintSync) {
      graphics.copyArea(0,(delta > 0) ? delta*magnification : 0,this.getWidth(),this.getHeight()-(Math.abs(delta)*magnification),0,delta*magnification*-1);
      int width = getWidth() / magnification;
      int height = getHeight() / magnification;
      int lwidth = landscape.getSizeX();
      int lheight = landscape.getSizeY();
      int lx,ly;
      int lim = (delta > 0) ? height : Math.abs(delta);

      for (int x=0;x<width;x++) {
        for (int y=((delta > 0) ? height - delta : 0);y<lim;y++) {
          lx = x + topX;
          ly = y + topY;

          if ((lx < lwidth)&&(ly < lheight)) {
            if (landscape.isOccupied(lx,ly)) {
              graphics.setColor((colorCoder == null) ? CELL_COLOR_DEFAULT : colorCoder.getColor(landscape.getCellByLocation(lx,ly)));
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            } else if (landscape.hasEnergy(lx,ly)) {
              graphics.setColor(FOOD_COLOR);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            } else if (landscape.isSolid(lx,ly)) {
              graphics.setColor(SOLID_COLOR);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            } else {
              graphics.setColor(Color.black);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            }
          } else {
            graphics.setColor(OUTOFBOUNDS_COLOR);
            graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  /**
   * Sets cell color coder
   *
   * @param cc Cell color coder or null to disable color coding (all white)
   */
  public void setColorCoder(CellColorCoder cc)
  {
    colorCoder = cc;
  }

  /**
   * Paints the component
   *
   * @param graphics Graphics object to paint into
   */
  public void paintComponent(Graphics graphics)
  {
//    synchronized(paintSync) {
      int width = getWidth() / magnification;
      int height = getHeight() / magnification;
      int lwidth = landscape.getSizeX();
      int lheight = landscape.getSizeY();
      int lx,ly;

      if (magnification <= 1) {
        if (tempImage == null)
          tempImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        else if ((tempImage.getWidth() != width)||(tempImage.getHeight() != height))
          tempImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for (int x=0;x<width;x++) {
          for (int y=0;y<height;y++) {
            lx = x + topX;
            ly = y + topY;

            if ((lx < lwidth)&&(ly < lheight)) {
              if (landscape.isOccupied(lx,ly))
                tempImage.setRGB(x,y,((colorCoder == null) ? CELL_COLOR_DEFAULT : colorCoder.getColor(landscape.getCellByLocation(lx,ly))).getRGB());
              else if (landscape.hasEnergy(lx,ly))
                tempImage.setRGB(x,y,FOOD_COLOR.getRGB());
              else tempImage.setRGB(x,y,(landscape.isSolid(lx,ly) ? SOLID_COLOR : Color.black).getRGB());
            } else tempImage.setRGB(x,y,OUTOFBOUNDS_COLOR.getRGB());
          }
        }
        graphics.drawImage(tempImage,0,0,null);
      } else {
        tempImage = null;
        for (int x=0;x<width;x++) {
          for (int y=0;y<height;y++) {
            lx = x + topX;
            ly = y + topY;

            if ((lx < lwidth)&&(ly < lheight)) {
              if (landscape.isOccupied(lx,ly)) {
                graphics.setColor((colorCoder == null) ? CELL_COLOR_DEFAULT : colorCoder.getColor(landscape.getCellByLocation(lx,ly)));
                graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
              } else if (landscape.hasEnergy(lx,ly)) {
                graphics.setColor(FOOD_COLOR);
                graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
              } else if (landscape.isSolid(lx,ly)) {
                graphics.setColor(SOLID_COLOR);
                graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
              } else {
                graphics.setColor(Color.black);
                graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
              }
            } else {
              graphics.setColor(OUTOFBOUNDS_COLOR);
              graphics.fillRect(x*magnification,y*magnification,magnification,magnification);
            }
          }
        }
      }
//    }
  }

  // -------------------------------------------------------------------------
  // Methods to implement Landscape2DObserver
  // -------------------------------------------------------------------------

  public void backgroundChanged()
  {
    repaint();
  }

  public void cellAdded(int x,int y,Cell cell)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        if (((x >= topX) && (x < (topX + width)))&&((y >= topY) && (y < (topY + height)))) {
          Graphics graphics = getGraphics();
          if (graphics != null) {
            graphics.setColor(((colorCoder == null) ? CELL_COLOR_DEFAULT : colorCoder.getColor(cell)));
            graphics.fillRect((x-topX)*magnification,(y-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  public void cellMoved(int oldX,int oldY,int newX,int newY,Cell cell)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        Graphics graphics = getGraphics();

        if (graphics != null) {
          // Remove old cell pixel
          if (((oldX >= topX) && (oldX < (topX + width)))&&((oldY >= topY) && (oldY < (topY + height)))) {
            graphics.setColor(Color.black);
            graphics.fillRect((oldX-topX)*magnification,(oldY-topY)*magnification,magnification,magnification);
          }

          // Draw new cell pixel
          if (((newX >= topX) && (newX < (topX + width)))&&((newY >= topY) && (newY < (topY + height)))) {
            graphics.setColor(((colorCoder == null) ? CELL_COLOR_DEFAULT : colorCoder.getColor(cell)));
            graphics.fillRect((newX-topX)*magnification,(newY-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  public void cellRemoved(int x,int y)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        if (((x >= topX) && (x < (topX + width)))&&((y >= topY) && (y < (topY + height)))) {
          Graphics graphics = getGraphics();
          if (graphics != null) {
            if (landscape.hasEnergy(x,y))
              graphics.setColor(FOOD_COLOR);
            else graphics.setColor((landscape.isSolid(x,y) ? SOLID_COLOR : Color.black));
            graphics.fillRect((x-topX)*magnification,(y-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  public void foodAdded(int x,int y)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        if (((x >= topX) && (x < (topX + width)))&&((y >= topY) && (y < (topY + height)))) {
          Graphics graphics = getGraphics();
          if (graphics != null) {
            graphics.setColor(FOOD_COLOR);
            graphics.fillRect((x-topX)*magnification,(y-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  public void infoAdded(int x,int y)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        if (((x >= topX) && (x < (topX + width)))&&((y >= topY) && (y < (topY + height)))) {
          Graphics graphics = getGraphics();
          if (graphics != null) {
            graphics.setColor(INFO_COLOR);
            graphics.fillRect((x-topX)*magnification,(y-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  public void infoRemoved(int x,int y)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        if (((x >= topX) && (x < (topX + width)))&&((y >= topY) && (y < (topY + height)))) {
          Graphics graphics = getGraphics();
          if (graphics == null) {
            graphics.setColor((landscape.isSolid(x,y) ? SOLID_COLOR : Color.black));
            graphics.fillRect((x-topX)*magnification,(y-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }

  public void foodRemoved(int x,int y)
  {
//    synchronized(paintSync) {
      if (isVisible()) {
        int width = getWidth() / magnification;
        int height = getHeight() / magnification;
        if (((x >= topX) && (x < (topX + width)))&&((y >= topY) && (y < (topY + height)))) {
          Graphics graphics = getGraphics();
          if (graphics == null) {
            graphics.setColor((landscape.isSolid(x,y) ? SOLID_COLOR : Color.black));
            graphics.fillRect((x-topX)*magnification,(y-topY)*magnification,magnification,magnification);
          }
        }
      }
//    }
  }
}

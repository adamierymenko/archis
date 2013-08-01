package org.api.archis.gui;

import java.util.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import org.api.archis.utils.RealNumberDataListener;

import java.awt.event.*;

/**
 * A simple horizontal line graph for double precision values
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class HorizontalLineGraph extends JComponent implements RealNumberDataListener,Scrollable
{
  private double[] data;
  private long[] indexes;
  private double floor;
  private double ceiling;
  private int dataSizeIncrement;
  private int currentLocation;
  private int dataPtr;
  private Color plotColor,plotBackground;
  private boolean autoScale;
  private JSlider scrollBar;

  /**
   * Constructs a new horizontal line graph
   *
   * @param initialDataSize Initial size of data array
   * @param dataSizeIncrement Size to increment array when it's full
   * @param plotColor Color of plot line
   * @param plotBackground Background color of plot
   */
  public HorizontalLineGraph(int initialDataSize,int dataSizeIncrement,Color plotColor,Color plotBackground)
  {
    setOpaque(true);
    setBackground(plotBackground);
    data = new double[initialDataSize];
    indexes = new long[initialDataSize];
    this.dataSizeIncrement = dataSizeIncrement;
    this.plotColor = plotColor;
    this.plotBackground = plotBackground;
    autoScale = true;
    floor = 0.0;
    ceiling = 0.0;
    currentLocation = 0;
    dataPtr = 0;
    scrollBar = null;
  }

  /**
   * Mouse motion listener for scroll bar
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private class ScrollBarMouseMotionListener implements MouseMotionListener
  {
    public void mouseDragged(MouseEvent e)
    {
      if (scrollBar.getValue() != currentLocation) {
        currentLocation = scrollBar.getValue();
        repaint();
      }
    }
    public void mouseMoved(MouseEvent e)
    {
    }
  }

  /**
   * Sets a scroll bar to be the scroll bar for this graph
   *
   * @param scrollBar Scroll bar
   */
  public void setScrollBar(JSlider scrollBar)
  {
    this.scrollBar = scrollBar;
    scrollBar.setMinimum(0);
    scrollBar.setMaximum(dataPtr-1);
    scrollBar.setValue(currentLocation);
    scrollBar.addMouseMotionListener(new ScrollBarMouseMotionListener());
  }

  /**
   * Add a point to the graph (implements RealNumberDataListener)
   *
   * @param index Index for data point
   * @param dataPoint Data point to add
   */
  public synchronized void input(long index,double dataPoint)
  {
    data[dataPtr] = dataPoint;
    indexes[dataPtr] = index;

    // Reallocate if needed
    if (++dataPtr >= data.length) {
      double[] nd = new double[data.length+dataSizeIncrement];
      for(int i=0;i<data.length;i++)
        nd[i] = data[i];
      data = nd;
      long[] idx = new long[nd.length];
      for(int i=0;i<indexes.length;i++)
        idx[i] = indexes[i];
      indexes = idx;
    }

    // Auto-scale if needed
    if (autoScale) {
      if (dataPoint < floor)
        floor = dataPoint;
      if (dataPoint > ceiling)
        ceiling = dataPoint;
    }

    // Auto-scroll
    if (scrollBar == null) {
      currentLocation = dataPtr - 1;
      repaint();
    } else if (scrollBar.getValue() == scrollBar.getMaximum()) {
      currentLocation = dataPtr - 1;
      scrollBar.setMaximum(currentLocation);
      scrollBar.setValue(currentLocation);
      repaint();
    }
  }

  /**
   * Turns on/off autoscaling
   *
   * @param autoScale Automatically scale graph?
   */
  public synchronized void setAutoScale(boolean autoScale)
  {
    if (autoScale) {
      if (!this.autoScale) {
        // If we're turning on autoscale, recompute ceiling and floor
        ceiling = 0.0;
        floor = 0.0;
        for(int i=0;i<dataPtr;i++) {
          if (data[i] < floor)
            floor = data[i];
          if (data[i] > ceiling)
            ceiling = data[i];
        }
        this.autoScale = true;
      }
    } else this.autoScale = autoScale;
  }

  /**
   * Sets the minimum of the graph
   *
   * @param floor Graph minimum
   */
  public synchronized void setFloor(double floor)
  {
    this.floor = floor;
  }

  /**
   * Sets the maximum of the graph
   *
   * @param ceiling Graph maximum
   */
  public synchronized void setCeiling(double ceiling)
  {
    this.ceiling = ceiling;
  }

  /**
   * Paints the component
   *
   * @param graphics Graphics object to paint into
   */
  public void paintComponent(Graphics graphics)
  {
    // Sanity checks
    if (graphics == null)
      return;
    if (!isVisible())
      return;

    int height = getHeight();
    int width = getWidth();
    double dheight = (double)height;
    int lastPointX = -1;
    int lastPointY = -1;
    graphics.setColor(plotBackground);
    graphics.fillRect(0,0,width,height);

    for(int i=width-1,delta=0;i>=0;i--,delta++) {
      if (((currentLocation-delta) < dataPtr)&&((currentLocation-delta) > 0)) {
        double dp = data[currentLocation-delta] - floor;
        if (dp > 0.0) {
          int point = (int)Math.round(dheight * (1.0 - (dp / ceiling)));
          if (point >= height)
            point = height-1;
          else if (point < 0)
            point = 0;
          graphics.setColor(plotColor);
          if (lastPointX < 0)
            graphics.drawLine(i,point,i,point);
          else graphics.drawLine(lastPointX,lastPointY,i,point);
          lastPointX = i;
          lastPointY = point;
        }
      }
    }
  }

  // -------------------------------------------------------------------------
  // Implement javax.swing.Scrollable
  // -------------------------------------------------------------------------

  public Dimension getPreferredScrollableViewportSize()
  {
    return getPreferredSize();
  }

  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 1;
  }

  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 1;
  }

  public boolean getScrollableTracksViewportWidth()
  {
    return false;
  }

  public boolean getScrollableTracksViewportHeight()
  {
    return false;
  }
}

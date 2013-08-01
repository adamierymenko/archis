package org.api.archis.universe.environmentalconditions;

import java.lang.ref.*;
import java.util.*;
import java.io.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.api.archis.*;
import org.api.archis.gui.conditions.*;
import org.api.archis.life.Cell;
import org.api.archis.life.DeathException;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

/**
 * <p>Implements a simple landscape as an EnvironmentalCondition</p>
 *
 * <p>
 * Landscape2D implements a simple landscape with energy, movement, and vision
 * capabilities within which cells can live and evolve.
 * </p>
 *
 * <p>
 * Energy production is configurable, and can be both across the entire landscape
 * and localized within a small circular or square area called a "garden of
 * eden."  Cells that die and still have some energy are also converted
 * into residual energy blocks.  When a cell moves, it automatically absorbs
 * any energy it encounters at it's new location.  It also has access to any
 * information in any information block it encounters at it's new location.
 * </p>
 *
 * <p>
 * The following channels are used:
 * </p>
 *
 * <p>
 * 2 - Vision of cells, movement<br>
 * 3 - Vision of energy blocks<br>
 * 4 - Attack and notification of attack<br>
 * 5 - Release and receipt of info blocks
 * </p>
 *
 * <p>
 * All three channels provide input to the cell.  Channel 0 accepts output
 * in the form of integers representing directions from 0 to 7 (NORTH,SOUTH,
 * EAST,WEST,NORTHEAST,NORTHWEST,SOUTHEAST,SOUTHWEST).  Any values outside
 * this range are mapped onto it as (Math.abs(value) % 8).
 * </p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class Landscape2D implements EnvironmentalCondition
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Two dimensional landscape permitting free energy, communication, attacks, and movement.";

  //
  // Directions
  //
  private static final int NORTH = 0;
  private static final int SOUTH = 1;
  private static final int EAST = 2;
  private static final int WEST = 3;
  private static final int NORTHEAST = 4;
  private static final int NORTHWEST = 5;
  private static final int SOUTHEAST = 6;
  private static final int SOUTHWEST = 7;

  //
  // Possible block contents
  //
  private static final byte BLOCK_EMPTY = (byte)0;
  private static final byte BLOCK_CELL = (byte)1;
  private static final byte BLOCK_ENERGY = (byte)2;
  private static final byte BLOCK_INFO = (byte)3;
  private static final byte BLOCK_SOLID = (byte)4;

  //
  // Directions to move to get to adjacent blocks on a 7x7 grid (for vision).
  //
  private static final int[][] ADJACENT7X7_CELL_DIRECTIONS = {
    { NORTHWEST,NORTHWEST,NORTH    ,NORTH    ,NORTH    ,NORTHEAST,NORTHEAST },
    { WEST     ,NORTHWEST,NORTHWEST,NORTH    ,NORTHEAST,NORTHEAST,EAST      },
    { WEST     ,WEST     ,NORTHWEST,NORTH    ,NORTHEAST,EAST     ,EAST      },
    { WEST     ,WEST     ,WEST     ,-1       ,EAST     ,EAST     ,EAST      },
    { WEST     ,WEST     ,SOUTHWEST,SOUTH    ,SOUTHEAST,EAST     ,EAST      },
    { WEST     ,SOUTHWEST,SOUTHWEST,SOUTH    ,SOUTHEAST,SOUTHEAST,EAST      },
    { SOUTHWEST,SOUTHWEST,SOUTH    ,SOUTH    ,SOUTH    ,SOUTHEAST,SOUTHEAST }
    };

  //
  // What to add or subtract to/from x,y coordinates to get to adjacent blocks
  // on a 7x7 grid (for vision).
  //
  private static final int[][][] ADJACENT7X7_CELL_MATH = {
    { { -3,3  },{ -2,3  },{ -1,3  },{ 0,3  },{ 1,3  },{ 2,3  },{ 3,3  } }, // 0
    { { -3,2  },{ -2,2  },{ -1,2  },{ 0,2  },{ 1,2  },{ 2,2  },{ 3,2  } }, // 1
    { { -3,1  },{ -2,1  },{ -1,1  },{ 0,1  },{ 1,1  },{ 2,1  },{ 3,1  } }, // 2
    { { -3,0  },{ -2,0  },{ -1,0  },{ 0,0  },{ 1,0  },{ 2,0  },{ 3,0  } }, // 3
    { { -3,-1 },{ -2,-1 },{ -1,-1 },{ 0,-1 },{ 1,-1 },{ 2,-1 },{ 3,-1 } }, // 4
    { { -3,-2 },{ -2,-2 },{ -1,-2 },{ 0,-2 },{ 1,-2 },{ 2,-2 },{ 3,-2 } }, // 5
    { { -3,-3 },{ -2,-3 },{ -1,-3 },{ 0,-3 },{ 1,-3 },{ 2,-3 },{ 3,-3 } }  // 6
    //    0         1         2        3        4        5        6
    };

  //
  // Describes a kind of spiraling order for the scanning of the 7x7 block
  // matrix.
  //
  private static final int[][] ADJACENT7X7_SEQUENCE = {
    { 2,3 },{ 2,4 },{ 3,4 },{ 4,4 },{ 4,3 },{ 4,2 },{ 3,2 },{ 2,2 },
    { 1,3 },{ 1,4 },{ 1,5 },{ 2,5 },{ 3,5 },{ 4,5 },{ 5,5 },{ 5,4 },{ 5,3 },
      { 5,2 },{ 5,1 },{ 4,1 },{ 3,1 },{ 2,1 },{ 1,1 },{ 1,2 },
    { 0,3 },{ 0,4 },{ 0,5 },{ 0,6 },{ 1,6 },{ 2,6 },{ 3,6 },{ 4,6 },{ 5,6 },
      { 6,6 },{ 6,5 },{ 6,4 },{ 6,3 },{ 6,2 },{ 6,1 },{ 6,0 },{ 5,0 },{ 4,0 },
      { 3,0 },{ 2,0 },{ 1,0 },{ 0,0 },{ 0,1 },{ 0,2 }
    };

  //
  // What to add to x,y coordinates to get to adjacent cells on a 3x3 grid
  //
  private static final int[][][] ADJACENT3X3_CELL_MATH = {
    { { -1,1  },{ 0,1  },{ 1,1  } },
    { { -1,0  },{ 0,0  },{ 1,0  } },
    { { -1,-1 },{ 0,-1 },{ 1,-1 } }
    };

  /**
   * Internal class for information block data
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class InfoBlock implements Externalizable
  {
    public int[] data;

    //
    // Methods to implement Externalizable
    //
    public void writeExternal(ObjectOutput out)
      throws IOException
    {
      out.writeInt(data.length);
      for(int i=0;i<data.length;i++)
        out.writeInt(data[i]);
    }
    public void readExternal(ObjectInput in)
      throws IOException
    {
      data = new int[in.readInt()];
      for(int i=0;i<data.length;i++)
        data[i] = in.readInt();
    }

    /**
     * Null constructor to implement Externalizable
     */
    public InfoBlock()
    {
    }
  }

  /**
   * Internal class for an energy block
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class EnergyBlock implements Externalizable
  {
    /**
     * Food item energy
     */
    public int energy;

    //
    // Methods to implement Externalizable
    //
    public void writeExternal(ObjectOutput out)
      throws IOException
    {
      out.writeInt(energy);
    }
    public void readExternal(ObjectInput in)
      throws IOException
    {
      energy = in.readInt();
    }

    /**
     * Null constructor to implement Externalizable; do not use
     */
    public EnergyBlock()
    {
    }

    /**
     * Constructs a new food item
     *
     * @param energy Energy level
     */
    public EnergyBlock(int energy)
    {
      this.energy = energy;
    }
  }

  //
  // Internal class for an X,Y location
  //
  private static class XYLocation implements Externalizable
  {
    /**
     * X location
     */
    public int x;

    /**
     * Y location
     */
    public int y;

    /**
     * Null constructor to implement Externalizable; do not use
     */
    public XYLocation()
    {
    }

    /**
     * Constructs a new XYLocation
     *
     * @param x X location
     * @param y Y location
     */
    public XYLocation(int x,int y)
    {
      this.x = x;
      this.y = y;
    }

    //
    // Methods to implement Externalizable
    //
    public void writeExternal(ObjectOutput out)
      throws IOException
    {
      out.writeInt(x);
      out.writeInt(y);
    }
    public void readExternal(ObjectInput in)
      throws IOException
    {
      x = in.readInt();
      y = in.readInt();
    }

    /**
     * Returns a hash code
     *
     * @return Hash code is (x * y)
     */
    public int hashCode()
    {
      return (x * y);
    }

    /**
     * Tests for equality with another object
     *
     * @param o Object to test against
     * @return True if other object is XYLocation with the same values
     */
    public boolean equals(Object o)
    {
      if (o != null) {
        if (o instanceof XYLocation) {
          if ((((XYLocation)o).x == x)&&(((XYLocation)o).y == y))
            return true;
        }
      }
      return false;
    }

    /**
     * Returns "x,y" as a string
     *
     * @return String representation
     */
    public String toString()
    {
      return Integer.toString(x)+","+Integer.toString(y);
    }
  }

  //
  // Key for cell location in cell info
  //
  private static final Object CELL_INFO_LOCATION = "L2D_L";

  //
  // Key for cell movement potential
  //
  private static final Object CELL_INFO_MP = "L2D_M";

  //
  // Key for attackers
  //
  private static final Object CELL_INFO_ATTACKERS = "L2D_A";

  //
  // Key for info blocks
  //
  private static final Object CELL_INFO_INFOBLOCK = "L2D_I";

  //
  // Key for info blocks that have been released
  //
  private static final Object CELL_INFO_INFOPENDING = "L2D_IP";

  // -------------------------------------------------------------------------
  // Instance Variables
  // -------------------------------------------------------------------------

  //
  // Dimensions of landscape
  //
  private int sizeX,sizeY;

  //
  // The landscape: bit fields by X,Y coordinate
  //
  private byte[][] landscape;

  //
  // Temporary XYLocation used only in getCellByLocation
  //
  private XYLocation tmpLocationKey_getCellByLocation;

  //
  // Cell locations and temporary buffer for movement processing
  //
  private HashMap cellsByLocation,cellsByLocation_new;

  //
  // Energy blocks by location
  //
  private HashMap energyByLocation;

  //
  // Info blocks by locaiton
  //
  private HashMap infoByLocation;

  //
  // Information about garden of eden
  //
  private boolean gardenOfEdenCircular;
  private int gardenOfEdenCenterX,gardenOfEdenCenterY;
  private int gardenOfEdenRadius;

  //
  // Attack success probability
  //
  private float attackSuccessProbability;

  //
  // Information about maintainence of food on landscape
  //
  private long maintainEnergy,energyDensity;

  //
  // Universe we inhabit
  //
  private Universe universe;

  //
  // Simulation we're running within
  //
  private Simulation simulation;

  //
  // Statistics
  //
  private volatile int movementActivityLastTurn;
  private volatile int successfulAttacksLastTurn;
  private volatile int unsuccessfulAttacksLastTurn;
  private volatile int communicationActivityLastTurn;
  private volatile long totalDeathsByPredation;
  private volatile long totalFreeEnergy;

  //
  // Observers
  //
  private LinkedList observers;

  //
  // Random number source
  //
  private RandomSource randomSource;

  /**
   * Calculates the memory usage of a landscape of the given size.
   *
   * @param sizeX Hypothetical X size
   * @param sizeY Hypothetical Y size
   * @return Estimated memory usage in bytes
   */
  public static long estimateMemoryUsage(int sizeX,int sizeY)
  {
    return ((long)sizeX * (long)sizeY);
  }

  /**
   * <p>Constructs a new landscape object</p>
   *
   * <p>The init methods must be used to actually create some size and
   * topography within the landscape.</p>
   */
  public Landscape2D()
  {
    sizeX = sizeY = 0;
    landscape = null;
    cellsByLocation = new HashMap(65536,0.75F);
    cellsByLocation_new = new HashMap(16384,0.75F);
    energyByLocation = new HashMap(65536,0.75F);
    infoByLocation = new HashMap(65536,0.75F);
    tmpLocationKey_getCellByLocation = new XYLocation(0,0);
    maintainEnergy = 0L;
    energyDensity = 100000L;
    gardenOfEdenCenterX = gardenOfEdenCenterY = -1;
    gardenOfEdenRadius = 0;
    gardenOfEdenCircular = true;
    movementActivityLastTurn = 0;
    successfulAttacksLastTurn = 0;
    unsuccessfulAttacksLastTurn = 0;
    communicationActivityLastTurn = 0;
    totalDeathsByPredation = 0L;
    totalFreeEnergy = 0L;
    observers = new LinkedList();
    attackSuccessProbability = 0.75F;
  }

  /**
   * <p>Adds an observer for the landscape</p>
   *
   * <p>
   * Internally, a WeakReference is used to store observers.  This means that
   * a reference to any observer must be kept elsewhere, and GC'd observers are
   * removed from the chain.
   * </p>
   *
   * @param observer Landscape observer
   */
  public void addObserver(Landscape2DObserver observer)
  {
    synchronized(observers) {
      observers.add(new WeakReference(observer));
    }
  }

  /**
   * Explicitly removes an observer from the landscape
   *
   * @param observer Landscape observer
   */
  public void removeObserver(Landscape2DObserver observer)
  {
    synchronized(observers) {
      for(Iterator i=observers.iterator();i.hasNext();) {
        WeakReference ref = (WeakReference)i.next();
        Landscape2DObserver o = (Landscape2DObserver)ref.get();
        if (o == null)
          i.remove();
        else if (o.equals(observer))
          i.remove();
      }
    }
  }

  /**
   * <p>Initializes a landscape with a blank flat topography</p>
   *
   * <p>Any existing lifeforms are randomly redistributed across the new
   * landscape.</p>
   *
   * @param sizeX X size
   * @param sizeY Y size
   */
  public void initFlatLandscape(int sizeX,int sizeY)
  {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    landscape = new byte[sizeX][sizeY];
    if (cellsByLocation.size() > 0)
      randomizeCellLocations();

    synchronized(observers) {
      for(Iterator i=observers.iterator();i.hasNext();) {
        WeakReference ref = (WeakReference)i.next();
        Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
        if (tmp == null)
          i.remove();
        else tmp.backgroundChanged();
      }
    }

    synchronized(energyByLocation) {
      energyByLocation.clear();
    }

    synchronized(infoByLocation) {
      infoByLocation.clear();
    }
  }

  /**
   * <p>Initializes the landscape from an image to give it a topography</p>
   *
   * <p>Any existing lifeforms are randomly redistributed across the new
   * landscape.</p>
   *
   * @param topographySourceImage Source of topography
   */
  public void initImageLandscape(BufferedImage topographySourceImage)
  {
    initFlatLandscape(topographySourceImage.getWidth(),topographySourceImage.getHeight());

    for(int x=0;x<sizeX;x++) {
      for(int y=0;y<sizeY;y++) {
        Color c = new Color(topographySourceImage.getRGB(x,y));
        // Block is solid if it's more than 1/2 white
        if ((c.getRed() > 128)&&(c.getGreen() > 128)&&(c.getBlue() > 128))
          landscape[x][y] = BLOCK_SOLID;
      }
    }

    if (cellsByLocation.size() > 0)
      randomizeCellLocations();

    synchronized(observers) {
      for(Iterator i=observers.iterator();i.hasNext();) {
        WeakReference ref = (WeakReference)i.next();
        Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
        if (tmp == null)
          i.remove();
        else tmp.backgroundChanged();
      }
    }
  }

  /**
   * Returns whether or not this landscape object has been initialized
   *
   * @return Is landscape initialized?
   */
  public boolean isInitialized()
  {
    return (landscape != null);
  }

  /**
   * Randomizes the topography of the landscape
   */
  public void randomizeLandscape()
  {
    for(int x=0;x<sizeX;x++) {
      for(int y=0;y<sizeY;y++) {
        if (randomSource.randomBoolean())
          landscape[x][y] = BLOCK_SOLID;
      }
    }

    synchronized(observers) {
      for(Iterator i=observers.iterator();i.hasNext();) {
        WeakReference ref = (WeakReference)i.next();
        Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
        if (tmp == null)
          i.remove();
        else tmp.backgroundChanged();
      }
    }
  }

  /**
   * Randomizes cell locations (any food at new locations is eaten)
   */
  public void randomizeCellLocations()
  {
    synchronized(cellsByLocation) {
      for(Iterator i=cellsByLocation.entrySet().iterator();i.hasNext();) {
        Map.Entry ent = (Map.Entry)i.next();
        XYLocation loc = (XYLocation)ent.getKey();
        Cell l = (Cell)ent.getValue();
        i.remove();

        // Clear old location and get new location
        landscape[loc.x][loc.y] = BLOCK_EMPTY;
        loc.x = randomSource.randomPositiveInteger() % sizeX;
        loc.y = randomSource.randomPositiveInteger() % sizeY;

        // Handle energy or info at new location
        if (landscape[loc.x][loc.y] == BLOCK_ENERGY) {
          synchronized(energyByLocation) {
            EnergyBlock fi = (EnergyBlock)energyByLocation.remove(loc);
            if (fi != null) {
              l.incEnergy(fi.energy);
              totalFreeEnergy -= (long)fi.energy;
            }
          }
        } else if (landscape[loc.x][loc.y] == BLOCK_INFO) {
          synchronized(infoByLocation) {
            InfoBlock info = (InfoBlock)infoByLocation.remove(loc);
            if (info != null)
              l.setMetaInfo(CELL_INFO_INFOBLOCK,info.data);
          }
        }

        // Move cell
        landscape[loc.x][loc.y] = BLOCK_CELL;
        l.setMetaInfo(CELL_INFO_LOCATION,loc);
        cellsByLocation_new.put(loc,l);
      }

      // Merge changes
      cellsByLocation.putAll(cellsByLocation_new);
      cellsByLocation_new.clear();
    }

    synchronized(observers) {
      for(Iterator i=observers.iterator();i.hasNext();) {
        WeakReference ref = (WeakReference)i.next();
        Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
        if (tmp == null)
          i.remove();
        else tmp.backgroundChanged();
      }
    }
  }

  /**
   * <p>
   * Sets the energy to maintain in the universe
   * </p>
   *
   * <p>
   * Set this to 0 to disable
   * </p>
   *
   * @param maintainEnergy Level of energy to maintain or 0 to disable
   */
  public void setMaintainEnergy(long maintainEnergy)
  {
    this.maintainEnergy = maintainEnergy;
  }

  /**
   * Gets the energy to maintain in the universe
   *
   * @return Energy to maintain
   */
  public long getMaintainEnergy()
  {
    return maintainEnergy;
  }

  /**
   * <p>Sets the energy density</p>
   *
   * <p>The amount of energy to maintain is divided by this value to
   * determine the value of each particle of energy created.  This
   * value must be &gt;= 1.  Any value of zero or below will be treated
   * as a value of 1.</p>
   *
   * @param energyDensity Energy density value
   */
  public void setEnergyDensity(long energyDensity)
  {
    this.energyDensity = ((energyDensity >= 1L) ? energyDensity : 1L);
  }

  /**
   * Gets the energy density
   *
   * @return Energy density value
   */
  public long getEnergyDensity()
  {
    return energyDensity;
  }

  /**
   * <p>Sets a concentrated area where free energy blocks should be created</p>
   *
   * <p>
   * If circular is false, a square will be created with sides 2*radius in
   * size.  If centerX and centerY are out of bounds, the system will crash
   * on the next tick.  If radius is out of bounds, the circle or square will
   * wrap around to the other side of the landscape.
   * </p>
   *
   * <p>
   * In addition, cells without parents will be created at a random location
   * within the garden of eden.  (Synthetic and random cells)  This creates
   * a very habitable place where genesis can occur or synthetic cells can
   * be planted.  (Cells with parents are placed adjacent to their parents.)
   * </p>
   *
   * @param centerX Center X coordinate
   * @param centerY Center Y coordinate
   * @param radius Radius of area (0 to disable)
   * @param circular Should area be circular?
   */
  public void setGardenOfEden(int centerX,int centerY,int radius,boolean circular)
  {
    gardenOfEdenCenterX = centerX;
    gardenOfEdenCenterY = centerY;
    gardenOfEdenRadius = Math.abs(radius);
    gardenOfEdenCircular = circular;
  }

  /**
   * Removes garden of eden, causing food to be created randomly across the landscape.
   */
  public void removeGardenOfEden()
  {
    gardenOfEdenCenterX = gardenOfEdenCenterY = -1;
    gardenOfEdenRadius = 0;
  }

  /**
   * <p>Gets a Map containing info about the garden of eden or null if none</p>
   *
   * <p>Map fields are:</p>
   * <p>
   * centerx - Integer<br>
   * centery - Integer<br>
   * circular - Boolean<br>
   * radius - Integer
   * </p>
   *
   * @return Map containing information about garden of eden or null if none
   */
  public Map getGardenOfEden()
  {
    if (gardenOfEdenRadius <= 0)
      return null;
    HashMap r = new HashMap(5,0.99F);
    r.put("centerx",new Integer(gardenOfEdenCenterX));
    r.put("centery",new Integer(gardenOfEdenCenterY));
    r.put("circular",(gardenOfEdenCircular ? Boolean.TRUE : Boolean.FALSE));
    r.put("radius",new Integer(gardenOfEdenRadius));
    return r;
  }

  /**
   * Gets the X dimension size of this landscape
   *
   * @return X size
   */
  public int getSizeX()
  {
    return sizeX;
  }

  /**
   * Gets the Y dimension size of this landscape
   *
   * @return Y size
   */
  public int getSizeY()
  {
    return sizeY;
  }

  /**
   * Gets the probability that an attack will be successful
   *
   * @return Attack success probability
   */
  public float getAttackSuccessProbability()
  {
    return attackSuccessProbability;
  }

  /**
   * <p>Sets the probability that an attack will be successful</p>
   *
   * <p>A value &lt;= 0.0 disables predation in the landscape.</p>
   *
   * @param attackSuccessProbability 0 &lt;= attackSuccessProbability &lt;= 1
   */
  public void setAttackSuccessProbability(float attackSuccessProbability)
  {
    this.attackSuccessProbability = attackSuccessProbability;
  }

  /**
   * Returns whether or not a cell is occupied.
   *
   * @param x X location
   * @param y Y location
   * @return Whether or not cell is occupied by a cell
   */
  public boolean isOccupied(int x,int y)
  {
    return (landscape[x][y] == BLOCK_CELL);
  }

  /**
   * Returns whether or not a cell has energy in it
   *
   * @param x X location
   * @param y Y location
   * @return Whether or not cell has energy in it
   */
  public boolean hasEnergy(int x,int y)
  {
    return (landscape[x][y] == BLOCK_ENERGY);
  }

  /**
   * Returns whether or not a cell has an info block in it
   *
   * @param x X location
   * @param y Y location
   * @return Whether or not a cell has info block in it
   */
  public boolean hasInfo(int x,int y)
  {
    return (landscape[x][y] == BLOCK_INFO);
  }

  /**
   * Returns whether or not a block is solid (impassible)
   *
   * @param x X location
   * @param y Y location
   * @return Whether or not block is solid
   */
  public boolean isSolid(int x,int y)
  {
    return (landscape[x][y] == BLOCK_SOLID);
  }

  /**
   * Gets the location of a cell
   *
   * @param l Cell to look up
   * @return Null if not found or array of int[2] in which [0] == x and [1] == y
   */
  public int[] getCellLocation(Cell l)
  {
    XYLocation loc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);
    if (loc == null)
      return null;
    else {
      int[] r = new int[2];
      r[0] = loc.x;
      r[1] = loc.y;
      return r;
    }
  }

  /**
   * Gets a cell from it's location
   *
   * @param x X location
   * @param y Y location
   * @return Cell or null if none
   */
  public Cell getCellByLocation(int x,int y)
  {
    synchronized(cellsByLocation) {
      tmpLocationKey_getCellByLocation.x = x;
      tmpLocationKey_getCellByLocation.y = y;
      return (Cell)cellsByLocation.get(tmpLocationKey_getCellByLocation);
    }
  }

  /**
   * Gets the value of energy at a given location
   *
   * @param x X location
   * @param y Y location
   * @return Energy value or 0 if no energy at location
   */
  public int getEnergyValueByLocation(int x,int y)
  {
    synchronized(energyByLocation) {
      EnergyBlock fi = (EnergyBlock)energyByLocation.get(new XYLocation(x,y));
      return ((fi == null) ? 0 : fi.energy);
    }
  }

  // -------------------------------------------------------------------------
  // Methods to implement EnvironmentalElement
  // -------------------------------------------------------------------------

  public void showGUI()
  {
    try {
      Landscape2DWindow win;
      (win = new Landscape2DWindow(simulation, this)).setVisible(true);
      if (landscape == null)
        win.showInitializeWindow();
    } catch (Throwable t) {}
  }

  public String getChannelDescription(int channel)
  {
    switch(channel) {
      case 2:
        return "Movement and vision of neighboring cells";
      case 3:
        return "Vision of neighboring energy";
      case 4:
        return "Attack and notification of attack";
      case 5:
        return "Release and receipt of info blocks";
    }
    return null;
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.universe = universe;
    this.simulation = simulation;
    this.randomSource = simulation.randomSource();
    universe.assignChannel(2,this);
    universe.assignChannel(3,this);
    universe.assignChannel(4,this);
    universe.assignChannel(5,this);
  }

  public void destroy()
  {
    universe.unassignChannel(2);
    universe.unassignChannel(3);
    universe.unassignChannel(4);
    universe.unassignChannel(5);
    simulation.removeStatistic("L01 [Landscape2D] Total Free Energy");
    simulation.removeStatistic("L02 [Landscape2D] Movement Last Turn");
    simulation.removeStatistic("L03 [Landscape2D] Communication Last Turn");
    simulation.removeStatistic("L04 [Landscape2D] Successful Attacks Last Turn");
    simulation.removeStatistic("L05 [Landscape2D] Total Deaths by Predation");
    simulation.removeStatistic("L06 [Landscape2D] Unsuccessful Attacks Last Turn");
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
    successfulAttacksLastTurn = 0;
    unsuccessfulAttacksLastTurn = 0;
    communicationActivityLastTurn = 0;
    movementActivityLastTurn = 0;
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    // Check attacks, move cells, create/absorb info, etc.
    synchronized(cellsByLocation) {
      for(Iterator it=cellsByLocation.entrySet().iterator();it.hasNext();) {
        Map.Entry ent = (Map.Entry)it.next();
        Cell l = (Cell)ent.getValue();
        XYLocation loc = (XYLocation)ent.getKey();
        if (l.alive()) {
          // Check for attackers and kill if one is successful
          List attackers = (List)l.getMetaInfo(CELL_INFO_ATTACKERS);
          if (attackers != null) {
            if (attackSuccessProbability > 0.0F) {
              for (Iterator i = attackers.iterator();i.hasNext();) {
                Object attacker = i.next();
                if (attacker instanceof Cell) {
                  if (randomSource.randomEvent(attackSuccessProbability)) {
                    // If attacker had higher energy, it wins
                    ((Cell)attacker).incEnergy(l.energy());
                    l.kill(true,"Killed by #" + ((Cell)attacker).id());

                    // Remove dead cell
                    it.remove();
                    loc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);
                    landscape[loc.x][loc.y] = BLOCK_EMPTY;

                    // Notify observers
                    synchronized (observers) {
                      for (Iterator itr = observers.iterator();itr.hasNext();) {
                        WeakReference ref = (WeakReference)itr.next();
                        Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
                        if (tmp == null)
                          itr.remove();
                        else tmp.cellRemoved(loc.x,loc.y);
                      }
                    }

                    ++successfulAttacksLastTurn;
                    ++totalDeathsByPredation;

                    break;
                  } else ++unsuccessfulAttacksLastTurn;
                }
              }
            }
            attackers.clear();
          }

          // Handle info and do movement if still alive
          if (l.alive()) {
            // Get pending info and create a neighboring info cell if necessary
            EfficientIntegerBuffer pinfo = (EfficientIntegerBuffer)l.removeMetaInfo(CELL_INFO_INFOPENDING);
            if (pinfo != null) {
              XYLocation parentLoc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);
              boolean found = false;
              int x = 0,y = 0;
              for(int dx=0;dx<ADJACENT3X3_CELL_MATH.length;dx++) {
                for(int dy=0;dy<ADJACENT3X3_CELL_MATH[dx].length;dy++) {
                  x = parentLoc.x + ADJACENT3X3_CELL_MATH[dx][dy][0];
                  y = parentLoc.y + ADJACENT3X3_CELL_MATH[dx][dy][1];

                  if ((x != parentLoc.x)||(y != parentLoc.y)) {
                    if (x < 0)
                      x += sizeX;
                    else if (x >= sizeX)
                      x -= sizeX;
                    if (y < 0)
                      y += sizeY;
                    else if (y >= sizeY)
                      y -= sizeY;

                    if ((found = (landscape[x][y] != BLOCK_SOLID)&&(landscape[x][y] != BLOCK_CELL)))
                      break;
                  }
                }
                if (found)
                  break;
              }
              if (found) {
                // Create info block at new location
                XYLocation infoLoc = new XYLocation(x,y);
                InfoBlock ib = new InfoBlock();
                ib.data = pinfo.getData();
                synchronized(infoByLocation) {
                  infoByLocation.put(infoLoc,ib);
                }
                landscape[x][y] = BLOCK_INFO;

                // Notify observers
                synchronized (observers) {
                  for (Iterator itr = observers.iterator();itr.hasNext();) {
                    WeakReference ref = (WeakReference)itr.next();
                    Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
                    if (tmp == null)
                      itr.remove();
                    else tmp.infoAdded(x,y);
                  }
                }
              }
            }

            // Get movement potential
            int[] mp = (int[])l.getMetaInfo(CELL_INFO_MP);

            // Only do anything if there is a movement potential
            if (mp != null) {
              if ((mp[0] != 0)||(mp[1] != 0)) {
                // Get destination of movement
                int destX,destY;
                if (mp[0] > 0)
                  destX = loc.x+1;
                else if (mp[0] < 0)
                  destX = loc.x-1;
                else destX = loc.x;
                if (mp[1] > 0)
                  destY = loc.y+1;
                else if (mp[1] < 0)
                  destY = loc.y-1;
                else destY = loc.y;
                if (destX >= sizeX)
                  destX -= sizeX;
                else if (destX < 0)
                  destX += sizeX;
                if (destY >= sizeY)
                  destY -= sizeY;
                else if (destY < 0)
                  destY += sizeY;

                // Check destination block and move if possible
                if ((landscape[destX][destY] != BLOCK_SOLID)&&(landscape[destX][destY] != BLOCK_CELL)) {
                  // Clear old location and figure out new location
                  landscape[loc.x][loc.y] = BLOCK_EMPTY;
                  it.remove();
                  int oldX = loc.x;
                  int oldY = loc.y;
                  loc.x = destX;
                  loc.y = destY;
                  l.setMetaInfo(CELL_INFO_LOCATION,loc);
                  cellsByLocation_new.put(loc,l);

                  // Notify observers
                  synchronized (observers) {
                    for (Iterator itr = observers.iterator();itr.hasNext();) {
                      WeakReference ref = (WeakReference)itr.next();
                      Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
                      if (tmp == null)
                        itr.remove();
                      else tmp.cellMoved(oldX,oldY,destX,destY,l);
                    }
                  }

                  // Handle energy or info at new location if any
                  if (landscape[destX][destY] == BLOCK_ENERGY) {
                    synchronized(energyByLocation) {
                      EnergyBlock fi = (EnergyBlock)energyByLocation.remove(loc);
                      if (fi != null) {
                        l.incEnergy(fi.energy);
                        totalFreeEnergy -= (long)fi.energy;
                      }
                    }
                  } else if (landscape[destX][destY] == BLOCK_INFO) {
                    synchronized(infoByLocation) {
                      InfoBlock info = (InfoBlock)infoByLocation.remove(loc);
                      if (info != null)
                        l.setMetaInfo(CELL_INFO_INFOBLOCK,info.data);
                    }
                  }

                  // Set destination as occupied
                  landscape[destX][destY] = BLOCK_CELL;

                  ++movementActivityLastTurn;
                }

                // Zero movement potential for next run
                mp[0] = 0;
                mp[1] = 0;
              }
            }
          }
        } else {
          // Remove dead cells and convert remaining value to energy blocks
          it.remove();
          loc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);
          if (l.energy() > 0) {
            landscape[loc.x][loc.y] = BLOCK_ENERGY;
            synchronized(energyByLocation) {
              energyByLocation.put(new XYLocation(loc.x,loc.y),new EnergyBlock(l.energy()));
            }
            totalFreeEnergy += (long)l.energy();
          } else landscape[loc.x][loc.y] = BLOCK_EMPTY;

          // Notify observers
          synchronized (observers) {
            for (Iterator itr = observers.iterator();itr.hasNext();) {
              WeakReference ref = (WeakReference)itr.next();
              Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
              if (tmp == null)
                itr.remove();
              else {
                tmp.cellRemoved(loc.x,loc.y);
                if (l.energy() > 0)
                  tmp.foodAdded(loc.x,loc.y);
              }
            }
          }
        }
      }

      // Merge changes
      cellsByLocation.putAll(cellsByLocation_new);
      cellsByLocation_new.clear();
    }

    // Create food to maintain energy
    if (maintainEnergy > 0L) {
      synchronized(energyByLocation) {
        long tv = totalFreeEnergy + universe.totalCellEnergy();
        int fv = (int)(maintainEnergy / energyDensity);
        double rsquared = (double)gardenOfEdenRadius * (double)gardenOfEdenRadius;

        while (tv < maintainEnergy) {
          if (gardenOfEdenRadius > 0) {
            // Create food within a circular or square area
            int x = gardenOfEdenCenterX;
            int y = gardenOfEdenCenterY;
            int xlen;
            if (randomSource.randomBoolean())
              x += (xlen = randomSource.randomPositiveInteger() % gardenOfEdenRadius);
            else x -= (xlen = randomSource.randomPositiveInteger() % gardenOfEdenRadius);
            if (randomSource.randomBoolean())
              y += randomSource.randomPositiveInteger() % (gardenOfEdenCircular ? Math.round((float)Math.sqrt(rsquared - (double)(xlen * xlen))) : gardenOfEdenRadius);
            else y -= randomSource.randomPositiveInteger() % (gardenOfEdenCircular ? Math.round((float)Math.sqrt(rsquared - (double)(xlen * xlen))) : gardenOfEdenRadius);

            if (x < 0)
              x += sizeX;
            else if (x >= sizeX)
              x -= sizeX;
            if (y < 0)
              y += sizeY;
            else if (y >= sizeY)
              y -= sizeY;
            XYLocation loc = new XYLocation(x,y);

            EnergyBlock fi = (EnergyBlock)energyByLocation.get(loc);
            if (fi != null)
              fi.energy += fv;
            else {
              fi = new EnergyBlock(fv);
              energyByLocation.put(loc,fi);
              landscape[loc.x][loc.y] = BLOCK_ENERGY;

              // Notify observers
              synchronized (observers) {
                for (Iterator itr = observers.iterator();itr.hasNext();) {
                  WeakReference ref = (WeakReference)itr.next();
                  Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
                  if (tmp == null)
                    itr.remove();
                  else tmp.foodAdded(loc.x,loc.y);
                }
              }
            }
            totalFreeEnergy += (long)fv;
            tv += (long)fv;
          } else {
            // Create food at random location
            XYLocation loc = new XYLocation((randomSource.randomPositiveInteger() % sizeX),(randomSource.randomPositiveInteger() % sizeY));

            EnergyBlock fi = (EnergyBlock)energyByLocation.get(loc);
            if (fi != null)
              fi.energy += fv;
            else {
              fi = new EnergyBlock(fv);
              energyByLocation.put(loc,fi);
              landscape[loc.x][loc.y] = BLOCK_ENERGY;

              // Notify observers
              synchronized (observers) {
                for (Iterator itr = observers.iterator();itr.hasNext();) {
                  WeakReference ref = (WeakReference)itr.next();
                  Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
                  if (tmp == null)
                    itr.remove();
                  else tmp.foodAdded(loc.x,loc.y);
                }
              }
            }
            totalFreeEnergy += (long)fv;
            tv += (long)fv;
          }
        }
      }
    }

    // Report some stats
    simulation.setStatistic("L01 [Landscape2D] Total Free Energy",totalFreeEnergy);
    simulation.setStatistic("L02 [Landscape2D] Movement Last Turn",movementActivityLastTurn);
    simulation.setStatistic("L03 [Landscape2D] Communication Last Turn",communicationActivityLastTurn);
    simulation.setStatistic("L04 [Landscape2D] Successful Attacks Last Turn",successfulAttacksLastTurn);
    simulation.setStatistic("L05 [Landscape2D] Total Deaths by Predation",totalDeathsByPredation);
    simulation.setStatistic("L06 [Landscape2D] Unsuccessful Attacks Last Turn",unsuccessfulAttacksLastTurn);
  }

  //
  // Internal class used by createInput to provide an IntegerInput that reads
  // locations of neighboring set attributes on demand on a 7X7 square area.
  //
  private class NeighborLocationsIntegerInput implements IntegerInput
  {
    private int centerX,centerY;
    private int n;
    private int attr;

    public NeighborLocationsIntegerInput(int centerX,int centerY,int attr)
    {
      this.centerX = centerX;
      this.centerY = centerY;
      this.attr = attr;
      this.n = -1;
    }

    public synchronized int read()
    {
      while (++n < ADJACENT7X7_SEQUENCE.length) {
        int x = centerX + ADJACENT7X7_CELL_MATH[ADJACENT7X7_SEQUENCE[n][0]][ADJACENT7X7_SEQUENCE[n][1]][0];
        int y = centerY + ADJACENT7X7_CELL_MATH[ADJACENT7X7_SEQUENCE[n][0]][ADJACENT7X7_SEQUENCE[n][1]][1];

        if (x < 0)
          x += sizeX;
        else if (x >= sizeX)
          x -= sizeX;
        if (y < 0)
          y += sizeY;
        else if (y >= sizeY)
          y -= sizeY;

        if (landscape[x][y] == attr)
          return ADJACENT7X7_CELL_DIRECTIONS[ADJACENT7X7_SEQUENCE[n][0]][ADJACENT7X7_SEQUENCE[n][1]];
      }
      return 0;
    }
  }

  public void preExecutionNotify(Cell l)
  {
    // Don't bother explaining things to a corpse
    if (!l.alive())
      return;

    //
    // Input consists of vision of nearby cells on channel 0 and vision
    // of nearby food on channel 1.  Input is a series of directions
    // representing the direction of adjacent cells or food.
    //

    // Get cell location
    XYLocation loc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);

    // Randomly place if for some weird reason we don't know where it is
    if (loc == null) {
      newCellNotify(null,l);
      loc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);
    }

    // Set input for channels 0 and 1 (cell and energy vision)
    l.setInput(2,new NeighborLocationsIntegerInput(loc.x,loc.y,BLOCK_CELL));
    l.setInput(3,new NeighborLocationsIntegerInput(loc.x,loc.y,BLOCK_ENERGY));

    // Set attacker input
    if (attackSuccessProbability > 0.0F) {
      List attackers = (List)l.getMetaInfo(CELL_INFO_ATTACKERS);
      if (attackers != null) {
        synchronized (attackers) {
          int[] at = new int[attackers.size()];
          int _at = 0;
          for (Iterator i = attackers.iterator();i.hasNext();) {
            Object n = i.next();
            if (n instanceof Integer)
              at[_at++] = ((Integer)n).intValue();
          }
          attackers.clear();
          l.setInput(4,new BufferedIntegerInput(at));
        }
      }
    }

    // Set info input
    int[] data = (int[])l.removeMetaInfo(CELL_INFO_INFOBLOCK);
    if (data != null)
      l.setInput(5,new BufferedIntegerInput(data));
  }

  public void evaluateOutput(Cell l, int channel, int value)
    throws DeathException
  {
    XYLocation loc;
    switch(channel) {
      case 2:
        // Movement

        // Get movement potential (x and y are [0] and [1] respectively)
        int[] mp = (int[])l.getMetaInfo(CELL_INFO_MP);
        if (mp == null) {
          mp = new int[2];
          l.setMetaInfo(CELL_INFO_MP,mp);
        }

        // Check directions and modify movement potential
        switch(((value == -2147483648) ? 2147483647 : Math.abs(value)) % 8) {
          case NORTH:
            ++mp[1];
            break;
          case SOUTH:
            --mp[1];
            break;
          case EAST:
            ++mp[0];
            break;
          case WEST:
            --mp[0];
            break;
          case NORTHEAST:
            ++mp[1];
            ++mp[0];
            break;
          case NORTHWEST:
            ++mp[1];
            --mp[0];
            break;
          case SOUTHEAST:
            --mp[1];
            ++mp[0];
            break;
          case SOUTHWEST:
            --mp[1];
            --mp[0];
            break;
        }
        break;
      case 4:
        // Attack and vision of attack

        if (attackSuccessProbability > 0.0F) {
          loc = (XYLocation)l.getMetaInfo(CELL_INFO_LOCATION);
          if (loc != null) {
            // Get attacked cell (if any)
            int x = loc.x,y = loc.y;
            int correspondingDir = 0;
            switch (((value == -2147483648) ? 2147483647 : Math.abs(value)) % 8) {
              case NORTH:
                ++y;
                correspondingDir = SOUTH;
                break;
              case SOUTH:
                --y;
                correspondingDir = NORTH;
                break;
              case EAST:
                ++x;
                correspondingDir = WEST;
                break;
              case WEST:
                --x;
                correspondingDir = EAST;
                break;
              case NORTHEAST:
                ++y;
                ++x;
                correspondingDir = SOUTHWEST;
                break;
              case NORTHWEST:
                ++y;
                --x;
                correspondingDir = SOUTHEAST;
                break;
              case SOUTHEAST:
                --y;
                ++x;
                correspondingDir = NORTHWEST;
                break;
              case SOUTHWEST:
                --y;
                --x;
                correspondingDir = NORTHEAST;
                break;
            }
            if (x < 0)
              x += sizeX;
            else if (x >= sizeX)
              x -= sizeX;
            if (y < 0)
              y += sizeY;
            else if (y >= sizeY)
              y -= sizeY;
            Cell target;
            synchronized (cellsByLocation) {
              target = (Cell)cellsByLocation.get(new XYLocation(x,y));
            }

            // Add attacking cell to list of attackers in target if target
            // was found.
            if (target != null) {
              List attackers = (List)target.getMetaInfo(CELL_INFO_ATTACKERS);
              if (attackers == null) {
                attackers = new LinkedList();
                target.setMetaInfo(CELL_INFO_ATTACKERS,attackers);
              }
              synchronized (attackers) {
                attackers.add(l);
                attackers.add(new Integer(correspondingDir));
              }
            }
          }
        }
        break;
      case 5:
        EfficientIntegerBuffer ip = (EfficientIntegerBuffer)l.getMetaInfo(CELL_INFO_INFOPENDING);
        if (ip == null) {
          ip = new EfficientIntegerBuffer(64);
          l.setMetaInfo(CELL_INFO_INFOPENDING,ip);
        }
        ip.add(value);
        break;
    }
  }

  public void deathNotify(Cell deadCell,String reason)
  {
  }

  public void initCellNotify(Cell cell)
  {
    // Randomly place pre-existing cells
    newCellNotify(null,cell);
  }

  public boolean newCellNotify(Cell parent,Cell newCell)
  {
    // Get new cell's location (random or within garden of eden if parent
    // is null)
    int x = 0,y = 0;
    if (parent != null) {
      XYLocation parentLoc = (XYLocation)parent.getMetaInfo(CELL_INFO_LOCATION);

      boolean found = false;
      for(int dx=0;dx<ADJACENT3X3_CELL_MATH.length;dx++) {
        for(int dy=0;dy<ADJACENT3X3_CELL_MATH[dx].length;dy++) {
          x = parentLoc.x + ADJACENT3X3_CELL_MATH[dx][dy][0];
          y = parentLoc.y + ADJACENT3X3_CELL_MATH[dx][dy][1];

          if ((x != parentLoc.x)||(y != parentLoc.y)) {
            if (x < 0)
              x += sizeX;
            else if (x >= sizeX)
              x -= sizeX;
            if (y < 0)
              y += sizeY;
            else if (y >= sizeY)
              y -= sizeY;

            if ((found = (landscape[x][y] != BLOCK_SOLID)&&(landscape[x][y] != BLOCK_CELL)))
              break;
          }
        }
        if (found)
          break;
      }

      if (!found)
        return false;
    } else {
      int n = 0;
      do {
        x = randomSource.randomPositiveInteger() % sizeX;
        y = randomSource.randomPositiveInteger() % sizeY;
        ++n;
      } while ((landscape[x][y] == BLOCK_CELL)||((landscape[x][y] == BLOCK_SOLID))&&(n < 131072));
      if (n >= 131072)
        return false;
    }

    // Create XYLocation object
    XYLocation loc = new XYLocation(x,y);

    // If there is energy at the new location, feed it to new cell
    if (landscape[x][y] == BLOCK_ENERGY) {
      synchronized(energyByLocation) {
        EnergyBlock fi = (EnergyBlock)energyByLocation.remove(loc);
        if (fi != null) {
          newCell.incEnergy(fi.energy);
          totalFreeEnergy -= (long)fi.energy;
        }
      }
    }

    // Place new cell at location
    newCell.setMetaInfo(CELL_INFO_LOCATION,loc);
    synchronized(cellsByLocation) {
      cellsByLocation.put(loc,newCell);
    }
    landscape[x][y] = BLOCK_CELL;

    // Notify observers
    synchronized (observers) {
      for (Iterator itr = observers.iterator();itr.hasNext();) {
        WeakReference ref = (WeakReference)itr.next();
        Landscape2DObserver tmp = (Landscape2DObserver)ref.get();
        if (tmp == null)
          itr.remove();
        else tmp.cellAdded(x,y,newCell);
      }
    }

    return true;
  }
}

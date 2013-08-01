package org.api.archis.life;

import java.util.*;
import java.io.*;

import org.api.archis.*;
import org.api.archis.life.vms.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

/**
 * A single cell
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class Cell
{
  // Internal static copy of a common DeathException
  private static DeathException starvationDeathException = new DeathException("Starvation");

  // Internal ID counter
  private static volatile long idCounter = 0L;

  // Is cell alive?
  private boolean alive;

  // Amount of energy this cell has
  private int energy;

  // Age of cell in heartbeats
  private long age;

  // Genetic code
  private Genome genome;

  // State memory
  private int[] memory;

  // ID and parentage information
  private long parentId,generation,id;

  // Name of simulation of origin
  private String origin;

  // Input for next run
  private IntegerInput[] input;

  // Universe that we belong to
  private Universe output;

  // Cell meta-info (can be used by universe, etc.)
  private HashMap metaInfo;

  // Information about why we were killed (if killed)
  private String killReason;
  private boolean killZeroEnergy;

  /**
   * Constructs a new cell with the given genetic code
   *
   * @param simulation Simulation inhabited by cell
   * @param output Universe to receive output
   * @param parent Parent cell or null if synthetic or random
   * @param initialEnergy Initial energy level
   * @param genome Genetic code
   */
  public Cell(Simulation simulation,Universe output,Cell parent,int initialEnergy,Genome genome)
  {
    memory = new int[Archis.CELL_STATE_MEMORY_SIZE];
    input = new IntegerInput[Archis.CHANNEL_COUNT];
    metaInfo = new HashMap(16,0.95F);
    this.output = output;
    this.genome = genome;
    this.origin = simulation.getName();

    energy = initialEnergy;
    age = 0L;
    alive = true;
    killReason = null;
    killZeroEnergy = false;

    id = ++idCounter;
    if (idCounter >= Long.MAX_VALUE)
      idCounter = 0L;
    if (parent != null) {
      parentId = parent.id;
      generation = parent.generation+1L;
    } else {
      parentId = generation = 0L;
    }
  }

  /**
   * Sets the genome for this cell to a new genome
   *
   * @param newGenome New genome for cell
   */
  public void setGenome(Genome newGenome)
  {
    this.genome = newGenome.canonicalize();
  }

  /**
   * Uses the canonicalize() method in genome to obtain a memory-saving copy
   */
  public void canonicalizeGenome()
  {
    genome = genome.canonicalize();
  }

  /**
   * Internal method to handle death
   *
   * @param reason Reason for death
   */
  private void onDeath(String reason)
  {
    if (alive) {
      alive = false;

      // Allow memory and input to be garbage collected on death to save RAM
      memory = null;
      input = null;

      // Notify universe of death
      output.deathNotify(this,reason);
    }
  }

  /**
   * Increments energy by one
   */
  public void incEnergy()
  {
    if (alive)
      ++energy;
  }

  /**
   * Increments energy by a given amount
   *
   * @param amount Amount to increment energy
   */
  public void incEnergy(int amount)
  {
    if (alive)
      energy += amount;
  }

  /**
   * Decrements energy by one
   *
   * @throws DeathException Death occurred due to energy reaching zero
   */
  public void decEnergy()
    throws DeathException
  {
    if (alive) {
      if (--energy <= 0) {
        onDeath("Starvation");
        throw starvationDeathException;
      }
    } else --energy;
  }

  /**
   * Decrements energy by a given amount
   *
   * @param amount Amount to decrement energy by
   * @throws DeathException Death occurred due to energy reaching zero
   */
  public void decEnergy(int amount)
    throws DeathException
  {
    if (alive) {
      if ((energy -= amount) <= 0) {
        onDeath("Starvation");
        throw starvationDeathException;
      }
    } else energy -= amount;
  }

  /**
   * Sets the input source for a channel on the next run
   *
   * @param channel Input channel
   * @param input Input source
   */
  public void setInput(int channel,IntegerInput input)
  {
    this.input[channel] = input;
  }

  /**
   * Causes this cell's genome to execute once
   *
   * @throws DeathException Death occurred for some reason
   */
  public void heartbeat()
    throws DeathException
  {
    if (alive) {
      if (killReason != null) {
        if (killZeroEnergy)
          energy = 0;
        onDeath(killReason);
        throw new DeathException(killReason);
      }

      try {
        genome.execute(input,output,this,memory);
      } catch (DeathException e) {
        onDeath(e.getCauseOfDeath());
        throw e;
      } catch (Throwable t) {
        t.printStackTrace();
        String r = "Unexpected exception: "+t.toString();
        onDeath(r);
        throw new DeathException(r);
      }

      if (killReason != null) {
        if (killZeroEnergy)
          energy = 0;
        onDeath(killReason);
        throw new DeathException(killReason);
      }

      // Reset inputs for next round
      for(int i=0;i<input.length;i++)
        input[i] = null;

      ++age;
    }
  }

  /**
   * Kill this cell
   *
   * @param zeroEnergy Set energy to zero? true/false
   * @param reason Reason for death
   */
  public void kill(boolean zeroEnergy,String reason)
  {
    this.killZeroEnergy = zeroEnergy;
    this.killReason = reason;
  }

  /**
   * Returns whether or not cell is alive
   *
   * @return Is cell alive?
   */
  public boolean alive()
  {
    return (alive&&(killReason == null));
  }

  /**
   * Returns the energy of this cell
   *
   * @return Energy (can be negative! cell may be dead!)
   */
  public int energy()
  {
    return energy;
  }

  /**
   * Returns the age of this cell in heartbeats
   *
   * @return Age in heartbeats
   */
  public long age()
  {
    return age;
  }

  /**
   * Returns the genome object associated with this cell
   *
   * @return Cell's genome (not a copy)
   */
  public Genome genome()
  {
    return genome;
  }

  /**
   * Gets this cell's unique ID
   *
   * @return Unique cell ID
   */
  public long id()
  {
    return id;
  }

  /**
   * Gets the ID of this cell's parent
   *
   * @return Parent ID or 0 if synthetic or random
   */
  public long parentId()
  {
    return parentId;
  }

  /**
   * Returns this cell's generation from zero
   *
   * @return Generation or 0 if synthetic or random
   */
  public long generation()
  {
    return generation;
  }

  /**
   * <p>Gets cell state memory</p>
   *
   * <p>Note that the array returned is not a copy, so any modifications will
   * modify the real state memory for the cell.</p>
   *
   * @return Cell state memory
   */
  public int[] stateMemory()
  {
    return memory;
  }

  /**
   * <p>Sets a piece of meta info</p>
   *
   * <p>
   * Meta-info can be used by conditions to store info about cells in an
   * efficient manner without the use of extra maps.  Meta-info persists for
   * the life of the cell object.
   * </p>
   *
   * @param key Key for meta info
   * @param value Value (any object)
   */
  public void setMetaInfo(Object key,Object value)
  {
    synchronized(metaInfo) {
      metaInfo.put(key,value);
    }
  }

  /**
   * Gets a piece of meta info
   *
   * @param key Key for meta info
   * @return Value or null if not found
   */
  public Object getMetaInfo(Object key)
  {
    synchronized(metaInfo) {
      return metaInfo.get(key);
    }
  }

  /**
   * Removes a piece of meta info
   *
   * @param key Key to remove
   * @return Value of key or null if key was not set
   */
  public Object removeMetaInfo(Object key)
  {
    synchronized(metaInfo) {
      return metaInfo.remove(key);
    }
  }
}

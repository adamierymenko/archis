package org.api.archis.universe;

import java.util.*;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.utils.*;

/**
 * <p>The Universe</p>
 *
 * <p>Most of the guts of the core loop of the Archis simulation are here.
 * The universe serves as a plugin and cell database manager, and handles
 * the invocation of plugins and the adding and removing of cells.  The
 * main tick() method in here is called from Simulation and is the core
 * loop function of the program.</p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class Universe
{
  // Conditions and changes
  private Condition[] conditions;
  private Set newConditions,removeConditions;

  // Probes and changes
  private Probe[] probes;
  private Set newProbes,removeProbes;

  // IO handlers
  private IOHandler[] ioChannelAssignments;

  // Random source (from simulation)
  private RandomSource randomSource;

  // Cells (grouped into execution batches)
  private LinkedList[] cells;

  // New cells for current run
  private ArrayList newCells,newCellParents;

  // Object to notify threads to start
  private Object startNotify;

  // Execution agents (null if only one)
  private MultiThreadedExecutionAgent[] threads;

  // Number of threads currently running (used to wait for thread completion)
  private volatile int threadsRunning;
  private Object threadsRunningSync;

  // Simulation clock
  private long clock;

  // Probabilities for 'static' in output to channels
  private float staticProbability;

  // Statistics
  private volatile long totalCellEnergy;
  private volatile long totalBirths;
  private volatile long totalIntroducedBirths;
  private volatile long totalNaturalBirths;
  private volatile long totalDeaths;
  private volatile long maxGeneration;
  private volatile long maxGenerationThisRun;
  private volatile long minGenerationThisRun;
  private volatile long genomeSizeSum;
  private volatile int maxGenomeSize,minGenomeSize;
  private volatile long ageSum;
  private volatile long totalFailedBirths;
  private volatile int totalStatic;

  // Simulation
  private Simulation simulation;

  // Stopping point or <= 0 for none
  private long stopPoint;

  /**
   * An iterator that iterates over all the cells in the universe
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private class CellIterator implements Iterator
  {
    private int ca;
    private Iterator ci;

    public CellIterator()
    {
      ca = 1;
      ci = cells[0].iterator();
    }

    public boolean hasNext()
    {
      if (ci.hasNext())
        return true;
      else if (ca < cells.length)
        return (cells[ca].size() > 0);
      else return false;
    }

    public Object next()
      throws NoSuchElementException
    {
      if (ci.hasNext())
        return ci.next();
      else if (ca < cells.length) {
        ci = cells[ca++].iterator();
        return ci.next();
      } else throw new NoSuchElementException();
    }

    public void remove()
      throws UnsupportedOperationException
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Internal thread to execute cells concurrently
   */
  private class MultiThreadedExecutionAgent extends Thread
  {
    public volatile boolean die;
    public volatile boolean running;
    public int batch;
    public Object endNotify;
    public long time;

    public MultiThreadedExecutionAgent(int batch)
    {
      super("Universe concurrent execution thread [batch="+batch+"]");
      super.setDaemon(true);
      this.batch = batch;
      die = false;
      running = false;
      endNotify = new Object();
      time = 0L;
      super.start();
    }

    public void run()
    {
      for(;;) {
        if (!running) {
          try {
            synchronized(startNotify) {
              startNotify.wait(1000L);
            }
          } catch (InterruptedException e) {}
        }

        if (die)
          break;
        else if (running) {
          long _st = System.currentTimeMillis();
          executeCellBatch(batch);
          time = System.currentTimeMillis() - _st;

          running = false;
          synchronized(threadsRunningSync) {
            --threadsRunning;
          }

          synchronized(endNotify) {
            endNotify.notifyAll();
          }
        }
      }
    }
  }

  /**
   * Constructs a new universe
   *
   * @param nthreads Number of concurrent threads to execute
   * @param simulation Simulation this universe is running within
   */
  public Universe(int nthreads,Simulation simulation)
  {
    startNotify = new Object();

    cells = new LinkedList[nthreads];
    for(int i=0;i<nthreads;i++)
      cells[i] = new LinkedList();

    if (nthreads > 1) {
      // Start n-1 threads (this thread will function as thread 0)
      threads = new MultiThreadedExecutionAgent[nthreads-1];
      for(int i=0;i<(nthreads-1);i++)
        threads[i] = new MultiThreadedExecutionAgent(i+1);
    } else threads = null;
    threadsRunningSync = new Object();
    threadsRunning = 0;

    this.simulation = simulation;
    randomSource = simulation.randomSource();
    newCells = new ArrayList(65536);
    newCellParents = new ArrayList(65536);
    ioChannelAssignments = new IOHandler[Archis.CHANNEL_COUNT];
    newProbes = new HashSet(32,0.75F);
    newConditions = new HashSet(32,0.75F);
    removeProbes = new HashSet(32,0.75F);
    removeConditions = new HashSet(32,0.75F);
    probes = new Probe[0];
    conditions = new Condition[0];

    staticProbability = 0.0005F;

    totalCellEnergy = 0L;
    totalBirths = 0L;
    totalIntroducedBirths = 0L;
    totalNaturalBirths = 0L;
    totalFailedBirths = 0L;
    totalDeaths = 0L;
    totalStatic = 0;
    maxGeneration = 0L;
    maxGenerationThisRun = 0L;
  }

  /**
   * <p>Returns an iterator over all the cells in the population</p>
   *
   * <p>Note that using this <i>during</i> processing is likely to result
   * in spurious ConcurrentModificationExceptions.  It should only be used
   * from preTickNotify or postTickNotify in conditions or when the
   * simulation is paused.</p>
   *
   * <p>Depending on where we are in the cycle, some of the cells found
   * in this iterator may not be alive.  Code that needs to be aware of
   * this should check.</p>
   *
   * <p>This iterator does <i>not</i> implement the remove() method!  Use
   * the kill method of cells instead.</p>
   *
   * @return Iterator over all cells in the population
   */
  public Iterator populationIterator()
  {
    return new CellIterator();
  }

  /**
   * Destroys this universe and kills all associated threads (this should be called explicitly!)
   */
  public void destroy()
  {
    if (threads != null) {
      for(int i=0;i<threads.length;i++) {
        threads[i].die = true;
        threads[i].interrupt();
      }
      threads = null;
    }
  }

  /**
   * Destroy on finalize
   *
   * @throws Throwable Any exception
   */
  protected void finalize()
    throws Throwable
  {
    destroy();
  }

  /**
   * <p>Gets the current I/O channel assignments</p>
   *
   * @return Array of IOHandlers corresponding to channels
   */
  public IOHandler[] getChannelAssignments()
  {
    IOHandler[] r = new IOHandler[ioChannelAssignments.length];
    for(int i=0;i<r.length;i++)
      r[i] = ioChannelAssignments[i];
    return r;
  }

  /**
   * Assigns an I/O channel to a handler
   *
   * @param channel Channel to set/change assignment for
   * @param handler New handler for channel
   */
  public void assignChannel(int channel,IOHandler handler)
  {
    ioChannelAssignments[channel] = handler;
  }

  /**
   * Unassigns an I/O channel
   *
   * @param channel Channel to clear assignment(s) for
   */
  public void unassignChannel(int channel)
  {
    ioChannelAssignments[channel] = null;
  }

  /**
   * Adds an observation probe to the universe
   *
   * @param probe Probe to add
   */
  public void addProbe(Probe probe)
  {
    if (probe != null) {
      synchronized (newProbes) {
        newProbes.add(probe);
      }
      probe.init(this,simulation);
    }
  }

  /**
   * Removes an observation probe from the universe
   *
   * @param probe Probe to remove
   */
  public void removeProbe(Probe probe)
  {
    if (probe != null) {
      synchronized (probes) {
        removeProbes.add(probe);
      }
    }
  }

  /**
   * Adds a condition to this universe
   *
   * @param condition Condition
   */
  public void addCondition(Condition condition)
  {
    if (condition != null) {
      synchronized (newConditions) {
        newConditions.add(condition);
      }
      condition.init(this,simulation);
    }
  }

  /**
   * Removes a condition from this universe
   *
   * @param condition Condition to remove
   */
  public void removeCondition(Condition condition)
  {
    if (condition != null) {
      synchronized (removeConditions) {
        removeConditions.add(condition);
      }
    }
  }

  /**
   * Returns a set of conditions present in the universe
   *
   * @return Set of conditions
   */
  public Set getConditions()
  {
    HashSet r = new HashSet(conditions.length+16,0.99F);
    for(int i=0;i<conditions.length;i++)
      r.add(conditions[i]);
    synchronized(newConditions) {
      r.addAll(newConditions);
    }
    return Collections.unmodifiableSet(r);
  }

  /**
   * Returns a set of probes present in the universe
   *
   * @return Set of probes
   */
  public Set getProbes()
  {
    HashSet r = new HashSet(probes.length+16,0.99F);
    for(int i=0;i<probes.length;i++)
      r.add(probes[i]);
    synchronized(newProbes) {
      r.addAll(newProbes);
    }
    return Collections.unmodifiableSet(r);
  }

  /**
   * <p>Returns whether or not a condition exists in the universe by class name</p>
   *
   * <p>The endsWith method is used to check the name, so the preceding class
   * path is not requrired.</p>
   *
   * @param className Class name to check for
   * @return Does condition exist?
   */
  public boolean hasConditionByName(String className)
  {
    if (className == null)
      return false;
    for(int i=0;i<conditions.length;i++) {
      if (conditions[i].getClass().getName().endsWith(className))
        return true;
    }
    synchronized(newConditions) {
      for (Iterator i = newConditions.iterator();i.hasNext();) {
        if (i.next().getClass().getName().endsWith(className))
          return true;
      }
    }
    return false;
  }

  /**
   * <p>Returns whether or not a probe exists in the universe by class name</p>
   *
   * <p>The endsWith method is used to check the name, so the preceding class
   * path is not requrired.</p>
   *
   * @param className Class name to check for
   * @return Does probe exist?
   */
  public boolean hasProbeByName(String className)
  {
    if (className == null)
      return false;
    for(int i=0;i<probes.length;i++) {
      if (probes[i].getClass().getName().endsWith(className))
        return true;
    }
    synchronized(newProbes) {
      for (Iterator i = newProbes.iterator();i.hasNext();) {
        if (i.next().getClass().getName().endsWith(className))
          return true;
      }
    }
    return false;
  }

  /**
   * Attempts to add a cell to the universe (may be rejected by conditions)
   *
   * @param parent Parent or null if none (random or synthetic)
   * @param newCell New cell
   */
  public void addCell(Cell parent,Cell newCell)
  {
    synchronized(newCells) {
      newCells.add(newCell);
      newCellParents.add(parent);
    }
  }

  /**
   * Internal method to execute a given batch of cells
   *
   * @param batch Batch (thread) number
   */
  private void executeCellBatch(int batch)
  {
    synchronized(cells[batch]) {
      int gs;
      long gen;
      Object[] tmpp;
      Cell cell;

      for(Iterator it=cells[batch].iterator();it.hasNext();) {
        cell = (Cell)it.next();

        if (cell.alive()) {
          try {
            // Run conditions against cell
            for(int i=0;i<conditions.length;i++)
              conditions[i].preExecutionNotify(cell);

            // Run cell if still alive
            if (cell.alive()) {
              cell.heartbeat();

              // Execute probes against cell
              for(int i=0;i<probes.length;i++)
                probes[i].probeScanCell(cell);

              // Add cell's energy to total cell energy in universe
              // if it survived.
              totalCellEnergy += (long)cell.energy();

              // Add to some sums used in calculating averages
              genomeSizeSum += (long)(gs = cell.genome().size());
              if (gs > maxGenomeSize)
                maxGenomeSize = gs;
              if (gs < minGenomeSize)
                minGenomeSize = gs;
              ageSum += cell.age();

              // Update max and min living generation this run
              if ((gen = cell.generation()) > maxGenerationThisRun)
                maxGenerationThisRun = gen;
              if (gen < minGenerationThisRun)
                minGenerationThisRun = gen;
            } else it.remove();
          } catch (DeathException e) {
            // Remove cell from batch on death
            it.remove();
          }
        } else it.remove();
      }
    }
  }

  /**
   * Called only by genome to send output back to universe. Do not call from other code.
   *
   * @param l Cell from which output originated
   * @param channel Channel of output
   * @param value Value of output
   * @throws DeathException Death occurred during evaluation
   */
  public void evaluateOutput(Cell l,int channel,int value)
    throws DeathException
  {
    if (channel < 0)
      channel = ((channel == -2147483648) ? 2147483647 : Math.abs(channel));
    if (channel > Archis.CHANNEL_COUNT)
      channel %= Archis.CHANNEL_COUNT;

    if (ioChannelAssignments[channel] != null) {
      boolean hadStatic = false;
      while (randomSource.randomEvent(staticProbability)) {
        ++totalStatic;

        // Insert extra value sometimes
        if (randomSource.randomBoolean())
          ioChannelAssignments[channel].evaluateOutput(l,channel,randomSource.randomInteger());
        // 50/50 chance of outputting the real value within a piece of static
        if (randomSource.randomBoolean())
          ioChannelAssignments[channel].evaluateOutput(l,channel,value);

        hadStatic = true;
      }

      // If there was no static, output the real value
      if (!hadStatic)
        ioChannelAssignments[channel].evaluateOutput(l,channel,value);
    }
  }

  /**
   * <p>Sets the probability of "static" in I/O channel output by cells</p>
   *
   * <p>This probability determines how often an error will be introduced
   * into cell output to I/O channels.  This affects mutation during
   * reproduction and adds a small degree of erraticness to the phenotype
   * in general.  This is analogous to the natural entropy that occurs within
   * molecular machinery of real cells.</p>
   *
   * <p>The probability must be between 0.0 and 1.0</p>
   *
   * @param staticProbability Probability from 0.0 to 1.0
   */
  public void setStaticProbability(float staticProbability)
  {
    this.staticProbability = staticProbability;
  }

  /**
   * Gets the probability of "static" in I/O channel output by cells
   *
   * @return Probability of static in cell I/O output
   */
  public float getStaticProbability()
  {
    return staticProbability;
  }

  /**
   * Called only by cell to notify of it's death. Do not call from other code.
   *
   * @param deadCell Cell that died
   * @param reason Reason for death
   */
  public void deathNotify(Cell deadCell,String reason)
  {
    // Notify environmental conditions of death
    for(int i=0;i<conditions.length;i++)
      conditions[i].deathNotify(deadCell,reason);

    ++totalDeaths;

    // Dead cells are removed from cell lists during runs, not here.
  }

  /**
   * <p>Executes a single tick of universe time</p>
   *
   * <p>Do not call this directly.  Use the methods in Simulation.</p>
   */
  public synchronized void tick()
  {
    // Handle pre-tick on probes
    synchronized(newProbes) {
      synchronized(removeProbes) {
        if ((newProbes.size() > 0) || (removeProbes.size() > 0)) {
          for(Iterator i=removeProbes.iterator();i.hasNext();)
            ((Probe)i.next()).destroy();
          for (int i = 0;i < probes.length;i++)
            newProbes.add(probes[i]);
          newProbes.removeAll(removeProbes);
          int x = 0;
          probes = new Probe[newProbes.size()];
          for (Iterator i = newProbes.iterator();i.hasNext();)
            probes[x++] = (Probe)i.next();
          newProbes.clear();
          removeProbes.clear();
        }
      }
    }
    for(int i=0;i<probes.length;i++)
      probes[i].preTickNotify();

    // Handle pre-tick on conditions
    synchronized(newConditions) {
      synchronized(removeConditions) {
        // Add and remove conditions
        if ((newConditions.size() > 0) || (removeConditions.size() > 0)) {
          for(Iterator i=newConditions.iterator();i.hasNext();) {
            Condition c = (Condition)i.next();
            for(int b=0;b<cells.length;b++) {
              for(Iterator ci=cells[b].iterator();ci.hasNext();) {
                Cell cell = (Cell)ci.next();
                if (cell != null)
                  c.initCellNotify(cell);
              }
            }
          }
          for(Iterator i=removeConditions.iterator();i.hasNext();)
            ((Condition)i.next()).destroy();
          for (int i = 0;i < conditions.length;i++)
            newConditions.add(conditions[i]);
          newConditions.removeAll(removeConditions);
          int x = 0;
          conditions = new Condition[newConditions.size()];
          for (Iterator i = newConditions.iterator();i.hasNext();)
            conditions[x++] = (Condition)i.next();
          newConditions.clear();
          removeConditions.clear();
        }

        // Pre-tick notify conditions
        for(int i=0;i<conditions.length;i++) {
          try {
            conditions[i].preTickNotify();
          } catch (ConditionExpirationException e) {
            removeConditions.add(conditions[i]);
          }
        }

        // Remove any that threw ConditionExpirationException
        if (removeConditions.size() > 0) {
          for (int i = 0;i < conditions.length;i++)
            newConditions.add(conditions[i]);
          newProbes.removeAll(removeConditions);
          int x = 0;
          conditions = new Condition[newConditions.size()];
          for (Iterator i = newConditions.iterator();i.hasNext();)
            conditions[x++] = (Condition)i.next();
        }
      }
    }

    // Handle new cells
    if (newCells.size() > 0) {
      synchronized(newCells) {
        // Figure out which of the cells[] lists is emptiest
        List emptiest = cells[0];
        int emptiestSize = cells[0].size();
        for(int i=1;i<cells.length;i++) {
          if (cells[i].size() < emptiestSize) {
            emptiestSize = cells[i].size();
            emptiest = cells[i];
          }
        }

        // Add cells
        for(int x=0,s=newCells.size();x<s;x++) {
          Cell newCell = (Cell)newCells.get(x);
          Cell parent = (Cell)newCellParents.get(x);

          boolean cellok = true;
          for(int i=0;i<conditions.length;i++) {
            if (!conditions[i].newCellNotify(parent,newCell))
              cellok = false;
          }

          if (cellok) {
            for(int i=0;i<probes.length;i++)
              probes[i].probeNewCell(parent,newCell);
            if (parent != null)
              ++totalNaturalBirths;
            else ++totalIntroducedBirths;
            emptiest.add(newCell);
          } else ++totalFailedBirths;
        }

        newCells.clear();
        newCellParents.clear();
      }
    }

    // Reset per-run stats
    totalCellEnergy = 0L;
    maxGenerationThisRun = 0L;
    minGenerationThisRun = Long.MAX_VALUE;
    ageSum = 0L;
    genomeSizeSum = 0L;
    maxGenomeSize = 0;
    minGenomeSize = 2147483647;
    totalStatic = 0;

    if (threads == null) {
      // Just run in this thread if we're running single-threaded
      executeCellBatch(0);
    } else {
      // Set number of threads running
      threadsRunning = threads.length;

      // Set 'running' on all threads
      for(int i=0;i<threads.length;i++)
        threads[i].running = true;

      // Start all threads
      synchronized (startNotify) {
        startNotify.notifyAll();
      }

      // Execute cell batch 0 in this thread (and time it as thread 0)
      executeCellBatch(0);

      // Wait for all other threads to be finished
      while (threadsRunning > 0) {
        for (int i=0;i<threads.length;i++) {
          if (threads[i].running) {
            try {
              synchronized (threads[i].endNotify) {
                threads[i].endNotify.wait(1000L);
              }
            } catch (InterruptedException e) {}
          }
        }
      }
    }

    // Update max generation
    if (maxGenerationThisRun > maxGeneration)
      maxGeneration = maxGenerationThisRun;

    // Handle post-tick on conditions
    synchronized(removeConditions) {
      for(int i=0;i<conditions.length;i++) {
        try {
          conditions[i].postTickNotify();
        } catch (ConditionExpirationException e) {
          removeConditions.add(conditions[i]);
        }
      }
    }

    // Handle post-tick on probes
    for(int i=0;i<probes.length;i++)
      probes[i].postTickNotify();

    // Advance simulation clock and check stop points
    ++clock;
    if ((stopPoint > 0L)&&(clock >= stopPoint))
      simulation.halt("Stopped at ["+stopPoint+"].");

    // Report some stats
    updateStats();
  }

  /**
   * Causes the universe to update it's statistics to the Simulation
   */
  public void updateStats()
  {
    simulation.setStatistic("U01 [Universe] Introduced Births",totalIntroducedBirths);
    simulation.setStatistic("U02 [Universe] Natural Births",totalNaturalBirths);
    simulation.setStatistic("U03 [Universe] Total Births",totalBirths);
    simulation.setStatistic("U04 [Universe] Failed Births",totalFailedBirths);
    simulation.setStatistic("U05 [Universe] Total Deaths",totalDeaths);
    int population = 0;
    for(int i=0;i<cells.length;i++)
      population += cells[i].size();
    simulation.setStatistic("U06 [Universe] Population",population);
    simulation.setStatistic("U07 [Universe] Total Living Cell Energy",totalCellEnergy);
    simulation.setStatistic("U08 [Universe] Average Genome Size",((population > 0) ? (genomeSizeSum / (long)population) : 0L));
    simulation.setStatistic("U09 [Universe] Largest Living Genome Size",maxGenomeSize);
    simulation.setStatistic("U10 [Universe] Smallest Living Genome Size",((minGenomeSize == 2147483647) ? 0 : minGenomeSize));
    simulation.setStatistic("U11 [Universe] Highest Generation Reached",maxGeneration);
    simulation.setStatistic("U12 [Universe] Highest Generation Currently Living",maxGenerationThisRun);
    simulation.setStatistic("U13 [Universe] Average Age of Living Cells",((population > 0) ? (ageSum / (long)population) : 0L));
    simulation.setStatistic("U14 [Universe] Static in I/O (this tick)",totalStatic);
  }

  /**
   * Gets the current simulation clock
   *
   * @return Simulation clock, in ticks
   */
  public long clock()
  {
    return clock;
  }

  /**
   * <p>Sets the point at which the universe will stop the simulation</p>
   *
   * <p>Use -1 to clear the stop point.</p>
   *
   * @param stopPoint Stopping point or -1 to clear
   */
  public void setStopPoint(long stopPoint)
  {
    this.stopPoint = stopPoint;
  }

  /**
   * Returns the current stop point
   *
   * @return Current stop point
   */
  public long getStopPoint()
  {
    return stopPoint;
  }

  /**
   * Gets the current population of cells
   *
   * @return Current population
   */
  public int population()
  {
    int r = 0;
    for(int i=0;i<cells.length;i++)
      r += cells[i].size();
    return r;
  }

  /**
   * Gets the maximum generation alive this last run
   *
   * @return Minimum living generation
   */
  public long minGenerationThisRun()
  {
    return minGenerationThisRun;
  }

  /**
   * Gets the maximum generation alive this last run
   *
   * @return Maximum living generation
   */
  public long maxGenerationThisRun()
  {
    return maxGenerationThisRun;
  }

  /**
   * Gets the total energy of living cells in the universe (valid at/after postTickNotify)
   *
   * @return Total energy of living cells
   */
  public long totalCellEnergy()
  {
    return totalCellEnergy;
  }
}

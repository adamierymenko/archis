package org.api.archis;

import java.util.*;
import java.lang.ref.*;

import javax.swing.JFrame;

import org.api.archis.universe.*;
import org.api.archis.utils.RandomSource;

/**
 * <p>Main class for a running simulation</p>
 *
 * <p>This class starts a simulation thread to handle simulation asynchronously.
 * When started, the simulator is not running and must be started with start().
 * The thread created by this class can be destroyed by destroy() or will be
 * stopped if this class is garbage collected.</p>
 *
 * <p>This is really just a wrapper; most of the guts are in Universe.java.</p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class Simulation
{
  // Random source for simulation
  private RandomSource randomSource;

  // Universe that forms the core of this simulation
  private Universe universe;

  // LinkedList of WeakReference references to SimulationObserver objects
  private LinkedList observers;

  // Simulation run thread
  private SimulationRunnerThread srthread;

  // Statistics
  private HashMap statistics;

  // Name of simulation
  private String name;

  // Number of threads
  private int nthreads;

  // Frames open for GUI in this simulation
  private LinkedList openFrames;

  // Halt reason or null for none
  private String haltReason;

  //
  // Internal thread for running the simulation
  //
  private class SimulationRunnerThread extends Thread
  {
    public volatile boolean run;
    public volatile boolean running;
    public volatile boolean step;
    public volatile boolean die;
    public long startTime,stopTime;

    public SimulationRunnerThread()
    {
      super("Archis Simulation Thread");
      super.setDaemon(false);
      super.start();
      run = false;
      running = false;
      die = false;
      step = false;
      startTime = stopTime = 0L;
    }

    public void run()
    {
      for(;;) {
        if (die)
          break;

        if (run||step) {
          running = true;
          startTime = System.currentTimeMillis();
          universe.tick();
          stopTime = System.currentTimeMillis();
          synchronized(observers) {
            for(Iterator i=observers.iterator();i.hasNext();) {
              WeakReference ref = (WeakReference)i.next();
              SimulationObserver obs = (SimulationObserver)ref.get();
              if (obs == null)
                i.remove();
              else {
                try {
                  obs.tick();
                } catch (Throwable t) {}
              }
            }
          }
          step = false;
          if (haltReason != null) {
            run = false;
            synchronized(observers) {
              for(Iterator i=observers.iterator();i.hasNext();) {
                WeakReference ref = (WeakReference)i.next();
                SimulationObserver obs = (SimulationObserver)ref.get();
                if (obs == null)
                  i.remove();
                else {
                  try {
                    obs.halted(haltReason);
                  } catch (Throwable t) {}
                }
              }
            }
          }
        } else {
          running = false;
          step = false;
          try {
            Thread.sleep(10000L);
          } catch (InterruptedException e) {}
        }
      }
    }
  }

  /**
   * <p>Constructs a new simulation</p>
   *
   * <p>The recommended number of threads is 1 for single processor systems,
   * 1 or 2 for duals (try both and see what performs better),and n-1 for
   * systems with more than 2 processors where n is the number of processors.</p>
   *
   * @param nthreads Number of concurrent threads
   * @param name Name of this simulation
   * @param randomSource PRNG for this simulation
   */
  public Simulation(int nthreads,String name,RandomSource randomSource)
  {
    this.name = name;
    this.nthreads = nthreads;
    this.randomSource = randomSource;
    observers = new LinkedList();
    srthread = new SimulationRunnerThread();
    statistics = new HashMap(128,0.75F);
    openFrames = new LinkedList();
    universe = new Universe(nthreads,this);
  }

  /**
   * <p>Halts simulation at end of next tick</p>
   *
   * <p>Use null as a reason to cancel a halt.  Note that starting the
   * simulation clears halt reasons, so if the simulation is stopped this
   * will have no effect.</p>
   *
   * @param reason Halt reason
   */
  public void halt(String reason)
  {
    haltReason = reason;
  }

  /**
   * Notifies simulation of a new frame
   *
   * @param frame GUI frame that has opened
   */
  public void newFrameNotify(JFrame frame)
  {
    synchronized(openFrames) {
      openFrames.add(new WeakReference(frame));
      for (Iterator i = openFrames.iterator();i.hasNext();) {
        WeakReference tmp = (WeakReference)i.next();
        if (tmp.get() == null)
          i.remove();
      }
    }
  }

  /**
   * Returns the number of processing threads this simulation is using
   *
   * @return Number of processing threads
   */
  public int nThreads()
  {
    return nthreads;
  }

  /**
   * Gets the name of this simulation
   *
   * @return Name of simulation
   */
  public String getName()
  {
    return name;
  }

  /**
   * Adds a simulation observer to be notified on completion of each simulation tick
   *
   * @param observer Observer to add
   */
  public void addObserver(SimulationObserver observer)
  {
    synchronized(observers) {
      observers.add(new WeakReference(observer));
    }
  }

  /**
   * <p>Removes a simulation observer</p>
   *
   * <p>Note: observers are stored in WeakReference objects and are removed
   * automatically if they are garbage collected.</p>
   *
   * @param observer Observer to remove
   */
  public void removeObserver(SimulationObserver observer)
  {
    synchronized(observers) {
      for(Iterator i=observers.iterator();i.hasNext();) {
        WeakReference ref = (WeakReference)i.next();
        if (ref.get() == null)
          i.remove();
        else if (observer.equals(ref.get()))
          i.remove();
      }
    }
  }

  /**
   * Starts the simulation
   */
  public void start()
  {
    haltReason = null;
    srthread.run = true;
    srthread.interrupt();
  }

  /**
   * Stops the simulation
   *
   * @param wait Wait to return until current tick has finished?
   */
  public void stop(boolean wait)
  {
    srthread.run = false;
    if (wait) {
      while (srthread.running) {
        try {
          Thread.sleep(500L);
        } catch (InterruptedException e) {}
      }
    }
  }

  /**
   * Steps forward a single tick
   *
   * @throws IllegalStateException Simulation was already running
   */
  public void step()
    throws IllegalStateException
  {
    if (srthread.running||srthread.run)
      throw new IllegalStateException("Simulation is already running");
    haltReason = null;
    srthread.step = true;
    srthread.interrupt();
  }

  /**
   * Kills this simulation and any threads or frames associated with it (this must be used!)
   */
  public void kill()
  {
    srthread.die = true;
    srthread.interrupt();
    srthread = null;
    universe.destroy();
    synchronized(openFrames) {
      for(Iterator i=openFrames.iterator();i.hasNext();) {
        WeakReference tmp = (WeakReference)i.next();
        JFrame f = (JFrame)tmp.get();
        if (f != null) {
          try {
            f.setVisible(false);
            f.dispose();
          } catch (Throwable t) {}
        }
      }
      openFrames.clear();
    }
  }

  /**
   * Retuerns whether this simulation has been killed (killed simulations are no longer usable)
   *
   * @return Has this simulation been killed with kill()?
   */
  public boolean isKilled()
  {
    return (srthread == null);
  }

  /**
   * Returns whether the simulation is currently running
   *
   * @return Is simulation running?
   */
  public boolean isRunning()
  {
    return srthread.running;
  }

  /**
   * Gets the universe associated with this simulation
   *
   * @return Universe associated with simulation
   */
  public Universe universe()
  {
    return universe;
  }

  /**
   * Removes a simulation statistic
   *
   * @param name Name of statistic
   */
  public void removeStatistic(String name)
  {
    synchronized(statistics) {
      statistics.remove(name);
    }
  }

  /**
   * Sets the value of a simulation statistic
   *
   * @param name Name of statistic (should begin with [module])
   * @param value Value of statistic
   */
  public void setStatistic(String name,int value)
  {
    synchronized(statistics) {
      statistics.put(name,new Integer(value));
    }
  }

  /**
   * Sets the value of a simulation statistic
   *
   * @param name Name of statistic (should begin with [module])
   * @param value Value of statistic
   */
  public void setStatistic(String name,long value)
  {
    synchronized(statistics) {
      statistics.put(name,new Long(value));
    }
  }

  /**
   * Sets the value of a simulation statistic
   *
   * @param name Name of statistic (should begin with [module])
   * @param value Value of statistic
   */
  public void setStatistic(String name,String value)
  {
    synchronized(statistics) {
      statistics.put(name,value);
    }
  }

  /**
   * Sets the value of a simulation statistic
   *
   * @param name Name of statistic (should begin with [module])
   * @param value Value of statistic
   */
  public void setStatistic(String name,float value)
  {
    synchronized(statistics) {
      statistics.put(name,new Float(value));
    }
  }

  /**
   * Sets the value of a simulation statistic
   *
   * @param name Name of statistic (should begin with [module])
   * @param value Value of statistic
   */
  public void setStatistic(String name,double value)
  {
    synchronized(statistics) {
      statistics.put(name,new Double(value));
    }
  }

  /**
   * Sets the value of a simulation statistic
   *
   * @param name Name of statistic (should begin with [module])
   * @param value Value of statistic
   */
  public void setStatistic(String name,boolean value)
  {
    synchronized(statistics) {
      statistics.put(name,(value ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  /**
   * Gets the current statistics for this simulation
   *
   * @return Current statistics in a Map sorted by name
   */
  public SortedMap getStatistics()
  {
    TreeMap r = new TreeMap();
    r.putAll(statistics);
    return r;
  }

  /**
   * Puts all statistics into a pre-existing Map
   *
   * @param dest Map to put all statistics into
   */
  public void getStatistics(Map dest)
  {
    dest.putAll(statistics);
  }

  /**
   * <p>Returns the time in ms that the last tick took</p>
   *
   * <p>If this is called while a tick is in progress, it may return an
   * incorrect result.  It may be called from the tick() method of a
   * SimulationObserver while the simulation is running and the result will
   * be correct.</p>
   *
   * @return Time last tick took (ms)
   */
  public long getLastTickTime()
  {
    return srthread.stopTime - srthread.startTime;
  }

  /**
   * Gets the random number source for this simulation
   *
   * @return Random number source
   */
  public RandomSource randomSource()
  {
    return randomSource;
  }
}

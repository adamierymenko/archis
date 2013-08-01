package org.api.archis.universe.probes;

import java.util.*;
import java.util.zip.*;
import java.lang.ref.WeakReference;
import java.io.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.RealNumberDataListener;

/**
 * <p>Runs all cellular genome data through a compression algorithm to
 * determine a rough estimate of information content.</p>
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class CompressibilityProbe implements Probe
{
  public static final String PROBE_DESCRIPTION = "Uses deflate compressibility to measure relative entropy of all genome data in universe.";
  private Simulation simulation;
  private Universe universe;
  private PrintStream dataOutput;
  private File dataOutputFile;
  private NumberFormat dataOutputFormat;
  private long totalBeforeCompression;
  private ArrayList dataListeners;
  private CompressionThread thread;

  /**
   * Internal class to count output bytes from deflater output stream
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class NullCounterOutputStream extends OutputStream
  {
    public long bytes;

    public NullCounterOutputStream()
    {
      super();
      bytes = 0L;
    }

    public void write(int b)
      throws IOException
    {
      ++bytes;
    }

    public void write(byte[] b)
      throws IOException
    {
      bytes += (long)b.length;
    }

    public void write(byte[] b,int start,int len)
      throws IOException
    {
      bytes += (long)len;
    }

    public void close()
    {
    }
  }

  /**
   * Thread to do the actual work in the background
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class CompressionThread extends Thread
  {
    private CompressibilityProbe.NullCounterOutputStream counter;
    private Deflater deflater;
    private DeflaterOutputStream deflaterOutput;
    private PipedInputStream queueOutput;
    private Object loopNotify;
    private long total;

    /**
     * Input for queue (write genome data to this)
     */
    public OutputStream queueInput;

    /**
     * Set to true to kill
     */
    public volatile boolean die;

    public CompressionThread()
    {
      super("CompressibilityProbe Thread");
      counter = new CompressibilityProbe.NullCounterOutputStream();
      deflater = new Deflater(Deflater.BEST_COMPRESSION,false);
      loopNotify = new Object();
      die = false;
      try {
        queueOutput = new PipedInputStream();
        queueInput = new BufferedOutputStream(new PipedOutputStream(queueOutput),131072);
      } catch (IOException e) {}
      setDaemon(true);
      start();
      reset();
    }

    public void reset()
    {
      deflaterOutput = null;
      deflater.reset();
      counter.bytes = 0L;
      total = 0L;
      deflaterOutput = new DeflaterOutputStream(counter,deflater,1024);
    }

    public long finish(long waitFor)
    {
      try {
        queueInput.flush();
      } catch (IOException e) {}
      while (total < waitFor) {
        synchronized(loopNotify) {
          try {
            loopNotify.wait(1000L);
          } catch (InterruptedException e) {}
        }
      }
      try {
        deflaterOutput.finish();
      } catch (IOException e) {}
      return counter.bytes;
    }

    public void run()
    {
      byte[] buf = new byte[16384];
      int n;
      while (!die) {
        try {
          if ((n = queueOutput.read(buf)) > 0) {
            deflaterOutput.write(buf,0,n);
            total += (long)n;
          }
        } catch (IOException e) {}
        synchronized(loopNotify) {
          loopNotify.notifyAll();
        }
      }
    }
  }

  public CompressibilityProbe()
  {
    totalBeforeCompression = 0L;
    dataOutput = null;
    dataOutputFile = null;
    dataOutputFormat = DecimalFormat.getNumberInstance();
    dataOutputFormat.setMaximumFractionDigits(8);
    dataOutputFormat.setMinimumFractionDigits(1);
    dataOutputFormat.setGroupingUsed(false);
    dataOutputFormat.setMinimumIntegerDigits(1);
    dataOutputFormat.setMaximumIntegerDigits(16384);
    dataListeners = new ArrayList(16);
    thread = new CompressionThread();
  }

  /**
   * Sets the file to log to (set to null to turn off)
   *
   * @throws IOException I/O error opening output file
   * @param out Output file
   */
  public void setOutput(File out)
    throws IOException
  {
    if (dataOutput != null)
      dataOutput.close();
    dataOutputFile = null;
    dataOutput = null;
    dataOutput = new PrintStream(new FileOutputStream(out),true);
    dataOutputFile = out;
  }

  /**
   * Gets the current output file or null if none
   *
   * @return Output file or null
   */
  public File getOutputFile()
  {
    return dataOutputFile;
  }

  /**
   * <p>Adds a data listener, such as HorizontalLineGraph</p>
   *
   * <p>Data listeners are stored internally within WeakReference references
   * and so they must be stored elsewhere or they will be GC'd.</p>
   *
   * @param dataListener Data listener
   */
  public void addDataListener(RealNumberDataListener dataListener)
  {
    synchronized(dataListeners) {
      dataListeners.add(new WeakReference(dataListener));
    }
  }

  /**
   * Removes a data listener
   *
   * @param dataListener Data listener to remove
   */
  public void removeDataListener(RealNumberDataListener dataListener)
  {
    synchronized(dataListeners) {
      for(Iterator i=dataListeners.iterator();i.hasNext();) {
        RealNumberDataListener l = (RealNumberDataListener)((WeakReference)i.next()).get();
        if (l == null)
          i.remove();
        else if (l.equals(dataListener))
          i.remove();
      }
    }
  }

  public void showGUI()
  {
    new org.api.archis.gui.probes.CompressibilityProbeWindow(this,simulation).setVisible(true);
  }

  public void init(Universe universe, Simulation simulation)
  {
    this.simulation = simulation;
    this.universe = universe;
  }

  public void destroy()
  {
    if (dataOutput != null)
      dataOutput.close();
    thread.die = true;
    try {
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.write(1);
      thread.queueInput.flush();
      thread.interrupt();
    } catch (Throwable t) {}
  }

  public void preTickNotify()
  {
    totalBeforeCompression = 0L;
    thread.reset();
  }

  public void postTickNotify()
  {
    // Compute data point
    double dp = ((totalBeforeCompression <= 0L) ? 0.0 : ((double)thread.finish(totalBeforeCompression) / (double)totalBeforeCompression));

    // Output data point to file
    if (dataOutput != null)
      dataOutput.println(dataOutputFormat.format(dp));

    // Send data point to listener(s)
    synchronized(dataListeners) {
      for (Iterator i = dataListeners.iterator();i.hasNext();) {
        RealNumberDataListener l = (RealNumberDataListener)((WeakReference)i.next()).get();
        if (l == null)
          i.remove();
        else l.input(simulation.universe().clock(),dp);
      }
    }
  }

  public void probeScanCell(Cell cell)
  {
    try {
      synchronized(thread.queueInput) {
        totalBeforeCompression += (long)cell.genome().writeTo(thread.queueInput);
      }
    } catch (IOException e) {}
  }

  public void probeNewCell(Cell parent,Cell newCell)
  {
  }
}

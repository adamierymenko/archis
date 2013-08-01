package org.api.archis.utils;

/**
 * An IntegerInput implementation that reads from an int[] buffer.
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class BufferedIntegerInput implements IntegerInput
{
  private int[] buf;
  private int ptr;

  /**
   * <p>Constructs a new buffered integer input</p>
   *
   * <p>Note that buf is not copied, and so it should not be modified after
   * this is created.</p>
   *
   * @param buf Buffer to read from
   */
  public BufferedIntegerInput(int[] buf)
  {
    ptr = 0;
    this.buf = buf;
  }

  public int read()
  {
    return ((ptr < buf.length) ? buf[ptr++] : 0);
  }
}
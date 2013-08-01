package org.api.archis.utils;

/**
 * A byte buffer that allocates block-by-block to use minimal memory and CPU
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class EfficientByteBuffer
{
  private IntLL backlog,top;
  private byte[] current;
  private int ptr;
  private int size;
  private int blockSize;

  //
  // Internal class for linked list of ints
  //
  private static class IntLL
  {
    public byte[] d;
    public IntLL next;
  }

  /**
   * Constructs a new buffer
   *
   * @param blockSize Block size to allocate at a time
   */
  public EfficientByteBuffer(int blockSize)
  {
    this.blockSize = blockSize;
    current = null;
    ptr = 0;
    size = 0;
    backlog = null;
    top = null;
  }

  /**
   * Adds a value to this buffer
   *
   * @param n Byte to add
   */
  public void add(byte n)
  {
    if (current == null)
      current = new byte[blockSize];
    current[ptr++] = n;
    ++size;
    if (ptr >= current.length) {
      IntLL bl = new IntLL();
      bl.d = current;
      if (top != null)
        top.next = bl;
      top = bl;
      if (backlog == null)
        backlog = bl;
      current = new byte[blockSize];
      ptr = 0;
    }
  }

  /**
   * Returns the number of values in the buffer
   *
   * @return Size of buffer
   */
  public int size()
  {
    return size;
  }

  /**
   * Gets the content of this buffer and clears it
   *
   * @return Data from buffer
   */
  public byte[] getData()
  {
    byte[] r = new byte[size];
    int c = 0;
    IntLL bl = backlog;
    while (bl != null) {
      for(int i=0;i<bl.d.length;i++)
        r[c++] = bl.d[i];
      bl = bl.next;
    }
    for(int i=0;i<ptr;i++)
      r[c++] = current[i];
    ptr = 0;
    size = 0;
    backlog = null;
    top = null;
    current = null;
    return r;
  }

  /**
   * Clears this buffer
   */
  public void clear()
  {
    ptr = 0;
    size = 0;
    backlog = null;
    top = null;
    current = null;
  }
}

package org.api.archis.utils;

import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
 * An inflating companion to the block compressing output stream
 *
 * @author Adam Ierymenko [public domain, no warranty or license, use freely]
 * @version 1.0
 */

public class BlockCompressingInputStream extends InputStream
{
  private InputStream in;
  private Inflater inflater;
  private byte[] decompBuf;
  private byte[] resultBuf;
  private int resultBufPtr,resultSize,decompBufPtr,decompBufNeeded;
  private boolean eof;

  /**
   * Constructs a new inflating input stream
   *
   * @param in Input stream to wrap
   */
  public BlockCompressingInputStream(InputStream in)
  {
    this.in = in;
    inflater = new Inflater(false);
    resultBufPtr = 0;
    decompBufPtr = 0;
    decompBufNeeded = 0;
    decompBuf = null;
    resultBuf = null;
    eof = false;
  }

  public int available()
    throws java.io.IOException
  {
    return in.available();
  }

  public int read(byte[] b,int start,int len)
    throws java.io.IOException
  {
    int x;
    for(int i=0;i<len;i++) {
      x = read();
      if (x < 0)
        return i;
      b[start+i] = (byte)x;
    }
    return len;
  }

  public synchronized void mark(int readlimit)
  {
    throw new UnsupportedOperationException();
  }

  public void close()
    throws java.io.IOException
  {
    in.close();
  }

  public boolean markSupported()
  {
    return false;
  }

  public long skip(long n)
    throws java.io.IOException
  {
    long i;
    for (i=0;i<n;i++)
      read();
    return i;
  }

  public int read(byte[] b)
    throws java.io.IOException
  {
    int x;
    for(int i=0;i<b.length;i++) {
      x = read();
      if (x < 0)
        return i;
      b[i] = (byte)x;
    }
    return b.length;
  }

  public synchronized int read()
    throws java.io.IOException
  {
    if (eof)
      return -1;

    // Read block size as first thing from stream
    if ((decompBuf == null)||(resultBuf == null)) {
      int bs = 0;
      int x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      bs |= (x & 0x000000ff);
      x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      bs |= ((x << 8) & 0x0000ff00);
      x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      bs |= ((x << 16) & 0x00ff0000);
      x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      bs |= ((x << 24) & 0xff000000);
      resultBuf = new byte[bs];
      decompBuf = new byte[bs];
    }

    // Return a result byte if there is one
    if ((resultBufPtr < resultSize)&&(resultBufPtr < resultBuf.length))
      return (((int)resultBuf[resultBufPtr++]) & 0x000000ff);

    // Otherwise read another block or the remaining portion of one and
    // decompress when done.
    if (decompBufNeeded <= 0) {
      // Read the compressed block size if it's a new block
      decompBufNeeded = 0;
      int x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      decompBufNeeded |= (x & 0x000000ff);
      x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      decompBufNeeded |= ((x << 8) & 0x0000ff00);
      x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      decompBufNeeded |= ((x << 16) & 0x00ff0000);
      x = in.read();
      if (x < 0) {
        eof = true;
        return -1;
      }
      decompBufNeeded |= ((x << 24) & 0xff000000);
      decompBufPtr = 0;
    }

    int n = in.read(decompBuf,decompBufPtr,(decompBufNeeded - decompBufPtr));
    if (n <= 0) {
      eof = true;
      return -1;
    } else decompBufPtr += n;

    if (decompBufPtr >= decompBufNeeded) {
      // Decompress block if block is finished
      inflater.reset();
      inflater.setInput(decompBuf,0,decompBufNeeded);
      try {
        resultSize = inflater.inflate(resultBuf);
        resultBufPtr = 0;
      } catch (DataFormatException e) {
        eof = true;
        throw new IOException("Compressed data corrupt ("+e.getMessage()+")");
      }
      decompBufNeeded = 0;
    }

    // Recurse to get a byte of result
    return read();
  }

  public synchronized void reset()
    throws java.io.IOException
  {
    throw new UnsupportedOperationException();
  }
}

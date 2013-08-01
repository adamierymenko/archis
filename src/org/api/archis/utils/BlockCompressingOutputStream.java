package org.api.archis.utils;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * <p>A compressing output stream wrapper suitable for realtime use.</p>
 *
 * <p>The DeflaterOutputStream and DeflaterInputStream streams in the
 * java.util.zip package are not suitable for use over sockets or pipes
 * since their flush() methods do not work.  This stream uses block
 * compression and has a working flush() method, and thus is suitable
 * for realtime network compression use.</p>
 *
 * <p>Large buffer sizes will improve compression performance but will
 * cause performance to be 'blockier' (i.e. it will block to compress
 * every n bytes of output and this will take longer).  Also note that
 * memory use for buffers is 2x block size since a compression destination
 * buffer must also be created internally.</p>
 *
 * <p>Note that this stream buffers according to it's block size, so there
 * is no need to chain a BufferedOutputStream before it.  However, it may
 * benefit from a BufferedOutputStream <i>after</i> it in the stream chain.</p>
 *
 * <p>Deflater in java.util.zip is used to actually perform compression.</p>
 *
 * @author Adam Ierymenko [public domain, no warranty or license, use freely]
 * @version 1.0
 */

public class BlockCompressingOutputStream extends OutputStream
{
  private OutputStream out;
  private byte[] dataBuf;
  private int dataBufPtr;
  private byte[] compBuf;
  private Deflater deflater;
  private boolean needsBlockSizeInfo;

  /**
   * Constructs a new block compressing output stream
   *
   * @param blockSize Size of compression buffer
   * @param deflaterCompressionLevel Compresion level, see java.util.zip.Deflate
   * @param out Underlying output stream to wrap
   */
  public BlockCompressingOutputStream(int blockSize,int deflaterCompressionLevel,OutputStream out)
    throws IOException
  {
    this.out = out;
    this.dataBuf = new byte[blockSize];
    this.compBuf = new byte[blockSize];
    this.dataBufPtr = 0;
    this.deflater = new Deflater(deflaterCompressionLevel,false);
    this.needsBlockSizeInfo = true;
  }

  /**
   * Flushes this stream and closes the underlying stream as well as this one.
   *
   * @throws IOException I/O error flushing or closing
   */
  public void close()
    throws IOException
  {
    flush();
    out.close();
  }

  /**
   * Causes the contents of the buffer to be compressed and written and also
   * flushes underlying stream.
   *
   * @throws IOException I/O error writing to underlying stream
   */
  public synchronized void flush()
    throws IOException
  {
    if (dataBufPtr > 0) {
      // Write block size as first thing in stream if it hasn't been before
      if (needsBlockSizeInfo) {
        out.write(dataBuf.length & 0x000000ff);
        out.write((dataBuf.length >> 8) & 0x000000ff);
        out.write((dataBuf.length >> 16) & 0x000000ff);
        out.write((dataBuf.length >> 24) & 0x000000ff);
        needsBlockSizeInfo = false;
      }

      // Compress block and reset buffer pointer
      deflater.reset();
      deflater.setInput(dataBuf,0,dataBufPtr);
      deflater.finish();
      int s = deflater.deflate(compBuf);
      dataBufPtr = 0;

      // Write block size
      out.write(s & 0x000000ff);
      out.write((s >> 8) & 0x000000ff);
      out.write((s >> 16) & 0x000000ff);
      out.write((s >> 24) & 0x000000ff);

      // Write block
      out.write(compBuf,0,s);
    }
    out.flush();
  }

  /**
   * Writes a byte of data (this version is mostly used internally)
   *
   * @param b Byte to write
   * @throws IOException I/O error writing byte
   */
  private synchronized void write(byte b)
    throws IOException
  {
    dataBuf[dataBufPtr++] = b;
    if (dataBufPtr >= dataBuf.length) {
      // Write block size as first thing in stream if it hasn't been before
      if (needsBlockSizeInfo) {
        out.write(dataBuf.length & 0x000000ff);
        out.write((dataBuf.length >> 8) & 0x000000ff);
        out.write((dataBuf.length >> 16) & 0x000000ff);
        out.write((dataBuf.length >> 24) & 0x000000ff);
        needsBlockSizeInfo = false;
      }

      // Compress block and reset buffer pointer
      deflater.reset();
      deflater.setInput(dataBuf);
      deflater.finish();
      int s = deflater.deflate(compBuf);
      dataBufPtr = 0;

      // Write block size
      out.write(s & 0x000000ff);
      out.write((s >> 8) & 0x000000ff);
      out.write((s >> 16) & 0x000000ff);
      out.write((s >> 24) & 0x000000ff);

      // Write block
      out.write(compBuf,0,s);
    }
  }

  /**
   * Writes a byte of data
   *
   * @param b Byte to write (top 8 bits are used only)
   * @throws IOException I/O error writing byte
   */
  public void write(int b)
    throws IOException
  {
    write((byte)b);
  }

  /**
   * Writes an array of bytes
   *
   * @param bytes Bytes to write
   * @throws IOException I/O error writing bytes
   */
  public void write(byte[] bytes)
    throws IOException
  {
    for(int i=0;i<bytes.length;i++)
      write(bytes[i]);
  }

  /**
   * Writes an array of bytes with size and offset arguments
   *
   * @param bytes Bytes to write
   * @param start Where it start in array
   * @param len Number of bytes to write
   * @throws IOException I/O error writing bytes
   */
  public void write(byte[] bytes,int start,int len)
    throws IOException
  {
    for(int i=start,j=start+len;i<j;i++)
      write(bytes[i]);
  }
}


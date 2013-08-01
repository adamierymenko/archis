package org.api.archis.utils;

import java.util.*;
import java.lang.ref.WeakReference;

/**
 * <p>A simple, fast, fixed-size hash table with long int keys and object values.</p>
 *
 * <p>This table is synchronized and thread-safe.</p>
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class LongHashtable
{
  // Internal class used to store linked lists of records
  private static class BucketRecord
  {
    public long n;
    public Object v;
    public BucketRecord nextRecord;
  }

  private BucketRecord[] buckets;
  private Stack recycleBin;
  private int size;

  //
  // Internal class for iterators
  //
  private class LongHashtableIterator implements Iterator
  {
    private int n;
    private int c;
    private int s;
    private LongHashtable.BucketRecord rec;

    public LongHashtableIterator(LongHashtable parent)
    {
      n = 0;
      c = 0;
      s = parent.size;
      rec = buckets[0];
    }

    public boolean hasNext()
    {
      return (c < size);
    }

    public Object next()
      throws NoSuchElementException
    {
      if (s != size)
        throw new ConcurrentModificationException();
      if (++c > size)
        throw new NoSuchElementException();
      while (rec == null) {
        if (++n >= buckets.length)
          throw new NoSuchElementException();
        rec = buckets[n];
      }
      Object v = rec.v;
      rec = rec.nextRecord;
      return v;
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Constructs a table with the given number of buckets
   *
   * @param nbuckets Number of buckets to use
   */
  public LongHashtable(int nbuckets)
  {
    buckets = new BucketRecord[nbuckets];
    recycleBin = new Stack();
    size = 0;
  }

  /**
   * <p>Returns an iterator over the values in this table.</p>
   *
   * <p>This iterator does not support the remove() method.  If you attempt
   * to modify this table while using this iterator, strange things may happen.</p>
   *
   * @return Iterator over all values in table
   */
  public Iterator valuesIterator()
  {
    return new LongHashtableIterator(this);
  }

  /**
   * Removes all entries whose values are empty WeakReference containers.
   */
  public void cleanEmptyWeakReferences()
  {
    synchronized(buckets) {
      for(int i=0;i<buckets.length;i++) {
        BucketRecord rec = buckets[i];
        BucketRecord prevrec = null;
        while (rec != null) {
          if (rec.v != null) {
            if (rec.v instanceof WeakReference) {
              if (((WeakReference)rec.v).get() == null) {
                if (prevrec == null)
                  buckets[i] = rec.nextRecord;
                else prevrec.nextRecord = rec.nextRecord;

                rec.v = null;
                rec.nextRecord = null;
                synchronized(recycleBin) {
                  recycleBin.push(rec);
                }
                --size;
                rec = ((prevrec == null) ? buckets[i] : prevrec.nextRecord);
                continue;
              }
            }
          }
          prevrec = rec;
          rec = rec.nextRecord;
        }
      }
    }
  }

  /**
   * Clears this table
   */
  public void clear()
  {
    synchronized(buckets) {
      for(int i=0;i<buckets.length;i++)
        buckets[i] = null;
      size = 0;
    }
    synchronized(recycleBin) {
      recycleBin.clear();
    }
  }

  /**
   * Puts a value into the table
   *
   * @param key Key for value
   * @param value Value to add
   */
  public void put(long key,Object value)
  {
    BucketRecord rec;
    synchronized(recycleBin) {
      rec = (recycleBin.isEmpty() ? new BucketRecord() : (BucketRecord)recycleBin.pop());
    }

    int idx = Math.abs((int)key) % buckets.length;
    rec.n = key;
    rec.v = value;
    synchronized(buckets) {
      if (buckets[idx] != null)
        rec.nextRecord = buckets[idx];
      buckets[idx] = rec;

      ++size;
    }
  }

  /**
   * Gets the value of a key from the table
   *
   * @param key Key to retrieve
   * @return Retrieved value or null if value was not present or null
   */
  public Object get(long key)
  {
    int idx = Math.abs((int)key) % buckets.length;
    BucketRecord rec;
    synchronized(buckets) {
      rec = buckets[idx];
      while (rec != null) {
        if (rec.n == key)
          break;
        rec = rec.nextRecord;
      }
    }
    return ((rec == null) ? null : rec.v);
  }

  /**
   * Removes a key from the table (does nothing if not present)
   *
   * @param key Key to remove
   */
  public void remove(long key)
  {
    int idx = Math.abs((int)key) % buckets.length;
    BucketRecord rec,prevrec = null;
    synchronized(buckets) {
      rec = buckets[idx];
      while (rec != null) {
        if (rec.n == key)
          break;
        prevrec = rec;
        rec = rec.nextRecord;
      }

      if (rec == null)
        return;
      if (prevrec != null)
        prevrec.nextRecord = rec.nextRecord;
      else buckets[idx] = rec.nextRecord;
    }

    rec.nextRecord = null;
    rec.v = null;
    synchronized(recycleBin) {
      recycleBin.push(rec);
    }
    --size;
  }

  /**
   * Returns the number of objects in this table
   *
   * @return Size of table
   */
  public int size()
  {
    return size;
  }
}

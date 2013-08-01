package org.api.archis.life.vms;

import java.util.*;
import java.math.*;
import java.io.*;
import java.lang.ref.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

/**
 * <p>Register machine genome implementation</p>
 *
 * <p>
 * The genome is made up of a series of 6-bit codons that are executed against
 * a memory array contained within the state information of a cell.
 * </p>
 *
 * <p>
 * This virtual machine is a simple register machine with a memory pointer and
 * a single 'accumulator' register. The instructions that are coded for by
 * codons in the genome are in the table below.  Note that the result of
 * <i>add, mul, div, and, or, </i>and<i> xor</i> are stored back into the
 * accumulator.  The <i>go</i> instruction doubles as a start codon.  Note
 * that as the first instruction with the accumulator as zero this has no
 * effect.  Gaps between stop and start codons can constitute noncoding
 * regions between "genes."  Genes could regulate each other by modifying
 * memory, could be regulated by data read from a channel, or could be always
 * on.
 * </p>
 *
 * <p>
 * There is a limit to how many instructions will be executed in a single tick,
 * and any exceeding of this limit will cause a DeathException to be thrown.
 * This also occurs if the loop/rep tracking stack is overflowed, which
 * indicates a certain kind of nested infinite loop.  The per-tick limit is
 * (genomeSize^2 * (loops+1)) where genomeSize is the size of the genome in
 * codons and loops is the number of LOOP instructions in the genome.
 * </p>
 *
 * <p>
 * <table border=0 cellpadding=4 cellspacing=0>
 * <tr><td><b>Instruction</b></td><td><b>Shorthand</b></td><td><b>Effect</b></td></tr>
 * <tr><td>fwd</td><td>f</td><td>Move memory pointer forward</td></tr>
 * <tr><td>back</td><td>b</td><td>Move memory pointer backward</td></tr>
 * <tr><td>go</td><td>g</td><td>Move memory pointer to location stored in accumulator (wraps if out of bounds)</td></tr>
 * <tr><td>add</td><td>+</td><td>Add memory value at pointer to accumulator</td></tr>
 * <tr><td>mul</td><td>*</td><td>Multiply memory value at pointer with accumulator</td></tr>
 * <tr><td>div</td><td>/</td><td>Divide memory value at pointer with accumulator</td></tr>
 * <tr><td>and</td><td>&amp;</td><td>AND memory value at pointer with accumulator</td></tr>
 * <tr><td>or</td><td>|</td><td>OR memory value at pointer with accumulator</td></tr>
 * <tr><td>xor</td><td>^</td><td>XOR memory value at pointer with accumulator</td></tr>
 * <tr><td>shl</td><td>[</td><td>Bitwise left shift accumulator</td></tr>
 * <tr><td>shr</td><td>]</td><td>Bitwise right shift accumulator</td></tr>
 * <tr><td>inc</td><td>&gt;</td><td>Increment accumulator</td></tr>
 * <tr><td>dec</td><td>&lt;</td><td>Decrement accumulator</td></tr>
 * <tr><td>sta</td><td>#</td><td>Store accumulator to memory at current pointer location and zero accumulator</td></tr>
 * <tr><td>cmp</td><td>?</td><td>Set accumulator to -1, 0, or 1 depending on whether memory at pointer is less than, equal to, or greater than accumulator</td></tr>
 * <tr><td>loop</td><td>{</td><td>Jump to matching <i>rep</i> if accumulator is zero</td></tr>
 * <tr><td>rep</td><td>}</td><td>Jump back to mactching <i>loop</i> if accumulator is nonzero</td></tr>
 * <tr><td>read</td><td>r</td><td>Read from current channel to accumulator</td></tr>
 * <tr><td>write</td><td>w</td><td>Write accumulator to current channel</td></tr>
 * <tr><td>sch</td><td>s</td><td>Set current channel to accumulator (wraps if out of bounds)</td></tr>
 * <tr><td>stop</td><td>!</td><td>Stops execution until start codon is reached; zero accumulator, memory pointer, and channel</td></tr>
 * </table>
 * </p>
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class RegisterMachineGenome implements Genome
{
  // Initialize some static internal stuff
  private static int[] BITS,NOTBITS;
  private static DeathException instructionOverflowException;
  static
  {
    BITS = new int[32];
    NOTBITS = new int[32];

    BITS[31] = 0x00000001;
    for(int i=30;i>=0;i--)
      BITS[i] = BITS[i+1] << 1;
    for(int i=0;i<32;i++)
      NOTBITS[i] = ~BITS[i];

    instructionOverflowException = new DeathException("Loop Overflow");
  }

  //
  // Human-readable codes for codons
  //
  public static final char CODON_FWD = 'f';
  public static final char CODON_BACK = 'b';
  public static final char CODON_GO = 'g';
  public static final char CODON_ADD = '+';
  public static final char CODON_MUL = '*';
  public static final char CODON_DIV = '/';
  public static final char CODON_AND = '&';
  public static final char CODON_OR = '|';
  public static final char CODON_XOR = '^';
  public static final char CODON_SHL = '[';
  public static final char CODON_SHR = ']';
  public static final char CODON_INC = '>';
  public static final char CODON_DEC = '<';
  public static final char CODON_STA = '#';
  public static final char CODON_CMP = '?';
  public static final char CODON_LOOP = '{';
  public static final char CODON_REP = '}';
  public static final char CODON_READ = 'r';
  public static final char CODON_WRITE = 'w';
  public static final char CODON_SCH = 's';
  public static final char CODON_STOP = '!';

  //
  // Mappings of codons to the top 6 bits of a series of bytes
  //
  // This mapping had a score of 18.5 and was the best after over 4 billion
  // trials of the csearch.c hill climbing search program.
  //
  private static final char[] CODON_CHAR_MAPPING = {
    CODON_CMP,    // 0x00
    CODON_DIV,    // 0x01
    CODON_SHR,    // 0x02
    CODON_SHR,    // 0x03
    CODON_XOR,    // 0x04
    CODON_XOR,    // 0x05
    CODON_XOR,    // 0x06
    CODON_XOR,    // 0x07
    CODON_CMP,    // 0x08
    CODON_DIV,    // 0x09
    CODON_SHR,    // 0x0a
    CODON_SHR,    // 0x0b
    CODON_SHL,    // 0x0c
    CODON_SHL,    // 0x0d
    CODON_SHL,    // 0x0e
    CODON_SHL,    // 0x0f
    CODON_DEC,    // 0x10
    CODON_INC,    // 0x11
    CODON_REP,    // 0x12
    CODON_SCH,    // 0x13
    CODON_DEC,    // 0x14
    CODON_INC,    // 0x15
    CODON_REP,    // 0x16
    CODON_SCH,    // 0x17
    CODON_DEC,    // 0x18
    CODON_INC,    // 0x19
    CODON_STOP,   // 0x1a
    CODON_REP,    // 0x1b
    CODON_DEC,    // 0x1c
    CODON_INC,    // 0x1d
    CODON_STOP,   // 0x1e
    CODON_REP,    // 0x1f
    CODON_OR,     // 0x20
    CODON_OR,     // 0x21
    CODON_OR,     // 0x22
    CODON_OR,     // 0x23
    CODON_ADD,    // 0x24
    CODON_ADD,    // 0x25
    CODON_MUL,    // 0x26
    CODON_MUL,    // 0x27
    CODON_READ,   // 0x28
    CODON_READ,   // 0x29
    CODON_STA,    // 0x2a
    CODON_AND,    // 0x2b
    CODON_ADD,    // 0x2c
    CODON_ADD,    // 0x2d
    CODON_STA,    // 0x2e
    CODON_AND,    // 0x2f
    CODON_BACK,   // 0x30
    CODON_GO,     // 0x31
    CODON_WRITE,  // 0x32
    CODON_FWD,    // 0x33
    CODON_BACK,   // 0x34
    CODON_GO,     // 0x35
    CODON_WRITE,  // 0x36
    CODON_FWD,    // 0x37
    CODON_BACK,   // 0x38
    CODON_BACK,   // 0x39
    CODON_LOOP,   // 0x3a
    CODON_FWD,    // 0x3b
    CODON_BACK,   // 0x3c
    CODON_BACK,   // 0x3d
    CODON_LOOP,   // 0x3e
    CODON_FWD };  // 0x3f

  //
  // Mappings of codons to the top 6 bits of a series of bytes (int values
  // for execution engine)
  //
  // This mapping had a score of 18.5 and was the best after over 4 billion
  // trials of the csearch.c hill climbing search program.
  //
  private static final byte[] CODON_EXEC_MAPPING = {
     3,    // 0x00
     5,    // 0x01
    16,    // 0x02
    16,    // 0x03
    20,    // 0x04
    20,    // 0x05
    20,    // 0x06
    20,    // 0x07
     3,    // 0x08
     5,    // 0x09
    16,    // 0x0a
    16,    // 0x0b
    15,    // 0x0c
    15,    // 0x0d
    15,    // 0x0e
    15,    // 0x0f
     4,    // 0x10
     8,    // 0x11
    13,    // 0x12
    14,    // 0x13
     4,    // 0x14
     8,    // 0x15
    13,    // 0x16
    14,    // 0x17
     4,    // 0x18
     8,    // 0x19
    18,    // 0x1a
    13,    // 0x1b
     4,    // 0x1c
     8,    // 0x1d
    18,    // 0x1e
    13,    // 0x1f
    11,    // 0x20
    11,    // 0x21
    11,    // 0x22
    11,    // 0x23
     0,    // 0x24
     0,    // 0x25
    10,    // 0x26
    10,    // 0x27
    12,    // 0x28
    12,    // 0x29
    17,    // 0x2a
     1,    // 0x2b
     0,    // 0x2c
     0,    // 0x2d
    17,    // 0x2e
     1,    // 0x2f
     2,    // 0x30
     7,    // 0x31
    19,    // 0x32
     6,    // 0x33
     2,    // 0x34
     7,    // 0x35
    19,    // 0x36
     6,    // 0x37
     2,    // 0x38
     2,    // 0x39
     9,    // 0x3a
     6,    // 0x3b
     2,    // 0x3c
     2,    // 0x3d
     9,    // 0x3e
     6 };  // 0x3f

  // Maximum value of codons (top 6 bits of byte)
  public static final byte CODON_VALUE_MAX = (byte)0x3f;

  // 30 bits of 32-bit integer
  public static final int USED_INT_BITS = 0x3fffffff;

  // Top 6 bits of int
  public static final int TOP_6_INT_BITS = 0x3f;

  // Map containing codon values by character meaning-- used by the synthetic
  // genome constructor.
  private static Map characterToCodeMap = null;

  // Internal static WeakHashMap for canonicalization
  private static WeakHashMap canonicalGenomes = null;

  /**
   * Stack for position state information (used for loop/rep handling)
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class PositionInformationStack
  {
    public int[] stack;
    public int stackPtr;

    public PositionInformationStack()
    {
      stack = new int[262144];
      stackPtr = 0;
    }
  }

  /**
   * ThreadLocal to return copies of the stack for each thread
   *
   * @author Adam Ierymenko
   * @version 1.0
   */
  private static class ThreadLocalPositionInformationStack extends ThreadLocal
  {
    protected Object initialValue()
    {
      return new RegisterMachineGenome.PositionInformationStack();
    }
  }

  /**
   * ThreadLocal stack source
   */
  private static ThreadLocalPositionInformationStack threadLocalStack = new ThreadLocalPositionInformationStack();

  // -------------------------------------------------------------------------

  // Integer array for bit field
  private int[] genomeBits;

  // Size of genome in codons
  private int genomeSize;

  // Maximum number of instructions per execution
  private int maxInstructions;

  // Hash code
  private int hashCode;

  //
  // Externalizable methods for networked object transfer
  //
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(maxInstructions);
    out.writeInt(genomeSize);
    out.writeInt(genomeBits.length);
    for(int i=0;i<genomeBits.length;i++)
      out.writeInt(genomeBits[i]);
  }
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    hashCode = -1;
    maxInstructions = in.readInt();
    genomeSize = in.readInt();
    try {
      genomeBits = new int[in.readInt()];
      for(int i=0;i<genomeBits.length;i++)
        genomeBits[i] = in.readInt();
    } catch (IOException e) {
      throw e;
    } catch (Throwable t) {
      throw new StreamCorruptedException("Unexpected exception: "+t.toString());
    }
  }

  /**
   * Main method: run this class to benchmark it
   *
   * @param argv Arguments (not used)
   */
  public static void main(String[] argv)
  {
    try {
      System.out.println("Benchmarking RegisterMachineGenome...");
      System.out.println();

      Compiler.compileClass(Class.forName("org.api.archis.life.vms.RegisterMachineGenome"));
      Thread.sleep(1000L);
      System.gc();

      IntegerInput[] noInput = new IntegerInput[0];
      int[] memory = new int[Archis.CELL_STATE_MEMORY_SIZE];
      RegisterMachineGenome test = new RegisterMachineGenome(new MersenneTwisterRandomSource(System.currentTimeMillis()),"ggggg?????>[[[{<}>[[[{<}>[[[{<}>[[[{<}>[[[{<}>[[[{<}>[[[[[[#+++++{<<}");
      long ni = 0L;
      for(int i=0;i<1000;i++)
        test.execute(noInput,null,null,memory);

      System.gc();

      long start = System.currentTimeMillis();
      for(int i=0;i<500000;i++)
        ni += (long)test.execute(noInput,null,null,memory);
      long end = System.currentTimeMillis();

      NumberFormat df = DecimalFormat.getNumberInstance();
      df.setMaximumFractionDigits(2);
      df.setMinimumFractionDigits(2);
      df.setMinimumIntegerDigits(1);
      df.setMaximumIntegerDigits(16384);
      df.setGroupingUsed(false);
      System.out.println("Instructions Executed: "+ni);
      System.out.println("Execution Time: "+df.format((double)(end-start) / 1000.0)+"sec");
      System.out.println("Instructions/Second: "+df.format((double)ni / ((double)(end-start) / 1000.0)));
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * For internal and serialization use only; DO NOT USE
   */
  public RegisterMachineGenome()
  {
  }

  /**
   * <p>Constructs a genome from a byte array of codons in 8-bit representation</p>
   *
   * <p>Each byte holds a single 6-bit codon.  Only the topmost 6 bits of each
   * byte are significant.  The first 2 bits are ignored.</p>
   *
   * @param codons Codons to place in genome
   * @param start Where to start in array
   * @param length Length of array
   */
  public RegisterMachineGenome(byte[] codons,int start,int length)
  {
    genomeSize = length;
    int tmp1 = genomeSize / 5;
    while ((tmp1 * 5) < genomeSize)
      ++tmp1;
    genomeBits = new int[tmp1];

    int bptr = 0;
    int pos = 0;
    int loops = 0;
    for(int i=0;i<length;i++) {
      switch(pos) {
        case 0:
          genomeBits[bptr] |= (int)(codons[i+start] & CODON_VALUE_MAX) << 24;
          pos = 1;
          break;
        case 1:
          genomeBits[bptr] |= (int)(codons[i+start] & CODON_VALUE_MAX) << 18;
          pos = 2;
          break;
        case 2:
          genomeBits[bptr] |= (int)(codons[i+start] & CODON_VALUE_MAX) << 12;
          pos = 3;
          break;
        case 3:
          genomeBits[bptr] |= (int)(codons[i+start] & CODON_VALUE_MAX) << 6;
          pos = 4;
          break;
        case 4:
          genomeBits[bptr++] |= (int)(codons[i+start] & CODON_VALUE_MAX);
          pos = 0;
          break;
      }
      if (CODON_CHAR_MAPPING[codons[i+start] & CODON_VALUE_MAX] == CODON_LOOP)
        ++loops;
    }

    // Init maxInstructions and hashCode
    maxInstructions = ((loops > 1) ? (genomeSize * genomeSize * (loops+1)) : (genomeSize * genomeSize * 3));
    hashCode = -1;
  }

  public Genome createNew(byte[] codons,int start,int length)
  {
    return new RegisterMachineGenome(codons,start,length);
  }

  /**
   * <p>Constructs a genome from a byte array of codons in 8-bit representation</p>
   *
   * <p>Each byte holds a single 6-bit codon.  Only the topmost 6 bits of each
   * byte are significant.  The first 2 bits are ignored.</p>
   *
   * @param codons Codons to place in genome
   */
  public RegisterMachineGenome(byte[] codons)
  {
    this(codons,0,codons.length);
  }

  public Genome createNew(byte[] codons)
  {
    return new RegisterMachineGenome(codons,0,codons.length);
  }

  /**
   * <p>Constructs a genome from a genome in character notation.</p>
   *
   * <p>In cases in which there is more than one code for an instruction,
   * an equivalent code will be chosen randomly from those available.</p>
   *
   * <p>Invalid characters such as spaces and carriage returns are ignored.</p>
   *
   * @param randomSource Random source for randomly selecting codons for instructions
   * @param syntheticGenome Genome in human-readable character notation
   */
  public RegisterMachineGenome(RandomSource randomSource,String syntheticGenome)
  {
    // Create static character to code map if it doesn't exist already
    if (characterToCodeMap == null) {
      characterToCodeMap = new HashMap(128,0.99F);
      for(int i=0;i<CODON_CHAR_MAPPING.length;i++) {
        Character c = new Character(CODON_CHAR_MAPPING[i]);
        Vector byteValues = (Vector)characterToCodeMap.get(c);
        if (byteValues == null) {
          byteValues = new Vector(8,4);
          characterToCodeMap.put(c,byteValues);
        }
        byteValues.add(new Byte((byte)i));
      }
    }

    // Create an array of codons from the string
    byte[] codons = new byte[syntheticGenome.length()];
    int cptr = 0;
    for(int i=0;i<codons.length;i++) {
      Vector possibleCodons = (Vector)characterToCodeMap.get(new Character(syntheticGenome.charAt(i)));
      if (possibleCodons != null) {
        Byte codon = (Byte)possibleCodons.get(randomSource.randomPositiveInteger() % possibleCodons.size());
        if (codon != null)
          codons[cptr++] = codon.byteValue();
      }
    }

    // Create bitmap array
    genomeSize = cptr;
    int tmp1 = genomeSize / 5;
    while ((tmp1 * 5) < genomeSize)
      ++tmp1;
    genomeBits = new int[tmp1];

    // Load codons into bitmap
    int bptr = 0;
    int pos = 0;
    int loops = 0;
    for(int i=0;i<genomeSize;i++) {
      switch(pos) {
        case 0:
          genomeBits[bptr] |= (int)codons[i] << 24;
          pos = 1;
          break;
        case 1:
          genomeBits[bptr] |= (int)codons[i] << 18;
          pos = 2;
          break;
        case 2:
          genomeBits[bptr] |= (int)codons[i] << 12;
          pos = 3;
          break;
        case 3:
          genomeBits[bptr] |= (int)codons[i] << 6;
          pos = 4;
          break;
        case 4:
          genomeBits[bptr++] |= (int)codons[i];
          pos = 0;
          break;
      }
      if (CODON_CHAR_MAPPING[codons[i]] == CODON_LOOP)
        ++loops;
    }

    // Init maxInstructions and hashCode
    maxInstructions = ((loops > 1) ? (genomeSize * genomeSize * (loops+1)) : (genomeSize * genomeSize * 3));
    hashCode = -1;
  }

  public Genome createNew(RandomSource randomSource,String syntheticGenome)
  {
    return new RegisterMachineGenome(randomSource,syntheticGenome);
  }

  /**
   * <p>Constructs a random genome with the given mean size +/- up to the given
   * size deviation.</p>
   *
   * @param randomSource Source for random numbers to generate genome
   * @param meanSize Mean size of genome in codons
   * @param sizeDeviation Maximum random deviation (+ or -) from size
   */
  public RegisterMachineGenome(RandomSource randomSource,int meanSize,int sizeDeviation)
  {
    // Figure out what the genome size should be
    if (randomSource.randomBoolean())
      genomeSize = meanSize + (randomSource.randomPositiveInteger() % sizeDeviation);
    else genomeSize = meanSize - (randomSource.randomPositiveInteger() % sizeDeviation);
    if (genomeSize <= 0)
      genomeSize = meanSize;

    // Create the genome bits array
    int tmp1 = genomeSize / 5;
    while ((tmp1 * 5) < genomeSize)
      ++tmp1;
    genomeBits = new int[tmp1];

    // Create random bits in bottom-most 30 bits of each int
    for(int i=0;i<genomeBits.length;i++)
      genomeBits[i] = randomSource.randomInteger() & USED_INT_BITS;

    // Init maxInstructions and hashCode
    maxInstructions = genomeSize * genomeSize * 4;
    hashCode = -1;
  }

  public Genome createNew(RandomSource randomSource,int meanSize,int sizeDeviation)
  {
    return new RegisterMachineGenome(randomSource,meanSize,sizeDeviation);
  }

  public Genome canonicalize()
  {
    WeakReference r;
    Genome g;
    if (canonicalGenomes == null)
      canonicalGenomes = new WeakHashMap(131072,0.75F);
    synchronized(canonicalGenomes) {
      r = (WeakReference)canonicalGenomes.get(this);
      if (r == null) {
        canonicalGenomes.put(this,new WeakReference(this));
        return this;
      } else if ((g = (Genome)r.get()) == null) {
        canonicalGenomes.put(this,new WeakReference(this));
        return this;
      }
    }
    return g;
  }

  /**
   * Returns a hash code for this genome
   *
   * @return Hash code
   */
  public int hashCode()
  {
    if (hashCode == -1) {
      for(int i=0;i<genomeBits.length;i++)
        hashCode ^= genomeBits[i];
    }
    return hashCode;
  }

  /**
   * Returns whether or not an object is equal to this genome object
   *
   * @param o Object to compare against
   * @return Equal?
   */
  public boolean equals(Object o)
  {
    if (o != null) {
      if (o instanceof RegisterMachineGenome) {
        if ((((RegisterMachineGenome)o).hashCode == hashCode)&&(((RegisterMachineGenome)o).genomeSize == genomeSize))
          return Arrays.equals(((RegisterMachineGenome)o).genomeBits,genomeBits);
      }
    }
    return false;
  }

  public Genome pointMutation(RandomSource randomSource)
  {
    // Make a copy of this genome
    RegisterMachineGenome newGenome = new RegisterMachineGenome();
    newGenome.maxInstructions = maxInstructions;
    newGenome.hashCode = -1;
    newGenome.genomeSize = genomeSize;
    newGenome.genomeBits = new int[genomeBits.length];
    for(int i=0;i<genomeBits.length;i++)
      newGenome.genomeBits[i] = genomeBits[i];

    // Mutate a bit
    int bit = (randomSource.randomPositiveInteger() % 30)+1;
    int n = randomSource.randomPositiveInteger() % genomeBits.length;
    if ((newGenome.genomeBits[n] & BITS[bit]) == 0)
      newGenome.genomeBits[n] |= BITS[bit];
    else newGenome.genomeBits[n] &= NOTBITS[bit];

    return newGenome;
  }

  /**
   * Returns a human-readable String of the instruction sequence of this genome
   *
   * @return Human-readable string
   */
  public String toString()
  {
    int pos = 0;
    int bptr = 0;
    int codon = 0;
    char[] r = new char[genomeSize];
    for(int i=0;i<genomeSize;i++) {
      // Get codon at current position and advance
      switch (pos) {
        case 0:
          codon = (genomeBits[bptr] >> 24) & TOP_6_INT_BITS;
          pos = 1;
          break;
        case 1:
          codon = (genomeBits[bptr] >> 18) & TOP_6_INT_BITS;
          pos = 2;
          break;
        case 2:
          codon = (genomeBits[bptr] >> 12) & TOP_6_INT_BITS;
          pos = 3;
          break;
        case 3:
          codon = (genomeBits[bptr] >> 6) & TOP_6_INT_BITS;
          pos = 4;
          break;
        case 4:
          codon = genomeBits[bptr++] & TOP_6_INT_BITS;
          pos = 0;
          break;
      }
      r[i] = CODON_CHAR_MAPPING[codon];
    }
    return new String(r);
  }

  public int writeTo(OutputStream out)
    throws IOException
  {
    int n = 0;
    for(int i=0;i<genomeBits.length;i++) {
      out.write((genomeBits[i] >> 24) & 0x3f);
      out.write((genomeBits[i] >> 16) & 0xff);
      out.write((genomeBits[i] >> 8) & 0xff);
      out.write(genomeBits[i] & 0xff);
      n += 4;
    }
    return n;
  }

  public int getCodonCount()
  {
    return 64;
  }

  public void getCodonDistribution(long[] codonCounts)
  {
    int pos = 0;
    int bptr = 0;
    for(int i=0;i<genomeSize;i++) {
      ++codonCounts[(genomeBits[bptr] >> (24-(6*pos++))) & TOP_6_INT_BITS];
      if (pos >= 5) {
        ++bptr;
        pos = 0;
      }
    }
  }

  public int checksum()
  {
    return hashCode();
  }

  public int size()
  {
    return genomeSize;
  }

  public int sizeBytes()
  {
    return (genomeBits.length * 4);
  }

  public int execute(IntegerInput[] input,Universe output,Cell cell,int[] memory)
    throws DeathException
  {
    // State of virtual machine
    int a = 0;
    int p = 0;
    int channel = 0;

    // Instruction execution counter
    int ictr = 0;

    // Are we inside an intron?
    boolean betweenGenes = true;

    // Depth of false LOOPs
    int falseLoopDepth = 0;

    // Get a stack and make sure it's big enough to hold as many loops as we
    // could conceivably have
    PositionInformationStack loopStack = (PositionInformationStack)threadLocalStack.get();
    if (loopStack.stack.length < (genomeSize*2))
      loopStack.stack = new int[genomeSize*2];

    // Execute genome by stepping through bitmap six-bits by six-bits
    int bptr = 0;
    int pos = 0;
    int thispos,thisbptr;
    int mv = 0;
    for(int i=0;i<genomeSize;i++) {
      // Save existing position information so it can be pushed on loops
      thispos = pos;
      thisbptr = bptr;

      if (betweenGenes) {
        // If we're between genes, look for GO which is the start codon as well
        if (CODON_EXEC_MAPPING[(genomeBits[bptr] >> (24-(6*pos++))) & TOP_6_INT_BITS] == 7) /* 7 == GO */
          betweenGenes = false;
      } else if (falseLoopDepth > 0) {
        // If we've passed a LOOP and a==0, keep tracing through LOOP/REP
        // pairs until matching REP is found
        switch(CODON_EXEC_MAPPING[(genomeBits[bptr] >> (24-(6*pos++))) & TOP_6_INT_BITS]) {
          case 9: /* LOOP */
            ++falseLoopDepth;
            break;
          case 13: /* REP */
            --falseLoopDepth;
            break;
        }
      } else {
        // Each instruction actually executed costs one point
        if (cell != null)
          cell.decEnergy();

        // Increment instructions executed counter and check for overflow
        if (++ictr > maxInstructions)
          throw instructionOverflowException;

        // Execute codon instruction
        switch (CODON_EXEC_MAPPING[(genomeBits[bptr] >> (24-(6*pos++))) & TOP_6_INT_BITS]) {
          case 0: /* ADD */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                a += memory[p];
              else a += memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              a += (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            break;
          case 1: /* AND */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                a &= memory[p];
              else a &= memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              a &= (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            break;
          case 2: /* BACK */
            --p;
            break;
          case 3: /* CMP */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                mv = memory[p];
              else mv = memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              mv = (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            a = ((a < mv) ? -1 : ((a == mv) ? 0 : 1));
            break;
          case 4: /* DEC */
            --a;
            break;
          case 5: /* DIV */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                mv = memory[p];
              else mv = memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              mv = (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            if (mv == 0)
              a = 0;
            else a /= mv;
            break;
          case 6: /* FWD */
            ++p;
            break;
          case 7: /* GO */
            p = a;
            break;
          case 8: /* INC */
            ++a;
            break;
          case 9: /* LOOP */
            // Push position if 'a' is nonzero
            if (a == 0)
              ++falseLoopDepth;
            else {
              if (loopStack.stackPtr+4 >= loopStack.stack.length)
                throw instructionOverflowException;
              loopStack.stack[loopStack.stackPtr++] = thisbptr;
              loopStack.stack[loopStack.stackPtr++] = thispos;
              loopStack.stack[loopStack.stackPtr++] = i;
            }
            break;
          case 10: /* MUL */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                a *= memory[p];
              else a *= memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              a *= (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            break;
          case 11: /* OR */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                a |= memory[p];
              else a |= memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              a |= (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            break;
          case 12: /* READ */
            a = ((input == null) ? 0 : ((input[channel] == null) ? 0 : input[channel].read()));
            break;
          case 13: /* REP */
            if (loopStack.stackPtr > 0) {
              if (a != 0) {
                // Restore position of matching LOOP if 'a' is nonzero
                i = loopStack.stack[--loopStack.stackPtr]-1;
                pos = loopStack.stack[--loopStack.stackPtr];
                bptr = loopStack.stack[--loopStack.stackPtr];
                continue;
              } else {
                // Drop it off the stack and move on if a == 0
                loopStack.stackPtr -= 3;
              }
            }
            break;
          case 14: /* SCH */
            channel = ((a == -2147483648) ? 2147483647 : Math.abs(a)) % Archis.CHANNEL_COUNT;
            break;
          case 15: /* SHL */
            a <<= 1;
            break;
          case 16: /* SHR */
            a >>= 1;
            break;
          case 17: /* STA */
            if (p >= 0) {
              // Set memory value at location
              if (p < memory.length)
                memory[p] = a;
              else memory[p % memory.length] = a;
            } else {
              // Set genome value at location (not implemented at the moment)
            }
            a = 0;
            break;
          case 18: /* STOP */
            a = 0;
            channel = 0;
            p = 0;
            betweenGenes = true;
            break;
          case 19: /* WRITE */
            if (output != null)
              output.evaluateOutput(cell,channel,a);
            break;
          case 20: /* XOR */
            if (p >= 0) {
              // Pointers >= 0 read memory locations
              if (p < memory.length)
                a ^= memory[p];
              else a ^= memory[p % memory.length];
            } else {
              // Pointers < 0 read genome codons at ((abs(p)-1) % genomeSize)
              mv = (((p == -2147483648) ? 2147483647 : Math.abs(p))-1) % genomeSize;
              a ^= (genomeBits[mv / 5] >> (24-(6*(mv % 5)))) & TOP_6_INT_BITS;
            }
            break;
        }
      }

      // Handle pos/bptr to wrap around to next integer if necessary
      if (pos >= 5) {
        ++bptr;
        pos = 0;
      }
    }

    return ictr;
  }
}

package org.api.archis.life;

import java.util.*;
import java.io.*;

import org.api.archis.*;
import org.api.archis.universe.*;
import org.api.archis.utils.*;

public interface Genome extends Externalizable
{
  /**
   * Constructs a new genome of the same type
   *
   * @param codons Codons to construct new genome from
   * @param start Start of codons in byte array
   * @param length Length of codons in byte array
   * @return New genome object (unrelated to this one)
   */
  Genome createNew(byte[] codons,int start,int length);

  /**
   * Constructs a new genome of the same type
   *
   * @param codons Codons to construct new genome from
   * @return New genome object (unrelated to this one)
   */
  Genome createNew(byte[] codons);

  /**
   * Constructs a new genome of the same type from codons in human readable form
   *
   * @param randomSource Source for random numbers to select codons for instructions (if needed for this genome type)
   * @param syntheticGenome Codons in some human readable format (genome type specific)
   * @return New genome object (unrelated to this one)
   */
  Genome createNew(RandomSource randomSource,String syntheticGenome);

  /**
   * Constructs a new random genome of the same type
   *
   * @param randomSource Source for random numbers to create random genome
   * @param meanSize Mean size in codons (actual size of a codon is genome type specific)
   * @param sizeDeviation Size deviation +/- mean
   * @return New genome object (unrelated to this one)
   */
  Genome createNew(RandomSource randomSource,int meanSize,int sizeDeviation);

  /**
   * <p>Returns a 'canonical' equivalent copy of this genome.</p>
   *
   * <p>
   * This method works by maintaining an internal Map with weak keys and
   * values containing genomes.  When this is called, any equivalent genomes
   * within this Map are returned.  Otherwise, the current genome is added
   * to the map and the method returns itself.  The result of using this is
   * to ensure that only one copy of each genome actually resides in memory.
   * Use of this method saves memory at the expense of a small CPU overhead.
   * </p>
   *
   * <p>
   * Genomes must implement this.
   * </p>
   *
   * @return Genome equal to this one (may return the same object)
   */
  Genome canonicalize();

  /**
   * Returns a genome with a single point mutation from this one
   *
   * @param randomSource Randomness source
   * @return Point mutated genome
   */
  Genome pointMutation(RandomSource randomSource);

  /**
   * Calculates a checksum of this genome
   *
   * @return Checksum value
   */
  int checksum();

  /**
   * Executes the genome and returns the resulting output
   *
   * @param input Input data as an array of int[] arrays (any other type will cause type cast error)
   * @param output Recipient of output
   * @param cell Cell genome is executing for
   * @param memory Memory of cell
   * @throws DeathException Cell death occurred during execution
   * @return Number of instructions executed (not including introns)
   */
  int execute(IntegerInput[] input,Universe output,Cell cell,int[] memory)
    throws DeathException;

  /**
   * Get size in codons
   *
   * @return Number of codons in genome
   */
  int size();

  /**
   * Get actual size in bytes in memory
   *
   * @return Number of bytes taken up by genome
   */
  int sizeBytes();

  /**
   * Dumps out the genome as bytes to an output stream
   *
   * @param out Output stream
   * @throws IOException An error occurred writing to the stream
   * @return Number of bytes written
   */
  int writeTo(OutputStream out)
    throws IOException;

  /**
   * Returns the number of codons that this genome type uses
   *
   * @return Number of codons
   */
  int getCodonCount();

  /**
   * <p>Counts all codons in genome</p>
   *
   * <p>
   * This method increments each element of the array based on it's corresponding
   * codon.  The array must be at least getCodonCount() in length or an
   * ArrayIndexOutOfBoundsException may occur.
   * </p>
   *
   * @param codonCounts Array of counts to be incremented
   */
  void getCodonDistribution(long[] codonCounts);
}

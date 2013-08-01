package org.api.archis.life;

import java.util.*;

import org.api.archis.life.vms.*;
import org.api.archis.utils.RandomSource;

/**
 * Class to create genomes and manage genome types
 *
 * <p>When creating children, use the create() methods of the Genome object.
 * They are faster than looking up the type and then calling this.</p>
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class GenomeFactory
{
  /**
   * Randomly select any genome type
   */
  public static final int GENOME_TYPE_RANDOM = 0;
  /**
   * 21-instruction register machine
   */
  public static final int GENOME_TYPE_REGISTERMACHINE = 1;

  /**
   * Mod value for random selection
   */
  private static final int GENOME_TYPE__MAX = 2;

  /**
   * Random number generator
   */
  private static Random random = new Random(System.currentTimeMillis());

  /**
   * Genome types and values
   */
  private static Map genomeTypes;
  static
  {
    HashMap tmp = new HashMap(16,0.99F);
    tmp.put("(Random Type)",new Integer(GENOME_TYPE_RANDOM));
    tmp.put("Register Machine",new Integer(GENOME_TYPE_REGISTERMACHINE));
    genomeTypes = Collections.unmodifiableMap(tmp);
  }

  /**
   * <p>Gets a Map containing all genome types</p>
   *
   * <p>Keys in this map are type names, and values are Integer objects
   * corresponding to the types defined here as constants.</p>
   *
   * @return Genome types available
   */
  public static Map getGenomeTypes()
  {
    return genomeTypes;
  }

  /**
   * Creates a new genome
   *
   * @param type Type of genome
   * @param codons Codons for genome
   * @return New genome object
   * @throws IllegalArgumentException Invalid genome type
   */
  public static Genome create(int type,byte[] codons)
    throws IllegalArgumentException
  {
    switch(type) {
      case GENOME_TYPE_RANDOM:
        return create(Math.abs(random.nextInt()) % GENOME_TYPE__MAX,codons);
      case GENOME_TYPE_REGISTERMACHINE:
        return new RegisterMachineGenome(codons);
    }
    throw new IllegalArgumentException("Unrecognized genome type");
  }

  /**
   * Creates a new genome
   *
   * @param type Type of genome
   * @param codons Codons for genome
   * @param start Start of codons
   * @param length Length of codons
   * @return New genome object
   * @throws IllegalArgumentException Invalid genome type
   */
  public static Genome create(int type,byte[] codons,int start,int length)
    throws IllegalArgumentException
  {
    switch(type) {
      case GENOME_TYPE_RANDOM:
        return create(Math.abs(random.nextInt()) % GENOME_TYPE__MAX,codons,start,length);
      case GENOME_TYPE_REGISTERMACHINE:
        return new RegisterMachineGenome(codons,start,length);
    }
    throw new IllegalArgumentException("Unrecognized genome type");
  }

  /**
   * Creates a new genome from a human readable codon set
   *
   * @param type Type of genome
   * @param randomSource Random number generator
   * @param syntheticGenome Human readable codons (format is type specific)
   * @return New genome object
   * @throws IllegalArgumentException Invalid genome type
   */
  public static Genome create(int type,RandomSource randomSource,String syntheticGenome)
    throws IllegalArgumentException
  {
    switch(type) {
      case GENOME_TYPE_RANDOM:
        return create(Math.abs(random.nextInt()) % GENOME_TYPE__MAX,randomSource,syntheticGenome);
      case GENOME_TYPE_REGISTERMACHINE:
        return new RegisterMachineGenome(randomSource,syntheticGenome);
    }
    throw new IllegalArgumentException("Unrecognized genome type");
  }

  /**
   * Creates a new random genome
   *
   * @param type Type of genome
   * @param randomSource Random number generator
   * @param meanSize Mean size in codons (what a codon means is genome type specific)
   * @param sizeDeviation Standard deviation +/- mean size
   * @return New genome object
   * @throws IllegalArgumentException Invalid genome type
   */
  public static Genome create(int type,RandomSource randomSource,int meanSize,int sizeDeviation)
    throws IllegalArgumentException
  {
    switch(type) {
      case GENOME_TYPE_RANDOM:
        return create(Math.abs(random.nextInt()) % GENOME_TYPE__MAX,randomSource,meanSize,sizeDeviation);
      case GENOME_TYPE_REGISTERMACHINE:
        return new RegisterMachineGenome(randomSource,meanSize,sizeDeviation);
    }
    throw new IllegalArgumentException("Unrecognized genome type");
  }
}

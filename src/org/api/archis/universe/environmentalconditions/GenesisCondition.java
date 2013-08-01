package org.api.archis.universe.environmentalconditions;

import java.util.*;
import java.rmi.RemoteException;

import org.api.archis.*;
import org.api.archis.life.*;
import org.api.archis.universe.*;
import org.api.archis.utils.RandomSource;

/**
 * <p>A condition to induce genesis in an empty universe containing no life.</p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class GenesisCondition implements EnvironmentalCondition
{
  /**
   * Description of condition
   */
  public static final String CONDITION_DESCRIPTION = "Generates cells with random genomes to create genesis from randomness.";

  private Universe universe;
  private Simulation simulation;
  private RandomSource randomSource;
  private int createCellsPerTick;
  private int genomeMeanSize;
  private int genomeSizeDeviation;
  private int genomeType;
  private int newCellEnergy;
  private long totalCellsCreated;

  public GenesisCondition()
  {
    totalCellsCreated = 0L;
    newCellEnergy = 100000;
    genomeMeanSize = 100;
    genomeSizeDeviation = 50;
    createCellsPerTick = 10;
    genomeType = GenomeFactory.GENOME_TYPE_RANDOM;
  }

  /**
   * Sets the number of cells to create per tick
   *
   * @param createCellsPerTick Number to create per tick or 0 for none
   */
  public void setCreateCellsPerTick(int createCellsPerTick)
  {
    this.createCellsPerTick = createCellsPerTick;
  }

  /**
   * Gets the number of cells to create per tick
   *
   * @return Number of cells to create per tick
   */
  public int getCreateCellsPerTick()
  {
    return createCellsPerTick;
  }

  /**
   * Gets the genome type
   *
   * @return Genome type as defined in GenomeFactory
   * @see org.api.archis.life.GenomeFactory
   */
  public int getGenomeType()
  {
    return genomeType;
  }

  /**
   * Sets the genome type
   *
   * @param genomeType Genome type as defined in GenomeFactory
   * @see org.api.archis.life.GenomeFactory
   */
  public void setGenomeType(int genomeType)
  {
    this.genomeType = genomeType;
  }

  /**
   * Get the mean genome size
   *
   * @return Mean genome size
   */
  public int getGenomeMeanSize()
  {
    return genomeMeanSize;
  }

  /**
   * Get deviation +/- mean genome size
   *
   * @return Genome size deviation
   */
  public int getGenomeSizeDeviation()
  {
    return genomeSizeDeviation;
  }

  /**
   * Gets the energy to give new cells
   *
   * @return New cell energy
   */
  public int getNewCellEnergy()
  {
    return newCellEnergy;
  }

  /**
   * Set mean size of new genomes
   *
   * @param genomeMeanSize Mean size of new genomes
   */
  public void setGenomeMeanSize(int genomeMeanSize)
  {
    this.genomeMeanSize = genomeMeanSize;
  }

  /**
   * Set genome size deviation (+/- mean size)
   *
   * @param genomeSizeDeviation Genome size deviation
   */
  public void setGenomeSizeDeviation(int genomeSizeDeviation)
  {
    this.genomeSizeDeviation = genomeSizeDeviation;
  }

  /**
   * Set energy of new cells
   *
   * @param newCellEnergy Energy for new cells
   */
  public void setNewCellEnergy(int newCellEnergy)
  {
    this.newCellEnergy = newCellEnergy;
  }

  public void init(Universe universe,Simulation simulation)
  {
    this.universe = universe;
    this.simulation = simulation;
    randomSource = simulation.randomSource();
  }

  public void destroy()
  {
    simulation.removeStatistic("GC1 [GenesisCondition] Total Cells Created");
  }

  public void showGUI()
  {
    new org.api.archis.gui.conditions.GenesisConditionWindow(this,simulation).setVisible(true);
  }

  public void preTickNotify()
    throws ConditionExpirationException
  {
  }

  public void postTickNotify()
    throws ConditionExpirationException
  {
    for(int i=0;i<createCellsPerTick;i++) {
      universe.addCell(null,new Cell(simulation,universe,null,newCellEnergy,GenomeFactory.create(genomeType,randomSource,genomeMeanSize,genomeSizeDeviation)));
      ++totalCellsCreated;
    }

    simulation.setStatistic("GC1 [GenesisCondition] Total Random Cells Created",totalCellsCreated);
  }

  public void preExecutionNotify(Cell cell)
  {
  }

  public void evaluateOutput(Cell l, int channel, int value)
    throws DeathException
  {
  }

  public void deathNotify(Cell deadCell,String reason)
  {
  }

  public boolean newCellNotify(Cell parent,Cell newCell)
  {
    return true;
  }

  public void initCellNotify(Cell cell)
  {
  }

  public String getChannelDescription(int channel)
  {
    return null;
  }
}

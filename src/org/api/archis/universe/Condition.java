package org.api.archis.universe;

import org.api.archis.Simulation;
import org.api.archis.life.Cell;

/**
 * <p>A condition defines some aspect of the universe relative to cells</p>
 *
 * <p>Typically, conditions will implement one of the subclasses:
 * EnvironmentalCondition, Catastrophe, or RewardFunction.</p>
 *
 * <p>Conditions should contain a public static String called
 * CONDITION_DESCRIPTION that carries a description of the condition.</p>
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public interface Condition
{
  /**
   * Opens Swing or AWT GUI if it exists, otherwise does nothing.
   */
  void showGUI();

  /**
   * Called by the universe whenever a new cell is added/created
   *
   * @param parent Parent cell or null if synthetic or random
   * @param newCell New cell
   * @return Should new cell be allowed to be created? true/false
   */
  boolean newCellNotify(Cell parent,Cell newCell);

  /**
   * Called by the universe whenever a cell dies
   *
   * @param deadCell Cell that died
   * @param reason Reason for death
   */
  void deathNotify(Cell deadCell,String reason);

  /**
   * <p>Called before each cell execution</p>
   *
   * <p>This method may kill the cell, and this will prevent execution
   * but will not prevent preExecutionNotify from being called for other
   * cells.  When doing other things, alive() should be checked in the
   * cell first to save processing time.</p>
   *
   * @param l Cell to create input for
   */
  void preExecutionNotify(Cell l);

  /**
   * Called by universe when added to universe
   *
   * @param universe Universe we exist within
   * @param simulation Simulation currently running
   */
  void init(Universe universe,Simulation simulation);

  /**
   * Called by universe after init() for every existing cell to notify
   * of pre-existing cells.
   *
   * @param cell Cell that currently exists
   */
  void initCellNotify(Cell cell);

  /**
   * Called by universe when removed from universe (always!)
   */
  void destroy();

  /**
   * Called before every tick begins
   *
   * @throws ConditionExpirationException Condition should be removed from chain
   */
  void preTickNotify()
    throws ConditionExpirationException;

  /**
   * Called after every tick ends
   *
   * @throws ConditionExpirationException Condition should be removed from chain
   */
  void postTickNotify()
    throws ConditionExpirationException;
}

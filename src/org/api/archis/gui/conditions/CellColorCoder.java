package org.api.archis.gui.conditions;

import java.awt.Color;

import org.api.archis.life.Cell;

/**
 * Interface for a class that can color-code cells
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface CellColorCoder
{
  Color getColor(Cell c);
}

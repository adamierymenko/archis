package org.api.archis.universe;

import java.util.Map;

import org.api.archis.Simulation;
import org.api.archis.life.*;

/**
 * </p>An environmental condition</p>
 *
 * <p>An environmental condition is an element of the universe, such as a
 * landscape or a metaphysical condition, with which lifeforms can interact
 * through I/O ports.</p>
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public interface EnvironmentalCondition extends Condition,IOHandler
{
}

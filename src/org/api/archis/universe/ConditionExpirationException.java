package org.api.archis.universe;

/**
 * Exception triggered by conditions to signal that they should
 * be removed from the condition chain.
 *
 * @author Adam Ierymenko
 * @version 2.0
 */

public class ConditionExpirationException extends Exception
{
  public ConditionExpirationException() { super(); }
}
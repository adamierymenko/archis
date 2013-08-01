package org.api.archis.life;

/**
 * Thrown by a cell upon death
 *
 * @author Adam Ierymenko
 * @version 1.0
 */

public class DeathException extends Exception
{
  private String cause;

  /**
   * Constructs a new DeathException
   *
   * @param cause Cause of death
   */
  public DeathException(String cause)
  {
    super(cause);
    this.cause = cause;
  }

  /**
   * Gets the cause of death
   *
   * @return Description of cause of death
   */
  public String getCauseOfDeath()
  {
    return cause;
  }
}

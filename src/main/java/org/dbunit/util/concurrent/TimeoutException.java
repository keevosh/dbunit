/*
  File: TimeoutException.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  29Jun1998  dl               Create public version
   4Aug1998  dl               Change to extend InterruptedException
*/

package org.dbunit.util.concurrent;

/**
 * Thrown by synchronization classes that report
 * timeouts via exceptions. The exception is treated
 * as a form (subclass) of InterruptedException. This both
 * simplifies handling, and conceptually reflects the fact that
 * timed-out operations are artificially interrupted by timers.
 * 
 * @author Doug Lea
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 766 $ $Date: 2008-08-01 06:05:20 -0500 (Fri, 01 Aug 2008) $
 * @since ? (pre 2.1)
 */
public class TimeoutException extends InterruptedException {

  /** 
   * The approximate time that the operation lasted before 
   * this timeout exception was thrown.
   **/

  public final long duration;
  /**
   * Constructs a TimeoutException with given duration value.
   **/
  public TimeoutException(long time) {
    duration = time;
  }

  /**
     * Constructs a TimeoutException with the
     * specified duration value and detail message.
     */
  public TimeoutException(long time, String message) {
    super(message);
    duration = time;
  }
}

/*
  File: BoundedChannel.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
*/


package org.dbunit.util.concurrent;

/**
 * A channel that is known to have a capacity, signifying
 * that <code>put</code> operations may block when the
 * capacity is reached. Various implementations may have
 * intrinsically hard-wired capacities, capacities that are fixed upon
 * construction, or dynamically adjustable capacities.
 * @see DefaultChannelCapacity
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>] <p>
 * 
 * @author Doug Lea
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 766 $ $Date: 2008-08-01 06:05:20 -0500 (Fri, 01 Aug 2008) $
 * @since ? (pre 2.1)
 * 
 */
public interface BoundedChannel extends Channel {
  /** 
   * Return the maximum number of elements that can be held.
   * @return the capacity of this channel.
   **/
  public int capacity();
}

/*
  File: LinkedNode.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
  25may2000  dl               Change class access to public
  26nov2001  dl               Added no-arg constructor, all public access.
*/

package org.dbunit.util.concurrent;

/** 
 * A standard linked list node used in various queue classes
 * 
 * @author Doug Lea
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 766 $ $Date: 2008-08-01 06:05:20 -0500 (Fri, 01 Aug 2008) $
 * @since ? (pre 2.1)
 */
public class LinkedNode { 
  public Object value;
  public LinkedNode next;
  public LinkedNode() {}
  public LinkedNode(Object x) { value = x; }
  public LinkedNode(Object x, LinkedNode n) { value = x; next = n; }
}

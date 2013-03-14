/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.dataset;


/**
 * Provides arbitrary values for one single database/ITable row.
 * 
 * @author gommmma
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 766 $ $Date: 2008-08-01 06:05:20 -0500 (Fri, 01 Aug 2008) $
 * @since 2.3.0
 */
public interface IRowValueProvider {

	/**
	 * Returns the column value for the column with the given name of the currently processed row
     * @param columnName The db column name for which the value should be provided (current row's value)
     * @return The value of the given column in the current row
     * @throws DataSetException
     */
    public Object getColumnValue(String columnName) throws DataSetException;
}

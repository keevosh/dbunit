/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2009, DbUnit.org
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

package org.dbunit.dataset.datatype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.ITable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Inserts and reads {@link BigInteger} values into/from a database.
 * 
 * @author gommma (gommma AT users.sourceforge.net)
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 1032 $ $Date: 2009-09-14 02:15:47 -0500 (Mon, 14 Sep 2009) $
 * @since 2.4.6
 */
public class BigIntegerDataType extends AbstractDataType
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(BigIntegerDataType.class);

    public BigIntegerDataType()
    {
        super("BIGINT", Types.BIGINT, BigInteger.class, true);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException
    {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE)
        {
            return null;
        }

        if (value instanceof BigInteger)
        {
            return (BigInteger)value;
        }
        else if(value instanceof BigDecimal)
        {
            return ((BigDecimal)value).toBigInteger();
        }
        else if (value instanceof Number)
        {
            long l = ((Number)value).longValue();
            return new BigInteger(String.valueOf(l));
        }

        try
        {
            BigDecimal bd = new BigDecimal(value.toString());
            return bd.toBigInteger();
        }
        catch (java.lang.NumberFormatException e)
        {
            throw new TypeCastException(value, this, e);
        }
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("getSqlValue(column={}, resultSet={}) - start", new Integer(column), resultSet);

        BigDecimal value = resultSet.getBigDecimal(column);
        if (resultSet.wasNull())
        {
            return null;
        }
        return value.toBigInteger();
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
    	if(logger.isDebugEnabled())
    		logger.debug("setSqlValue(value={}, column={}, statement={}) - start",
        		new Object[]{value, new Integer(column), statement} );

    	BigInteger val = (BigInteger)typeCast(value);
        statement.setBigDecimal(column, new BigDecimal(val));
    }
}






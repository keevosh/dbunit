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

package org.dbunit.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitRuntimeException;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DataSetUtils;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;
import org.dbunit.dataset.OrderedTableNameMap;
import org.dbunit.dataset.filter.ITableFilterSimple;
import org.dbunit.util.QualifiedTableName;
import org.dbunit.util.SQLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to a database instance as a {@link IDataSet}.
 * 
 * @author Manuel Laflamme
 * @author Last changed by: $Author: jeffjensen $
 * @version $Revision: 1189 $ $Date: 2010-06-05 17:49:22 -0500 (Sat, 05 Jun 2010) $
 * @since 1.0 (Feb 17, 2002)
 */
public class DatabaseDataSet extends AbstractDataSet
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDataSet.class);

    private final IDatabaseConnection _connection;
    private OrderedTableNameMap _tableMap = null;

    private final ITableFilterSimple _tableFilter;
    private final ITableFilterSimple _oracleRecycleBinTableFilter;

    /**
     * Creates a new database data set
     * @param connection
     * @throws SQLException
     */
    DatabaseDataSet(IDatabaseConnection connection) throws SQLException
    {
        this(connection, connection.getConfig().getFeature(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES));
    }


    /**
     * Creates a new database data set
     * @param connection The database connection
     * @param caseSensitiveTableNames Whether or not this dataset should use case sensitive table names
     * @throws SQLException
     * @since 2.4
     */
    public DatabaseDataSet(IDatabaseConnection connection, boolean caseSensitiveTableNames) throws SQLException
    {
        this(connection, caseSensitiveTableNames, null);
    }

    /**
     * Creates a new database data set
     * @param connection The database connection
     * @param caseSensitiveTableNames Whether or not this dataset should use case sensitive table names
     * @param tableFilter Table filter to specify tables to be omitted in this dataset. Can be <code>null</code>.
     * @throws SQLException
     * @since 2.4.3
     */
    public DatabaseDataSet(IDatabaseConnection connection, boolean caseSensitiveTableNames, ITableFilterSimple tableFilter)
    throws SQLException
    {
        super(caseSensitiveTableNames);
        if (connection == null) {
            throw new NullPointerException(
            "The parameter 'connection' must not be null");
        }
        _connection = connection;
        _tableFilter = tableFilter;
        _oracleRecycleBinTableFilter = new OracleRecycleBinTableFilter(connection.getConfig());
    }



    static String getSelectStatement(String schema, ITableMetaData metaData, String escapePattern)
    throws DataSetException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("getSelectStatement(schema={}, metaData={}, escapePattern={}) - start",
                    new Object[] { schema, metaData, escapePattern });
        }

        Column[] columns = metaData.getColumns();
        Column[] primaryKeys = metaData.getPrimaryKeys();

        if(columns.length==0){
            throw new DatabaseUnitRuntimeException("At least one column is required to build a valid select statement. "+
                    "Cannot load data for " + metaData);
        }

        // select
        StringBuffer sqlBuffer = new StringBuffer(128);
        sqlBuffer.append("select ");
        for (int i = 0; i < columns.length; i++)
        {
            if (i > 0)
            {
                sqlBuffer.append(", ");
            }
            String columnName = new QualifiedTableName(
                    columns[i].getColumnName(), null, escapePattern).getQualifiedName();
            sqlBuffer.append(columnName);
        }

        // from
        sqlBuffer.append(" from ");
        sqlBuffer.append(new QualifiedTableName(
                metaData.getTableName(), schema, escapePattern).getQualifiedName());

        // order by
        for (int i = 0; i < primaryKeys.length; i++)
        {
            if (i == 0)
            {
                sqlBuffer.append(" order by ");
            }
            else
            {
                sqlBuffer.append(", ");
            }
            sqlBuffer.append(new QualifiedTableName(primaryKeys[i].getColumnName(), null, escapePattern).getQualifiedName());

        }

        return sqlBuffer.toString();
    }

    /**
     * Get all the table names form the database that are not system tables.
     */
    private void initialize() throws DataSetException
    {
        logger.debug("initialize() - start");

        if (_tableMap != null)
        {
            return;
        }

        try
        {
            logger.debug("Initializing the data set from the database...");

            Connection jdbcConnection = _connection.getConnection();
            DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();

            String schema = _connection.getSchema();

            if(SQLHelper.isSybaseDb(jdbcConnection.getMetaData()) && !jdbcConnection.getMetaData().getUserName().equals(schema) ){
                logger.warn("For sybase the schema name should be equal to the user name. " +
                        "Otherwise the DatabaseMetaData#getTables() method might not return any columns. " +
                "See dbunit tracker #1628896 and http://issues.apache.org/jira/browse/TORQUE-40?page=all");
            }

            DatabaseConfig config = _connection.getConfig();
            String[] tableType = (String[])config.getProperty(DatabaseConfig.PROPERTY_TABLE_TYPE);
            IMetadataHandler metadataHandler = (IMetadataHandler) config.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);

            ResultSet resultSet = metadataHandler.getTables(databaseMetaData, schema, tableType);

            if(logger.isDebugEnabled())
            {
                logger.debug(SQLHelper.getDatabaseInfo(jdbcConnection.getMetaData()));
                logger.debug("metadata resultset={}", resultSet);
            }

            try
            {
                OrderedTableNameMap tableMap = super.createTableNameMap();
                while (resultSet.next())
                {
                    String schemaName = metadataHandler.getSchema(resultSet);
                    String tableName = resultSet.getString(3);

                    if(_tableFilter != null && !_tableFilter.accept(tableName))
                    {
                        logger.debug("Skipping table '{}'", tableName);
                        continue;
                    }
                    if(!_oracleRecycleBinTableFilter.accept(tableName))
                    {
                        logger.debug("Skipping oracle recycle bin table '{}'", tableName);
                        continue;
                    }


                    QualifiedTableName qualifiedTableName = new QualifiedTableName(tableName, schemaName);
                    tableName = qualifiedTableName.getQualifiedNameIfEnabled(config);

                    // Put the table into the table map
                    tableMap.add(tableName, null);
                }

                _tableMap = tableMap;
            }
            finally
            {
                resultSet.close();
            }
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbstractDataSet class

    protected ITableIterator createIterator(boolean reversed)
    throws DataSetException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("createIterator(reversed={}) - start", String.valueOf(reversed));
        }

        String[] names = getTableNames();
        if (reversed)
        {
            names = DataSetUtils.reverseStringArray(names);
        }

        return new DatabaseTableIterator(names, this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // IDataSet interface

    public String[] getTableNames() throws DataSetException
    {
        initialize();

        return _tableMap.getTableNames();
    }

    public ITableMetaData getTableMetaData(String tableName) throws DataSetException
    {
        logger.debug("getTableMetaData(tableName={}) - start", tableName);

        initialize();

        // Verify if table exist in the database
        if (!_tableMap.containsTable(tableName))
        {
            logger.error("Table '{}' not found in tableMap={}", tableName,
                    _tableMap);
            throw new NoSuchTableException(tableName);
        }

        // Try to find cached metadata
        ITableMetaData metaData = (ITableMetaData)_tableMap.get(tableName);
        if (metaData != null)
        {
            return metaData;
        }

        // Create metadata and cache it
        metaData = new DatabaseTableMetaData(tableName, _connection, true, super.isCaseSensitiveTableNames());
        // Put the metadata object into the cache map
        _tableMap.update(tableName, metaData);

        return metaData;
    }

    public ITable getTable(String tableName) throws DataSetException
    {
        logger.debug("getTable(tableName={}) - start", tableName);

        initialize();

        try
        {
            ITableMetaData metaData = getTableMetaData(tableName);

            DatabaseConfig config = _connection.getConfig();
            IResultSetTableFactory factory = (IResultSetTableFactory)config.getProperty(
                    DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY);
            return factory.createTable(metaData, _connection);
        }
        catch (SQLException e)
        {
            throw new DataSetException(e);
        }
    }


    private static class OracleRecycleBinTableFilter implements ITableFilterSimple
    {
        private final DatabaseConfig _config;

        public OracleRecycleBinTableFilter(DatabaseConfig config)
        {
            this._config = config;
        }

        public boolean accept(String tableName) throws DataSetException
        {
            // skip oracle 10g recycle bin system tables if enabled
            if(_config.getFeature(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES)) {
                // Oracle 10g workaround
                // don't process system tables (oracle recycle bin tables) which
                // are reported to the application due a bug in the oracle JDBC driver
                if (tableName.startsWith("BIN$"))
                {
                    return false;
                }
            }

            return true;
        }
    }

}

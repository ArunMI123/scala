import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;

import oracle.jdbc.OracleConnection;

public class SchemaConversion {
	static Properties props = new Properties();
	static Statement statement = null;
	static Statement schemaStatement = null;
	static Statement constraintStatement = null;
	static Statement foreignStatement = null;
	static Connection conSQL = null;
	static Connection conOracle = null;

	public static void getConnection() throws ClassNotFoundException, SQLException {
		// sql connectivity
		try {
			Class.forName(props.getProperty("jdbc.driver.sql"));
			conSQL = DriverManager.getConnection(props.getProperty("jdbc.url.sql"),
					props.getProperty("jdbc.username.sql"), props.getProperty("jdbc.pwd.sql"));

			// oracle connectivity
			Class.forName(props.getProperty("jdbc.driver"));
			conOracle = DriverManager.getConnection(props.getProperty("jdbc.url"), props.getProperty("jdbc.username"),
					props.getProperty("jdbc.pwd"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void getResultSet(Connection conSQL, int tableName) throws SQLException {
		try {
			System.out.println("Table count is: " + tableName);
			schemaStatement.executeBatch();
			statement.executeBatch();
			constraintStatement.executeBatch();
			conSQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	public static void getForeignKeyResultSet(Connection conSQL) throws SQLException {
		try {
			foreignStatement.executeBatch();
			conSQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	public static void checkQuery(Connection conOracle, String tableName)
			throws SQLException {
		StringBuilder checkConstraint = new StringBuilder();
		Statement checkStmt = null;
		ResultSet checkRS = null;
		try {
			String constrKeys = "select CONSTRAINT_NAME,  SEARCH_CONDITION from SYS.ALL_CONSTRAINTS where owner='"
					+ props.getProperty("jdbc.schema.name") + "'AND TABLE_NAME= '" + tableName
					+ "' and CONSTRAINT_TYPE='" + props.getProperty("check.key") + "'";
			checkStmt = conOracle.createStatement();
			checkRS = checkStmt.executeQuery(constrKeys);
			while (checkRS.next()) {
				checkConstraint.append("ALTER TABLE " + props.getProperty("jdbc.schema.name") + ".[" + tableName
						+ "] ADD CONSTRAINT " + checkRS.getString("CONSTRAINT_NAME") + " CHECK ("
						+ checkRS.getString("SEARCH_CONDITION") + ")\n");
			}
			System.out.println(checkConstraint.toString());
			constraintStatement.addBatch(checkConstraint.toString());

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.closeResultSet(checkRS);
			ConnectionHelper.closeStatement(checkStmt);
		}
	}

	public static void createQuery(StringBuilder query, String tableName, StringBuilder commentQuery)
			throws SQLException {
		String createQuery = "Create table " + props.getProperty("jdbc.schema.name") + ".[" + tableName + "](" + query
				+ ") \n" + commentQuery.toString();
		statement.addBatch(createQuery);
		System.out.println(createQuery);
	}

	public static void alterQuery(StringBuilder foreignKey, String tableName, StringBuilder fkQuery)
			throws SQLException {
		String foreignkeys = null;
		if (foreignKey != null && !foreignKey.toString().equals("")) {
			foreignkeys = "Alter table " + props.getProperty("jdbc.schema.name") + "." + tableName + " ADD \n"
					+ foreignKey;
			System.out.println(foreignkeys);
			foreignStatement.addBatch(foreignkeys);
		}
	}

	public static String dataTypeConvert(String colType, ResultSet rsOracleColumn) throws SQLException {
		String precision = rsOracleColumn.getString("COLUMN_SIZE");
		switch (colType) {
		case "VARCHAR2":
			return Constants.SQL_VARCHAR + "(" + precision + ")";
			
		case "NVARCHAR2":
			return Constants.SQL_NVARCHAR + "(" + precision + ")";

		case "DATE":
			return Constants.SQL_DATE;

		case "CHAR":
			return Constants.SQL_CHAR + "(" + precision + ")";

		case "BLOB":
			return Constants.SQL_BLOB;
			
		case "CLOB":
			return Constants.SQL_CLOB;

		case "LONG RAW":
			return Constants.SQL_LONGRAW;
		case "RAW":
			return Constants.SQL_RAW;

		case "TIMESTAMP(6)":
			return Constants.SQL_TIME;
		case "UROWID":
			return Constants.SQL_UROWID;

		default:
			return "unknown datatype";
		}
	}

	public static String dataTypeConvertNumber(int precision, int scale, String columnName, ArrayList<String> pkColList)
			throws SQLException {
		if (precision != 0 && scale != 0) {
			return Constants.SQL_DECIMAL;
		} else if (precision != 0 && scale == 0) {
			if (precision <= 2) {
				return Constants.SQL_TINY;
			} else if (precision > 2 && precision <= 4) {
				return Constants.SQL_SMALL;
			} else if (precision > 4 && precision <= 9) {
				return Constants.SQL_INT;
			} else if (precision > 9 && precision <= 21) {
				return Constants.SQL_BIGINT;
			} else if (precision == 22 && scale == 0) {
				if (pkColList.size() != 0) {
					for (String pkColumn : pkColList) {
						if (columnName.equals(pkColumn)) {
							return Constants.SQL_BIGINT;
						} else {
							return Constants.SQL_FLOAT;
						}
					}
				} else {
					return Constants.SQL_FLOAT;
				}
			}
			else if(precision > 22 && scale == 0) {
				return Constants.SQL_NUMERIC + "(" + precision + "," + scale + ")";
			}
		}
		return "unknown precision and scale";
	}

	public static String defaultTypeConvertion(String defaultValue) {
		if (defaultValue.equals("SYSDATE"))
			return Constants.SQL_DATE_DEFAULT;
		else if (defaultValue.contains("TO_DATE")) {
			String[] s = defaultValue.split("'");
			return "TRY_CONVERT(DATETIME, '" + s[1] + "')";
		}
		return defaultValue;
	}

	public static String primaryKeyConstraint(ResultSet primaryRS, StringBuilder primaryKey) throws SQLException {
		// Result Set for primary key
		TreeMap<Integer, String> pkTreeMap = new TreeMap<Integer, String>();
		String pkName = null;
		int pkSeq = 0;
		StringBuilder pkComposite = new StringBuilder();
		try {

			String pkColName = null;
			String pkConstraint = null;

			while (primaryRS.next()) {
				pkName = primaryRS.getString("PK_NAME");
				pkColName = primaryRS.getString("COLUMN_NAME");
				pkSeq = primaryRS.getInt("KEY_SEQ");
				pkTreeMap.put(pkSeq, pkColName);
			}
			for (String name : pkTreeMap.values()) {
				pkComposite.append(name + ", ");
			}
			if (pkName != null) {
				pkComposite.deleteCharAt(pkComposite.length() - 2);
				pkConstraint = "CONSTRAINT " + pkName + " PRIMARY KEY (" + pkComposite + ")";
				primaryKey.append(pkConstraint + ",\n");
			}

			if (primaryKey.length() != 0) {
				primaryKey.deleteCharAt(primaryKey.length() - 2);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConnectionHelper.closeResultSet(primaryRS);
		}
		return pkName;
	}

	public static void foreignKeyConstraint(String tableName, String schema, StringBuilder foreignKey,
			Connection conOracle) throws SQLException {
		// Result Set for foreign key
		Statement checkStmt = null;
		ResultSet foreignRS = null;
		try {
			String constrKeys = "SELECT a.table_name,LISTAGG(a.column_name,',')within group (order by a.POSITION) COLUMN_NAME, a.constraint_name, c.owner, "
					+ "c.r_owner, c_pk.table_name as r_table_name, c_pk.constraint_name as r_pk,LISTAGG(b.column_name,',')within group (order by b.POSITION) R_COLUMN_NAME "
					+ "FROM all_cons_columns a " + "JOIN all_constraints c ON a.owner = c.owner "
					+ "AND a.constraint_name = c.constraint_name "
					+ "JOIN all_constraints c_pk ON c.r_owner = c_pk.owner "
					+ "AND c.r_constraint_name = c_pk.constraint_name "
					+ "JOIN  all_cons_columns b ON c_pk.owner = b.owner "
					+ "and c_pk.CONSTRAINT_NAME = b.CONSTRAINT_NAME " + "and a.POSITION =b.POSITION "
					+ "WHERE c.constraint_type = '" + props.getProperty("Foreign.key") + "'" + " and c.owner ='"
					+ schema + "'" + " AND a.table_name = '" + tableName
					+ "' group by a.table_name,a.constraint_name,c.owner,c.r_owner,c_pk.table_name,c_pk.constraint_name";
			checkStmt = conOracle.createStatement();
			foreignRS = checkStmt.executeQuery(constrKeys);
			String fkColName = null;
			String fkName = null;
			String pkTable = null;
			String pkRefColName = null;
			String fkConstraint = null;
			while (foreignRS.next()) {
				pkTable = foreignRS.getString("r_table_name");
				fkName = foreignRS.getString("constraint_name");
				pkRefColName = foreignRS.getString("R_COLUMN_NAME");
				fkColName = foreignRS.getString("COLUMN_NAME");
				StringBuilder fkComposite = new StringBuilder();
				StringBuilder pkComposite = new StringBuilder();
				fkComposite.append(fkColName);
				pkComposite.append(pkRefColName);
				fkConstraint = "CONSTRAINT " + fkName + " FOREIGN KEY (" + (fkComposite) + ")" + " REFERENCES "
						+ props.getProperty("jdbc.schema.name") + "." + pkTable + "(" + pkComposite + "),\n";
				foreignKey.append(fkConstraint);
			}
			if (foreignKey.length() != 0) {
				foreignKey.deleteCharAt(foreignKey.length() - 2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.closeResultSet(foreignRS);
			ConnectionHelper.closeStatement(checkStmt);
		}
	}

	public static void getColumns(ResultSet rsOracleColumn, StringBuilder createQuery, ArrayList<String> pkColList,
			StringBuilder commentQuery) {
		try {
			while (rsOracleColumn.next()) {
				String columnName = rsOracleColumn.getString("COLUMN_NAME");
				String comment = rsOracleColumn.getString("REMARKS");
				String defaultType = null;
				String columnType = rsOracleColumn.getString("TYPE_NAME");
				int precision = rsOracleColumn.getInt("COLUMN_SIZE");
				int scale = rsOracleColumn.getInt("DECIMAL_DIGITS");
				String defaultValue = rsOracleColumn.getString("COLUMN_DEF");
				int Nullvalue = rsOracleColumn.getInt("NULLABLE");
				if (columnType.equals("NUMBER")) {
					String numberDatatype = dataTypeConvertNumber(precision, scale, columnName, pkColList);
					createQuery.append("["+ columnName + "] " + numberDatatype);
				} else {
					String Datatype = dataTypeConvert(columnType, rsOracleColumn);
					createQuery.append("["+ columnName + "] " + Datatype);
				}
				if (Nullvalue == 0) {
					createQuery.append(" NOT NULL");
				}
				if (defaultValue != null) {
					defaultType = defaultTypeConvertion(defaultValue.trim().toUpperCase());
					createQuery.append(" DEFAULT (" + defaultType + ")");
				}

				createQuery.append(", \n");

				if (comment != null) {
					comment = comment.replaceAll("'", "''");

					commentQuery.append("EXEC dbo.sp_addextendedproperty @name = N'MS_Description', @value = N" + "'"
							+ comment + "'" + ", @level0type = N'SCHEMA', @level0name = N'"
							+ props.getProperty("jdbc.schema.name") + "', @level1type = N'TABLE', @level1name = N'"
							+ rsOracleColumn.getString("TABLE_NAME") + "', @level2type = N'COLUMN', @level2name = N'"
							+ columnName + "' \n\n");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConnectionHelper.closeResultSet(rsOracleColumn);
		}
	}

	private static void getIndexs(DatabaseMetaData metaData, String schema, String tableName, String primaryKey) throws Exception {
		ResultSet rsOracleUniqueIndex = metaData.getIndexInfo(null, schema, tableName, false, false);
		StringBuilder index = new StringBuilder();
		String columnName = null;
		String uniqueIndexName = null;
		boolean nonUnique = false;
		try {
			while (rsOracleUniqueIndex.next()) {
				if (rsOracleUniqueIndex.getString("INDEX_NAME") != null
						&& !rsOracleUniqueIndex.getString("TABLE_NAME").equals("USER_PROFILE")	//RRE_ADM Functional Index
						&& !rsOracleUniqueIndex.getString("TABLE_NAME").equals("CUSTOMER")		//RRE_OWN 
						&& !rsOracleUniqueIndex.getString("TABLE_NAME").equals("FCGENERALINFO") //RISKANALYST
						&& !rsOracleUniqueIndex.getString("TABLE_NAME").equals("UPCUSTOMER")	//RISKANALYST
						&& !rsOracleUniqueIndex.getString("TABLE_NAME").equals("ICIF01_CEAS_CUSTOMER_STG") //CEAS_FEEDS
						&& !rsOracleUniqueIndex.getString("TABLE_NAME").equals("ICIF03_CEAS_CUSTOMER_STG")) { //CEAS_FEEDS
					if (!(rsOracleUniqueIndex.getString("INDEX_NAME").equals(uniqueIndexName))) {
						if (uniqueIndexName != null) {
							compositeColumnIndex(uniqueIndexName, columnName, index, tableName, nonUnique, primaryKey);
						}
						uniqueIndexName = rsOracleUniqueIndex.getString("INDEX_NAME");
						columnName = rsOracleUniqueIndex.getString("COLUMN_NAME");
						nonUnique = rsOracleUniqueIndex.getBoolean("NON_UNIQUE");
					} else {
						uniqueIndexName = rsOracleUniqueIndex.getString("INDEX_NAME");
						nonUnique = rsOracleUniqueIndex.getBoolean("NON_UNIQUE");
						if (columnName != null) {
							columnName = columnName + "," + rsOracleUniqueIndex.getString("COLUMN_NAME");
						} else {
							columnName = rsOracleUniqueIndex.getString("COLUMN_NAME");
						}
					}
				}
			}
			compositeColumnIndex(uniqueIndexName, columnName, index, tableName, nonUnique, primaryKey);
			constraintStatement.addBatch(index.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.closeResultSet(rsOracleUniqueIndex);
		}
	}

	private static void compositeColumnIndex(String uniqueIndexName, String columnName, StringBuilder index,
			String tableName, boolean nonUnique, String primaryKey) {
		if (uniqueIndexName != null) {
			// Non Cluster index
			if (nonUnique) {
				index.append("\nCreate NonClustered Index " + uniqueIndexName + " On "
						+ props.getProperty("jdbc.schema.name") + ".[" + tableName + "](" + columnName + ")");
			}
			// Non-Cluster with Uniqe key no primary key index
			else if (primaryKey == null) {
				index.append("ALTER TABLE " + props.getProperty("jdbc.schema.name") + ".[" + tableName
						+ "] ADD CONSTRAINT " + uniqueIndexName + " UNIQUE (" + columnName + ")\n");
			}
			// Non-Cluster with Uniqe key
			else if (!primaryKey.equals(uniqueIndexName) && primaryKey != null) {
				index.append("ALTER TABLE " + props.getProperty("jdbc.schema.name") + ".[" + tableName
						+ "] ADD CONSTRAINT " + uniqueIndexName + " UNIQUE (" + columnName + ")\n");
			}
		}
	}

	public static void main(String[] args) {
		ResultSet rsOracle = null;
		try {
			PropertyConfiguration configuration = new PropertyConfiguration();
			props = configuration.propertiyReader();
			getConnection();
			DatabaseMetaData metaData = conOracle.getMetaData();
			String schema = props.getProperty("jdbc.schema.name");
			rsOracle = metaData.getTables(null, schema, "%", null);
			// Enable oracle comments column's
			OracleConnection oraCon = (OracleConnection) conOracle;
			oraCon.setRemarksReporting(true);
			// getting the tables from the specified schema
			String tableName = null;
			StringBuilder fkQuery = new StringBuilder();
			
			statement = conSQL.createStatement();
			schemaStatement = conSQL.createStatement();
			constraintStatement = conSQL.createStatement();
			foreignStatement = conSQL.createStatement();
			String query = "IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = '"
					+ props.getProperty("jdbc.schema.name") + "') BEGIN EXEC('CREATE SCHEMA "
					+ props.getProperty("jdbc.schema.name") + "') END";
			System.out.println(query);
			schemaStatement.addBatch(query);
			ArrayList<String> pkColList = new ArrayList<String>();
			int tableCount = 0;
			while (rsOracle.next()) {
				tableCount += 1;
				StringBuilder foreignKey = new StringBuilder();
				StringBuilder createQuery = new StringBuilder();
				StringBuilder commentQuery = new StringBuilder();
				
				StringBuilder primaryKey = new StringBuilder();
				if (rsOracle.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.1"))) {
					tableName = rsOracle.getString("TABLE_NAME");
					ResultSet primaryRS = metaData.getPrimaryKeys(null, schema, tableName);
					String pkName = primaryKeyConstraint(primaryRS, primaryKey);
					foreignKeyConstraint(tableName, schema, foreignKey, conOracle);
					ResultSet rsOracleColumn = metaData.getColumns(null, schema, tableName, null);
					getColumns(rsOracleColumn, createQuery, pkColList, commentQuery);
					createQuery.append(primaryKey);
					createQuery(createQuery, tableName, commentQuery);
					checkQuery(conOracle, tableName);
					alterQuery(foreignKey, tableName, fkQuery);
					getIndexs(metaData, schema, tableName, pkName);
					if (tableCount == 100) {
						getResultSet(conSQL,tableCount);
						tableCount = 0;
					}
				}
			}
			getResultSet(conSQL,tableCount);
			getForeignKeyResultSet(conSQL);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.closeResultSet(rsOracle);
			ConnectionHelper.closeConnection(conOracle);
			ConnectionHelper.closeConnection(conSQL);
			ConnectionHelper.closeStatement(statement);
			ConnectionHelper.closeConnection(conSQL);
			ConnectionHelper.closeStatement(schemaStatement);
			ConnectionHelper.closeStatement(constraintStatement);
			ConnectionHelper.closeStatement(foreignStatement);
		}
	}
}
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ForeignKeyTool {
	static Properties props = new Properties();
	static Statement statement = null;
	static Statement schemaStatement = null;
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

	public static void getRsltSet(Connection conSQL) throws SQLException {
		try {
			schemaStatement.executeBatch();
			statement.executeBatch();
			conSQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.closeStatement(schemaStatement);
			ConnectionHelper.closeStatement(statement);
			ConnectionHelper.closeConnection(conSQL);
		}
	}

	public static void alterQuery(StringBuilder foreignKey, String tableName)
			throws SQLException {
		String foreignkeys = null;
		if (foreignKey != null && !foreignKey.toString().equals("")) {
			foreignkeys = "Alter table " + props.getProperty("jdbc.schema.name") + ".[" + tableName + "] ADD \n"
					+ foreignKey;
			System.out.println(foreignkeys);
			statement.addBatch(foreignkeys);
		}
	}

	public static void foreignKeyConstraint(String tableName, String schema, StringBuilder foreignKey, Connection conOracle)
			throws SQLException {
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

	public static void main(String[] args) {
		ResultSet rsOracle = null;
		try {
			PropertyConfiguration configuration = new PropertyConfiguration();
			props = configuration.propertiyReader();
			getConnection();
			DatabaseMetaData metaData = conOracle.getMetaData();
			String schema = props.getProperty("jdbc.schema.name");
			rsOracle = metaData.getTables(null, schema, "%", null);
			
			// getting the tables from the specified schema
			String tableName = null;
			statement = conSQL.createStatement();
			schemaStatement = conSQL.createStatement();
			String query = "IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = '"
					+ props.getProperty("jdbc.schema.name") + "') BEGIN EXEC('CREATE SCHEMA "
					+ props.getProperty("jdbc.schema.name") + "') END";
			System.out.println(query);
			schemaStatement.addBatch(query);
			
			while (rsOracle.next()) {
				StringBuilder foreignKey = new StringBuilder();
				if (rsOracle.getString(4).equalsIgnoreCase(props.getProperty("metadata.types.1"))) {
//					if (rsOracle.getString("TABLE_NAME").equalsIgnoreCase("QUALIFYING_QUESTION_SET")
//							|| rsOracle.getString("TABLE_NAME").equalsIgnoreCase("QQ_SET_MASTER")
//							|| rsOracle.getString("TABLE_NAME").equalsIgnoreCase("QUALIFYING_ANSWER")
//							|| rsOracle.getString("TABLE_NAME").equalsIgnoreCase("QUALIFYING_QUESTION")
//							|| rsOracle.getString("TABLE_NAME").equalsIgnoreCase("RM_PARAMETER")) {
					tableName = rsOracle.getString("TABLE_NAME");
					foreignKeyConstraint(tableName, schema, foreignKey, conOracle);
					alterQuery(foreignKey, tableName);
				}
				}
			//}
			getRsltSet(conSQL);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionHelper.closeResultSet(rsOracle);
			ConnectionHelper.closeConnection(conOracle);
			ConnectionHelper.closeConnection(conSQL);
		}
	}
}